package com.example.storyapp.ui.register

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
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.UserViewModelFactory
import com.example.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val viewModel: RegisterViewModel by viewModels {
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
        setupAction()
        setupAnimation()
    }

    private fun setupAction() {
        binding.btnGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edtName.error = getString(R.string.err_name_field)
                }

                email.isEmpty() -> {
                    binding.edtEmail.error = getString(R.string.err_email)
                }

                password.isEmpty() -> {

                }
            }

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(
                    name, email, password
                )

                viewModel.registerResult.observe(this) { resultState ->
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


                            showToast(resultState.data.message.toString())
                            moveToLogin()
                        }
                    }
                }
            } else {
                if (name.isEmpty()) {
                    binding.edtName.error = getString(R.string.err_name_field)
                    binding.edtName.requestFocus()
                }
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

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
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
        val nameTv = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.edtLName, View.ALPHA, 1f).setDuration(100)
        val emailTv = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.edtLEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTv = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.edtLPassword, View.ALPHA, 1f).setDuration(100)
        val orTv = ObjectAnimator.ofFloat(binding.tvOr, View.ALPHA, 1f).setDuration(100)
        val line1 = ObjectAnimator.ofFloat(binding.line1, View.ALPHA, 1f).setDuration(100)
        val line2 = ObjectAnimator.ofFloat(binding.line2, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnGoToLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                headline,
                nameTv,
                nameEditTextLayout,
                emailTv,
                emailEditTextLayout,
                passwordTv,
                passwordEditTextLayout,
                orTv,
                line1,
                line2,
                register,
                login
            )
            startDelay = 100
        }.start()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}