package com.joyner.toastcomposelibrary.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptionCurveEaseOut

private const val ToastShortDurationSeconds = 2.0
private const val ToastLongDurationSeconds = 3.5
private const val ToastFadeInDuration = 0.3
private const val ToastFadeOutDuration = 0.3
private const val ToastBackgroundAlpha = 0.75
private const val ToastWidthRatio = 0.8
private const val ToastHeight = 48.0
private const val ToastCornerRadius = 8.0
private const val ToastFontSize = 14.0
private const val ToastBottomOffset = 80.0

@Immutable
actual class ToastNative {
    @OptIn(ExperimentalForeignApi::class)
    actual fun show(message: String, duration: ToastNativeDuration) {
        val delaySeconds =
            if (duration == ToastNativeDuration.LONG) {
                ToastLongDurationSeconds
            } else {
                ToastShortDurationSeconds
            }

        val keyWindow = UIApplication.sharedApplication.keyWindow ?: return

        val windowWidth = keyWindow.frame.useContents { size.width }
        val windowHeight = keyWindow.frame.useContents { size.height }
        val toastWidth = windowWidth * ToastWidthRatio
        val toastX = (windowWidth - toastWidth) / 2.0
        val toastY = windowHeight - ToastHeight - ToastBottomOffset

        val toastLabel = UILabel(frame = CGRectMake(toastX, toastY, toastWidth, ToastHeight))
        toastLabel.backgroundColor =
            UIColor.blackColor.colorWithAlphaComponent(ToastBackgroundAlpha)
        toastLabel.textColor = UIColor.whiteColor
        toastLabel.textAlignment = NSTextAlignmentCenter
        toastLabel.text = message
        toastLabel.alpha = 0.0
        toastLabel.layer.cornerRadius = ToastCornerRadius
        toastLabel.clipsToBounds = true
        toastLabel.numberOfLines = 0
        toastLabel.font = UIFont.systemFontOfSize(ToastFontSize)

        keyWindow.addSubview(toastLabel)

        UIView.animateWithDuration(ToastFadeInDuration) { toastLabel.alpha = 1.0 }

        UIView.animateWithDuration(
            duration = ToastFadeOutDuration,
            delay = delaySeconds,
            options = UIViewAnimationOptionCurveEaseOut,
            animations = { toastLabel.alpha = 0.0 },
            completion = { _ -> toastLabel.removeFromSuperview() }
        )
    }
}

@Composable
actual fun rememberToastNative(): ToastNative = remember { ToastNative() }
