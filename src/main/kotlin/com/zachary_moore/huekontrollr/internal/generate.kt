package com.zachary_moore.huekontrollr.internal

import com.zachary_moore.huekontrollr.data.DataGenerator
import com.zachary_moore.huekontrollr.data.HueData
import com.zachary_moore.huekontrollr.data.light.Light
import com.zachary_moore.huekontrollr.data.light.State
import com.zachary_moore.huekontrollr.data.light.XY
import mu.KotlinLogging
import org.json.JSONArray
import org.json.JSONObject

private val LIGHT_DESERIALIZER = LightDeserializer()
private val STATE_DESERIALIZER = StateDeserializer()
private val XY_DESERIALIZER = XYDeserializer()
private val IP_DISCOVERY_RESPONSE_DESERIALIZER = IpDiscoveryResponseDeserializer()
private val DEVICE_DESERIALIZER = DeviceDeserializer()
private val USER_CREATE_BODY_DESERIALIZER = UserCreateBodyDeserializer()
private val USER_CREATE_RESPONSE_DESERIALIZER = UserCreateResponseDeserializer()

private val logger = KotlinLogging.logger {}

internal inline fun <reified T : HueData<T>> generate(
    input: Any
): T {
    return when (T::class) {
        Light::class -> LIGHT_DESERIALIZER.process(input)
        State::class -> STATE_DESERIALIZER.process(input)
        XY::class -> XY_DESERIALIZER.process(input)
        IpDiscoveryResponse::class -> IP_DISCOVERY_RESPONSE_DESERIALIZER.process(input)
        Device::class -> DEVICE_DESERIALIZER.process(input)
        UserCreateBody::class -> USER_CREATE_BODY_DESERIALIZER.process(input)
        UserCreateResponse::class -> USER_CREATE_RESPONSE_DESERIALIZER.process(input)
        else -> null
    } as T
}

internal fun <T : HueData<T>> formatForPost(
    input: T
): Any? {
    return removeNulls(
        when (input) {
            is Light -> LIGHT_DESERIALIZER.formatForPost(input)
            is State -> STATE_DESERIALIZER.formatForPost(input)
            is XY -> XY_DESERIALIZER.formatForPost(input)
            is IpDiscoveryResponse -> IP_DISCOVERY_RESPONSE_DESERIALIZER.formatForPost(input)
            is Device -> DEVICE_DESERIALIZER.formatForPost(input)
            is UserCreateBody -> USER_CREATE_BODY_DESERIALIZER.formatForPost(input)
            is UserCreateResponse -> USER_CREATE_RESPONSE_DESERIALIZER.formatForPost(input)
            else -> null
        }
    )
}

private fun removeNulls(mapOrArray: Any?): Any? {
    return when (mapOrArray) {
        is Map<*, *> -> {
            mapOrArray.mapValues { entry ->
                if (isMapOrArray(entry.value)) {
                    removeNulls(entry.value)
                } else {
                    entry.value
                }
            }.filterValues {
                it != null
            }
        }
        is Array<*> -> {
            mapOrArray.mapNotNull { value ->
                if (isMapOrArray(value)) {
                    removeNulls(value)
                } else {
                    value
                }
            }
        }
        else -> null
    }
}

private fun isMapOrArray(any: Any?) = any is Map<*, *> || any is Array<*>

// Light API
internal class LightDeserializer : DataGenerator<Light> {
    override fun fromJsonObject(jsonObject: JSONObject) = Light(
        STATE_DESERIALIZER.process(jsonObject.get("state"))
    )

    override fun formatForPost(toFormat: Light?): Any? {
        return toFormat?.let { light ->
            mapOf(
                "state" to STATE_DESERIALIZER.formatForPost(light.state)
            )
        }
    }
}

internal class StateDeserializer : DataGenerator<State> {
    override fun fromJsonObject(jsonObject: JSONObject) = State(
        jsonObject.getBoolean("on"),
        jsonObject.getInt("bri"),
        jsonObject.getInt("hue"),
        jsonObject.getInt("sat"),
        jsonObject.getString("effect"),
        XY_DESERIALIZER.process(jsonObject.getJSONArray("xy")),
        jsonObject.getInt("ct"),
        jsonObject.getString("alert"),
        jsonObject.getString("colormode"),
        jsonObject.getString("mode"),
        jsonObject.getBoolean("reachable")
    )

    override fun formatForPost(toFormat: State?): Any? {
        return toFormat?.let { state ->
            mapOf(
                "on" to state.on,
                "bri" to state.brightness,
                "hue" to state.hue,
                "sat" to state.saturation,
                "effect" to state.effect,
                "xy" to XY_DESERIALIZER.formatForPost(state.xy),
                "ct" to state.colorTemperature,
                "alert" to state.alert,
                "colormode" to state.colorMode,
                "mode" to state.mode,
                "reachable" to state.reachable
            )
        }
    }
}

internal class XYDeserializer : DataGenerator<XY> {
    override fun fromJsonArray(jsonArray: JSONArray) = XY(
        jsonArray.getDouble(0).toFloat(),
        jsonArray.getDouble(1).toFloat()
    )

    override fun formatForPost(toFormat: XY?): Any? {
        return toFormat?.let { xy ->
            arrayOf(
                xy.x,
                xy.y
            )
        }
    }
}

// Login Api
internal class IpDiscoveryResponseDeserializer : DataGenerator<IpDiscoveryResponse> {
    override fun fromJsonArray(jsonArray: JSONArray): IpDiscoveryResponse {
        val devices = mutableListOf<Device>()
        for (i in 0 until jsonArray.length()) {
            devices.add(DEVICE_DESERIALIZER.process(jsonArray.getJSONObject(i)))
        }
        return IpDiscoveryResponse(devices)
    }

    override fun formatForPost(toFormat: IpDiscoveryResponse?): Any? {
        return toFormat?.let { ipDiscoveryResponse ->
            JSONArray(ipDiscoveryResponse.devices.map(DEVICE_DESERIALIZER::formatForPost))
        }
    }
}

internal class DeviceDeserializer : DataGenerator<Device> {
    override fun fromJsonObject(jsonObject: JSONObject) = Device(
        jsonObject.getString("id"),
        jsonObject.getString("internalipaddress")
    )

    override fun formatForPost(toFormat: Device?): Any? {
        return toFormat?.let { device ->
            mapOf(
                "id" to device.id,
                "internalipaddress" to device.internalIpAddress
            )
        }
    }
}

internal class UserCreateBodyDeserializer: DataGenerator<UserCreateBody> {
    override fun fromJsonObject(jsonObject: JSONObject) = UserCreateBody(
        jsonObject.getString("devicetype").split("#")[0],
        jsonObject.getString("devicetype").split("#")[1]
    )

    override fun formatForPost(toFormat: UserCreateBody?): Any? {
        return toFormat?.let { userCreateBody ->
            mapOf(
                "devicetype" to "${userCreateBody.applicationName}#${userCreateBody.deviceName}"
            )
        }
    }
}

internal class UserCreateResponseDeserializer: DataGenerator<UserCreateResponse> {
    override fun fromJsonArray(jsonArray: JSONArray): UserCreateResponse? {
        return try {
            jsonArray.getJSONObject(0)
                ?.getJSONObject("success")
                ?.getString("username")
                ?.let(::UserCreateResponse)
        } catch (e: Exception) {
            logger.error(e) {
                "Unable to get user response from user create.  Did you click your Hue bridge?"
            }
            null
        }
    }

    override fun formatForPost(toFormat: UserCreateResponse?): Any? {
        return toFormat?.let { userCreateResponse ->
            mapOf(
                "username" to userCreateResponse.username
            )
        }
    }
}