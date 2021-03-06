package com.example.internetcookbook.fragmentview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.CalendarView
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ReceiptListAdapter
import com.example.internetcookbook.animations.Bounce
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.dialog.CustomDialog
import com.example.internetcookbook.dialog.DateDialog
import com.example.internetcookbook.dialog.QueryDialog
import com.example.internetcookbook.fragmentpresenter.CameraFragmentPresenter
import com.example.internetcookbook.fragmentpresenter.saveDate
import com.example.internetcookbook.fragmentpresenter.saveShop
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.helper.readImageFromPath
import com.example.internetcookbook.models.FoodMasterModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_custom.*
import kotlinx.android.synthetic.main.camera_show.*
import kotlinx.android.synthetic.main.camera_show.view.*
import kotlinx.android.synthetic.main.camera_show.view.mConfirmOption
import kotlinx.android.synthetic.main.date_dialog.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.listitems.view.*
import kotlinx.android.synthetic.main.query_dialog.*
import org.jetbrains.anko.*
import java.nio.ByteBuffer
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

val storedFood: ArrayList<FoodMasterModel> = ArrayList()
val validFoodItems = ArrayList<FoodMasterModel>()

class CameraFragmentView : BaseView(), LifecycleOwner,AnkoLogger {

    private val REQUEST_CODE_PERMISSIONS = 10

    // This is an array of all the permission specified in the manifest.
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    lateinit var cameraView: View
    var captureCheck = false
    var torch = false
    lateinit var customDialog: CustomDialog
    lateinit var dateDialog: DateDialog
    lateinit var queryDialog: QueryDialog
    var foodCreateCheck = false
    val elementArrayList = ArrayList<String>()
    val lineArrayList = ArrayList<String>()
    val filteredArrayList = ArrayList<String>()


    lateinit var presenter: CameraFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_camera, container, false)
        cameraView = view
        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)
        presenter = initPresenter(CameraFragmentPresenter(this)) as CameraFragmentPresenter

        view.mFoodListRecyclerView.layoutManager = layoutManager
        view.mRetakePicture.setOnClickListener {
            resetInformation()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
//      Check what page came from
        if (arguments != null) {
            val images = CameraFragmentViewArgs.fromBundle(arguments!!).foodcreate
            if (images == "food_create") {
                foodCreateCheck = true
                cameraView.mButtonFindText.visibility = View.INVISIBLE
                cameraView.mConfirmOption.visibility = View.INVISIBLE
            }
        }else {
            if (storedFood.size != 0) {
                cameraView.mListItems.visibility = View.VISIBLE
                cameraView.mListItems.bringToFront()
                cameraView.view_finder.visibility = View.INVISIBLE
                cameraView.mCameraShow.visibility = View.GONE
                presenter.doFoodCreatePageUpdate()
                if(cameraView.mShoppedAt.text == "Shop") {
                    cameraView.mShoppedAt.text = saveShop
                    cameraView.mFoodDate.text = saveDate
                }else{
                    saveShop = cameraView.mShoppedAt.text.toString()
                    saveDate = cameraView.mFoodDate.text.toString()
                }
            }
        }

        cameraView.mFoodListRecyclerView.adapter = ReceiptListAdapter(
            storedFood,
            presenter,
            validFoodItems,
            cameraView
        )
        cameraView.mFoodListRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()

        viewFinder = view!!.findViewById(R.id.view_finder)

        // Request camera permissions
        if (allPermissionsGranted()) {
                doAsync {
                    viewFinder.post { startCamera() }
                }
        } else {
            ActivityCompat.requestPermissions(activity!!, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }
    // Add this after onCreate

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    private fun startCamera() {
//        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(viewFinder.display.rotation)
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

//      Turns the torch on an off
        cameraView.mCameraFlashButton.setOnClickListener {
            torch = !torch
            if (torch) {
                val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
                val interpolator =
                    Bounce(0.2, 20.0)
                myAnim.interpolator = interpolator
                cameraView.mCameraFlashButton.startAnimation(myAnim)
                cameraView.mCameraFlashButton.setImageResource(R.drawable.flashon)
            }else{
                val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
                val interpolator =
                    Bounce(0.2, 20.0)
                myAnim.interpolator = interpolator
                cameraView.mCameraFlashButton.startAnimation(myAnim)
                cameraView.mCameraFlashButton.setImageResource(R.drawable.baseline_flash_off_white_36)
            }
        }

//      Take picture
        cameraView.mCameraCaptureButton.setOnClickListener {
            if(!captureCheck) {
                doAsync {
                    try {
                        if (torch) {
                            preview.enableTorch(torch)
                            Thread.sleep(1000)
                            preview.enableTorch(false)
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    uiThread {
                        captureCheck = true
                        setImageBitmap()
                        if (!foodCreateCheck) {
                            cameraView.mButtonFindText.visibility = View.VISIBLE
                        }
                    }
                }
                if (foodCreateCheck){
                    cameraView.mConfirmOption.visibility = View.VISIBLE
                }
            }else{
                captureCheck = false
                clearImageBitmap()
                if (!foodCreateCheck) {
                    cameraView.mButtonFindText.visibility = View.INVISIBLE
                }
                if (foodCreateCheck){
                    cameraView.mConfirmOption.visibility = View.INVISIBLE
                }
            }
        }

        cameraView.mAddCupboard.setOnClickListener {
            if(validFoodItems.size != 0) {
                val random = Random()
                val randomFoodItem = validFoodItems[random.nextInt(validFoodItems.size)]
                val expirationtime = randomFoodItem.food.expirationTimeReliability.toInt()
                val imagepath = randomFoodItem.food.imagePathReliability.toInt()
                val price = randomFoodItem.food.priceReliability.toInt()
                val shop = randomFoodItem.food.shopReliability.toInt()
                val itemArray = intArrayOf(expirationtime, imagepath, price, shop)
                when (getSmallest(itemArray, itemArray.size)) {
                    expirationtime -> {
                        queryDialog = QueryDialog(activity!!)
                        queryDialog.show()
                        queryDialog.mQuestionDialog.text = "Is the Expiration Date Correct"
                        queryDialog.mFoodImageDialog.setImageBitmap(readBit64ImageSingle(randomFoodItem.image))
                        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yy")
                        val c = Calendar.getInstance()
                        try {
                            c.time = dateFormat.parse(saveDate)!!
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        c.add(Calendar.DATE, randomFoodItem.food.expirationTime.toInt())
                        val dateFormat1: DateFormat = SimpleDateFormat("dd/MM/yy")
                        val output = dateFormat1.format(c.time)
                        queryDialog.mQueryDialog.text =
                            "${randomFoodItem.food.name} will expire on $output"
                        queryDialog.setCanceledOnTouchOutside(false)
                        queryDialog.mQueryDialogYes.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doExpirationYes(randomFoodItem.food.oid)
                        }
                        queryDialog.mQueryDialogNo.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doExpireNo(randomFoodItem.food.oid)
                        }
                    }
                    imagepath -> {
                        queryDialog = QueryDialog(activity!!)
                        queryDialog.show()
                        queryDialog.mQuestionDialog.text = "Is this a good Image?"
                        queryDialog.mFoodImageDialog.setImageBitmap(
                            readBit64ImageSingle(
                                randomFoodItem.image
                            )
                        )
                        queryDialog.mQueryDialog.visibility = View.INVISIBLE
                        queryDialog.setCanceledOnTouchOutside(false)
                        queryDialog.mQueryDialogYes.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doImageYes(randomFoodItem.food.oid)
                        }
                        queryDialog.mQueryDialogNo.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doImageNo(randomFoodItem.food.oid)
                        }
                    }
                    price -> {
                        queryDialog = QueryDialog(activity!!)
                        queryDialog.show()
                        queryDialog.mQuestionDialog.text = "Is this price correct for this item?"
                        queryDialog.mFoodImageDialog.setImageBitmap(
                            readBit64ImageSingle(
                                randomFoodItem.image
                            )
                        )
                        queryDialog.mQueryDialog.text = "Price of item ${randomFoodItem.food.price}"
                        queryDialog.setCanceledOnTouchOutside(false)
                        queryDialog.mQueryDialogYes.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doPriceYes(randomFoodItem.food.oid)
                        }
                        queryDialog.mQueryDialogNo.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doPriceNo(randomFoodItem.food.oid)
                        }
                    }
                    shop -> {
                        queryDialog = QueryDialog(activity!!)
                        queryDialog.show()
                        queryDialog.mQuestionDialog.text = "Is this the correct shop for this item?"
                        queryDialog.mFoodImageDialog.setImageBitmap(
                            readBit64ImageSingle(
                                randomFoodItem.image
                            )
                        )
                        queryDialog.mQueryDialog.text = randomFoodItem.food.shop
                        queryDialog.setCanceledOnTouchOutside(false)
                        queryDialog.mQueryDialogYes.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doShopYes(randomFoodItem.food.oid)
                        }
                        queryDialog.mQueryDialogNo.setOnClickListener {
                            queryDialog.dismiss()
                            presenter.doAddCupboard(validFoodItems)
                            presenter.doShopNo(randomFoodItem.food.oid)
                        }
                    }
                }
            }else{
                Snackbar.make(cameraView,"No Data to Add", Snackbar.LENGTH_SHORT).show()
            }
        }

        cameraView.mButtonFindText.setOnClickListener {
            val bitmap = (cameraView.mCapturedImage.drawable as BitmapDrawable).bitmap
//            Resets data
            storedFood.clear()
            validFoodItems.clear()
            saveDate = ""
            saveShop = ""
            mChangeImageToText(bitmap)
        }

        if(foodCreateCheck){
            mConfirmOption.visibility = View.VISIBLE
        }

        cameraView.mConfirmOption.setOnClickListener {
            if (foodCreateCheck){
                val bitmap = (cameraView.mCapturedImage.drawable as BitmapDrawable).bitmap
                presenter.storeImage(bitmap)
                cameraView.findNavController().navigateUp()
            }else {
                presenter.doSelectImage()
            }
        }



        // Setup image analysis pipeline that computes average pixel luminance
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        // Build the image analysis use case and instantiate our analyzer
        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, LuminosityAnalyzer())
        }
        try {
            CameraX.bindToLifecycle(this, preview, analyzerUseCase)
        }catch (e: java.lang.Exception){
            e.getStackTraceString()
        }
    }

    private fun clearImageBitmap() {
        cameraView.view_finder.visibility = View.VISIBLE
        cameraView.mCapturedImage.setImageBitmap(null)
    }

    override fun noResults(){
        Snackbar.make(cameraView,"No Text Detected", Snackbar.LENGTH_SHORT).show()
    }

    fun mChangeImageToText(bitmap: Bitmap) {
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image).addOnSuccessListener { firebaseVisionText ->
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                }
    }

    private fun processResultText(resultText: FirebaseVisionText) {
        info { "Select Country Started" }
        showProgress()
        presenter.cameraSearch(elementArrayList,lineArrayList,filteredArrayList,resultText)
    }

    override fun hideCamera(){
        cameraView.mCameraShow.visibility = View.INVISIBLE
        cameraView.mCapturedImage.visibility = View.INVISIBLE
        cameraView.mFoodListRecyclerView.visibility = View.VISIBLE
        cameraView.mListItems.visibility = View.VISIBLE
    }


    override fun resetInformation(){
        storedFood.clear()
        validFoodItems.clear()
        cameraView.mListItems.visibility = View.GONE
        cameraView.mButtonFindText.visibility = View.INVISIBLE
        cameraView.mCameraShow.visibility = View.VISIBLE
        captureCheck = false
        clearImageBitmap()
    }

    private fun setImageBitmap() {
        cameraView.view_finder.visibility = View.INVISIBLE
        cameraView.mCapturedImage.setImageBitmap(viewFinder.bitmap)
        cameraView.mCapturedImage.visibility = View.VISIBLE
    }

    fun getSmallest(a: IntArray, total: Int): Int {
        var temp: Int
        for (i in 0 until total) {
            for (j in i + 1 until total) {
                if (a[i] > a[j]) {
                    temp = a[i]
                    a[i] = a[j]
                    a[j] = temp
                }
            }
        }
        return a[0]
    }

    override fun addImageToCamera(stringData: String) {
        val bitmap = readImageFromPath(cameraView.context,stringData)
        cameraView.view_finder.visibility = View.INVISIBLE
        cameraView.mCapturedImage.setImageBitmap(bitmap)
        cameraView.mCapturedImage.visibility = View.VISIBLE
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f
        try {
            // Correct preview output to account for display rotation
            val rotationDegrees = when (viewFinder.display.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> return
            }
            matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

            // Finally, apply transformations to our TextureView
            viewFinder.setTransform(matrix)
        }catch (e: Exception){
            e.getStackTraceString()
        }
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Snackbar.make(cameraView,"Permissions not granted by the user.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            presenter.doActivityResult(requestCode,resultCode,data,cameraView.context)
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(cameraView.context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun showShopDialog(){
        customDialog = CustomDialog(activity!!)
        customDialog.show()
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.mConfirm.setOnClickListener {
            customDialog.cancel()
            for (foodItem in filteredArrayList) {
                val foodModel = FoodMasterModel()
                for (item in filteredArrayList){
                    if(foodItem == item){
                        foodModel.food.itemsCounter++
                    }
                }
                foodModel.food.name = foodItem
                foodModel.food.shop = customDialog.mDialogSearch.text.toString()
                if (!storedFood.contains(foodModel)) {
                    storedFood.add(foodModel)
                }
            }
            presenter.findFoodItems()
            cameraView.mShoppedAt.text = customDialog.mDialogSearch.text
            saveShop = customDialog.mDialogSearch.text.toString()
        }
        customDialog.mRetakeShop.setOnClickListener {
            storedFood.clear()
            cameraView.mListItems.visibility = View.GONE
            cameraView.mCameraShow.visibility = View.VISIBLE
            captureCheck = false
            cameraView.view_finder.visibility = View.VISIBLE
            cameraView.mButtonFindText.visibility = View.INVISIBLE
            cameraView.mCapturedImage.setImageBitmap(null)
            customDialog.cancel()
        }
    }

    override fun showFoodItems(){
        cameraView.mFoodListRecyclerView.adapter = ReceiptListAdapter(storedFood,presenter, validFoodItems,cameraView)
        cameraView.mFoodListRecyclerView.adapter?.notifyDataSetChanged()
        if (saveShop.isNotEmpty()) {
            cameraView.mShoppedAt.text = saveShop
        }
        if (saveDate.isNotEmpty()){
            cameraView.mFoodDate.text = saveDate
        }
        hideProgress()
    }

    override fun showDateDialog(date: String) {
        dateDialog = DateDialog(activity!!)
        dateDialog.show()
        dateDialog.setCanceledOnTouchOutside(false)
        dateDialog.mDateCalender.setOnDateChangeListener(CalendarView.OnDateChangeListener(){
                view, year, month, dayOfMonth ->
            saveDate = "$dayOfMonth/$month/$year"
            dateDialog.cancel()
            presenter.findShop(elementArrayList, filteredArrayList)
        })
    }

    override fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun showProgress(){
        progressBar.visibility = View.VISIBLE
    }
}


private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimestamp = 0L

    /**
     * Helper extension function used to extract a byte array from an
     * image plane buffer
     */
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        val currentTimestamp = System.currentTimeMillis()
        // Calculate the average luma no more often than every second
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.SECONDS.toMillis(1)) {
            // Since format in ImageAnalysis is YUV, image.planes[0]
            // contains the Y (luminance) plane
            val buffer = image.planes[0].buffer
            // Extract image data from callback object
            val data = buffer.toByteArray()
            // Convert the data into an array of pixel values
            val pixels = data.map { it.toInt() and 0xFF }
            // Compute average luminance for the image
            val luma = pixels.average()
            // Update timestamp of last analyzed frame
            lastAnalyzedTimestamp = currentTimestamp
        }
    }
}
