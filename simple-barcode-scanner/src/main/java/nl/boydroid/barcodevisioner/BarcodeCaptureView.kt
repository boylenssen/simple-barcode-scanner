package nl.boydroid.barcodevisioner

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import nl.boydroid.Permission

class BarcodeCaptureView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), Detector.Processor<Barcode>, SurfaceHolder.Callback {
    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) { }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        cameraSource.stop()
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        if (Permission.isGranted(context, android.Manifest.permission.CAMERA)) {
            cameraSource.start(holder)
        }
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
        if (detections.detectedItems.size() > 0)
            Log.d("BOY", "DETECTED: " + detections.detectedItems.valueAt(0).displayValue)
    }


}