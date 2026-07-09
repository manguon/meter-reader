package com.example.meterreader.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Insert
    suspend fun insert(reading: ReadingEntity): Long

    @Delete
    suspend fun delete(reading: ReadingEntity)

    @Query("SELECT * FROM readings ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ReadingEntity>>

    @Query("SELECT * FROM readings WHERE meterType = :type ORDER BY timestamp DESC")
    fun getByType(type: MeterType): Flow<List<ReadingEntity>>
}
