package org.d3if3082.checknote.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.d3if3082.checknote.model.MetaDataResponse
import org.d3if3082.checknote.model.Notes
import org.d3if3082.checknote.model.OpStatus
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

private const val BASE_URL =
    "https://checknote-5a3f0-default-rtdb.asia-southeast1.firebasedatabase.app/"

interface NotesApiService {
    @GET("notes.json")
    suspend fun getNotes(): Map<String, Notes>

    @GET("images%2F{fileRef}")
    suspend fun getFileMetadata(
        @Path("fileRef") fileRef: String,
    ): MetaDataResponse

    @POST("notes.json")
    suspend fun postNotes(
        @Body requestBody: RequestBody
    ): OpStatus

    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @DELETE("notes/{id}.json")
    suspend fun deleteNotes(
        @Path("id") id: String,
    ): Response<Unit>
}

private const val STORAGE =
    "https://firebasestorage.googleapis.com/v0/b/checknote-5a3f0.appspot.com/o/"

object NotesApi {
    private val gson: Gson = GsonBuilder().create()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val serviceGson: NotesApiService by lazy {
        retrofit.create(NotesApiService::class.java)
    }

    private val retrofitStorage = Retrofit.Builder()
        .baseUrl(STORAGE)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val serviceGsonStorage = retrofitStorage.create(NotesApiService::class.java)


    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofitOld = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()
    val service: NotesApiService by lazy {
        retrofitOld.create(NotesApiService::class.java)
    }

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofitHeader = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val notesApiService = retrofitHeader.create(NotesApiService::class.java)

    fun parseFirebaseStorageResponse(jsonString: String): Pair<String, String> {
        val jsonObject = JSONObject(jsonString)
        val name = jsonObject.getString("name")
        val downloadTokens = jsonObject.getString("downloadTokens")
        return Pair(name, downloadTokens)
    }
}

object RetrofitStorage {
    private val client = OkHttpClient.Builder().build()

    val instance: NotesApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(STORAGE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(NotesApiService::class.java)
    }
}