package com.joyner.toastcomposelibrary.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.joyner.toastcomposelibrary.toast.ToastCompose
import com.joyner.toastcomposelibrary.toast.ToastHost
import com.joyner.toastcomposelibrary.toast.ToastIcon
import com.joyner.toastcomposelibrary.toast.ToastNative
import com.joyner.toastcomposelibrary.toast.ToastNativeDuration
import com.joyner.toastcomposelibrary.toast.ToastState
import com.joyner.toastcomposelibrary.toast.ToastType
import com.joyner.toastcomposelibrary.toast.rememberToastNative
import com.joyner.toastcomposelibrary.toast.rememberToastState
import com.joyner.toastcomposelibrary.toast.show

private val ColorSuccess = Color(0xFF2E7D32)
private val ColorError = Color(0xFFC62828)
private val ColorInfo = Color(0xFF1565C0)
private val ColorWarning = Color(0xFFE65100)
private val ColorCustom = Color(0xFF6A1B9A)
private val ColorTeal = Color(0xFF00695C)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { SampleApp() }
    }
}

@Composable
private fun SampleApp() {
    MaterialTheme {
        val toastState = rememberToastState()
        val nativeToast = rememberToastNative()
        Scaffold(
            snackbarHost = { ToastHost(toastState = toastState, showProgressBar = true) }
        ) { innerPadding ->
            SampleContent(
                toastState = toastState,
                toastNative = nativeToast,
                modifier = Modifier.padding(paddingValues = innerPadding)
            )
        }
    }
}

@Composable
private fun SampleContent(
    toastState: ToastState,
    toastNative: ToastNative,
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
        BasicToastSection(toastState = toastState)

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Native Toast Demo", style = MaterialTheme.typography.headlineSmall)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { toastNative.show("Short native toast") }
        ) { Text("Show Native SHORT") }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { toastNative.show("Long native toast", ToastNativeDuration.LONG) }
        ) { Text("Show Native LONG") }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        CustomToastSection(toastState = toastState)

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        QueueToastSection(toastState = toastState)

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        ShowEffectSection(toastState = toastState)
    }
}

@Composable
private fun BasicToastSection(toastState: ToastState) {
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    var showError by rememberSaveable { mutableStateOf(false) }
    var showInfo by rememberSaveable { mutableStateOf(false) }
    var showWarning by rememberSaveable { mutableStateOf(false) }

    ToastCompose(
        toastState = toastState,
        condition = showSuccess,
        message = "Operation successful",
        type = ToastType.SUCCESS,
        onDismiss = { showSuccess = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = showError,
        message = "An error occurred",
        type = ToastType.ERROR,
        onDismiss = { showError = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = showInfo,
        message = "Important information",
        type = ToastType.INFO,
        onDismiss = { showInfo = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = showWarning,
        message = "Attention required",
        type = ToastType.WARNING,
        onDismiss = { showWarning = false }
    )

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
        onClick = { showSuccess = true }
    ) { Text("Show SUCCESS") }
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorError),
        onClick = { showError = true }
    ) { Text("Show ERROR") }
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorInfo),
        onClick = { showInfo = true }
    ) { Text("Show INFO") }
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorWarning),
        onClick = { showWarning = true }
    ) { Text("Show WARNING") }
}

@Composable
private fun CustomToastSection(toastState: ToastState) {
    val composePainter = painterResource(R.drawable.ic_compose_multiplatform)
    var showCustomVector by rememberSaveable { mutableStateOf(false) }
    var showCustomDrawable by rememberSaveable { mutableStateOf(false) }

    ToastCompose(
        toastState = toastState,
        condition = showCustomVector,
        message = "Custom icon, color and font",
        icon = ToastIcon.Vector(Icons.Filled.Star),
        backgroundColor = ColorCustom,
        textColor = ColorTeal,
        fontFamily = FontFamily.Cursive,
        onDismiss = { showCustomVector = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = showCustomDrawable,
        message = "Toast with drawable icon",
        icon = ToastIcon.Resource(composePainter),
        backgroundColor = ColorTeal,
        onDismiss = { showCustomDrawable = false }
    )

    Text(text = "Custom Toast Demo", style = MaterialTheme.typography.headlineSmall)
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorCustom),
        onClick = { showCustomVector = true }
    ) { Text("Show Custom Vector") }
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorTeal),
        onClick = { showCustomDrawable = true }
    ) { Text("Show Custom Drawable") }

    ActionToastSection(toastState = toastState)
}

@Composable
private fun ActionToastSection(toastState: ToastState) {
    var showActionUndo by rememberSaveable { mutableStateOf(false) }
    var showActionView by rememberSaveable { mutableStateOf(false) }

    ToastCompose(
        toastState = toastState,
        condition = showActionUndo,
        message = "Item deleted",
        type = ToastType.ERROR,
        onAction = {},
        onDismiss = { showActionUndo = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = showActionView,
        message = "File saved",
        type = ToastType.SUCCESS,
        actionLabel = "View",
        onAction = {},
        onDismiss = { showActionView = false }
    )

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorError),
        onClick = { showActionUndo = true }
    ) { Text("Action: Undo (default)") }
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorInfo),
        onClick = { showActionView = true }
    ) { Text("Action: View (custom)") }
}

@Composable
private fun QueueToastSection(toastState: ToastState) {
    var showQueue by rememberSaveable { mutableStateOf(false) }

    ToastCompose(
        toastState = toastState,
        condition = showQueue,
        message = "First toast in queue",
        type = ToastType.SUCCESS,
        onDismiss = { showQueue = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = showQueue,
        message = "Second toast in queue",
        type = ToastType.INFO
    )
    ToastCompose(
        toastState = toastState,
        condition = showQueue,
        message = "Third toast in queue",
        type = ToastType.WARNING
    )

    Text(text = "Queue Demo", style = MaterialTheme.typography.headlineSmall)
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { showQueue = true }
    ) { Text("Show 3 in queue") }
}

@Composable
private fun ShowEffectSection(toastState: ToastState) {
    var loginSuccess by rememberSaveable { mutableStateOf(false) }
    var uploadError by rememberSaveable { mutableStateOf(false) }

    ToastCompose(
        toastState = toastState,
        condition = loginSuccess,
        message = "Login successful!",
        type = ToastType.SUCCESS,
        onDismiss = { loginSuccess = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = uploadError,
        message = "Upload failed. Try again.",
        type = ToastType.ERROR,
        onDismiss = { uploadError = false }
    )

    Text(text = "ShowToast Demo", style = MaterialTheme.typography.headlineSmall)
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
        onClick = { loginSuccess = true }
    ) { Text("Simulate Login Success") }
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ColorError),
        onClick = { uploadError = true }
    ) { Text("Simulate Upload Error") }
}
