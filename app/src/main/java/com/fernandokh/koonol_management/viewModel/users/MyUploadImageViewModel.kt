package com.fernandokh.koonol_management.viewModel.users

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UploadApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MyUploadImageViewModel: ViewModel() {
    private val apiService = RetrofitInstance.create(UploadApiService::class.java)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun uploadImage(uri: Uri, context: Context, onResult: (String?) -> Unit) {
        _isLoading.value = true
        val file = getFileFromUri(uri, context)
        if (file == null){
            _isLoading.value = false
            onResult(null)
            return
        }
        val image =  MultipartBody.Part
            .createFormData(
                "image",
                file.name,
                file.asRequestBody()
            )

        viewModelScope.launch {
            try {
                val response = apiService.uploadImage(image = image, "true")
                Log.i("dev-debug", "Imagen subida en la url: ${response.data?.url}")
                onResult(response.data?.url)
            } catch (e: HttpException) {
                val errorResponse = e.response()
                val errorBody = errorResponse?.errorBody()?.string()

                //val gson = Gson()
                //val error = gson.fromJson(errorBody, ApiResponseError::class.java)

                Log.e("dev-debug", "Error Body: $errorBody")
                //showToast(error.message)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ah ocurrido un error")
                onResult(null)
                //showToast("Ocurrio un error al borrar")
            } finally {
                file.delete()
                _isLoading.value = false
            }
        }
    }

    private fun getFileFromUri(uri: Uri, context: Context): File? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            val file = File(context.cacheDir, name)

            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                return file
            } catch (e: Exception) {
                Log.e("dev-debug", e.message ?: "Error occurred")
            }
        }
        return null
    }
}