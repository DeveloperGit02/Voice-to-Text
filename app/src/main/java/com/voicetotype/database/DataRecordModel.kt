package com.voicetotype.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "DataRecordModel")
data class DataRecordModel (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dateTime: String,
    val type: String,
    val data: String // Large text data
)


