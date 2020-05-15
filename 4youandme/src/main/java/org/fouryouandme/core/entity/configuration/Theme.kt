package org.fouryouandme.core.entity.configuration

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import androidx.annotation.ColorInt
import org.fouryouandme.R
import kotlin.math.roundToInt

data class HEXColor(val hex: String) {

    @ColorInt
    fun color(): Int = Color.parseColor(hex)
}

fun String.toHEXColor(): HEXColor =
    HEXColor(this)

data class HEXGradient(val primaryHex: String, val secondaryHex: String) {

    fun drawable(): GradientDrawable {

        val gd =
            GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor(primaryHex),
                    Color.parseColor(secondaryHex)
                )
            )
        gd.cornerRadius = 0f
        return gd
    }

    companion object {

        fun from(start: HEXColor, end: HEXColor): HEXGradient =
            HEXGradient(
                start.hex,
                end.hex
            )

    }
}

data class Theme(
    val primaryColorStart: HEXColor,
    val primaryColorEnd: HEXColor,
    val secondaryColor: HEXColor,
    val tertiaryColorStart: HEXColor,
    val tertiaryColorEnd: HEXColor,
    val primaryTextColor: HEXColor,
    val secondaryTextColor: HEXColor,
    val tertiaryTextColor: HEXColor,
    val fourthTextColor: HEXColor,
    val primaryMenuColor: HEXColor,
    val secondaryMenuColor: HEXColor,
    val activeColor: HEXColor,
    val deactiveColor: HEXColor
)