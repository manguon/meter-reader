package com.example.meterreader.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.meterreader.data.MeterType
import com.example.meterreader.data.ReadingEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object CsvExporter {

    fun exportToCsv(context: Context, readings: List<ReadingEntity>): Uri {
        val exportsDir = File(context.getExternalFilesDir("exports"), "").apply { mkdirs() }
        val fileName = "chi_so_dong_ho_${System.currentTimeMillis()}.csv"
        val file = File(exportsDir, fileName)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))

        file.bufferedWriter().use { writer ->
            writer.write("Loại đồng hồ,Chỉ số,Ngày giờ đọc\n")
            readings.forEach { reading ->
                val typeLabel = if (reading.meterType == MeterType.WATER) "Nước" else "Điện"
                writer.write("$typeLabel,${reading.value},${dateFormat.format(reading.timestamp)}\n")
            }
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
