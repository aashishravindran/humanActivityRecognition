package com.example.aashishravindran.cps_project

import android.util.Log
import android.util.Log.d
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.io.File
import java.io.InputStream

class NetCli {

    fun get(url: String, file_name: String, image_File: String): InputStream {
        d("anubav_testing","api_found-1-------")
        val MEDIA_TYPE_JPG = MediaType.parse("jpg/*")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("input_video", "${file_name}.jpg", RequestBody.create(MEDIA_TYPE_JPG, File(image_File)))
            .build()
        d("anubav_testing","api_found-2-------")
        val request = Request.Builder().url(url).post(requestBody).build()
        d("anubav_testing","api_found-3-------")
        val response = OkHttpClient().newCall(request).execute()
        d("anubav_testing","api_found-4-------")
        val body = response.body()
        d("anubav_testing","api_found-5-------")
        d("reponse-text",response.toString())
        // body.toString() returns a string representing the object and not the body itself, probably
        // kotlins fault when using third party libraries. Use byteStream() and convert it to a String
        return body!!.byteStream()
    }
}