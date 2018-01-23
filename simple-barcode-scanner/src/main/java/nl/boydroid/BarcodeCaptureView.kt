package nl.boydroid

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class BarcodeCaptureView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), Detector.Processor<Barcode>, SurfaceHolder.Callback {

    interface OnResultHandler {
        fun onCodeScanned(code: String);
    }

    var resultHandler: OnResultHandler? = null

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        cameraSource.stop()
    }

    @SuppressLint("MissingPermission")
    override fun surfaceCreated(p0: SurfaceHolder?) {
        Permission.request(android.Manifest.permission.CAMERA,
                permissionGranted = { granted ->
                    if (granted) {
                        cameraSource.start(holder)
                    }
                })
    }

    init {
        holder.addCallback(this)
    }

    private val barcodeDetector by lazy {
        val barcodeDetector = BarcodeDetector.Builder(context).build()

        barcodeDetector.setProcessor(this)
        barcodeDetector
    }

    private val cameraSource by lazy {
        CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true).build()
    }

    override fun release() {

    }

    override fun receiveDetections(detections: Detector.Detections<Barcode>) {
        if (detections.detectedItems.size() > 0) {
            val code = detections.detectedItems.valueAt(0).displayValue
            resultHandler?.onCodeScanned(code)
        }
    }


}