package com.example.data

import android.content.Context
import com.example.R
import com.example.model.Barbero
import com.example.model.Business
import com.example.model.CategoriaCliente
import com.example.model.Cita
import com.example.model.Cliente
import com.example.model.EstadoCita
import com.example.model.PlanType
import com.example.model.ProductoInventario
import com.example.model.VentaAdicional
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class FirebaseService(
    val firestore: FirebaseFirestore
) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    /**
     * Crea o inicializa el documento de un nuevo negocio en Firestore (/negocios/{uid})
     * con el plan VIP_TRIAL de 30 días y base de datos 100% limpia sin datos falsos.
     */
    suspend fun inicializarNegocioVIP(
        uid: String,
        nombreNegocio: String,
        telefono: String,
        email: String,
        codigoUnico: String = "CY" + (1000..9999).random(),
        categoria: String = "Barbería",
        ciudad: String = "Santo Domingo",
        direccion: String = "Distrito Nacional"
    ): Result<Business> {
        return try {
            val fechaActual = Timestamp.now()

            // Calculamos exactamente 30 días a partir del momento actual
            val calendar = Calendar.getInstance().apply {
                time = fechaActual.toDate()
                add(Calendar.DAY_OF_YEAR, 30)
            }
            val fechaFinPrueba = Timestamp(calendar.time)

            val datosNegocio = hashMapOf(
                "id" to uid,
                "uid" to uid,
                "userId" to uid,
                "nombre" to nombreNegocio,
                "telefono" to telefono,
                "email" to email,
                "correo" to email,
                "categoria" to categoria,
                "ciudad" to ciudad,
                "direccion" to direccion,
                "codigoUnico" to codigoUnico,
                "plan" to "VIP_TRIAL",
                "citasUsadasMes" to 0,
                "ingresosHoy" to 0.0,
                "balanceNeto" to 0.0,
                "dineroRecibido" to 0.0,
                "dineroEnEspera" to 0.0,
                "gastosMes" to 0.0,
                "tasaOcupacion" to 0,
                "tasaAusencias" to 0,
                "productividad" to 0,
                "fechaInicioPrueba" to fechaActual,
                "fechaFinPrueba" to fechaFinPrueba,
                "creadoEn" to fechaActual,
                "activo" to true
            )

            val docRef = firestore.collection("negocios").document(uid)
            docRef.set(datosNegocio, SetOptions.merge()).await()

            val nuevoNegocio = Business(
                id = uid,
                nombre = nombreNegocio,
                telefono = telefono,
                correo = email,
                categoria = categoria,
                ciudad = ciudad,
                direccion = direccion,
                codigoUnico = codigoUnico,
                plan = PlanType.VIP_TRIAL,
                citasUsadasMes = 0,
                fechaInicioPrueba = fechaActual.toDate().time,
                fechaFinPrueba = fechaFinPrueba.toDate().time,
                ingresosHoy = 0.0,
                balanceNeto = 0.0,
                dineroRecibido = 0.0,
                dineroEnEspera = 0.0,
                gastosMes = 0.0,
                tasaOcupacion = 0,
                tasaAusencias = 0,
                productividad = 0
            )

            Result.success(nuevoNegocio)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "negocios/$uid")
            Result.failure(e)
        }
    }

    /**
     * Confirma el pago de Stripe y activa el plan VIP_ACTIVO por 30 días adicionales en Firestore.
     */
    suspend fun activarPlanVipSuscripcion(uid: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("negocios").document(uid)
            val docSnapshot = docRef.get().await()

            val ahora = Timestamp.now()
            val fechaFinActual = (docSnapshot.get("fechaFinPrueba") as? Timestamp)?.toDate()?.time
                ?: (docSnapshot.get("fechaVencimientoPlan") as? Timestamp)?.toDate()?.time
                ?: ahora.toDate().time

            val baseTime = if (fechaFinActual > ahora.toDate().time) fechaFinActual else ahora.toDate().time
            val calendar = Calendar.getInstance().apply {
                timeInMillis = baseTime
                add(Calendar.DAY_OF_YEAR, 30)
            }
            val nuevaFechaFin = Timestamp(calendar.time)

            val updates = hashMapOf<String, Any>(
                "plan" to "VIP_ACTIVO",
                "fechaFinPrueba" to nuevaFechaFin,
                "fechaVencimientoPlan" to nuevaFechaFin,
                "activo" to true,
                "fechaUltimoPago" to ahora
            )

            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "negocios/$uid")
            Result.failure(e)
        }
    }

    /**
     * Activa un plan de pago (PRO / VIP) por 30 días adicionales tras confirmarse el pago.
     */
    suspend fun activarPlanSuscripcion(uid: String, plan: PlanType): Result<Unit> {
        return try {
            val docRef = firestore.collection("negocios").document(uid)
            val docSnapshot = docRef.get().await()

            val ahora = Timestamp.now()
            val fechaFinActual = (docSnapshot.get("fechaVencimientoPlan") as? Timestamp)?.toDate()?.time
                ?: (docSnapshot.get("fechaFinPrueba") as? Timestamp)?.toDate()?.time
                ?: ahora.toDate().time

            val baseTime = if (fechaFinActual > ahora.toDate().time) fechaFinActual else ahora.toDate().time
            val calendar = Calendar.getInstance().apply {
                timeInMillis = baseTime
                add(Calendar.DAY_OF_YEAR, 30)
            }
            val nuevaFechaFin = Timestamp(calendar.time)

            val updates = hashMapOf<String, Any>(
                "plan" to plan.name,
                "fechaFinPrueba" to nuevaFechaFin,
                "fechaVencimientoPlan" to nuevaFechaFin,
                "activo" to true,
                "fechaUltimoPago" to ahora
            )

            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "negocios/$uid")
            Result.failure(e)
        }
    }

    /**
     * Busca un negocio por su código único de 6 caracteres en Firestore.
     * Retorna null si no existe ningún negocio con ese código (Cero invención de datos).
     */
    suspend fun buscarNegocioPorCodigo(codigo: String): Business? {
        val codigoLimpio = codigo.trim().uppercase()
        if (codigoLimpio.isBlank()) return null

        return try {
            val querySnapshot = firestore.collection("negocios")
                .whereEqualTo("codigoUnico", codigoLimpio)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                null
            } else {
                val doc = querySnapshot.documents[0]
                mapDocToBusiness(doc.id, doc.data ?: emptyMap())
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "negocios?codigoUnico=$codigoLimpio")
            null
        }
    }

    /**
     * Obtiene los datos del negocio por su UID.
     */
    suspend fun obtenerNegocio(uid: String): Business? {
        return try {
            val doc = firestore.collection("negocios").document(uid).get().await()
            if (doc.exists()) {
                mapDocToBusiness(doc.id, doc.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, "negocios/$uid")
            null
        }
    }

    /**
     * Obtiene los datos crudos del documento del negocio para pruebas y compatibilidad.
     */
    suspend fun obtenerDatosNegocio(uid: String): Map<String, Any>? {
        return try {
            val doc = firestore.collection("negocios").document(uid).get().await()
            doc.data
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, "negocios/$uid")
            null
        }
    }

    suspend fun cargarBarberos(negocioId: String): List<Barbero> {
        return try {
            val snapshot = firestore.collection("negocios")
                .document(negocioId)
                .collection("barberos")
                .get()
                .await()

            snapshot.documents.map { doc ->
                val id = doc.id
                val nombre = doc.getString("nombre") ?: ""
                val apodo = doc.getString("apodo") ?: ""
                val telefono = doc.getString("telefono") ?: ""
                val especialidad = doc.getString("especialidad") ?: "Cortes & Barba"
                val activo = doc.getBoolean("activo") ?: true
                val comision = doc.getDouble("comisionPorcentaje") ?: 50.0
                val citasHoy = doc.getLong("citasHoy")?.toInt() ?: 0
                val ingresosHoy = doc.getDouble("ingresosHoy") ?: 0.0
                val rating = doc.getDouble("rating") ?: 5.0
                val avatar = doc.getString("avatarInitials") ?: (nombre.take(2).uppercase())

                Barbero(
                    id = id,
                    nombre = nombre,
                    apodo = apodo,
                    telefono = telefono,
                    especialidad = especialidad,
                    activo = activo,
                    comisionPorcentaje = comision,
                    citasHoy = citasHoy,
                    ingresosHoy = ingresosHoy,
                    rating = rating,
                    avatarInitials = avatar
                )
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "negocios/$negocioId/barberos")
            emptyList()
        }
    }

    suspend fun guardarBarbero(negocioId: String, barbero: Barbero): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to barbero.id,
                "negocioId" to negocioId,
                "userId" to negocioId,
                "nombre" to barbero.nombre,
                "apodo" to barbero.apodo,
                "telefono" to barbero.telefono,
                "especialidad" to barbero.especialidad,
                "activo" to barbero.activo,
                "comisionPorcentaje" to barbero.comisionPorcentaje,
                "citasHoy" to barbero.citasHoy,
                "ingresosHoy" to barbero.ingresosHoy,
                "rating" to barbero.rating,
                "avatarInitials" to barbero.avatarInitials
            )
            firestore.collection("negocios")
                .document(negocioId)
                .collection("barberos")
                .document(barbero.id)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "negocios/$negocioId/barberos/${barbero.id}")
            Result.failure(e)
        }
    }

    suspend fun cargarCitas(negocioId: String): List<Cita> {
        return try {
            val snapshot = firestore.collection("negocios")
                .document(negocioId)
                .collection("citas")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val clienteNombre = doc.getString("clienteNombre") ?: return@mapNotNull null
                val clienteTelefono = doc.getString("clienteTelefono") ?: ""
                val barberoId = doc.getString("barberoId") ?: ""
                val barberoNombre = doc.getString("barberoNombre") ?: ""
                val servicio = doc.getString("servicio") ?: ""
                val precio = doc.getDouble("precio") ?: 0.0
                val fecha = doc.getString("fecha") ?: "Hoy"
                val hora = doc.getString("hora") ?: ""
                val duracion = doc.getLong("duracionMinutos")?.toInt() ?: 60
                val estadoStr = doc.getString("estado") ?: EstadoCita.CONFIRMADA.name
                val estado = try { EstadoCita.valueOf(estadoStr) } catch (_: Exception) { EstadoCita.CONFIRMADA }
                val totalCobrado = doc.getDouble("totalCobrado") ?: precio
                val esWalkIn = doc.getBoolean("esWalkIn") ?: false

                Cita(
                    id = id,
                    clienteNombre = clienteNombre,
                    clienteTelefono = clienteTelefono,
                    barberoId = barberoId,
                    barberoNombre = barberoNombre,
                    servicio = servicio,
                    precio = precio,
                    fecha = fecha,
                    hora = hora,
                    duracionMinutos = duracion,
                    estado = estado,
                    totalCobrado = totalCobrado,
                    esWalkIn = esWalkIn
                )
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "negocios/$negocioId/citas")
            emptyList()
        }
    }

    suspend fun guardarCita(negocioId: String, cita: Cita): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to cita.id,
                "negocioId" to negocioId,
                "userId" to negocioId,
                "clienteNombre" to cita.clienteNombre,
                "clienteTelefono" to cita.clienteTelefono,
                "barberoId" to cita.barberoId,
                "barberoNombre" to cita.barberoNombre,
                "servicio" to cita.servicio,
                "precio" to cita.precio,
                "fecha" to cita.fecha,
                "hora" to cita.hora,
                "duracionMinutos" to cita.duracionMinutos,
                "estado" to cita.estado.name,
                "totalCobrado" to cita.totalCobrado,
                "esWalkIn" to cita.esWalkIn,
                "fechaRegistro" to FieldValue.serverTimestamp()
            )
            firestore.collection("negocios")
                .document(negocioId)
                .collection("citas")
                .document(cita.id)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "negocios/$negocioId/citas/${cita.id}")
            Result.failure(e)
        }
    }

    suspend fun actualizarEstadoCita(
        negocioId: String,
        citaId: String,
        estado: EstadoCita,
        totalCobrado: Double,
        ventasAdicionales: List<VentaAdicional> = emptyList()
    ): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "estado" to estado.name,
                "totalCobrado" to totalCobrado
            )
            firestore.collection("negocios")
                .document(negocioId)
                .collection("citas")
                .document(citaId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "negocios/$negocioId/citas/$citaId")
            Result.failure(e)
        }
    }

    suspend fun cargarInventario(negocioId: String): List<ProductoInventario> {
        return try {
            val snapshot = firestore.collection("negocios")
                .document(negocioId)
                .collection("inventario")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val nombre = doc.getString("nombre") ?: return@mapNotNull null
                val categoria = doc.getString("categoria") ?: "General"
                val precioVenta = doc.getDouble("precioVenta") ?: 0.0
                val costo = doc.getDouble("costo") ?: 0.0
                val stock = doc.getLong("stock")?.toInt() ?: 0
                val stockMinimo = doc.getLong("stockMinimo")?.toInt() ?: 2

                ProductoInventario(
                    id = id,
                    nombre = nombre,
                    categoria = categoria,
                    precioVenta = precioVenta,
                    costo = costo,
                    stock = stock,
                    stockMinimo = stockMinimo
                )
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "negocios/$negocioId/inventario")
            emptyList()
        }
    }

    suspend fun guardarProducto(negocioId: String, prod: ProductoInventario): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to prod.id,
                "negocioId" to negocioId,
                "userId" to negocioId,
                "nombre" to prod.nombre,
                "categoria" to prod.categoria,
                "precioVenta" to prod.precioVenta,
                "costo" to prod.costo,
                "stock" to prod.stock,
                "stockMinimo" to prod.stockMinimo
            )
            firestore.collection("negocios")
                .document(negocioId)
                .collection("inventario")
                .document(prod.id)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "negocios/$negocioId/inventario/${prod.id}")
            Result.failure(e)
        }
    }

    /**
     * Actualiza métricas a nivel de negocio (ingresos, balance, ausencias, etc.)
     * en el documento raíz del negocio.
     */
    suspend fun actualizarMetricasNegocio(negocioId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("negocios").document(negocioId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "negocios/$negocioId")
            Result.failure(e)
        }
    }

    /**
     * Carga las fichas CRM de clientes del negocio desde Firestore.
     */
    suspend fun cargarClientes(negocioId: String): List<Cliente> {
        return try {
            val snapshot = firestore.collection("negocios")
                .document(negocioId)
                .collection("clientes")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val nombre = doc.getString("nombre") ?: return@mapNotNull null
                val correo = doc.getString("correo") ?: ""
                val telefono = doc.getString("telefono") ?: ""
                val categoriaStr = doc.getString("categoria") ?: CategoriaCliente.NUEVO.name
                val categoria = try { CategoriaCliente.valueOf(categoriaStr) } catch (_: Exception) { CategoriaCliente.NUEVO }
                val totalGastado = (doc.get("totalGastado") as? Number)?.toDouble() ?: 0.0
                val ultimaVisita = doc.getString("ultimaVisita") ?: ""
                val visitasCount = (doc.get("visitasCount") as? Number)?.toInt() ?: 0
                val servicioFavorito = doc.getString("servicioFavorito") ?: "Corte & Barba"
                val barberoFavorito = doc.getString("barberoFavorito") ?: ""
                val notas = doc.getString("notas") ?: ""

                Cliente(
                    id = id,
                    nombre = nombre,
                    correo = correo,
                    telefono = telefono,
                    categoria = categoria,
                    totalGastado = totalGastado,
                    ultimaVisita = ultimaVisita,
                    visitasCount = visitasCount,
                    servicioFavorito = servicioFavorito,
                    barberoFavorito = barberoFavorito,
                    notas = notas
                )
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "negocios/$negocioId/clientes")
            emptyList()
        }
    }

    /**
     * Crea o actualiza la ficha CRM de un cliente en Firestore.
     */
    suspend fun guardarCliente(negocioId: String, cliente: Cliente): Result<Unit> {
        return try {
            val data = hashMapOf<String, Any>(
                "id" to cliente.id,
                "negocioId" to negocioId,
                "userId" to negocioId,
                "nombre" to cliente.nombre,
                "correo" to cliente.correo,
                "telefono" to cliente.telefono,
                "categoria" to cliente.categoria.name,
                "totalGastado" to cliente.totalGastado,
                "ultimaVisita" to cliente.ultimaVisita,
                "visitasCount" to cliente.visitasCount,
                "servicioFavorito" to cliente.servicioFavorito,
                "barberoFavorito" to cliente.barberoFavorito,
                "notas" to cliente.notas
            )
            firestore.collection("negocios")
                .document(negocioId)
                .collection("clientes")
                .document(cliente.id)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "negocios/$negocioId/clientes/${cliente.id}")
            Result.failure(e)
        }
    }

    private fun mapDocToBusiness(id: String, data: Map<String, Any>): Business {
        val nombre = data["nombre"] as? String ?: ""
        val telefono = data["telefono"] as? String ?: ""
        val correo = (data["correo"] ?: data["email"]) as? String ?: ""
        val categoria = data["categoria"] as? String ?: "Barbería"
        val ciudad = data["ciudad"] as? String ?: "Santo Domingo"
        val direccion = data["direccion"] as? String ?: ""
        val codigoUnico = data["codigoUnico"] as? String ?: ""
        val planStr = data["plan"] as? String ?: "VIP_TRIAL"
        val plan = try { PlanType.valueOf(planStr) } catch (_: Exception) { PlanType.VIP_TRIAL }
        val citasUsadas = (data["citasUsadasMes"] as? Number)?.toInt() ?: 0
        val ingresosHoy = (data["ingresosHoy"] as? Number)?.toDouble() ?: 0.0
        val balanceNeto = (data["balanceNeto"] as? Number)?.toDouble() ?: 0.0
        val dineroRecibido = (data["dineroRecibido"] as? Number)?.toDouble() ?: 0.0
        val dineroEnEspera = (data["dineroEnEspera"] as? Number)?.toDouble() ?: 0.0
        val gastosMes = (data["gastosMes"] as? Number)?.toDouble() ?: 0.0
        val tasaOcupacion = (data["tasaOcupacion"] as? Number)?.toInt() ?: 0
        val tasaAusencias = (data["tasaAusencias"] as? Number)?.toInt() ?: 0
        val productividad = (data["productividad"] as? Number)?.toInt() ?: 0

        val fechaInicioTimestamp = data["fechaInicioPrueba"] as? Timestamp
        val fechaFinTimestamp = data["fechaFinPrueba"] as? Timestamp

        return Business(
            id = id,
            nombre = nombre,
            telefono = telefono,
            correo = correo,
            categoria = categoria,
            ciudad = ciudad,
            direccion = direccion,
            codigoUnico = codigoUnico,
            plan = plan,
            citasUsadasMes = citasUsadas,
            fechaInicioPrueba = fechaInicioTimestamp?.toDate()?.time ?: System.currentTimeMillis(),
            fechaFinPrueba = fechaFinTimestamp?.toDate()?.time ?: (System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),
            ingresosHoy = ingresosHoy,
            balanceNeto = balanceNeto,
            dineroRecibido = dineroRecibido,
            dineroEnEspera = dineroEnEspera,
            gastosMes = gastosMes,
            tasaOcupacion = tasaOcupacion,
            tasaAusencias = tasaAusencias,
            productividad = productividad
        )
    }

    companion object {
        @Volatile
        private var instance: FirebaseService? = null

        fun getInstance(context: Context): FirebaseService {
            return instance ?: synchronized(this) {
                instance ?: FirebaseService(context.applicationContext).also { instance = it }
            }
        }
    }
}
