package com.zachary_moore.huekontrollr.internal

import com.zachary_moore.huekontrollr.data.HueData

internal class Login {

    internal fun getAllDevices(onResponse: (IpDiscoveryResponse) -> Unit) {
        get({ DISCOVERY_URL }, onResponse)
    }

    internal fun createUser(
        applicationName: String,
        deviceName: String,
        ipAddress: String,
        onUserCreateResponse: (UserCreateResponse) -> Unit
    ) {
        post(
            { "http://${ipAddress}/${ROOT}" },
            UserCreateBody(applicationName, deviceName),
            onUserCreateResponse
        )
    }

    private companion object {
        private const val DISCOVERY_URL = "https://discovery.meethue.com/"
    }
}

internal data class IpDiscoveryResponse(
    val devices: List<Device>
) : HueData<IpDiscoveryResponse>

internal data class Device(
    val id: String,
    val internalIpAddress: String
) : HueData<Device>

internal data class UserCreateBody(
    val applicationName: String,
    val deviceName: String
) : HueData<UserCreateBody>

internal data class UserCreateResponse(
    val username: String
) : HueData<UserCreateResponse>