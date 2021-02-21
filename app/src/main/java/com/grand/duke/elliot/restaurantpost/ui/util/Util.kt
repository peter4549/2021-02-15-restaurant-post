package com.grand.duke.elliot.restaurantpost.ui.util

import android.content.Context
import android.content.res.Resources
import android.text.Html
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.annotation.ColorInt
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

const val blank = ""

fun Long.toSimpleDateFormat(pattern: String): String = SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun convertDpToPx(context: Context, dp: Float)
        = dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun Int.isZero() = this == 0

fun Int.toDp(): Float {
    return this / Resources.getSystem().displayMetrics.density
}

fun Int.toPx(): Float {
    return this * Resources.getSystem().displayMetrics.density
}

fun Double.roundTo(place: Int): Double {
    val pow = 10.0.pow(place)
    return (this * pow).roundToInt() / pow
}

fun hashString(input: String, size: Int = 32, algorithm: String = "SHA-256"): String {
    return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { string, it -> string + "%02x".format(it) }).chunked(size)[0]
}

fun setTextWithSearchWordColorChange(
    textView: TextView, text: String,
    searchWord: String, @ColorInt color: Int?
) {
    if (color == null) {
        textView.text = text
        return
    }

    if (text.isBlank()) {
        textView.text = text
        return
    }

    val hexColor = color.toHexColor()
    val htmlText = text.replaceFirst(
        searchWord,
        "<font color='$hexColor'>$searchWord</font>"
    )
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        textView.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    else
        @Suppress("DEPRECATION")
        textView.text = Html.fromHtml(htmlText)
}

fun Post?.isNotNull() = this != null

fun <T> List<T>.difference(list: List<T>) =
        this.filterNot { list.contains(it) } to list.filterNot { this.contains(it) }