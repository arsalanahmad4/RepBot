package org.multiplatform.repbot

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun GlassBlurBox(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        setBackgroundColor(android.graphics.Color.argb(80, 255, 255, 255))
                        setRenderEffect(
                            RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
                        )
                    }
                },
                modifier = Modifier.matchParentSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }

        content()
    }
}
