package com.example.storyapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "stories")
@Parcelize
data class StoryEntity(
    @PrimaryKey
    val id: String,

    @field:ColumnInfo("photo_url")
    val photoUrl: String? = null,

    @field:ColumnInfo("name")
    val name: String? = null,

    @field:ColumnInfo("description")
    val description: String? = null,

    @field:ColumnInfo("created_at")
    val createdAt: String? = null,
) : Parcelable