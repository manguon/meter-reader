package com.example.meterreader.util

data class BillResult(
    val roomFee: Long,
    val elecConsumption: Double,
    val elecCost: Long,
    val waterConsumption: Double,
    val waterCost: Long,
    val trashFee: Long,
    val total: Long
)

object BillCalculator {

    /**
     * Tính hoá đơn dựa trên chỉ số cũ/mới của điện và nước.
     * Nếu số mới nhỏ hơn số cũ (nhập nhầm), tiêu thụ được tính là 0 thay vì số âm.
     */
    fun calculate(
        prevElec: Double,
        currElec: Double,
        prevWater: Double,
        currWater: Double,
        roomFee: Long = 800_000,
        elecPricePerKwh: Long = 3_500,
        waterPricePerM3: Long = 6_000,
        trashFee: Long = 20_000
    ): BillResult {
        val elecConsumption = (currElec - prevElec).coerceAtLeast(0.0)
        val waterConsumption = (currWater - prevWater).coerceAtLeast(0.0)
        val elecCost = Math.round(elecConsumption * elecPricePerKwh)
        val waterCost = Math.round(waterConsumption * waterPricePerM3)
        val total = roomFee + elecCost + waterCost + trashFee

        return BillResult(
            roomFee = roomFee,
            elecConsumption = elecConsumption,
            elecCost = elecCost,
            waterConsumption = waterConsumption,
            waterCost = waterCost,
            trashFee = trashFee,
            total = total
        )
    }
}
