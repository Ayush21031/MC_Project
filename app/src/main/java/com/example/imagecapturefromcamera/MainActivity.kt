//package com.example.imagecapturefromcamera
//
//import android.Manifest
//import android.app.Activity
//import android.content.ContentResolver
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.util.Base64
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.app.ActivityCompat.startActivityForResult
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import coil.compose.rememberImagePainter
//import com.example.imagecapturefromcamera.api.apiCall
//import com.example.imagecapturefromcamera.ui.theme.ImageCaptureFromCameraTheme
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.io.ByteArrayOutputStream
//import java.io.File
//import java.io.InputStream
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Objects
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//
//class MainActivity : ComponentActivity() {
//    private lateinit var capturedImageUri: Uri
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//
//            val PICK_IMAGE = 1
//            val REQUEST_IMAGE_CAPTURE = 2
//
//            val context = LocalContext.current
//            val file = context.createImageFile()
//            val uri = FileProvider.getUriForFile(
//                Objects.requireNonNull(context),
//                context.packageName + ".provider", file
//            )
//
//            var capturedImageUri by remember {
//                mutableStateOf<Uri>(Uri.EMPTY)
//            }
//
//
//            val cameraLauncher =
//                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()){
//                    capturedImageUri = uri
//                }
//
//
//            val permissionLauncher = rememberLauncherForActivityResult(
//                ActivityResultContracts.RequestPermission()
//            ){
//                if (it)
//                {
//                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
//                    cameraLauncher.launch(uri)
//                }
//                else
//                {
//                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(10.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Bottom
//            ) {
//
//                Button(onClick = {
//                    val permissionCheckResult =
//                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
//
//                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED)
//                    {
//                        cameraLauncher.launch(uri)
//                    }
//                    else
//                    {
//                        permissionLauncher.launch(Manifest.permission.CAMERA)
//                    }
//                }) {
//                    Text(text = "Capture Image")
//                }
//                Button(onClick = {
//
//                }) {
//                    Text(text = "Select Image")
//
//                }
//                Button(onClick = {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val bitmap: Bitmap? = uriToBitmap(contentResolver, capturedImageUri)
//                        if (bitmap != null)
//                        {
//                            val base64 = bitmapToBase64(bitmap)
//                            Log.d("API", "Base64: $base64")
//                            val retrofit = Retrofit.Builder()
//                                .baseUrl("http://34.135.235.87:8000/")
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .build()
//                            val api = retrofit.create(apiCall::class.java)
//                            val call = api.scanImage(base64)
//                            call.execute()
//                            Log.d("API", "API called")
//                            Log.d("API", "Call: $call")
//                        }
//                        else{
//                            Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }){
//                    Text(text = "Scan QR Code")
//                }
//            }
//            if (capturedImageUri.path?.isNotEmpty() == true)
//            {
//                Image(
//                    modifier = Modifier
//                        .padding(16.dp, 8.dp),
//                    painter = rememberImagePainter(capturedImageUri),
//                    contentDescription = null
//                )
//            }
//            else
//            {
//                Image(
//                    modifier = Modifier
//                        .padding(16.dp, 8.dp),
//                    painter = painterResource(id = R.drawable.ic_image),
//                    contentDescription = null
//                )
//            }
//        }
//    }
//
//    private fun callAPI(imageBitmap: Uri) {
//        val bitmap = uriToBitmap(contentResolver, imageBitmap)
//        val base64 = bitmapToBase64(bitmap!!)
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://34.135.235.87:8000/getLSTM/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api = retrofit.create(apiCall::class.java)
//        val call = api.scanImage(base64)
//        call.execute()
//        Log.d("API", "API called")
//        Log.d("API", "Call: $call")
//    }
//    fun bitmapToBase64(bitmap: Bitmap): String {
//        val outputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//        val byteArray = outputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }
//    private fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
//        var bitmap: Bitmap? = null
//        try {
//            val inputStream: InputStream? = contentResolver.openInputStream(uri)
//            if (inputStream != null) {
//                bitmap = BitmapFactory.decodeStream(inputStream)
//                inputStream.close()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return bitmap
//    }
//}
//
//
//fun Context.createImageFile(): File {
//    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
//    val imageFileName = "JPEG_" + timeStamp + "_"
//    val image = File.createTempFile(
//        imageFileName,
//        ".jpg",
//        externalCacheDir
//    )
//
//    return image
//}


package com.example.imagecapturefromcamera

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.example.imagecapturefromcamera.api.ApiCall
//import com.example.imagecapturefromcamera.api.ImageResponse
import com.example.imagecapturefromcamera.ui.theme.ImageCaptureFromCameraTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var capturedImageUri: Uri
    private lateinit var api: ApiCall
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val PICK_IMAGE = 1
            val REQUEST_IMAGE_CAPTURE = 2

            val context = LocalContext.current
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )

            var capturedImageUri by remember {
                mutableStateOf<Uri>(Uri.EMPTY)
            }


            val cameraLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()){
                    capturedImageUri = uri
                }


            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ){
                if (it)
                {
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                    cameraLauncher.launch(uri)
                }
                else
                {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                Button(onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED)
                    {
                        cameraLauncher.launch(uri)
                    }
                    else
                    {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text(text = "Capture Image")
                }
                Button(onClick = {

                }) {
                    Text(text = "Select Image")

                }
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val bitmap: Bitmap? = uriToBitmap(contentResolver, capturedImageUri)
                        if (bitmap != null)
                        {
                            val base64Chunks = bitmapToBase64Chunks(bitmap)
                            Log.d("API", "Base64: $base64Chunks")
                            val retrofit = Retrofit.Builder()
                                .baseUrl("http://34.135.235.87:8000/extract_link/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            api = retrofit.create(ApiCall::class.java)
//                            val call = api.scanImage(image_chunk = base64Chunks)
//                            call.enqueue(object : Callback<ResponseBody> {
//                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                                    Log.d("API", "API called")
//                                    Log.d("API", "Response: ${response.body()}")
//                                }
//
//                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                                    Log.e("API", "API call failed", t)
//                                }
//                            })
                            val linkdat = linkdata(base64Chunks)
                            api.scanImage(linkdat).enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    Log.d("API", "API called")
                                    Log.d("API", "Response: ${response.body()}")
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Log.e("API", "API call failed", t)
                                }
                            })

                        }
                        else{
                            Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }){
                    Text(text = "Scan QR Code")
                }
            }
            if (capturedImageUri.path?.isNotEmpty() == true)
            {
                Image(
                    modifier = Modifier
                        .padding(16.dp, 8.dp),
                    painter = rememberImagePainter(capturedImageUri),
                    contentDescription = null
                )
            }
            else
            {
                Image(
                    modifier = Modifier
                        .padding(16.dp, 8.dp),
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = null
                )
            }
        }
    }

    private fun bitmapToBase64Chunks(bitmap: Bitmap): List<String> {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        val chunkSize = 1000 // Specify your chunk size here
        val base64Chunks = mutableListOf<String>()
        for (i in 0 until base64.length step chunkSize) {
            val end = if (i + chunkSize < base64.length) i + chunkSize else base64.length
            base64Chunks.add(base64.substring(i, end))
        }
        return base64Chunks
    }

    private fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )

    return image
}
