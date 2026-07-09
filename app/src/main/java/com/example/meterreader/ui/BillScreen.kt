package com.example.meterreader.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.meterreader.data.MeterType
import com.example.meterreader.util.BillPrefs
import com.example.meterreader.viewmodel.ReadingViewModel
import java.text.NumberFormat
import java.util.Locale

private data class BillResult(
    val elecConsumption: Double,
    val elecCost: Double,
    val waterConsumption: Double,
    val waterCost: Double,
    val roomFee: Double,
    val garbageFee: Double
) {
    val total: Double get() = elecCost + waterCost + roomFee + garbageFee
}

private fun String.toSafeDouble(): Double =
    this.trim().replace(",", ".").toDoubleOrNull() ?: 0.0

private fun Double.formatQuantity(): String =
    if (this == this.toLong().toDouble()) this.toLong().toString()
    else String.format(Locale("vi", "VN"), "%.2f", this)

@Composable
fun BillScreen(viewModel: ReadingViewModel) {
    val context = LocalContext.current
    val readings by viewModel.readings.collectAsState()
    val currencyFormat = remember { NumberFormat.getInstance(Locale("vi", "VN")) }

    var initialized by remember { mutableStateOf(false) }

    var elecPrev by remember { mutableStateOf("") }
    var elecCurr by remember { mutableStateOf("") }
    var waterPrev by remember { mutableStateOf("") }
    var waterCurr by remember { mutableStateOf("") }

    LaunchedEffect(readings) {
        if (!initialized && readings.isNotEmpty()) {
            val elec = readings.filter { it.meterType == MeterType.ELECTRICITY }
            val water = readings.filter { it.meterType == MeterType.WATER }
            if (elec.isNotEmpty()) elecCurr = elec[0].value
            if (elec.size >= 2) elecPrev = elec[1].value
            if (water.isNotEmpty()) waterCurr = water[0].value
            if (water.size >= 2) waterPrev = water[1].value
            initialized = true
        }
    }

    var roomFee by remember { mutableStateOf(BillPrefs.getRoomFee(context).toLong().toString()) }
    var elecRate by remember { mutableStateOf(BillPrefs.getElecRate(context).toLong().toString()) }
    var waterRate by remember { mutableStateOf(BillPrefs.getWaterRate(context).toLong().toString()) }
    var garbageFee by remember { mutableStateOf(BillPrefs.getGarbageFee(context).toLong().toString()) }

    var result by remember { mutableStateOf<BillResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var savedMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Tính tiền phòng trọ", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Text("Chỉ số điện (kWh)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = elecPrev,
                onValueChange = { elecPrev = it },
                label = { Text("Kỳ trước") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = elecCurr,
                onValueChange = { elecCurr = it },
                label = { Text("Kỳ này") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Chỉ số nước (m³)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = waterPrev,
                onValueChange = { waterPrev = it },
                label = { Text("Kỳ trước") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = waterCurr,
                onValueChange = { waterCurr = it },
                label = { Text("Kỳ này") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Chỉ số kỳ trước/kỳ này được tự điền từ 2 lần đọc gần nhất trong lịch sử — bạn có thể sửa lại bất kỳ lúc nào.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(Modifier.height(20.dp))

        Text("Đơn giá", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = roomFee,
            onValueChange = { roomFee = it },
            label = { Text("Tiền phòng (đ, cố định)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = elecRate,
            onValueChange = { elecRate = it },
            label = { Text("Giá điện (đ/kWh)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = waterRate,
            onValueChange = { waterRate = it },
            label = { Text("Giá nước (đ/m³)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = garbageFee,
            onValueChange = { garbageFee = it },
            label = { Text("Tiền rác (đ, cố định)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                BillPrefs.saveRates(
                    context,
                    roomFee.toSafeDouble(),
                    elecRate.toSafeDouble(),
                    waterRate.toSafeDouble(),
                    garbageFee.toSafeDouble()
                )
                savedMessage = "Đã lưu đơn giá làm mặc định."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu đơn giá làm mặc định")
        }
        savedMessage?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val elecConsumption = elecCurr.toSafeDouble() - elecPrev.toSafeDouble()
                val waterConsumption = waterCurr.toSafeDouble() - waterPrev.toSafeDouble()

                if (elecConsumption < 0 || waterConsumption < 0) {
                    errorMessage = "Chỉ số kỳ này phải lớn hơn hoặc bằng kỳ trước."
                    result = null
                    return@Button
                }
                errorMessage = null

                result = BillResult(
                    elecConsumption = elecConsumption,
                    elecCost = elecConsumption * elecRate.toSafeDouble(),
                    waterConsumption = waterConsumption,
                    waterCost = waterConsumption * waterRate.toSafeDouble(),
                    roomFee = roomFee.toSafeDouble(),
                    garbageFee = garbageFee.toSafeDouble()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Tính tiền")
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        result?.let { bill ->
            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text("Chi tiết hoá đơn", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            BillRow("Tiền phòng", "${currencyFormat.format(bill.roomFee)} đ")
            BillRow(
                "Tiền điện (${bill.elecConsumption.formatQuantity()} kWh)",
                "${currencyFormat.format(bill.elecCost)} đ"
            )
            BillRow(
                "Tiền nước (${bill.waterConsumption.formatQuantity()} m³)",
                "${currencyFormat.format(bill.waterCost)} đ"
            )
            BillRow("Tiền rác", "${currencyFormat.format(bill.garbageFee)} đ")

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tổng cộng", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${currencyFormat.format(bill.total)} đ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BillRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value)
    }
}
