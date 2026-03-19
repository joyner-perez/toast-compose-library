package com.joyner.toastcomposelibrary

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform