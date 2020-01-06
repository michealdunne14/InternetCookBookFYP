package com.example.internetcookbook.fragmentview

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ReceiptListAdapter
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.Common
import com.example.internetcookbook.network.HttpDataHandler
import com.example.internetcookbook.network.InformationStore
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_camera.view.*
import kotlinx.android.synthetic.main.listitems.*
import kotlinx.android.synthetic.main.listitems.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), LifecycleOwner,AnkoLogger {

    private val REQUEST_CODE_PERMISSIONS = 10

    // This is an array of all the permission specified in the manifest.
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    lateinit var homeView: View
    var storedFood = ArrayList<FoodModel>()
    var captureCheck = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_camera, container, false)
        homeView = view
        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)

        view.mFoodListRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
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
            viewFinder.post { startCamera() }
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
            setTargetResolution(Size(640, 480))
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

        homeView.capture_button.setOnClickListener {
            if(!captureCheck) {
                captureCheck = true
                setImageBitmap()
            }else{
                captureCheck = false
                clearImageBitmap()
            }
        }

        homeView.mFindText.setOnClickListener {
            mChangeImageToText(viewFinder.bitmap)
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


    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            Toast.makeText(homeView.context,"No Text Found",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
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
