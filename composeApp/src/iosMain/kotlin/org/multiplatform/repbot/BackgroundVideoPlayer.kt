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
    // Try to find the video in the bundle with the exact name first
    println("iOS: Trying to find video with exact name: $videoAssetName")
    var url = NSBundle.mainBundle.URLForResource(
        name = videoAssetName,
        withExtension = null
    )

    // If not found, try with the name without extension and explicit mp4 extension
    if (url == null) {
        println("iOS: Trying with name without extension: ${videoAssetName.substringBeforeLast(".")} and extension mp4")
        url = NSBundle.mainBundle.URLForResource(
            name = videoAssetName.substringBeforeLast("."),
            withExtension = "mp4"
        )
    }

    // If still not found, try looking in different directories
    if (url == null) {
        println("iOS: Trying in videos directory with exact name: videos/$videoAssetName")
        url = NSBundle.mainBundle.URLForResource(
            name = "videos/${videoAssetName}",
            withExtension = null
        )
    }

    // One more attempt with videos directory and explicit extension
    if (url == null) {
        println("iOS: Trying in videos directory with name without extension: videos/${videoAssetName.substringBeforeLast(".")} and extension mp4")
        url = NSBundle.mainBundle.URLForResource(
            name = "videos/${videoAssetName.substringBeforeLast(".")}",
            withExtension = "mp4"
        )
    }

    // Try in compose-resources directory
    if (url == null) {
        println("iOS: Trying in compose-resources directory with exact name: compose-resources/$videoAssetName")
        url = NSBundle.mainBundle.URLForResource(
            name = "compose-resources/${videoAssetName}",
            withExtension = null
        )
    }

    // Try in compose-resources directory with explicit extension
    if (url == null) {
        println("iOS: Trying in compose-resources directory with name without extension: compose-resources/${videoAssetName.substringBeforeLast(".")} and extension mp4")
        url = NSBundle.mainBundle.URLForResource(
            name = "compose-resources/${videoAssetName.substringBeforeLast(".")}",
            withExtension = "mp4"
        )
    }

    // Try in compose-resources/videos directory
    if (url == null) {
        println("iOS: Trying in compose-resources/videos directory with exact name: compose-resources/videos/$videoAssetName")
        url = NSBundle.mainBundle.URLForResource(
            name = "compose-resources/videos/${videoAssetName}",
            withExtension = null
        )
    }

    // Try in compose-resources/videos directory with explicit extension
    if (url == null) {
        println("iOS: Trying in compose-resources/videos directory with name without extension: compose-resources/videos/${videoAssetName.substringBeforeLast(".")} and extension mp4")
        url = NSBundle.mainBundle.URLForResource(
            name = "compose-resources/videos/${videoAssetName.substringBeforeLast(".")}",
            withExtension = "mp4"
        )
    }

    if (url != null) {
        println("iOS: Successfully found video at: ${url.absoluteString}")
    } else {
        println("iOS: Failed to find video: $videoAssetName")
    }

    if(url == null)
        return
    // Create a mutable state to track if the player is ready
    val isPlayerReady = remember { mutableStateOf(false) }

    val player = remember {
        AVPlayer.playerWithURL(url!!).apply {
            actionAtItemEnd = AVPlayerActionAtItemEndNone
            println("iOS: Creating player with URL: ${url.absoluteString}")
            println("iOS: Player status: ${this.status}")

            // Don't play immediately, wait until we've verified the item is ready
        }
    }

    val playerLayer = remember {
        AVPlayerLayer.playerLayerWithPlayer(player).apply {
            videoGravity = AVLayerVideoGravityResize // Changed to Resize for full screen without preserving aspect ratio
            // Make sure the layer is visible and opaque
            setOpaque(true)
            setOpacity(1.0f)
        }
    }

    UIKitView(
        factory = {
            UIView().apply {
                backgroundColor = UIColor.blackColor
                layer.addSublayer(playerLayer)

                // Ensure the view is not hidden and has proper dimensions
                setHidden(false)
                setClipsToBounds(true)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            // Ensure the player layer frame is set correctly to fill the entire view
            playerLayer.frame = view.bounds

            // Set the video gravity to ensure it fills the entire screen without preserving aspect ratio
            playerLayer.videoGravity = AVLayerVideoGravityResize

            playerLayer.setNeedsLayout()
            playerLayer.setNeedsDisplay()

            // If player is ready, start playback
            if (isPlayerReady.value) {
                player.play()
            }
        }
    )


    LaunchedEffect(player) {
        // Add observer for player item status changes
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = null
        ) { _ ->
            println("iOS: Video reached end, restarting playback")
            player.seekToTime(CMTimeMakeWithSeconds(0.0, 1))
            player.play()
        }

        // Add observer for player errors
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemFailedToPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = null
        ) { notification ->
            val error = notification?.userInfo?.get(AVPlayerItemFailedToPlayToEndTimeErrorKey)
            println("iOS: Error playing video: $error")
        }

        // Log initial player item status
        println("iOS: Initial player item status: ${player.currentItem?.status}")

        // Check if the player item is ready to play
        if (player.currentItem?.status == AVPlayerItemStatusReadyToPlay) {
            println("iOS: Player item is ready to play")
            isPlayerReady.value = true
            player.play()
        } else {
            println("iOS: Player item is not ready to play, status: ${player.currentItem?.status}")

            // Add a delay and check again
            kotlinx.coroutines.delay(500)
            println("iOS: Checking player item status again after delay")
            if (player.currentItem?.status == AVPlayerItemStatusReadyToPlay) {
                println("iOS: Player item is now ready to play after delay")
                isPlayerReady.value = true
                player.play()
            } else {
                println("iOS: Player item is still not ready after delay, status: ${player.currentItem?.status}")

                // Force play anyway after a delay
                kotlinx.coroutines.delay(1000)
                println("iOS: Forcing playback after delay")
                isPlayerReady.value = true
                player.play()
            }
        }
    }
}
