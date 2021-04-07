package com.zachary_moore.huekontrollr.internal

const val ROOT = "api"

internal fun interface Request {
    fun getUrl(): String
}

internal class BaseRequest(
    private val bridgeIp: String,
    private val username: String
) : Request {
    private var currentApiUrl = "http://$bridgeIp/$ROOT/$username"

    private fun appendToApiUrl(
        routeToAdd: String
    ): BaseRequest = this.apply {
            currentApiUrl += "/$routeToAdd"
        }

    internal fun getSubApiRequest(
        subApiRoute: String
    ): BaseRequest = BaseRequest(
        bridgeIp,
        username
    ).appendToApiUrl(subApiRoute)

    override fun getUrl() = currentApiUrl
}

internal class SubApiRequest(
    baseRequest: BaseRequest,
    subApiRoute: String
) : Request {
    private val subRequest = baseRequest.getSubApiRequest(subApiRoute)
    override fun getUrl() = subRequest.getUrl()

    fun getRequest(
        subRoute: String
    ) : Request = Request { subRequest.getUrl() + "/$subRoute" }
}