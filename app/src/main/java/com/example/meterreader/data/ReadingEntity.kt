package com.example.meterreader.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MeterType {
    WATER, ELECTRICITY
}

@Entity(tableName = "readings")
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val meterType: MeterType,
    val value: String,
    val timestamp: Long,
    val imagePath: String?
)
