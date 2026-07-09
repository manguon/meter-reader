package com.example.meterreader.util

import android.content.Context

object BillPrefs {
    private const val PREFS_NAME = "bill_prefs"
    private const val KEY_ROOM_FEE = "room_fee"
    private const val KEY_ELEC_RATE = "elec_rate"
    private const val KEY_WATER_RATE = "water_rate"
    private const val KEY_GARBAGE_FEE = "garbage_fee"

    // Đơn giá mặc định theo yêu cầu
    private const val DEFAULT_ROOM_FEE = 800000f
    private const val DEFAULT_ELEC_RATE = 3500f
    private const val DEFAULT_WATER_RATE = 6000f
    private const val DEFAULT_GARBAGE_FEE = 20000f

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getRoomFee(context: Context): Double =
        prefs(context).getFloat(KEY_ROOM_FEE, DEFAULT_ROOM_FEE).toDouble()

    fun getElecRate(context: Context): Double =
        prefs(context).getFloat(KEY_ELEC_RATE, DEFAULT_ELEC_RATE).toDouble()

    fun getWaterRate(context: Context): Double =
        prefs(context).getFloat(KEY_WATER_RATE, DEFAULT_WATER_RATE).toDouble()

    fun getGarbageFee(context: Context): Double =
        prefs(context).getFloat(KEY_GARBAGE_FEE, DEFAULT_GARBAGE_FEE).toDouble()

    fun saveRates(
        context: Context,
        roomFee: Double,
        elecRate: Double,
        waterRate: Double,
        garbageFee: Double
    ) {
        prefs(context).edit()
            .putFloat(KEY_ROOM_FEE, roomFee.toFloat())
            .putFloat(KEY_ELEC_RATE, elecRate.toFloat())
            .putFloat(KEY_WATER_RATE, waterRate.toFloat())
            .putFloat(KEY_GARBAGE_FEE, garbageFee.toFloat())
            .apply()
    }
}
