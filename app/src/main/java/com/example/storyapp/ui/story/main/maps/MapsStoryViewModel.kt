package com.example.storyapp.ui.story.main.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.pref.UserModel
import com.example.storyapp.data.remote.responses.StoriesResponse
import kotlinx.coroutines.launch

class MapsStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyMapResult = MutableLiveData<ResultState<StoriesResponse>>()
    val storyMapResult: LiveData<ResultState<StoriesResponse>> = _storyMapResult


    fun getStoriesWithLocation() {
        viewModelScope.launch {
            storyRepository.getStoriesWithLocation().collect {
                _storyMapResult.value = it
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }
}