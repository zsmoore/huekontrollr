package com.zachary_moore.huekontrollr.data.light

import com.zachary_moore.huekontrollr.data.HueData

data class Light(
    val state: State?
) : HueData<Light>

data class State(
    val on: Boolean? = null,
    val brightness: Int? = null,
    val hue: Int? = null,
    val saturation: Int? = null,
    val effect: String? = null,
    val xy: XY? = null,
    val colorTemperature: Int? = null,
    val alert: String? = null,
    val colorMode: String? = null,
    val mode: String? = null,
    val reachable: Boolean? = null
) : HueData<State>

data class XY(
    val x: Float,
    val y: Float
) : HueData<XY>