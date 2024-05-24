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
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoryListAdapter
import com.example.storyapp.databinding.ActivityMainStoryBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.onboarding.OnBoardingActivity
import com.example.storyapp.ui.story.add.AddStoryActivity
import com.example.storyapp.ui.story.main.maps.MapsStoryActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainStoryActivity : AppCompatActivity() {

    private lateinit var storyListAdapter: StoryListAdapter


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
        setupStoryAdapter()
        setupSession()

    }

    override fun onResume() {
        super.onResume()
        if (!storyListAdapter.snapshot().isEmpty()) {
            storyListAdapter.refresh()
            lifecycleScope.launch {
                storyListAdapter.loadStateFlow
                    .collect {
                        binding.contentMainStory.rvStory.smoothScrollToPosition(0)
                    }
            }
        }
    }

    private fun setupSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
            }
        }
    }

    private fun setupStoryAdapter() {
        storyListAdapter = StoryListAdapter()
        binding.contentMainStory.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyListAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyListAdapter.retry()
                }
            )
        }

        storyListAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.storyListResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            storyListAdapter.submitData(lifecycle, result)
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

                R.id.map -> {
                    startActivity(
                        Intent(
                            this@MainStoryActivity,
                            MapsStoryActivity::class.java
                        )
                    )
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}