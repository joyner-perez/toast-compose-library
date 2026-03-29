package com.joyner.toastcomposelibrary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.joyner.toastcomposelibrary.toast.ToastHost
import com.joyner.toastcomposelibrary.toast.ToastIcon
import com.joyner.toastcomposelibrary.toast.ToastNative
import com.joyner.toastcomposelibrary.toast.ToastNativeDuration
import com.joyner.toastcomposelibrary.toast.ToastState
import com.joyner.toastcomposelibrary.toast.ToastType
import com.joyner.toastcomposelibrary.toast.rememberToastNative
import com.joyner.toastcomposelibrary.toast.rememberToastState
import com.joyner.toastcomposelibrary.toast.show
import org.jetbrains.compose.resources.painterResource
import toastcomposelibrary.composeapp.generated.resources.Res
import toastcomposelibrary.composeapp.generated.resources.compose_multiplatform

private val ColorSuccess = Color(0xFF2E7D32)
private val ColorError = Color(0xFFC62828)
private val ColorInfo = Color(0xFF1565C0)
private val ColorWarning = Color(0xFFE65100)
private val ColorCustom = Color(0xFF6A1B9A)
private val ColorTeal = Color(0xFF00695C)

@Composable
internal fun SampleApp() {
    MaterialTheme {
        val toastState = rememberToastState()
        val nativeToast = rememberToastNative()

        Scaffold(
            snackbarHost = { ToastHost(toastState = toastState) }
        ) { innerPadding ->
            SampleContent(
                toastState = toastState,
                nativeToast = nativeToast,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun SampleContent(
    toastState: ToastState,
    nativeToast: ToastNative,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "ToastCompose Demo", style = MaterialTheme.typography.headlineSmall)

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

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text(text = "Native Toast Demo", style = MaterialTheme.typography.headlineSmall)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nativeToast.show("Toast nativo corto") }
        ) { Text("Mostrar Native SHORT") }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nativeToast.show("Toast nativo largo", ToastNativeDuration.LONG) }
        ) { Text("Mostrar Native LONG") }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        CustomToastSection(toastState = toastState)

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        QueueToastSection(toastState = toastState)
    }
}

@Composable
private fun CustomToastSection(toastState: ToastState) {
    val composePainter = painterResource(Res.drawable.compose_multiplatform)

    Text(text = "Custom Toast Demo", style = MaterialTheme.typography.headlineSmall)

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorCustom),
        onClick = {
            toastState.show(
                message = "Toast con icono, color y fuente personalizados",
                icon = ToastIcon.Vector(Icons.Filled.Star),
                backgroundColor = ColorCustom,
                textColor = ColorTeal,
                fontFamily = FontFamily.Cursive
            )
        }
    ) { Text("Mostrar Custom Vector") }

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorTeal),
        onClick = {
            toastState.show(
                message = "Toast con icono desde drawable",
                icon = ToastIcon.Resource(composePainter),
                backgroundColor = ColorTeal
            )
        }
    ) { Text("Mostrar Custom Drawable") }

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorError),
        onClick = {
            toastState.show(
                message = "Elemento eliminado",
                type = ToastType.ERROR,
                onAction = {}
            )
        }
    ) { Text("Acción: Deshacer (default)") }

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorInfo),
        onClick = {
            toastState.show(
                message = "Archivo guardado",
                type = ToastType.SUCCESS,
                actionLabel = "Ver",
                onAction = {}
            )
        }
    ) { Text("Acción: Ver (custom)") }
}

@Composable
private fun QueueToastSection(toastState: ToastState) {
    Text(text = "Queue Demo", style = MaterialTheme.typography.headlineSmall)

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            toastState.show("Primer toast en la cola", ToastType.SUCCESS)
            toastState.show("Segundo toast en la cola", ToastType.INFO)
            toastState.show("Tercer toast en la cola", ToastType.WARNING)
        }
    ) { Text("Mostrar 3 en cola") }
}
