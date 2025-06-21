package org.multiplatform.repbot

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Color as AndroidColor


@Composable
actual fun GlassBlurBox(
    modifier: Modifier,
    cornerRadius: Dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    // Tune this for overall translucency
    val baseAlpha = 0.05f // overall translucency of the glass surface
    val borderAlpha = 0.15f // opacity of the white border that outlines the glass
    val glossTopAlpha = 0.08f //brightness and strength of the top part of the glossy gradient overlay.
    val glossBottomAlpha = 0.02f //brightness and strength of the bottom part of the glossy gradient overlay.

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = baseAlpha))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = borderAlpha),
                shape = shape
            )
            .shadow(
                elevation = 0.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        setBackgroundColor(AndroidColor.TRANSPARENT)
                        setRenderEffect(
                            RenderEffect.createBlurEffect(
                                40f, 40f, Shader.TileMode.CLAMP
                            )
                        )
                    }
                },
                modifier = Modifier.matchParentSize()
            )
        } else {
            // Fallback for pre-Android 12
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }

        // Glossy gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = glossTopAlpha),
                            Color.White.copy(alpha = glossBottomAlpha)
                        )
                    )
                )
        )

        content()
    }
}


