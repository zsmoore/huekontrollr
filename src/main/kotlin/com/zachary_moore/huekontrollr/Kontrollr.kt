package com.zachary_moore.huekontrollr

import com.zachary_moore.huekontrollr.api.Lights
import com.zachary_moore.huekontrollr.internal.BaseRequest
import com.zachary_moore.huekontrollr.internal.Login

/**
 * Main class to interact with Hue apis
 */
class Kontrollr private constructor(
    val bridgeIpAddress: String,
    val userName: String
) {

    private val baseRequest = BaseRequest(bridgeIpAddress, userName)

    /**
     * [Lights] api configured with this current instance's [bridgeIpAddress] and [userName]
     */
    val lights = Lights(baseRequest)

    companion object {
        private val LOGIN: Login = Login()

        /**
         * Create a [Kontrollr] configured with the given [bridgeIpAddress] and [userName].
         *
         * This requires that you have discovered the [bridgeIpAddress] yourself and already created
         * a user on the bridge with [userName]
         */
        fun createFromIpAndUser(
            bridgeIpAddress: String,
            userName: String
        ): Kontrollr = Kontrollr(bridgeIpAddress, userName)

        /**
         * Create a [Kontrollr] configured with the given [bridgeIpAddress] and create a username
         * based off of the input [applicationName] and [deviceName].
         *
         * This will internally hit the api explained [here](https://developers.meethue.com/develop/get-started-2/)
         *
         * Since this hits creates our object async, we need a callback to return the resulting [Kontrollr]
         *
         * When you call this method you will need to manually click your bridge to allow for user creation
         * within 10 seconds prior to calling this function.
         */
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

        /**
         * Create a [Kontrollr] that auto discovers your Hue bridge ip using the process described
         * [here](https://developers.meethue.com/develop/application-design-guidance/hue-bridge-discovery/)
         *
         * This also creates a username on the discovered bridge based off of the input [applicationName] and
         * [deviceName]
         *
         * Since our [Kontrollr] will be created async, we need to take in a callback to return the resulting object.
         *
         * When you call this method you will need to manually click your bridge to allow for user creation
         * within 10 seconds prior to calling this function.
         */
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