package org.d3if3082.checknote.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if3082.checknote.R
import org.d3if3082.checknote.model.Notes
import org.d3if3082.checknote.network.ApiStatus
import org.d3if3082.checknote.network.NotesApi
import org.d3if3082.checknote.network.RetrofitStorage
import org.json.JSONObject
import java.io.File

class MainViewModel : ViewModel() {
    private val _data = MutableStateFlow<Map<String, Notes>>(emptyMap())
    val data: StateFlow<Map<String, Notes>> = _data

    //    private val _imageUrl = MutableLiveData<String>()
//    val imageUrl: LiveData<String> get() = _imageUrl
    private val _urlsGambar = MutableLiveData<Map<String, String>>()
    val urlsGambar: LiveData<Map<String, String>> = _urlsGambar

    private val _status = MutableStateFlow(ApiStatus.LOADING)
    val status: StateFlow<ApiStatus> = _status

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage

    suspend fun uploadImageToFirebaseStorage(file: File): String {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val uploadUrl =
            "images%2F${file.name}"

        val response = RetrofitStorage.instance.uploadImage(uploadUrl, body)

        if (response.isSuccessful) {
            val responseBody = response.body()?.string()
            return responseBody ?: ""
        } else {
            throw Exception("Upload failed: ${response.code()}")
        }
    }

    fun uploadImage(file: File) {
        viewModelScope.launch {
            try {
                val result = uploadImageToFirebaseStorage(file)
                Log.d("MainViewModel", "Upload Result: ${result}")
                retrieveData()
            } catch (e: Exception) {
                Log.d("MainViewModel", "Upload Error: ${e.message.toString()}")
            }
        }
    }

    fun loadImageUrl(fileRef: String?) {
        if (fileRef != null) {
            viewModelScope.launch {
                try {
                    val metadata = NotesApi.serviceGsonStorage.getFileMetadata(fileRef)
                    val downloadTokens = metadata.downloadTokens
                    Log.d("Metadata", "Download Tokens: $downloadTokens")
                    val imageUrl =
                        "https://firebasestorage.googleapis.com/v0/b/checknote-5a3f0.appspot.com/o/images%2F$fileRef?alt=media&token=$downloadTokens"
                    Log.d("ImageURL", "Generated Image URL: $imageUrl") // Tambahkan log untuk URL image
                    val updatedUrls = _urlsGambar.value.orEmpty().toMutableMap()
                    updatedUrls[fileRef] = imageUrl
                    _urlsGambar.value = updatedUrls
                } catch (e: Exception) {
                    Log.e("Metadata", "Error fetching metadata: ${e.message}")
                }
            }
        }
    }


//    fun loadImageUrl(fileRef: String?) {
//        if (fileRef != null) {
//            viewModelScope.launch {
//                try {
//                    val metadata = NotesApi.serviceGsonStorage.getFileMetadata(fileRef)
//                    val downloadTokens = metadata.downloadTokens
//                    Log.d("Metadata", "Download Tokens: $downloadTokens")
//                    val imageUrl =
//                        "https://firebasestorage.googleapis.com/v0/b/checknote-5a3f0.appspot.com/o/images%2F$fileRef?alt=media&token=$downloadTokens"
////                    _imageUrl.value = imageUrl
//                    val updatedUrls = _urlsGambar.value.orEmpty().toMutableMap()
//                    updatedUrls[fileRef] = imageUrl
//                    _urlsGambar.value = updatedUrls
//                } catch (e: Exception) {
//                    Log.e("Metadata", "Error fetching metadata: ${e.message}")
//                }
//            }
//
//        }
//    }

    fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            _status.value = ApiStatus.LOADING
            try {
                val response = NotesApi.serviceGson.getNotes()
                Log.d("MainViewModel", "Response: ${response}")
                _data.value = response  // Assign entire response to _data
                _status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Response: ${e.message }")
                if (e.message == "Response from org.d3if3082.checknote.network.NotesApiService.getNotes was null but response body type was declared as non-null") {
                    Log.d("MainViewModel", "Data Null")
                    _status.value = ApiStatus.SUCCESS
                } else {
                    Log.d("MainViewModel", "Failure: ${e.message}")
                    _status.value = ApiStatus.FAILED
                    _errorMessage.value = "Error: ${e.message}"
                }
            }
        }
    }

    fun saveData(userId: String, judul: String, desc:String, kategori:String, uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = getFileFromUri(uri, context)
                uploadImage(file)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val jsonObject = JSONObject()
                jsonObject.put("userId", userId)
                jsonObject.put("judul", judul)
                jsonObject.put("desc", desc)
                jsonObject.put("kategori", kategori)
                jsonObject.put("image", file.name)

                val requestBody =
                    jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val result = NotesApi.notesApiService.postNotes(requestBody)
                Log.d("MainViewModel", "Status: ${result.status}")
                Log.d("MainViewModel", "Message: ${result.message}")
                Log.d("MainViewModel", "Name: ${result.name}")

                withContext(Dispatchers.Main) {
                    if (result.name != null) {
                        retrieveData()
                        Toast.makeText(
                            context,
                            context.getString(R.string.tambah_note_sukses),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        throw Exception(result.message)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("MainViewModel", "Failure: ${e.message}")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri, context: Context): String {
        var fileName = "temp_image.jpg"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    private fun getFileFromUri(uri: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val fileName = getFileNameFromUri(uri, context)
        val file = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file
    }

    fun deleteNotesById(id: String, context: Context) {
        viewModelScope.launch {
            try {
                val result = NotesApi.notesApiService.deleteNotes(
                    id
                )
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        Log.d("MainViewModel", "Successful deletion: ${result.code()}")
                        Toast.makeText(
                            context,
                            context.getString(R.string.hapus_note_sukses),
                            Toast.LENGTH_SHORT
                        ).show()
                        retrieveData()
                    } else {
                        Log.e(
                            "MainViewModel",
                            "Delete request failed with code: ${result.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _errorMessage.value = null
    }
}