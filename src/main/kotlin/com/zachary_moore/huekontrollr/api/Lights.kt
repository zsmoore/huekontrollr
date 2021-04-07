package com.zachary_moore.huekontrollr.api

import com.zachary_moore.huekontrollr.data.Void
import com.zachary_moore.huekontrollr.data.light.Light
import com.zachary_moore.huekontrollr.data.light.State
import com.zachary_moore.huekontrollr.internal.BaseRequest
import com.zachary_moore.huekontrollr.internal.SubApiRequest
import com.zachary_moore.huekontrollr.internal.batchGet
import com.zachary_moore.huekontrollr.internal.put

private const val subAPIRoute = "lights"

class Lights internal constructor(
    baseRequest: BaseRequest
) {

    private val lightsRequest = SubApiRequest(baseRequest, subAPIRoute)

    fun getAll(onResponse: (Map<Int, Light>) -> Unit) {
        batchGet(lightsRequest, onResponse)
    }

    fun putState(
        lightId: Int,
        state: State
    ) {
        put<State, Void>(lightsRequest.getRequest("$lightId/state"), state)
    }
}