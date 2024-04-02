package com.rain0ash.chatgpt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class IPAddressValidator {
    private val client = OkHttpClient()

    suspend fun country(): Result<GeoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url("https://get.geojs.io/v1/ip/country.json").build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val geo = Json.decodeFromString<GeoResponse>(body!!)
                    Result.success(geo)
                } else {
                    Result.failure(Exception("API responded with ${response.code}"))
                }
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }
    }
}

