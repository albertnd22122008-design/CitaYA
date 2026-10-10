package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Integración de Stripe del lado cliente.
 *
 * IMPORTANTE: la app SOLO usa la clave PÚBLICA (pk_...). La clave SECRETA (sk_...) vive en el
 * backend (Cloud Function) y jamás debe incluirse aquí ni en el repositorio.
 * El backend devuelve un `clientSecret` con el que se inicializa PaymentSheet en Android.
 */
class StripePaymentService(
    private val backendUrl: String = BuildConfig.STRIPE_BACKEND_URL
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Solicita al backend la creación de un PaymentIntent para la suscripción VIP del negocio.
     * El backend (con la clave secreta guardada como secreto) responde con el clientSecret.
     */
    suspend fun crearPaymentIntentSuscripcion(
        negocioId: String,
        plan: String,
        dias: Int = 30
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply {
                    put("negocioId", negocioId)
                    put("plan", plan)
                    put("dias", dias)
                }
                val body = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(backendUrl)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (response.isSuccessful) {
                    val clientSecret = JSONObject(responseBody).optString("clientSecret")
                    if (clientSecret.isNotBlank()) {
                        Result.success(clientSecret)
                    } else {
                        Result.failure(Exception("clientSecret no encontrado en la respuesta del backend"))
                    }
                } else {
                    val errorMsg = try {
                        JSONObject(responseBody).optString("error").ifBlank { response.message }
                    } catch (_: Exception) {
                        response.message
                    }
                    Log.e("Stripe", "Error del backend al crear PaymentIntent: $errorMsg")
                    Result.failure(Exception("Error de Stripe: $errorMsg"))
                }
            } catch (e: Exception) {
                Log.e("Stripe", "Fallo de red al conectar con el backend de pagos", e)
                Result.failure(e)
            }
        }
    }

    companion object {
        /** Clave pública de Stripe (segura para el cliente). Se inyecta desde .env -> BuildConfig. */
        val publishableKey: String get() = BuildConfig.STRIPE_PUBLISHABLE_KEY

        @Volatile
        private var instance: StripePaymentService? = null

        fun getInstance(): StripePaymentService {
            return instance ?: synchronized(this) {
                instance ?: StripePaymentService().also { instance = it }
            }
        }
    }
}
