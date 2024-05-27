package com.example.storyapp.data.repository

import com.example.storyapp.data.ResultState
import com.example.storyapp.data.local.pref.UserModel
import com.example.storyapp.data.local.pref.UserPreference
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.responses.CommonResponse
import com.example.storyapp.data.remote.responses.LoginResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class UserRepository private constructor(
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

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
        ): UserRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: UserRepository(apiService, userPreference)
        }.also { INSTANCE = it }
    }
}