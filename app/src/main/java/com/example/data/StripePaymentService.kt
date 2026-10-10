package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StripePaymentService(
    private val stripeSecretKey: String = "sk_test_51UOzQ5Hjl0tQSkyTcCwhIOqnRFXElOl8LRv52WaP1ze8RviXz6efx9Y13rXLLheldVh2QorPbTZY6vpIfdTM1dHT00BZedpd2h"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Solicita la creación de un PaymentIntent para la suscripción VIP del negocio.
     * Retorna el clientSecret necesario para inicializar PaymentSheet en Android.
     */
    suspend fun crearPaymentIntentSuscripcion(
        negocioId: String,
        montoCentavos: Long = 2999L // $29.99 USD (~RD$ 1,750 - 2,999)
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val formBody = FormBody.Builder()
                    .add("amount", montoCentavos.toString())
                    .add("currency", "usd")
                    .add("description", "CitaYa SaaS - Plan VIP Negocio ($negocioId)")
                    .add("metadata[negocioId]", negocioId)
                    .add("metadata[plan]", "VIP_ACTIVO")
                    .add("metadata[dias]", "30")
                    .add("payment_method_types[]", "card")
                    .build()

                val request = Request.Builder()
                    .url("https://api.stripe.com/v1/payment_intents")
                    .header("Authorization", "Bearer $stripeSecretKey")
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val clientSecret = json.optString("client_secret")
                    if (clientSecret.isNotBlank()) {
                        Result.success(clientSecret)
                    } else {
                        Result.failure(Exception("client_secret no encontrado en la respuesta de Stripe"))
                    }
                } else {
                    val errorMsg = try {
                        JSONObject(responseBody).optJSONObject("error")?.optString("message") ?: response.message
                    } catch (_: Exception) {
                        response.message
                    }
                    Log.e("Stripe", "Error al crear PaymentIntent: $errorMsg")
                    Result.failure(Exception("Error de Stripe: $errorMsg"))
                }
            } catch (e: Exception) {
                Log.e("Stripe", "Fallo de red al conectar con Stripe", e)
                Result.failure(e)
            }
        }
    }

    companion object {
        const val STRIPE_PUBLISHABLE_KEY = "sk_test_51UOzQ5Hjl0tQSkyTcCwhIOqnRFXElOl8LRv52WaP1ze8RviXz6efx9Y13rXLLheldVh2QorPbTZY6vpIfdTM1dHT00BZedpd2h"

        @Volatile
        private var instance: StripePaymentService? = null

        fun getInstance(): StripePaymentService {
            return instance ?: synchronized(this) {
                instance ?: StripePaymentService().also { instance = it }
            }
        }
    }
}
