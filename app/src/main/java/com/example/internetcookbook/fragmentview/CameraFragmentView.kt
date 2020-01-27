package com.example.internetcookbook.fragmentview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.*
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ReceiptListAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentpresenter.CameraFragmentPresenter
import com.example.internetcookbook.helper.readImageFromPath
import com.example.internetcookbook.models.FoodModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.fragment_camera.view.*
import kotlinx.android.synthetic.main.listitems.*
import kotlinx.android.synthetic.main.listitems.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.uiThread
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragmentView : BaseView(), LifecycleOwner,AnkoLogger {

    private val REQUEST_CODE_PERMISSIONS = 10

    // This is an array of all the permission specified in the manifest.
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    lateinit var homeView: View
    var storedFood = ArrayList<FoodModel>()
    var captureCheck = false
    var torch = false

    lateinit var presenter: CameraFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_camera, container, false)
        homeView = view
        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)
        presenter = initPresenter(CameraFragmentPresenter(this)) as CameraFragmentPresenter


        view.mFoodListRecyclerView.layoutManager = layoutManager
        view.mFloatingButton.setOnClickListener {
            storedFood.clear()
            view.mListItems.visibility = View.GONE
            captureCheck = false
            clearImageBitmap()
        }

        return view
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
//        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
//        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)

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

//        homeView.mPostButton.setOnClickListener {
//            val action = PagerFragmentDirections.actionPagerFragmentToPostFragment2()
//            val extras = FragmentNavigatorExtras(
//                viewFinder to "image"
//            )
//            homeView.findNavController().navigate(action,extras)
//        }

        homeView.mCameraFlashButton.setOnClickListener {
            torch = !torch
        }

        homeView.mCameraCaptureButton.setOnClickListener {
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
                        homeView.mButtonFindText.visibility = View.VISIBLE
                    }
                }
            }else{
                captureCheck = false
                clearImageBitmap()
                homeView.mButtonFindText.visibility = View.INVISIBLE
            }
        }

        homeView.mButtonFindText.setOnClickListener {
            mChangeImageToText(viewFinder.bitmap)
        }

        homeView.mScanBarcodeButton.setOnClickListener {
//            mScanBarCode(viewFinder.bitmap)
            presenter.doSelectImage(this)
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
                    Snackbar.make(homeView,"No Barcode Found Found", Snackbar.LENGTH_SHORT).show()
                }
        }else{
            Snackbar.make(homeView,"No Barcode Found Found", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun clearImageBitmap() {
        homeView.view_finder.visibility = View.VISIBLE
        homeView.mCapturedImage.setImageBitmap(null)
    }



    fun mChangeImageToText(bitmap: Bitmap) {
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
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
        if (resultText.textBlocks.size == 0) {
            Snackbar.make(homeView,"No Text Found", Snackbar.LENGTH_SHORT).show()
            return
        }
        doAsync {
            for (block in resultText.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        val foodModel = FoodModel()
                        foodModel.foodName = element.text
                        storedFood.add(foodModel)

                    }
                }
            }
            uiThread {
                mFoodListRecyclerView.adapter = ReceiptListAdapter(storedFood)
                mFoodListRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

        homeView.mCapturedImage.visibility = View.INVISIBLE
        homeView.mFoodListRecyclerView.visibility = View.VISIBLE
        homeView.mListItems.visibility = View.VISIBLE
    }

    private fun setImageBitmap() {
        homeView.view_finder.visibility = View.INVISIBLE
        homeView.mCapturedImage.setImageBitmap(viewFinder.bitmap)
        homeView.mCapturedImage.visibility = View.VISIBLE
    }

    override fun addImageToCamera(stringData: String) {
        val bitmap = readImageFromPath(homeView.context,stringData)
        homeView.view_finder.visibility = View.INVISIBLE
        homeView.mCapturedImage.setImageBitmap(bitmap)
        homeView.mCapturedImage.visibility = View.VISIBLE
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
                Snackbar.make(homeView,"Permissions not granted by the user.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            presenter.doActivityResult(requestCode,resultCode,data,homeView.context)
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(homeView.context, it) == PackageManager.PERMISSION_GRANTED
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
