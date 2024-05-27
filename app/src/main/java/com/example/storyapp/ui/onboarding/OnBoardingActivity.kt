package com.example.storyapp.ui.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityOnBoardingBinding
import com.example.storyapp.ui.StoryViewModelFactory
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.ui.story.main.MainStoryActivity

class OnBoardingActivity : AppCompatActivity() {

    private val binding: ActivityOnBoardingBinding by lazy {
        ActivityOnBoardingBinding.inflate(layoutInflater)
    }

    private val viewModel: OnBoardingViewModel by viewModels {
        StoryViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainStoryActivity::class.java))
                finish()
            }

        }

        hideSystemUI()
        setupAction()
        setupAnimation()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAnimation() {
        val login = ObjectAnimator.ofFloat(binding.btnGoToLogin, View.ALPHA, 1f).setDuration(100)
        val register =
            ObjectAnimator.ofFloat(binding.btnGoToRegister, View.ALPHA, 1f).setDuration(100)
        val headline = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(100)
        val subHeadline =
            ObjectAnimator.ofFloat(binding.tvSubHeadline, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(login, register)

        }

        AnimatorSet().apply {
            playSequentially(headline, subHeadline, together)
            start()
        }

    }

    private fun setupAction() {
        binding.btnGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}