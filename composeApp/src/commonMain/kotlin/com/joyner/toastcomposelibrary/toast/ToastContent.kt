package com.joyner.toastcomposelibrary.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import toastcomposelibrary.composeapp.generated.resources.Res
import toastcomposelibrary.composeapp.generated.resources.toast_undo_label
import kotlin.time.Clock

@Composable
internal fun ToastRenderer(
    toastState: ToastState,
    modifier: Modifier = Modifier,
    showProgressBar: Boolean = false,
    enter: EnterTransition = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit: ExitTransition = slideOutVertically(targetOffsetY = { it }) + fadeOut()
) {
    val toast = toastState.currentToast

    var toastStartTime by rememberSaveable { mutableLongStateOf(value = 0L) }

    val progress = remember(toast.message) {
        val elapsed = if (toastStartTime > 0L && toast.message.isNotBlank()) {
            Clock.System.now().toEpochMilliseconds() - toastStartTime
        } else {
            0L
        }
        val initialFraction = if (toast.durationMillis > 0L) {
            1f - (elapsed.toFloat() / toast.durationMillis.toFloat()).coerceIn(0f, 1f)
        } else {
            1f
        }
        Animatable(initialValue = initialFraction)
    }

    ToastAnimatedContent(
        toast = toast,
        toastState = toastState,
        modifier = modifier,
        showProgressBar = showProgressBar,
        enter = enter,
        exit = exit,
        progress = { progress.value }
    )

    LaunchedEffect(toast.message) {
        if (toast.message.isNotBlank()) {
            val now = Clock.System.now().toEpochMilliseconds()
            if (toastStartTime == 0L) {
                toastStartTime = now
                progress.snapTo(targetValue = 1f)
            }
            val remainingMs = (toast.durationMillis - (now - toastStartTime)).coerceAtLeast(0L)
            launch {
                progress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = remainingMs.toInt(),
                        easing = LinearEasing
                    )
                )
            }
            delay(timeMillis = remainingMs)
            toastState.dismiss()
        } else {
            toastStartTime = 0L
        }
    }
}

@Composable
private fun ToastAnimatedContent(
    toast: ToastData,
    toastState: ToastState,
    modifier: Modifier,
    showProgressBar: Boolean,
    enter: EnterTransition,
    exit: ExitTransition,
    progress: () -> Float
) {
    AnimatedVisibility(
        visible = toastState.isVisible(),
        enter = enter,
        exit = exit,
        modifier = modifier
    ) {
        key(toast.message) {
            val dismissState = rememberSwipeToDismissBoxState()
            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                    toastState.dismiss()
                }
            }
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(toast.customShape)
                            .background(toast.customBackgroundColor)
                    )
                }
            ) {
                ToastItem(
                    toast = toast,
                    onDismiss = toastState::dismiss,
                    showProgressBar = showProgressBar,
                    progress = progress
                )
            }
        }
    }
}

@Composable
internal fun ToastItemIcon(toast: ToastData) {
    Box(
        modifier = Modifier
            .size(toast.customIconSize)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        when (val icon = toast.customIcon) {
            is ToastIcon.Vector -> Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                tint = toast.customIconTint
            )

            is ToastIcon.Resource -> Icon(
                painter = icon.painter,
                contentDescription = null,
                tint = toast.customIconTint
            )
        }
    }
}

@Composable
internal fun ToastItem(
    toast: ToastData,
    onDismiss: () -> Unit,
    showProgressBar: Boolean,
    progress: () -> Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = toast.customShape)
            .background(toast.customBackgroundColor)
            .clickable(onClick = onDismiss)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToastItemIcon(toast = toast)

            Text(
                text = toast.message,
                color = toast.customTextColor,
                fontSize = if (toast.customFontSize == TextUnit.Unspecified) {
                    15.sp
                } else {
                    toast.customFontSize
                },
                fontWeight = toast.customFontWeight,
                fontFamily = toast.customFontFamily,
                modifier = Modifier.weight(1f)
            )

            if (toast.onAction != null) {
                Text(
                    text = toast.actionLabel.ifBlank {
                        stringResource(Res.string.toast_undo_label)
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clickable {
                            toast.onAction.invoke()
                            onDismiss()
                        }
                )
            }
        }

        if (showProgressBar) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = Color.White.copy(alpha = 0.7f),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}
