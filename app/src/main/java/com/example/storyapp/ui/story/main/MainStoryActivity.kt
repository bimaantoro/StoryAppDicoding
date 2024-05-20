package com.example.storyapp.ui.story.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryListAdapter
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivityMainStoryBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.onboarding.OnBoardingActivity
import com.example.storyapp.ui.story.add.AddStoryActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainStoryActivity : AppCompatActivity() {

    private val binding: ActivityMainStoryBinding by lazy {
        ActivityMainStoryBinding.inflate(layoutInflater)
    }

    private val viewModel: MainStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
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

        setupAction()

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
            } else {
                viewModel.getStories()
            }
        }

        val storyListAdapter = StoryListAdapter()
        binding.contentMainStory.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyListAdapter
        }
        fetchStories(storyListAdapter)
    }


    private fun fetchStories(storyListAdapter: StoryListAdapter) {
        viewModel.storyListResult.observe(this) { resultState ->
            if (resultState != null) {
                when (resultState) {
                    is ResultState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ResultState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        val error = resultState.error

                        showToast("Terjadi kesalahan: $error")
                    }

                    is ResultState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val storiesData = resultState.data.listStory
                        storyListAdapter.submitList(storiesData)
                    }
                }
            }

        }
    }

    private fun setupAction() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    MaterialAlertDialogBuilder(this).apply {
                        setTitle(R.string.logout)
                        setMessage(R.string.logout_dialog_message)
                        setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                            viewModel.logout()
                            dialog.dismiss()
                        }
                        setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                            dialog.dismiss()
                        }
                    }
                        .create()
                        .show()
                    true
                }

                R.id.setting -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                else -> false
            }
        }


        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}