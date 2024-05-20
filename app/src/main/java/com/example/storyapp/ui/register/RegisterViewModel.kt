package com.example.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.responses.CommonResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<ResultState<CommonResponse>>()
    val registerResult: LiveData<ResultState<CommonResponse>> = _registerResult

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            storyRepository.register(name, email, password).collect {
                _registerResult.value = it
            }
        }
    }

}