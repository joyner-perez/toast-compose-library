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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
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

/**
 * Standalone toast composable managed by [toastState].
 *
 * Visibility is handled internally: the toast appears when [ToastState.show] is called,
 * auto-dismisses after [ToastData.durationMillis], and can also be dismissed by tapping it
 * or swiping it horizontally.
 *
 * **Option 1 — without Scaffold**, place it inside a [Box] and align it manually:
 * ```kotlin
 * val toastState = rememberToastState()
 *
 * Box(modifier = Modifier.fillMaxSize()) {
 *     MyScreenContent(
 *         onSuccess = { toastState.show("Guardado", ToastType.SUCCESS) }
 *     )
 *     ToastCompose(
 *         toastState = toastState,
 *         modifier = Modifier
 *             .align(Alignment.BottomCenter)
 *             .navigationBarsPadding()
 *             .padding(horizontal = 16.dp, vertical = 24.dp)
 *     )
 * }
 * ```
 *
 * **Option 2 — with Scaffold**, use [ToastHost] in the `snackbarHost` slot:
 * ```kotlin
 * Scaffold(
 *     snackbarHost = { ToastHost(toastState = toastState) }
 * ) { innerPadding ->
 *     MyScreenContent(modifier = Modifier.padding(innerPadding))
 * }
 * ```
 *
 * @param toastState State that controls what message is shown and when to dismiss.
 * Use [rememberToastState] to create and remember an instance.
 * @param modifier Optional [Modifier] to control size and position of the toast.
 * @param showProgressBar When `true`, a thin progress bar is shown at the bottom of the toast
 * indicating the remaining display time.
 * @param enter Transition used when the toast becomes visible. Defaults to slide-up + fade-in.
 * @param exit Transition used when the toast is dismissed. Defaults to slide-down + fade-out.
 */
@Composable
fun ToastCompose(
    toastState: ToastState,
    modifier: Modifier = Modifier,
    showProgressBar: Boolean = false,
    enter: EnterTransition = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit: ExitTransition = slideOutVertically(targetOffsetY = { it }) + fadeOut()
) {
    val toast = toastState.currentToast
    val progress = remember { Animatable(initialValue = 1f) }

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
                    progress = { progress.value }
                )
            }
        }
    }

    LaunchedEffect(toast.message) {
        if (toast.message.isNotBlank()) {
            progress.snapTo(targetValue = 1f)
            launch {
                progress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = toast.durationMillis.toInt(),
                        easing = LinearEasing
                    )
                )
            }
            delay(timeMillis = toast.durationMillis)
            toastState.dismiss()
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
