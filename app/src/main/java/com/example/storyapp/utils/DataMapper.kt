package com.example.storyapp.utils

import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.remote.responses.ListStoryItem

object DataMapper {
    fun mapStoryResponseToDataEntity(input: List<ListStoryItem>): List<StoryEntity> =
        input.map {
            StoryEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt
            )
        }
}