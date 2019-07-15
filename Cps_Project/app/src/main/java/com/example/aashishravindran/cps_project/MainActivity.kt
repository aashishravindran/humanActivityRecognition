
package com.example.aashishravindran.cps_project

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider

import android.support.v7.app.AppCompatActivity
import android.util.Log

import android.util.Log.d
import android.util.Log.i


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap.CompressFormat
import android.R.attr.bitmap
import android.content.pm.PackageManager
import java.net.HttpURLConnection
import java.net.URL


import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telecom.Call
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.*
import java.lang.Exception
import java.net.SocketTimeoutException
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    val REQUEST_IMAGE_CAPTURE = 1
    var mCurrentPhotoPath: String=""
    val REQUEST_TAKE_PHOTO = 1
    val SERVER_POST_URL="localhost:5000/predict"
    var uri:Uri?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //onSensorChanged(event=this.mSensor.SensorEvent)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100)
        }
        else {

        }
        CameraButton.setOnClickListener {
            dispatchTakePictureIntent()

           // d("Button Cicked ", "Clicked")
        }



    }

    open class GetJsonWithOkHttpClient(microwave_view: View, uri: String,ResPose: TextView) : AsyncTask<Unit, Unit, String>() {

        val mInnerTextView = uri
        val ResPose=ResPose
        val microwave_view=microwave_view

        override fun doInBackground(vararg params: Unit?): String? {

            val networkClient = NetCli()
            val stream = BufferedInputStream(networkClient.get(urlN, "video", mInnerTextView))
//            d("response-post",stream)
            return readStream(stream)


        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            d("response-post",result)
            ResPose.setText(result)
            microwave_view.visibility = View.VISIBLE



        }

        fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }




    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    companion object {

        @JvmField
        // val url = "https://raw.githubusercontent.com/irontec/android-kotlin-samples/master/common-data/bilbao.json"
        val urlN = "http://172.24.21.147:5000/recognizer"
        //val urlN = "https://server2.planetgroupbd.com/ords/pepsi/v1/outlet/118786"
    }





    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
          //  imageView.setImageBitmap(data.extras.get("data") as Bitmap)

             uri = saveImageToExternalStorage(imageBitmap)

            // Display the external storage saved image in image view
//            imageView.setImageURI(uri)

                GetJsonWithOkHttpClient(microwave, uri.toString(), ResPose).execute()


        }
    }




    private fun saveImageToExternalStorage(bitmap:Bitmap):Uri{


        // Get the external storage directory path
        val path = Environment.getExternalStorageDirectory().toString()

        // Create a file to save the image
        val file = File(path, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the output stream
            stream.flush()

            // Close the output stream
            stream.close()
            toast("Image saved successful.")
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
            toast("Error to save image.")
        }

        // Return the saved image path to uri
        return Uri.parse(file.absolutePath)
    }

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}








//     override  fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        //d("sd","sd")
//    }
//         override fun onSensorChanged(event:SensorEvent) {
//
//         d("Accelormeterdata","X "+event.values[0]+" Y "+event.values[1]+" Z "+event.values[2]+"timestamp"+event.timestamp)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mSensorManager!!.registerListener(this,mSensor,
//            SensorManager.SENSOR_DELAY_GAME)
//    }





