package com.example.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.ui.story.detail.DetailStoryActivity
import com.example.storyapp.ui.story.main.MainStoryActivity
import com.example.storyapp.utils.formatDateISO8601
import java.util.TimeZone

class StoryListAdapter :
    PagingDataAdapter<StoryEntity, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)

        if (story != null) {
            holder.bind(story)
        }


    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            binding.tvName.text = story.name
            binding.tvCreatedAt.text =
                formatDateISO8601(story.createdAt.toString(), TimeZone.getDefault().id)
            binding.tvDesc.text = story.description

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivThumbnail)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.STORY_EXTRA, story)
                }

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as MainStoryActivity,
                        Pair(binding.ivThumbnail, "thumbnail"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvDesc, "description"),
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean = oldItem == newItem
            }
    }
}