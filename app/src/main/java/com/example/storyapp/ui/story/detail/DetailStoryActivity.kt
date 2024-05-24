package com.example.storyapp.ui.story.detail

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.formatDateISO8601
import java.util.TimeZone

class DetailStoryActivity : AppCompatActivity() {

    private val binding: ActivityDetailStoryBinding by lazy {
        ActivityDetailStoryBinding.inflate(layoutInflater)
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
        setupData()
    }

    private fun setupAction() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupData() {
        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(STORY_EXTRA, StoryEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(STORY_EXTRA)
        }

        if (story != null) {
            binding.contentDetailStory.detailEdtName.setText(story.name)
            binding.contentDetailStory.detailEdtCreatedAt.setText(
                formatDateISO8601(
                    story.createdAt.toString(),
                    TimeZone.getDefault().id
                )
            )
            binding.contentDetailStory.detailEdtDesc.setText(story.description)

            Glide.with(this).load(story.photoUrl).into(binding.contentDetailStory.ivThumbnail)
        }
    }

    companion object {
        const val STORY_EXTRA = "story_extra"
    }
}