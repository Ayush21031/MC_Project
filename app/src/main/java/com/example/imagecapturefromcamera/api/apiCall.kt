//package com.example.imagecapturefromcamera.api
//import okhttp3.ResponseBody
//import retrofit2.http.GET
//import retrofit2.http.Query
//import retrofit2.Call
//import retrofit2.http.POST
//
//interface apiCall {
//    @POST("/extract_link/")
//    fun scanImage(
//        @Query("image_b64") image_b64: String
//    ): Call<ImageResponse>
//}
//
//data class ImageResponse(
//    val link: String
//)
//
//data class ImageRequest(
//    val image_b64: String
//)

package com.example.imagecapturefromcamera.api

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

//data class ImageResponse(
//    val link: String
//)
