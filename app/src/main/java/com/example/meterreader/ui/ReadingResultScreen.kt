package com.example.meterreader.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.meterreader.data.MeterType
import com.example.meterreader.ocr.TextRecognizerHelper

@Composable
fun ReadingResultScreen(
    imageUri: Uri,
    meterType: MeterType,
    onSave: (String, MeterType) -> Unit,
    onRetake: () -> Unit
) {
    val context = LocalContext.current
    var recognizedValue by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(true) }

    LaunchedEffect(imageUri) {
        isProcessing = true
        val bitmap = context.contentResolver.openInputStream(imageUri)?.use {
            BitmapFactory.decodeStream(it)
        }
        recognizedValue = if (bitmap != null) {
            try {
                TextRecognizerHelper.recognizeDigits(bitmap)
            } catch (e: Exception) {
                ""
            }
        } else ""
        isProcessing = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            if (meterType == MeterType.WATER) "Đồng hồ nước" else "Đồng hồ điện",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))

        AsyncImage(
            model = imageUri,
            contentDescription = "Ảnh đồng hồ",
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Spacer(Modifier.height(16.dp))

        if (isProcessing) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp).width(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đang nhận diện số...")
            }
        } else {
            Text("Kiểm tra và sửa số nếu cần:")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = recognizedValue,
                onValueChange = { recognizedValue = it },
                label = { Text("Chỉ số đồng hồ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (recognizedValue.isBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Không nhận diện được số, hãy nhập tay hoặc chụp lại ảnh rõ hơn.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row {
            OutlinedButton(onClick = onRetake, modifier = Modifier.weight(1f)) {
                Text("Chụp lại")
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = { onSave(recognizedValue, meterType) },
                enabled = recognizedValue.isNotBlank() && !isProcessing,
                modifier = Modifier.weight(1f)
            ) {
                Text("Lưu")
            }
        }
    }
}
