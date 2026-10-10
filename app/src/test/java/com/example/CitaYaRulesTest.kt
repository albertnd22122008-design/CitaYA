package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.FirebaseService
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class CitaYaRulesTest : FirestoreEmulatorTestBase() {

  @Test
  fun unauthenticatedUserCannotCreateNegocio() = runBlocking {
    try {
      firestore.collection("negocios").document("biz_fail").set(
        mapOf("nombre" to "No Auth", "userId" to "nobody", "plan" to "VIP_TRIAL")
      ).await()
      fail("Should fail for unauthenticated user")
    } catch (e: FirebaseFirestoreException) {
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
    }
  }

  @Test
  fun authenticatedUserCanCreateAndReadOwnNegocio() = runBlocking {
    val uid = signInTestUser("barbero@citaya.com")

    val data = mapOf(
      "id" to "biz_$uid",
      "userId" to uid,
      "nombre" to "Barbería Santiago",
      "plan" to "VIP_TRIAL",
      "citasUsadasMes" to 0
    )

    firestore.collection("negocios").document("biz_$uid").set(data).await()

    val snapshot = firestore.collection("negocios").document("biz_$uid").get().await()
    assertNotNull(snapshot)
    assertEquals("Barbería Santiago", snapshot.getString("nombre"))
  }

  @Test
  fun testFirebaseServiceInicializarNegocioVIP() = runBlocking {
    val uid = signInTestUser("vip@citaya.com")
    val service = FirebaseService(firestore)
    val res = service.inicializarNegocioVIP(uid, "VIP Barber Lounge", "8095559999", "vip@citaya.com")
    assertTrue(res.isSuccess)

    val datos = service.obtenerDatosNegocio(uid)
    assertNotNull(datos)
    assertEquals("VIP_TRIAL", datos?.get("plan"))
    assertEquals("VIP Barber Lounge", datos?.get("nombre"))
  }
}
