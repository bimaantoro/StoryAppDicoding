package com.example.storyapp.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.pref.UserModel

class OnBoardingViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }
}