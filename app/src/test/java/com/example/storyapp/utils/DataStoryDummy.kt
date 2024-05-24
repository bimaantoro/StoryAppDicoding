package com.example.storyapp.utils

import com.example.storyapp.data.local.entity.StoryEntity

object DataStoryDummy {
    fun generateDummyStoryEntity(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..10) {
            val story = StoryEntity(
                "story-$i",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "User $i",
                "Description $i",
                "2022-01-08T06:34:18.598Z"
            )
            items.add(story)
        }
        return items
    }
}