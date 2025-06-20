package org.multiplatform.repbot

import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.AssetDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
actual fun BackgroundVideoPlayer(videoAssetName: String) {
    val context = LocalContext.current
    val videoUri = remember(videoAssetName) {
        Uri.parse("asset:///$videoAssetName")
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val dataSpec = DataSpec(videoUri)
            val assetDataSource = AssetDataSource(context)
            assetDataSource.open(dataSpec)

            val factory = DataSource.Factory { assetDataSource }
            val mediaItem = MediaItem.fromUri(videoUri)

            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                useController = false
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
}
