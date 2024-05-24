package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.paging.StoryRemoteMediator
import com.example.storyapp.data.local.pref.UserModel
import com.example.storyapp.data.local.pref.UserPreference
import com.example.storyapp.data.local.room.StoryDatabase
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
    private val storyDatabase: StoryDatabase,
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

    fun getStories(): Flow<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreference),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }

    fun getStoriesWithLocation(): Flow<ResultState<StoriesResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val response = apiService.getStoriesWithLocation()

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
    }.flowOn(Dispatchers.IO)

    fun postStory(
        imageFile: File,
        description: String,
        lat: Double?,
        lon: Double?
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)

        val textPlainMediaType = "text/plain".toMediaType()

        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)
        val latRequestBody = lat.toString().toRequestBody(textPlainMediaType)
        val lonRequestBody = lon.toString().toRequestBody(textPlainMediaType)
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val response = apiService.postStory(
                multipartBody,
                descriptionRequestBody,
                latRequestBody,
                lonRequestBody
            )

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
            storyDatabase: StoryDatabase,
            apiService: ApiService,
            userPreference: UserPreference,
        ): StoryRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: StoryRepository(storyDatabase, apiService, userPreference)
        }.also { INSTANCE = it }
    }
}