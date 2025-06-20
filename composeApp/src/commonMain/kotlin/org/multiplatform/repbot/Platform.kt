package org.multiplatform.repbot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform