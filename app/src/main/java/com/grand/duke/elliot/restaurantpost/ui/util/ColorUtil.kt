package com.grand.duke.elliot.restaurantpost.ui.util

fun Int.toHexColor() = String.format("#%06X", 0xFFFFFF and this)