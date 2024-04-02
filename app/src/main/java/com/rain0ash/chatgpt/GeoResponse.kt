package com.rain0ash.chatgpt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoResponse(
    val ip: String,
    val name: String,
    @SerialName("country") val country2: String,
    @SerialName("country_3") val country3: String
)