package com.newbet.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object NewbetApiClient {

    const val BASE_URL = "https://newbet-api.vercel.app"
    const val SUPABASE_URL = "https://bovozexwmruwjywzhhao.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJvdm96ZXh3bXJ1d2p5d3poaGFvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTM1MzMzOTEsImV4cCI6MjEwOTEwOTM5MX0.rXUQxI3b73gt-hncUD0YrFJGEdQZpWeNX2ZU413mZII"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    var authToken: String? = null
    var currentUserId: String = "NB-849201"

    suspend fun registerOneClick(
        country: String = "Bénin",
        currency: String = "XOF",
        promoCode: String = "NEWBET200",
        name: String = "",
        phone: String = ""
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("country", country)
                put("currency", currency)
                put("promoCode", promoCode)
                if (name.isNotEmpty()) put("name", name)
                if (phone.isNotEmpty()) put("phone", phone)
            }
            val request = Request.Builder()
                .url("$BASE_URL/api/auth/one-click")
                .post(body.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "{}"
            val json = JSONObject(responseBody)
            if (response.isSuccessful) {
                authToken = json.optString("token")
                val creds = json.optJSONObject("credentials")
                if (creds != null) {
                    currentUserId = creds.optString("userId", currentUserId)
                }
                Result.success(json)
            } else {
                Result.failure(Exception(json.optString("error", "Erreur d'inscription 1-Clic")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(
        email: String,
        name: String = "",
        googleId: String = "",
        promoCode: String = "NEWBET200"
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("email", email)
                put("name", name)
                put("googleId", googleId)
                put("promoCode", promoCode)
            }
            val request = Request.Builder()
                .url("$BASE_URL/api/auth/google")
                .post(body.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "{}"
            val json = JSONObject(responseBody)
            if (response.isSuccessful) {
                authToken = json.optString("token")
                val user = json.optJSONObject("user")
                if (user != null) {
                    currentUserId = user.optString("id", currentUserId)
                }
                Result.success(json)
            } else {
                Result.failure(Exception(json.optString("error", "Erreur de connexion Google")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchFixtures(): Result<JSONArray> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$BASE_URL/api/sports/fixtures")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "[]"
            if (response.isSuccessful) {
                Result.success(JSONArray(responseBody))
            } else {
                Result.failure(Exception("Erreur de chargement des cotes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun placeBet(selections: JSONArray, stakeXOF: Long): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("selections", selections)
                put("stakeXOF", stakeXOF)
            }
            val reqBuilder = Request.Builder()
                .url("$BASE_URL/api/bets/place")
                .post(body.toString().toRequestBody(jsonMediaType))

            if (!authToken.isNullOrEmpty()) {
                reqBuilder.addHeader("Authorization", "Bearer $authToken")
            }

            val response = client.newCall(reqBuilder.build()).execute()
            val responseBody = response.body?.string() ?: "{}"
            val json = JSONObject(responseBody)
            if (response.isSuccessful) {
                Result.success(json)
            } else {
                Result.failure(Exception(json.optString("error", "Erreur lors du placement du pari")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeCashOut(betId: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("betId", betId)
            }
            val reqBuilder = Request.Builder()
                .url("$BASE_URL/api/bets/cashout")
                .post(body.toString().toRequestBody(jsonMediaType))

            if (!authToken.isNullOrEmpty()) {
                reqBuilder.addHeader("Authorization", "Bearer $authToken")
            }

            val response = client.newCall(reqBuilder.build()).execute()
            val responseBody = response.body?.string() ?: "{}"
            val json = JSONObject(responseBody)
            if (response.isSuccessful) {
                Result.success(json)
            } else {
                Result.failure(Exception(json.optString("error", "Erreur Cash Out")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deposit(amountXOF: Long, method: String, phone: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("amountXOF", amountXOF)
                put("method", method)
                put("phoneNumber", phone)
            }
            val reqBuilder = Request.Builder()
                .url("$BASE_URL/api/wallet/deposit")
                .post(body.toString().toRequestBody(jsonMediaType))

            if (!authToken.isNullOrEmpty()) {
                reqBuilder.addHeader("Authorization", "Bearer $authToken")
            }

            val response = client.newCall(reqBuilder.build()).execute()
            val responseBody = response.body?.string() ?: "{}"
            val json = JSONObject(responseBody)
            if (response.isSuccessful) {
                Result.success(json)
            } else {
                Result.failure(Exception(json.optString("error", "Erreur de dépôt Mobile Money")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

