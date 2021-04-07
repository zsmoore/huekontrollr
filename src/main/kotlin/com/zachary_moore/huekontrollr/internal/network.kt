package com.zachary_moore.huekontrollr.internal

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import com.zachary_moore.huekontrollr.data.HueData
import mu.KotlinLogging
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private val logger = KotlinLogging.logger {}

internal inline fun <reified T : HueData<T>> get(
    request: Request,
    crossinline onResponse: (T) -> Unit
) {
    request.getUrl().httpGet().responseString { _, _, result ->
        when (result) {
            is Result.Success -> {
                val data = try {
                    JSONObject(result.get())
                } catch (jsonException: JSONException) {
                    JSONArray(result.get())
                }

                onResponse.invoke(generate(data))
            }
            is Result.Failure -> logger.error(result.getException()) {
                "Unhandled network exception"
            }
        }
    }.join()
}

internal inline fun <reified T : HueData<T>> batchGet(
    request: Request,
    crossinline onResponse: (Map<Int, T>) -> Unit
) {
    request.getUrl().httpGet().responseString { _, _, result ->
        when (result) {
            is Result.Success -> {
                val data = JSONObject(result.get())
                onResponse(
                    data.keySet().associateWith {
                        generate<T>(data.getJSONObject(it))
                    }.mapKeys {
                        it.key.toInt()
                    }
                )
            }
            is Result.Failure -> logger.error(result.getException()) {
                "Unhandled network exception"
            }
        }
    }.join()
}

internal inline fun <reified T : HueData<T>, reified R : HueData<R>> put(
    request: Request,
    data: T,
    noinline onResponse: ((R) -> Unit)? = null
) {
    val jsonBody = if (formatForPost(data) is Map<*, *>) {
        JSONObject(formatForPost(data) as Map<*, *>).toString()
    } else {
        JSONArray(formatForPost(data) as List<*>).toString()
    }
    request.getUrl().httpPut().jsonBody(
        jsonBody
    ).responseString { _, _, result ->
        when (result) {
            is Result.Success -> {
                val responseData = try {
                    JSONObject(result.get())
                } catch (jsonException: JSONException) {
                    JSONArray(result.get())
                }

                onResponse?.invoke(generate(responseData))
            }
            is Result.Failure -> logger.error(result.getException()) {
                "Unhandled network exception"
            }
        }
    }.join()
}

internal inline fun <reified T : HueData<T>, reified R : HueData<R>> post(
    request: Request,
    data: T,
    noinline onResponse: ((R) -> Unit)? = null
) {
    val jsonBody = if (formatForPost(data) is Map<*, *>) {
        JSONObject(formatForPost(data) as Map<*, *>).toString()
    } else {
        JSONArray(formatForPost(data) as List<*>).toString()
    }
    request.getUrl().httpPost().jsonBody(
        jsonBody
    ).responseString { _, _, result ->
        when (result) {
            is Result.Success -> {
                val responseData = try {
                    JSONObject(result.get())
                } catch (jsonException: JSONException) {
                    JSONArray(result.get())
                }

                onResponse?.invoke(generate(responseData))
            }
            is Result.Failure -> logger.error(result.getException()) {
                "Unhandled network exception"
            }
        }
    }.join()
}