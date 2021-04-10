package com.zachary_moore.huekontrollr.data.light

import com.zachary_moore.huekontrollr.data.HueData

/**
 * Represents a Hue light.
 *
 * Example response which this data class is based off of
 * located [here](https://developers.meethue.com/develop/hue-api/lights-api/#get-all-lights)
 */
data class Light(
    val state: State?
) : HueData<Light>

/**
 * Represents the current state of a [Light] as referenced in the
 * example output response from the (hue documentation)[https://developers.meethue.com/develop/hue-api/lights-api/#get-all-lights]
 */
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

/**
 * Represents XY color values as described
 * [here](https://developers.meethue.com/develop/get-started-2/core-concepts/#colors-get-more-complicated)
 */
data class XY(
    val x: Float,
    val y: Float
) : HueData<XY>