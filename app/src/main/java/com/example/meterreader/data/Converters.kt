package com.example.meterreader.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromMeterType(value: MeterType): String = value.name

    @TypeConverter
    fun toMeterType(value: String): MeterType = MeterType.valueOf(value)
}
