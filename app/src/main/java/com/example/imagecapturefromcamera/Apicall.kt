package com.example.imagecapturefromcamera

import com.example.imagecapturefromcamera.linkdata
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiCall {
    //    @POST("/extract_link/")
    @POST("http://34.135.235.87:8000/extract_link/")
//    suspend fun scanImage(
////        @Body image_chunk: List<String>
//
//    ): Call<ResponseBody>
    fun scanImage(
        @Body listapi:linkdata
    ): Call<ResponseBody>
}
