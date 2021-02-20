package com.grand.duke.elliot.restaurantpost.application.shared_preferences

import android.app.Application
import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.grand.duke.elliot.restaurantpost.R
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(context: Context) {

    private object Name {
        const val ThemeColor = "com.grand.duke.elliot.restaurantpost.application.shared_preferences" +
                ".shared_preferences.name.theme_color"
        const val Filter = "com.grand.duke.elliot.restaurantpost.application.shared_preferences" +
                ".shared_preferences.name.filter"
    }

    private object Key {
        const val PrimaryThemeColor = "com.grand.duke.elliot.restaurantpost.application.shared_preferences" +
                ".shared_preferences.key.primary_theme_color"
        const val CheckedTagIdSet = "com.grand.duke.elliot.restaurantpost.application.shared_preferences" +
                ".shared_preferences.key.checked_tag_id_set"
    }

    private val themeColorSharedPreferences = context.getSharedPreferences(Name.ThemeColor, Context.MODE_PRIVATE)
    private val filterSharedPreferences = context.getSharedPreferences(Name.Filter, Context.MODE_PRIVATE)

    private val defaultThemePrimaryColor = ContextCompat.getColor(context, R.color.default_theme_primary)

    fun putPrimaryThemeColor(@ColorInt color: Int) {
        themeColorSharedPreferences.edit().putInt(Key.PrimaryThemeColor, color).apply()
    }

    @ColorInt
    fun getPrimaryThemeColor(): Int =
        themeColorSharedPreferences.getInt(Key.PrimaryThemeColor, defaultThemePrimaryColor)

    fun putCheckedTagIdSet(checkedTagIdSet: Set<String>) {
        filterSharedPreferences.edit().putStringSet(Key.CheckedTagIdSet, checkedTagIdSet).apply()
    }

    fun getCheckedTagIdSet(): Set<String> =
            filterSharedPreferences.getStringSet(Key.CheckedTagIdSet, setOf()) ?: setOf()
}