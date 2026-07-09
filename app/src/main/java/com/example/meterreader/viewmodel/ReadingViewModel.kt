package com.example.meterreader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.meterreader.data.AppDatabase
import com.example.meterreader.data.MeterType
import com.example.meterreader.data.ReadingEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReadingViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).readingDao()

    val readings = dao.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun saveReading(value: String, type: MeterType, imagePath: String?) {
        viewModelScope.launch {
            dao.insert(
                ReadingEntity(
                    meterType = type,
                    value = value,
                    timestamp = System.currentTimeMillis(),
                    imagePath = imagePath
                )
            )
        }
    }

    fun deleteReading(reading: ReadingEntity) {
        viewModelScope.launch {
            dao.delete(reading)
        }
    }
}
