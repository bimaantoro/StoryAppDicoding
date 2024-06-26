package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.local.pref.UserPreference
import com.example.storyapp.data.local.pref.dataStore
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(
            database,
            apiService,
            pref
        )
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}