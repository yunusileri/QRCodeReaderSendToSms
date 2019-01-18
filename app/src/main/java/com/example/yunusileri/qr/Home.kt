package com.example.yunusileri.qr

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.SurfaceHolder
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.scan.*
import java.io.IOException



class Home : AppCompatActivity() ,View.OnClickListener{

    private lateinit var dialog: Dialog
    private lateinit var message:TextView
    private lateinit var barcodeDetector:BarcodeDetector
    private lateinit var cameraSource :CameraSource


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1001 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    }
                    try {
                        cameraSource.start(dialog.ScanQr.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }
    override fun onClick(v: View?) {
        qrDialog()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val scanBtn:Button = findViewById(R.id.btnScan)
        scanBtn.setOnClickListener(this)

        val btnSms:Button=findViewById(R.id.btnSms)

        btnSms.setOnClickListener {
            val intent222=Intent(this,Sms::class.java)
            intent222.putExtra("1",message.text.toString())
            startActivity(intent222)
        }

        message=findViewById(R.id.messageShow)
        message.movementMethod = ScrollingMovementMethod()
        qrDialog()
    }


    private fun qrDialog() {
       dialog = Dialog(this)
       dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
       dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.run {
            setContentView(R.layout.scan)
            setCancelable(true)
            show()
        }


        barcodeDetector = BarcodeDetector.Builder(this@Home).setBarcodeFormats(Barcode.QR_CODE).build()
        cameraSource = CameraSource.Builder(this@Home, barcodeDetector).setAutoFocusEnabled(true).build()
        dialog.ScanQr.holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
                cameraSource.release()
                barcodeDetector.release()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions(this@Home, arrayOf(Manifest.permission.CAMERA), 1001)


                     return
                } else {
                    try {
                        cameraSource.start(holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val qrText = detections?.detectedItems
                if (qrText?.size() != 0) {
                    message.text = qrText?.valueAt(0)?.displayValue
                    dialog.dismiss()

                }
            }
        })

    }

}


