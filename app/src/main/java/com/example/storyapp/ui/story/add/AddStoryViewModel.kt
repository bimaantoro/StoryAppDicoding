package com.example.storyapp.ui.story.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.responses.CommonResponse
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _postStoryResult = MutableLiveData<ResultState<CommonResponse>>()
    val postStoryResult: LiveData<ResultState<CommonResponse>> = _postStoryResult

    fun postStory(
        imageFile: File,
        description: String
    ) {
        viewModelScope.launch {
            storyRepository.postStory(imageFile, description).collect {
                _postStoryResult.value = it
            }
        }
    }
}