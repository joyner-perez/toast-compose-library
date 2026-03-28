package com.joyner.toastcomposelibrary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.joyner.toastcomposelibrary.toast.ToastCompose
import com.joyner.toastcomposelibrary.toast.ToastType
import com.joyner.toastcomposelibrary.toast.rememberToastState

private val ColorSuccess = Color(0xFF2E7D32)
private val ColorError = Color(0xFFC62828)
private val ColorInfo = Color(0xFF1565C0)
private val ColorWarning = Color(0xFFE65100)

@Composable
internal fun SampleApp() {
    MaterialTheme {
        val toastState = rememberToastState()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = "ToastCompose Demo",
                    style = MaterialTheme.typography.headlineSmall
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                    onClick = { toastState.show("Operación exitosa", ToastType.SUCCESS) }
                ) { Text("Mostrar SUCCESS") }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorError),
                    onClick = { toastState.show("Ocurrió un error", ToastType.ERROR) }
                ) { Text("Mostrar ERROR") }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorInfo),
                    onClick = { toastState.show("Información importante", ToastType.INFO) }
                ) { Text("Mostrar INFO") }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorWarning),
                    onClick = { toastState.show("Atención requerida", ToastType.WARNING) }
                ) { Text("Mostrar WARNING") }
            }

            ToastCompose(
                toastState = toastState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            )
        }
    }
}
