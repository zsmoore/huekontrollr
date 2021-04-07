package com.zachary_moore.huekontrollr.util

import com.zachary_moore.huekontrollr.data.light.XY
import kotlin.math.pow

fun getFromRGB(
    r: Int,
    g: Int,
    b: Int
) : XY {
    require(r in 0..255)
    require(g in 0..255)
    require(b in 0..255)

    val rgbSet = arrayOf(
        r.toFloat(),
        g.toFloat(),
        b.toFloat()
    ).map {
        (it - 0) / (255 - 0)
    }.map(::gammaCorrect)

    val X = rgbSet[0] * 0.664511f + rgbSet[1] * 0.154324f + rgbSet[2] * 0.162028f
    val Y = rgbSet[0] * 0.283881f + rgbSet[1] * 0.668433f + rgbSet[2] * 0.047685f
    val Z = rgbSet[0] * 0.000088f + rgbSet[1] * 0.072310f + rgbSet[2] * 0.986039f

    var x = "%.4f".format(X / (X + Y + Z)).toFloat()
    var y = "%.4f".format(Y / (X + Y + Z)).toFloat()
    if (x.isNaN()) {
        x = 0f
    }

    if (y.isNaN()) {
        y = 0f
    }

    return XY(x,y)
}

private fun gammaCorrect(
    rgbVal: Float
): Float {
    return if (rgbVal > 0.04045f) {
        ((rgbVal + 0.055f) / (1.0f + 0.055f)).pow(2.4f)
    } else {
        rgbVal / 12.92f
    }
}
