package com.example.storyapp.ui.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.pref.UserModel
import com.example.storyapp.data.remote.responses.ListStoryItem
import com.example.storyapp.data.remote.responses.StoriesResponse
import kotlinx.coroutines.launch

class MainStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _storyListResult = MutableLiveData<ResultState<StoriesResponse>>()
    val storyListResult: LiveData<ResultState<StoriesResponse>> = _storyListResult

    fun getStories() {
        viewModelScope.launch {
            storyRepository.getStories().collect {
                _storyListResult.value = it
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }
}