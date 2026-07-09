package com.example.meterreader.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.meterreader.data.MeterType
import com.example.meterreader.data.ReadingEntity
import com.example.meterreader.util.CsvExporter
import com.example.meterreader.viewmodel.ReadingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: ReadingViewModel) {
    val context = LocalContext.current
    val readings by viewModel.readings.collectAsState()
    var filter by remember { mutableStateOf<MeterType?>(null) }

    val filtered = remember(readings, filter) {
        if (filter == null) readings else readings.filter { it.meterType == filter }
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN")) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lịch sử chỉ số", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = {
                if (filtered.isNotEmpty()) {
                    val uri = CsvExporter.exportToCsv(context, filtered)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Xuất file CSV"))
                }
            }) {
                Icon(Icons.Filled.Share, contentDescription = "Xuất CSV")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row {
            FilterChip(
                selected = filter == null,
                onClick = { filter = null },
                label = { Text("Tất cả") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = filter == MeterType.WATER,
                onClick = { filter = MeterType.WATER },
                label = { Text("Nước") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = filter == MeterType.ELECTRICITY,
                onClick = { filter = MeterType.ELECTRICITY },
                label = { Text("Điện") }
            )
        }

        Spacer(Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có dữ liệu nào")
            }
        } else {
            LazyColumn {
                items(filtered, key = { it.id }) { reading ->
                    ReadingRow(
                        reading = reading,
                        dateFormat = dateFormat,
                        onDelete = { viewModel.deleteReading(reading) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ReadingRow(
    reading: ReadingEntity,
    dateFormat: SimpleDateFormat,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                if (reading.meterType == MeterType.WATER) "Đồng hồ nước" else "Đồng hồ điện",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Chỉ số: ${reading.value}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                dateFormat.format(reading.timestamp),
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Xoá")
        }
    }
}
