package org.multiplatform.repbot

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.UIKit.*

@Composable
actual fun GlassBlurBox(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        UIKitView(
            factory = {
                UIVisualEffectView(effect = UIBlurEffect.effectWithStyle(UIBlurEffectStyle.UIBlurEffectStyleSystemMaterial)).apply {
                    backgroundColor = UIColor.whiteColor.colorWithAlphaComponent(0.3)
                }
            },
            modifier = Modifier.matchParentSize()
        )

        content()
    }
}

