package com.zachary_moore.huekontrollr

import com.zachary_moore.huekontrollr.internal.BaseRequest
import com.zachary_moore.huekontrollr.api.Lights
import com.zachary_moore.huekontrollr.internal.Login

class Kontrollr private constructor(
    val bridgeIpAddress: String,
    val userName: String
) {

    private val baseRequest = BaseRequest(bridgeIpAddress, userName)
    val lights = Lights(baseRequest)

    companion object {
        private val LOGIN: Login = Login()
        fun createFromIpAndUser(
            bridgeIpAddress: String,
            userName: String
        ): Kontrollr = Kontrollr(bridgeIpAddress, userName)

        fun createWithIpAndAutoCreateUsername(
            bridgeIpAddress: String,
            applicationName: String,
            deviceName: String,
            onKontrollrCreated: (Kontrollr) -> Unit
        ) {
            LOGIN.createUser(applicationName, deviceName, bridgeIpAddress) { userCreateResponse ->
                onKontrollrCreated(Kontrollr(bridgeIpAddress, userCreateResponse.username))
            }

        }
        fun createWithAutoIpAndUsername(
            applicationName: String,
            deviceName: String,
            onKontrollrCreated: (Kontrollr) -> Unit
        ) {
            LOGIN.getAllDevices { ipDiscoveryResponse ->
                val bridgeToUse = ipDiscoveryResponse.devices[0]
                LOGIN.createUser(applicationName, deviceName, bridgeToUse.internalIpAddress) { userCreateResponse ->
                    onKontrollrCreated(Kontrollr(bridgeToUse.internalIpAddress, userCreateResponse.username))
                }
            }
        }
    }
}