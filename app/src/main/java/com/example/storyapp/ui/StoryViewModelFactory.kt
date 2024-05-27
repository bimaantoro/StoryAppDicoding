package com.example.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.onboarding.OnBoardingViewModel
import com.example.storyapp.ui.register.RegisterViewModel
import com.example.storyapp.ui.story.add.AddStoryViewModel
import com.example.storyapp.ui.story.main.MainStoryViewModel
import com.example.storyapp.ui.story.main.maps.MapsStoryViewModel

class StoryViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(MainStoryViewModel::class.java) -> {
                MainStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(OnBoardingViewModel::class.java) -> {
                OnBoardingViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(MapsStoryViewModel::class.java) -> {
                MapsStoryViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class:  ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryViewModelFactory? = null

        fun getInstance(context: Context): StoryViewModelFactory = INSTANCE ?: synchronized(this) {
            INSTANCE ?: StoryViewModelFactory(Injection.provideStoryRepository(context))
        }.also { INSTANCE = it }
    }
}