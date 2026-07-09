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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.meterreader.data.MeterType
import com.example.meterreader.util.BillCalculator
import com.example.meterreader.viewmodel.ReadingViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BillingScreen(viewModel: ReadingViewModel) {
    val readings by viewModel.readings.collectAsState()

    // readings đã được sắp xếp mới nhất trước (DESC theo timestamp)
    val waterReadings = remember(readings) { readings.filter { it.meterType == MeterType.WATER } }
    val elecReadings = remember(readings) { readings.filter { it.meterType == MeterType.ELECTRICITY } }

    var prevWater by remember(waterReadings) { mutableStateOf(waterReadings.getOrNull(1)?.value ?: "") }
    var currWater by remember(waterReadings) { mutableStateOf(waterReadings.getOrNull(0)?.value ?: "") }
    var prevElec by remember(elecReadings) { mutableStateOf(elecReadings.getOrNull(1)?.value ?: "") }
    var currElec by remember(elecReadings) { mutableStateOf(elecReadings.getOrNull(0)?.value ?: "") }

    var roomFee by remember { mutableStateOf("800000") }
    var elecPrice by remember { mutableStateOf("3500") }
    var waterPrice by remember { mutableStateOf("6000") }
    var trashFee by remember { mutableStateOf("20000") }

    val result = remember(prevWater, currWater, prevElec, currElec, roomFee, elecPrice, waterPrice, trashFee) {
        BillCalculator.calculate(
            prevElec = prevElec.toDoubleOrNull() ?: 0.0,
            currElec = currElec.toDoubleOrNull() ?: 0.0,
            prevWater = prevWater.toDoubleOrNull() ?: 0.0,
            currWater = currWater.toDoubleOrNull() ?: 0.0,
            roomFee = roomFee.toLongOrNull() ?: 800_000,
            elecPricePerKwh = elecPrice.toLongOrNull() ?: 3_500,
            waterPricePerM3 = waterPrice.toLongOrNull() ?: 6_000,
            trashFee = trashFee.toLongOrNull() ?: 20_000
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Tính tiền phòng", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            "Số điện/nước được tự lấy từ 2 lần đọc gần nhất trong lịch sử. Có thể sửa lại nếu cần.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(20.dp))
        Text("Điện", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = prevElec,
                onValueChange = { prevElec = it },
                label = { Text("Số cũ (kWh)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = currElec,
                onValueChange = { currElec = it },
                label = { Text("Số mới (kWh)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))
        Text("Nước", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = prevWater,
                onValueChange = { prevWater = it },
                label = { Text("Số cũ (m³)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = currWater,
                onValueChange = { currWater = it },
                label = { Text("Số mới (m³)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("Đơn giá & phí cố định", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = roomFee,
            onValueChange = { roomFee = it },
            label = { Text("Tiền phòng (đ/tháng)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = elecPrice,
            onValueChange = { elecPrice = it },
            label = { Text("Giá điện (đ/kWh)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = waterPrice,
            onValueChange = { waterPrice = it },
            label = { Text("Giá nước (đ/m³)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = trashFee,
            onValueChange = { trashFee = it },
            label = { Text("Tiền rác (đ/tháng)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Text("Chi tiết hoá đơn", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        BillRow("Tiền phòng", formatVnd(result.roomFee))
        BillRow(
            "Tiền điện (${formatNumber(result.elecConsumption)} kWh × ${elecPrice} đ)",
            formatVnd(result.elecCost)
        )
        BillRow(
            "Tiền nước (${formatNumber(result.waterConsumption)} m³ × ${waterPrice} đ)",
            formatVnd(result.waterCost)
        )
        BillRow("Tiền rác", formatVnd(result.trashFee))

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng cộng", style = MaterialTheme.typography.titleLarge)
            Text(
                formatVnd(result.total),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))
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
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatVnd(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(amount)} đ"
}

private fun formatNumber(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", value)
    }
}
