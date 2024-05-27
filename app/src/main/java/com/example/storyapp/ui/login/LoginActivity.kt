package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.local.pref.UserModel
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.UserViewModelFactory
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.ui.story.main.MainStoryActivity

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by viewModels {
        UserViewModelFactory.getInstance(this)
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

        hideSystemUI()
        setupAnimation()
        setupAction()
    }

    private fun setupAction() {
        binding.btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)

                viewModel.loginResult.observe(this) { resultState ->
                    when (resultState) {
                        is ResultState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is ResultState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            val error = resultState.error
                            showToast(error)
                        }

                        is ResultState.Success -> {
                            binding.progressBar.visibility = View.GONE

                            val sessionData = resultState.data
                            viewModel.saveSession(
                                UserModel(
                                    sessionData.loginResult?.name ?: "",
                                    sessionData.loginResult?.token ?: "",
                                    true
                                )
                            )
                            moveToMain()
                        }
                    }

                }

            } else {
                if (email.isEmpty()) {
                    binding.edtEmail.error = getString(R.string.err_email)
                    binding.edtEmail.requestFocus()
                }

                if (password.isEmpty()) {
                    binding.edtPassword.error = getString(R.string.err_password_field)
                    binding.edtPassword.requestFocus()
                }
            }
        }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainStoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
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
        val headline = ObjectAnimator.ofFloat(binding.tvHeadline, View.ALPHA, 1f).setDuration(100)
        val emailTv = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.edtLEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTv = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.edtLPassword, View.ALPHA, 1f).setDuration(100)
        val orTv = ObjectAnimator.ofFloat(binding.tvOr, View.ALPHA, 1f).setDuration(100)
        val line1 = ObjectAnimator.ofFloat(binding.line1, View.ALPHA, 1f).setDuration(100)
        val line2 = ObjectAnimator.ofFloat(binding.line2, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val register =
            ObjectAnimator.ofFloat(binding.btnGoToRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                headline,
                emailTv,
                emailEditTextLayout,
                passwordTv,
                passwordEditTextLayout,
                orTv,
                line1,
                line2,
                login,
                register,
            )
            startDelay = 100
        }.start()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}