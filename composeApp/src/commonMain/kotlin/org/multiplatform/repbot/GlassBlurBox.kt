package org.multiplatform.repbot

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
expect fun GlassBlurBox(
    modifier: Modifier,
    cornerRadius: Dp,
    content: @Composable () -> Unit
)
