package com.example.storyapp.ui.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.pref.UserModel
import kotlinx.coroutines.launch

class MainStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    val storyListResult: LiveData<PagingData<StoryEntity>> =
        storyRepository.getStories().cachedIn(viewModelScope).asLiveData()


    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }
}