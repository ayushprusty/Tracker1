package com.example.objecttracker
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    private val TAG = "MainActivity"
    private lateinit var mOpenCvCameraView: CameraBridgeViewBase

    private val CAMERA_PERMISSION_REQUEST_CODE = 1
    private val LOWER_YELLOW = Scalar(20.0, 100.0, 100.0)
    private val UPPER_YELLOW = Scalar(30.0, 255.0, 255.0)

    private val mLoaderCallback: LoaderCallbackInterface = object : LoaderCallbackInterface {
        override fun onManagerConnected(status: Int) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully")
                mOpenCvCameraView.enableView()
            } else {
                Log.e(TAG, "OpenCV loading failed with status: $status")
            }
        }

        override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface) {
            // Handle package installation if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)

        mOpenCvCameraView = findViewById(R.id.camera_view)
        mOpenCvCameraView.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView.setCvCameraViewListener(this)

        // Check for camera permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            mOpenCvCameraView.enableView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mOpenCvCameraView.setCameraPermissionGranted()
                mOpenCvCameraView.enableView()
            } else {
                Log.e(TAG, "Camera permission not granted")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initLocal()
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onPause() {
        super.onPause()
        mOpenCvCameraView.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        val frame = inputFrame.rgba()
        val hsv = Mat()
        val mask = Mat()
        val contours: List<MatOfPoint> = ArrayList()

        // Convert the frame to HSV
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV)

        // Create a mask for yellow color
        Core.inRange(hsv, LOWER_YELLOW, UPPER_YELLOW, mask)

        // Find contours in the mask
        Imgproc.findContours(mask, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // Iterate through contours and draw a rectangle around the largest one
        var maxContourArea = 0.0
        var maxContourIndex = -1
        for (i in contours.indices) {
            val contourArea = Imgproc.contourArea(contours[i])
            if (contourArea > maxContourArea) {
                maxContourArea = contourArea
                maxContourIndex = i
            }
        }

        if (maxContourIndex != -1) {
            val contour = contours[maxContourIndex]
            val rect= Imgproc.boundingRect(contour)
            Imgproc.rectangle(frame, rect.tl(), rect.br(), Scalar(0.0, 255.0, 0.0), 2)
        }

        // Clean up resources
        hsv.release()
        mask.release()
        //contours.clear()

        return frame
    }
}

////---GOOD----
//package com.example.objecttracker
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import android.view.SurfaceView
//import android.view.WindowManager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import org.opencv.android.CameraBridgeViewBase
//import org.opencv.android.InstallCallbackInterface
//import org.opencv.android.LoaderCallbackInterface
//import org.opencv.android.OpenCVLoader
//import org.opencv.core.Core
//import org.opencv.core.Mat
//import org.opencv.core.Scalar
//import org.opencv.imgproc.Imgproc
//
//class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {
//
//    private val TAG = "MainActivity"
//    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
//
//    private val CAMERA_PERMISSION_REQUEST_CODE = 1
//    private val LOWER_YELLOW = Scalar(20.0, 100.0, 100.0)
//    private val UPPER_YELLOW = Scalar(30.0, 255.0, 255.0)
//
//    private val mLoaderCallback: LoaderCallbackInterface = object : LoaderCallbackInterface {
//        override fun onManagerConnected(status: Int) {
//            if (status == LoaderCallbackInterface.SUCCESS) {
//                Log.i(TAG, "OpenCV loaded successfully")
//                mOpenCvCameraView.enableView()
//            } else {
//                Log.e(TAG, "OpenCV loading failed with status: $status")
//            }
//        }
//
//        override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface) {
//            // Handle package installation if needed
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        setContentView(R.layout.activity_main)
//
//        mOpenCvCameraView = findViewById(R.id.camera_view)
//        mOpenCvCameraView.visibility = SurfaceView.VISIBLE
//        mOpenCvCameraView.setCvCameraViewListener(this)
//
//        // Check for camera permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
//        } else {
//            mOpenCvCameraView.enableView()
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mOpenCvCameraView.setCameraPermissionGranted()
//                mOpenCvCameraView.enableView()
//            } else {
//                Log.e(TAG, "Camera permission not granted")
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
//            OpenCVLoader.initLocal()
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!")
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mOpenCvCameraView.disableView()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mOpenCvCameraView.disableView()
//    }
//
//    override fun onCameraViewStarted(width: Int, height: Int) {}
//
//    override fun onCameraViewStopped() {}
//
//    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
//        val frame = inputFrame.rgba()
//        val hsv = Mat()
//        val mask = Mat()
//
//        // Convert the frame to HSV
//        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV)
//
//        // Create a mask for yellow color
//        Core.inRange(hsv, LOWER_YELLOW, UPPER_YELLOW, mask)
//
//        // Apply the mask on the original frame
//        val result = Mat()
//        Core.bitwise_and(frame, frame, result, mask)
//
//        // Clean up resources
//        hsv.release()
//        mask.release()
//
//        return result
//    }
//}
