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

    var barcodeTypes = Barcode.ALL_FORMATS

    private val frontCamera: Boolean
    private var startOnSurfaceCreated = false
    private var surfaceViewCreated = false

    init {
        holder.addCallback(this)
        val a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BarcodeCaptureView, 0, 0)
        frontCamera = a.getBoolean(R.styleable.BarcodeCaptureView_front_camera, false)
        a.recycle()
    }

    private val barcodeDetector by lazy {
        val barcodeDetector = BarcodeDetector
                .Builder(context)
                .setBarcodeFormats(barcodeTypes)
                .build()

        barcodeDetector.setProcessor(this)
        barcodeDetector
    }

    private val cameraSource by lazy {
        CameraSource.Builder(context, barcodeDetector)
                .setFacing(if (frontCamera) CameraSource.CAMERA_FACING_FRONT else CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true).build()
    }

    var resultHandler: OnResultHandler? = null

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        surfaceViewCreated = false
        cameraSource.stop()
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
        if (!surfaceViewCreated) {
            startOnSurfaceCreated = true
            return
        }

        cameraSource.start(holder)
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