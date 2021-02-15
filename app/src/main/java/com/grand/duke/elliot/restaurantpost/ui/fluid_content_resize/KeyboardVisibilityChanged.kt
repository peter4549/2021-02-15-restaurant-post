package com.grand.duke.elliot.restaurantpost.ui.fluid_content_resize

data class KeyboardVisibilityChanged(
    val visible: Boolean,
    val contentViewHeight: Int,
    val previousContentViewHeight: Int
)