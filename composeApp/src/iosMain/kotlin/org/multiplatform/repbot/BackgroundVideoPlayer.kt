package org.multiplatform.repbot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.*
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun BackgroundVideoPlayer(videoAssetName: String) {
    val url = NSBundle.mainBundle.URLForResource(
        name = videoAssetName.substringBeforeLast("."),
        withExtension = "mp4"
    )

    if(url == null)
        return
    val player = remember {
        AVPlayer.playerWithURL(url!!).apply {
            actionAtItemEnd = AVPlayerActionAtItemEndNone
            play()
        }
    }

    val playerLayer = remember {
        AVPlayerLayer.playerLayerWithPlayer(player).apply {
            videoGravity = AVLayerVideoGravityResizeAspectFill
        }
    }

    UIKitView(
        factory = {
            UIView().apply {
                backgroundColor = UIColor.blackColor
                layer.addSublayer(playerLayer)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            playerLayer.frame = view.bounds
            playerLayer.setNeedsDisplay()
        }
    )


    LaunchedEffect(player) {
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = null
        ) { _ ->
            player.seekToTime(CMTimeMakeWithSeconds(0.0, 1))
            player.play()
        }
    }
}

