package com.zachary_moore.huekontrollr.data

import org.json.JSONArray
import org.json.JSONObject
import java.lang.UnsupportedOperationException

internal interface HueData<T : HueData<T>>

internal interface Void: HueData<Void>

internal interface DataGenerator<T: HueData<T>>: FromJSON<T>, ToPostData<T> {
    fun process(input: Any): T {
        return when (input) {
            is JSONObject -> fromJsonObject(input)
            is JSONArray -> fromJsonArray(input)
            else -> null
        } ?: throw UnsupportedOperationException("Unable to get a response from input data")
    }
}

internal interface ToPostData<T: HueData<T>> {

    fun formatForPost(toFormat: T?): Any?
}

internal interface FromJSON<T: HueData<T>> {

    fun fromJsonObject(jsonObject: JSONObject): T? = null

    fun fromJsonArray(jsonArray: JSONArray): T? = null
}
