package com.example.storyapp.data.local.pref

data class UserModel(
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)