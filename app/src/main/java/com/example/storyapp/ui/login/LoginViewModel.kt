package com.example.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.pref.UserModel
import com.example.storyapp.data.remote.responses.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            storyRepository.login(email, password).collect {
                _loginResult.value = it
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            storyRepository.saveSession(user)
        }
    }
}