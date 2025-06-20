package org.multiplatform.repbot

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun GlassBlurBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
