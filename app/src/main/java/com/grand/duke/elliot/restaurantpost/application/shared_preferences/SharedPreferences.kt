package com.grand.duke.elliot.restaurantpost.application.shared_preferences

import android.app.Application
import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.grand.duke.elliot.restaurantpost.R
import javax.inject.Inject

class SharedPreferences @Inject constructor(context: Context) {

    private object Name {
        const val ThemeColor = "com.grand.duke.elliot.restaurantpost.application.shared_preferences" +
                ".SharedPreferences.Name.ThemeColor"
    }

    private object Key {
        const val PrimaryThemeColor = "com.grand.duke.elliot.restaurantpost.application.shared_preferences" +
                ".SharedPreferences.Key.PrimaryThemeColor"
    }

    private val themeColorSharedPreferences = context.getSharedPreferences(Name.ThemeColor, Context.MODE_PRIVATE)
    private val defaultThemePrimaryColor = ContextCompat.getColor(context, R.color.default_theme_primary)

    fun putPrimaryThemeColor(@ColorInt color: Int) {
        themeColorSharedPreferences.edit().putInt(Key.PrimaryThemeColor, color).apply()
    }

    @ColorInt
    fun getPrimaryThemeColor(): Int =
        themeColorSharedPreferences.getInt(Key.PrimaryThemeColor, defaultThemePrimaryColor)
}