package com.example.storyapp.data

import com.example.storyapp.data.local.pref.UserModel
import com.example.storyapp.data.local.pref.UserPreference
import com.example.storyapp.data.remote.responses.CommonResponse
import com.example.storyapp.data.remote.responses.LoginResponse
import com.example.storyapp.data.remote.responses.StoriesResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    fun register(
        name: String,
        email: String,
        password: String
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name, email, password)

            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }

        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    fun login(
        email: String,
        password: String
    ): Flow<ResultState<LoginResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)

            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }

        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    fun getStories(): Flow<ResultState<StoriesResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val response = apiService.getStories()

            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }

        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoriesResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }

    fun postStory(imageFile: File, description: String): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)

        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val response = apiService.postStory(multipartBody, requestBody)

            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }

        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
        ): StoryRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: StoryRepository(apiService, userPreference)
        }.also { INSTANCE = it }
    }
}