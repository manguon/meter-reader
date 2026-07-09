package com.example.meterreader

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meterreader.camera.CameraCaptureScreen
import com.example.meterreader.data.MeterType
import com.example.meterreader.ui.BillScreen
import com.example.meterreader.ui.HistoryScreen
import com.example.meterreader.ui.ReadingResultScreen
import com.example.meterreader.ui.theme.MeterReaderTheme
import com.example.meterreader.viewmodel.ReadingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ReadingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeterReaderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(viewModel: ReadingViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onGoCamera = { type -> navController.navigate("camera/$type") },
                onGoHistory = { navController.navigate("history") },
                onGoBill = { navController.navigate("bill") }
            )
        }
        composable("camera/{meterType}") { backStackEntry ->
            val typeArg = backStackEntry.arguments?.getString("meterType") ?: "WATER"
            val meterType = MeterType.valueOf(typeArg)
            var capturedUri by remember { mutableStateOf<Uri?>(null) }

            if (capturedUri == null) {
                CameraCaptureScreen(
                    onImageCaptured = { uri -> capturedUri = uri },
                    onCancel = { navController.popBackStack() }
                )
            } else {
                ReadingResultScreen(
                    imageUri = capturedUri!!,
                    meterType = meterType,
                    onSave = { value, type ->
                        viewModel.saveReading(value, type, capturedUri.toString())
                        navController.popBackStack("home", inclusive = false)
                    },
                    onRetake = { capturedUri = null }
                )
            }
        }
        composable("history") {
            HistoryScreen(viewModel)
        }
        composable("bill") {
            BillScreen(viewModel)
        }
    }
}

@Composable
fun HomeScreen(
    onGoCamera: (String) -> Unit,
    onGoHistory: () -> Unit,
    onGoBill: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Đọc Chỉ Số Đồng Hồ", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onGoCamera(MeterType.WATER.name) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Filled.WaterDrop, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Chụp đồng hồ nước")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onGoCamera(MeterType.ELECTRICITY.name) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Filled.Bolt, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Chụp đồng hồ điện")
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onGoHistory,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Filled.List, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Xem lịch sử")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onGoBill,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Filled.Calculate, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Tính tiền")
        }
    }
}
