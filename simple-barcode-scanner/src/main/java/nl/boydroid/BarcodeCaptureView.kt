package nl.boydroid

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.RequiresPermission
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import nl.boydroid.barcodevisioner.R

class BarcodeCaptureView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), Detector.Processor<Barcode>, SurfaceHolder.Callback {

    interface OnResultHandler {
        fun onCodeScanned(code: String)
    }

    // default the most used ones
    var barcodeTypes = Barcode.CODE_128 or Barcode.EAN_13 or Barcode.QR_CODE
    var resultHandler: OnResultHandler? = null

    private val frontCamera: Boolean
    private var startOnSurfaceCreated = false
    private var surfaceViewCreated = false
    private var doPropagate = true

    private var cameraSource: CameraSource? = null

    init {
        holder.addCallback(this)
        val a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BarcodeCaptureView, 0, 0)
        frontCamera = a.getBoolean(R.styleable.BarcodeCaptureView_front_camera, false)
        a.recycle()
    }

    private fun createBarcodeDetector(): BarcodeDetector {
        val barcodeDetector = BarcodeDetector
                .Builder(context)
                .setBarcodeFormats(barcodeTypes)
                .build()

        barcodeDetector.setProcessor(this)
        return barcodeDetector
    }

    private fun createCameraSource(): CameraSource {
        return CameraSource.Builder(context, createBarcodeDetector())
                .setFacing(if (frontCamera) CameraSource.CAMERA_FACING_FRONT else CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true).build()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        surfaceViewCreated = false
        cameraSource?.stop()
    }

    @SuppressLint("MissingPermission")
    override fun surfaceCreated(p0: SurfaceHolder?) {
        surfaceViewCreated = true
        if (startOnSurfaceCreated) {
            start()
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.CAMERA)
    fun start() {
        doPropagate = true

        if (!surfaceViewCreated) {
            startOnSurfaceCreated = true
            return
        }

        cameraSource?.stop()
        cameraSource = createCameraSource()
        cameraSource?.start(holder)
    }

    override fun release() {

    }

    override fun receiveDetections(detections: Detector.Detections<Barcode>) {
        if (!doPropagate) {
            return
        }

        if (detections.detectedItems.size() > 0) {
            val code = detections.detectedItems.valueAt(0).displayValue
            resultHandler?.onCodeScanned(code)
            doPropagate = false
        }
    }
}