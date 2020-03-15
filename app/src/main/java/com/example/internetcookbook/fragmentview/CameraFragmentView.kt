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
import com.example.internetcookbook.fragmentpresenter.CameraFragmentPresenter
import com.example.internetcookbook.helper.capitalize
import com.example.internetcookbook.helper.readImageFromPath
import com.example.internetcookbook.models.FoodMasterModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_custom.*
import kotlinx.android.synthetic.main.camera_show.view.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import kotlinx.android.synthetic.main.listitems.view.*
import org.jetbrains.anko.*
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
    var foodCreateCheck = false


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


        if (presenter.doGetReturnBack()){
            cameraView.mListItems.visibility = View.VISIBLE
            cameraView.mListItems.bringToFront()
            cameraView.view_finder.visibility = View.INVISIBLE
            cameraView.mCameraShow.visibility = View.GONE
            presenter.doSetReturnBack()
        }

        if (arguments != null) {
            val images = CameraFragmentViewArgs.fromBundle(arguments!!).foodcreate
            if (images == "food_create") {
                foodCreateCheck = true
                cameraView.mButtonFindText.visibility = View.INVISIBLE
                cameraView.mScanBarcodeButton.visibility = View.INVISIBLE
//                homeView.mScanBarcodeButton.setImageResource(R.drawable.cor)
            }
        }

        view.mFoodListRecyclerView.layoutManager = layoutManager
        view.mRetakePicture.setOnClickListener {
            resetInformation()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (presenter.findNewData().food.name.isNotEmpty()){
            validFoodItems.add(presenter.findNewData())
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
                    cameraView.mScanBarcodeButton.visibility = View.VISIBLE
                }
            }else{
                captureCheck = false
                clearImageBitmap()
                if (!foodCreateCheck) {
                    cameraView.mButtonFindText.visibility = View.INVISIBLE
                }
                if (foodCreateCheck){
                    cameraView.mScanBarcodeButton.visibility = View.INVISIBLE
                }
            }
        }

        cameraView.mAddCupboard.setOnClickListener {
            presenter.doAddCupboard(validFoodItems)
        }

        cameraView.mButtonFindText.setOnClickListener {
            cameraView.mCameraShow.visibility = View.INVISIBLE
            val bitmap = (cameraView.mCapturedImage.drawable as BitmapDrawable).bitmap
            mChangeImageToText(bitmap)
        }

        cameraView.mScanBarcodeButton.setOnClickListener {
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

    private fun mScanBarCode(bitmap: Bitmap?) {
        if(bitmap != null) {
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().visionBarcodeDetector
            val result = detector.detectInImage(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val bounds = barcode.boundingBox
                        val corners = barcode.cornerPoints

                        val rawValue = barcode.rawValue

                        val valueType = barcode.valueType
                        // See API reference for complete list of supported types
                        when (valueType) {
                            FirebaseVisionBarcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi!!.ssid
                                val password = barcode.wifi!!.password
                                val type = barcode.wifi!!.encryptionType
                            }
                            FirebaseVisionBarcode.TYPE_URL -> {
                                val title = barcode.url!!.title
                                val url = barcode.url!!.url
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    Snackbar.make(cameraView,"No Barcode Found Found", Snackbar.LENGTH_SHORT).show()
                }
        }else{
            Snackbar.make(cameraView,"No Barcode Found Found", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun clearImageBitmap() {
        cameraView.view_finder.visibility = View.VISIBLE
        cameraView.mCapturedImage.setImageBitmap(null)
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

//    private fun setUpTapToFocus() {
//        homeView.view_finder.setOnTouchListener { _, event ->
//            if (event.action != MotionEvent.ACTION_UP) {
//                return@setOnTouchListener false
//            }
//
//            val factory = TextureViewMeteringPointFactory(textureView)
//            val point = factory.createPoint(event.x, event.y)
//            val action = FocusMeteringAction.Builder.from(point).build()
//            cameraControl.startFocusAndMetering(action)
//            return@setOnTouchListener true
//        }
//    }


    private fun processResultText(resultText: FirebaseVisionText) {
//        if (resultText.textBlocks.size == 0) {
//            Snackbar.make(homeView,"No Text Found", Snackbar.LENGTH_SHORT).show()
//            return
//        }else{
//            homeView.mCameraShow.visibility = View.GONE
//        }
        info { "Select Country Started" }
        val elementArrayList = ArrayList<String>()
        val lineArrayList = ArrayList<String>()
        val filteredArrayList = ArrayList<String>()
        doAsync {
            for (block in resultText.textBlocks) {
                for (line in block.lines) {
                    lineArrayList.add(line.text.trim())
                    for (element in line.elements) {
                        elementArrayList.add(capitalize(element.text.trim()))
                    }
                }
            }
            onComplete {
                val foundDate = presenter.findDate(elementArrayList)
                if (foundDate != null){
                    cameraView.mFoodDate.text = foundDate
                }else{
//                    Dialog for purchase Date
                }
                for (line in lineArrayList){
                    val wordArrayList = ArrayList<String>()
                    val wordList = line.split(" ")
                    val removingWords = ArrayList<String>()
                    wordArrayList.addAll(wordList)
                    wordArrayList.forEachIndexed { index, word ->
                        val chars: CharArray = word.toCharArray()
                        for (c in chars) {
                            if (Character.isDigit(c)) {
                                removingWords.add(word)
                                break
                            }
                        }
                    }
                    for (remove in removingWords){
                        wordArrayList.remove(remove)
                    }
                    var foodItem = String()
                    wordArrayList.forEachIndexed { index, word ->
                        val capWord = capitalize(word)
                        if(index == 0){
                            foodItem += capWord
                        }else {
                            foodItem += " $capWord"
                        }
                    }

                    filteredArrayList.add(foodItem)
                }
                doAsync {
                    val foundShop = presenter.findShop(elementArrayList)
                    onComplete {
                        var foodModel = FoodMasterModel()
                        if(foundShop != null) {
                            for (foodItem in filteredArrayList) {
                                foodModel = FoodMasterModel()
                                foodModel.food.name = foodItem
                                foodModel.food.shop = foundShop.shop
                                if (!storedFood.contains(foodModel)) {
                                    storedFood.add(foodModel)
                                }
                            }
                            cameraView.mShoppedAt.text = foundShop.shop
                            findFoodItems()
                        }else{
                            customDialog = CustomDialog(activity!!)
                            customDialog.show()
                            customDialog.setCanceledOnTouchOutside(false)
                            customDialog.mConfirm.setOnClickListener {
                                customDialog.cancel()
                                for (foodItem in filteredArrayList) {
                                    foodModel = FoodMasterModel()
                                    foodModel.food.name = foodItem
                                    foodModel.food.shop = customDialog.mDialogSearch.text.toString()
                                    if (!storedFood.contains(foodModel)) {
                                        storedFood.add(foodModel)
                                    }
                                }
                                findFoodItems()
                                cameraView.mShoppedAt.text = customDialog.mDialogSearch.text
                            }
                            customDialog.mRetake.setOnClickListener {
                                storedFood.clear()
                                cameraView.mListItems.visibility = View.GONE
                                cameraView.mCameraShow.visibility = View.VISIBLE
                                captureCheck = false
                                cameraView.view_finder.visibility = View.VISIBLE
                                cameraView.mCapturedImage.setImageBitmap(null)
                                customDialog.cancel()
                            }
                        }
                    }
                }
            }
        }
        cameraView.mCapturedImage.visibility = View.INVISIBLE
        cameraView.mFoodListRecyclerView.visibility = View.VISIBLE
        cameraView.mListItems.visibility = View.VISIBLE
    }


    override fun resetInformation(){
        storedFood.clear()
        cameraView.mListItems.visibility = View.GONE
        cameraView.mButtonFindText.visibility = View.INVISIBLE
        cameraView.mCameraShow.visibility = View.VISIBLE
        captureCheck = false
        clearImageBitmap()
    }

    private fun findFoodItems(){
        doAsync {
            presenter.searchItemsInitial(storedFood)
            onComplete {
                if(presenter.itemsInDatabase().isNotEmpty()) {
                    for (foundFoodInDatabase in presenter.itemsInDatabase()) {
                        val search: FoodMasterModel? = storedFood.find { p -> p.food.name == foundFoodInDatabase.food.name }
                        if (search != null) {
                            if (search.food.name.isNotEmpty()) {
                                validFoodItems.add(search)
                                search.food.foundItem = true
                            }
                        }
                    }
                }
                cameraView.mFoodListRecyclerView.adapter = ReceiptListAdapter(storedFood,presenter,validFoodItems,cameraView)
                cameraView.mFoodListRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setImageBitmap() {
        cameraView.view_finder.visibility = View.INVISIBLE
        cameraView.mCapturedImage.setImageBitmap(viewFinder.bitmap)
        cameraView.mCapturedImage.visibility = View.VISIBLE
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

//    private fun runTextRecognition() {
//        val image = FirebaseVisionImage.fromBitmap(mSelectedImage)
//        val recognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
//        mTextButton.setEnabled(false)
//        recognizer.processImage(image)
//            .addOnSuccessListener { texts ->
////                mTextButton.setEnabled(true)
//                processTextRecognitionResult(texts!!)
//            }
//            .addOnFailureListener { e ->
//                // Task failed with an exception
////                mTextButton.setEnabled(true)
//                e.printStackTrace()
//            }
//    }
//
//    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
//        val blocks = texts.textBlocks
//        if (blocks.size == 0) {
////            showToast("No text found")
//            return
//        }
//        mGraphicOverlay.clear()
//        for (i in blocks.indices) {
//            val lines = blocks[i].lines
//            for (j in lines.indices) {
//                val elements =
//                    lines[j].elements
//                for (k in elements.indices) {
//                    val textGraphic: Graphic = TextGraphic(mGraphicOverlay, elements[k])
//                    mGraphicOverlay.add(textGraphic)
//                }
//            }
//        }
//    }
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
