package com.zachary_moore.huekontrollr.api

import com.zachary_moore.huekontrollr.data.Void
import com.zachary_moore.huekontrollr.data.light.Light
import com.zachary_moore.huekontrollr.data.light.State
import com.zachary_moore.huekontrollr.internal.BaseRequest
import com.zachary_moore.huekontrollr.internal.SubApiRequest
import com.zachary_moore.huekontrollr.internal.batchGet
import com.zachary_moore.huekontrollr.internal.put

private const val subAPIRoute = "lights"

/**
 * Holds functionality related to the Lights API for hue.
 * For detailed API information see [Lights API](https://developers.meethue.com/develop/hue-api/lights-api/)
 */
class Lights internal constructor(
    baseRequest: BaseRequest
) {

    private val lightsRequest = SubApiRequest(baseRequest, subAPIRoute)

    /**
     * Batch get response of identifier to [Light] for each [Light] available in the system
     */
    fun getAll(onResponse: (Map<Int, Light>) -> Unit) {
        batchGet(lightsRequest, onResponse)
    }

    /**
     * Perform a partial update on a [Light]'s [State] object.
     * Any non-set (null) values in the [State] object will be filtered out
     */
    fun putState(
        lightId: Int,
        state: State
    ) {
        put<State, Void>(lightsRequest.getRequest("$lightId/state"), state)
    }
}