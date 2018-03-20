package nl.boydroid.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.vision.barcode.Barcode
import com.greysonparrelli.permiso.PermisoActivity
import kotlinx.android.synthetic.main.activity_main.*
import nl.boydroid.BarcodeCaptureView
import nl.boydroid.grantMe

class MainActivity : PermisoActivity(), BarcodeCaptureView.OnResultHandler {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        barcodeCaptureView.resultHandler = this
        barcodeCaptureView.barcodeTypes = Barcode.EAN_13 or Barcode.CODE_128 or Barcode.QR_CODE

        grantMe(android.Manifest.permission.CAMERA, permissionGranted = { granted ->
            if (granted) {
                barcodeCaptureView.start()
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        barcodeCaptureView.start()
    }

    override fun onCodeScanned(code: String) {
        Log.d("Scanned", "code == $code")
        startActivity(Intent(this, ResultActivity::class.java).putExtra("CODE", code))

    }

}
