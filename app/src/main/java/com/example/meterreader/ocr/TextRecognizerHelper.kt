package com.example.meterreader.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object TextRecognizerHelper {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Nhận diện toàn bộ chữ trong ảnh rồi trích ra dãy số phù hợp nhất
     * (số công tơ nước/điện thường là dãy số dài nhất trong khung hình).
     */
    suspend fun recognizeDigits(bitmap: Bitmap): String {
        val image = InputImage.fromBitmap(bitmap, 0)
        val visionText = suspendCancellableCoroutine<Text> { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result -> cont.resume(result) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        return extractBestNumber(visionText.text)
    }

    private fun extractBestNumber(rawText: String): String {
        val candidates = Regex("[0-9]+([.,][0-9]+)?")
            .findAll(rawText)
            .map { it.value }
            .toList()

        if (candidates.isEmpty()) return ""

        // Ưu tiên dãy số có nhiều chữ số nhất (loại bỏ dấu phẩy/chấm khi so sánh độ dài)
        return candidates.maxByOrNull { it.replace(",", "").replace(".", "").length } ?: ""
    }
}
