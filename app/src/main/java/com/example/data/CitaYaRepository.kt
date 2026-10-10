package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.R
import com.example.model.Barbero
import com.example.model.Business
import com.example.model.CategoriaCliente
import com.example.model.Cita
import com.example.model.Cliente
import com.example.model.EstadoCita
import com.example.model.FlujoCajaMes
import com.example.model.PlanType
import com.example.model.ProductoInventario
import com.example.model.VentaAdicional
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

enum class ModoApp {
    NEGOCIO,
    CLIENTE
}

sealed class ResultadoOperacionCita {
    object Exito : ResultadoOperacionCita()
    data class LimiteAlcanzado(val mensaje: String) : ResultadoOperacionCita()
    data class ConflictoHorario(val mensaje: String) : ResultadoOperacionCita()
}

class CitaYaRepository(
    val firestore: FirebaseFirestore,
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    val firebaseService by lazy { FirebaseService(firestore) }

    private val _modoApp = MutableStateFlow(ModoApp.NEGOCIO)
    val modoApp: StateFlow<ModoApp> = _modoApp.asStateFlow()

    // Base de datos y estado local 100% LIMPIOS de datos falsos
    private val _business = MutableStateFlow(Business())
    val business: StateFlow<Business> = _business.asStateFlow()

    private val _barberos = MutableStateFlow<List<Barbero>>(emptyList())
    val barberos: StateFlow<List<Barbero>> = _barberos.asStateFlow()

    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas.asStateFlow()

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()

    private val _inventario = MutableStateFlow<List<ProductoInventario>>(emptyList())
    val inventario: StateFlow<List<ProductoInventario>> = _inventario.asStateFlow()

    private val _flujoCaja = MutableStateFlow<List<FlujoCajaMes>>(emptyList())
    val flujoCaja: StateFlow<List<FlujoCajaMes>> = _flujoCaja.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                sincronizarConFirestore(uid)
            } else {
                limpiarDatosLocales()
            }
        }
    }

    fun limpiarDatosLocales() {
        _business.value = Business()
        _barberos.value = emptyList()
        _citas.value = emptyList()
        _clientes.value = emptyList()
        _inventario.value = emptyList()
        _flujoCaja.value = emptyList()
    }

    fun sincronizarConFirestore(uid: String) {
        repositoryScope.launch {
            val biz = firebaseService.obtenerNegocio(uid)
            if (biz != null) {
                _business.value = biz
                // Cargar barberos reales
                val realBarberos = firebaseService.cargarBarberos(uid)
                _barberos.value = realBarberos

                // Cargar citas reales
                val realCitas = firebaseService.cargarCitas(uid)
                _citas.value = realCitas

                // Cargar inventario real
                val realInventario = firebaseService.cargarInventario(uid)
                _inventario.value = realInventario

                actualizarMetricasCalculadas(realCitas)
            }
        }
    }

    private fun actualizarMetricasCalculadas(citasList: List<Cita>) {
        val citasHoy = citasList.filter { it.fecha == "Hoy" || it.fecha.isBlank() }
        val ingresosCalculados = citasHoy.filter { it.estado == EstadoCita.SI_VINO }.sumOf { it.totalCobrado }

        _business.update { biz ->
            biz.copy(
                citasUsadasMes = citasList.size,
                ingresosHoy = ingresosCalculados,
                balanceNeto = citasList.filter { it.estado == EstadoCita.SI_VINO }.sumOf { it.totalCobrado }
            )
        }
    }

    fun setModoApp(modo: ModoApp) {
        _modoApp.value = modo
    }

    suspend fun buscarNegocioPorCodigo(codigo: String): Business? {
        return withContext(Dispatchers.IO) {
            firebaseService.buscarNegocioPorCodigo(codigo)
        }
    }

    suspend fun cargarBarberosDeNegocio(negocioId: String): List<Barbero> {
        return withContext(Dispatchers.IO) {
            firebaseService.cargarBarberos(negocioId)
        }
    }

    fun validarLimitePlan(): ResultadoOperacionCita {
        val biz = _business.value
        if (biz.plan == PlanType.GRATIS && biz.citasUsadasMes >= (PlanType.GRATIS.limiteCitas ?: 20)) {
            return ResultadoOperacionCita.LimiteAlcanzado(
                "🚀 Has alcanzado el límite de 20 citas del Plan Gratis este mes. Actualiza al Plan Pro para citas ilimitadas."
            )
        }
        return ResultadoOperacionCita.Exito
    }

    fun registrarSiVino(citaId: String, ventasAdicionales: List<VentaAdicional> = emptyList()): ResultadoOperacionCita {
        val limitCheck = validarLimitePlan()
        if (limitCheck is ResultadoOperacionCita.LimiteAlcanzado) {
            return limitCheck
        }

        val citaTarget = _citas.value.find { it.id == citaId } ?: return ResultadoOperacionCita.Exito
        val montoAdicional = ventasAdicionales.sumOf { it.precio * it.cantidad }
        val nuevoTotal = citaTarget.precio + montoAdicional

        _citas.update { list ->
            list.map { c ->
                if (c.id == citaId) {
                    c.copy(
                        estado = EstadoCita.SI_VINO,
                        ventasAdicionales = ventasAdicionales,
                        totalCobrado = nuevoTotal
                    )
                } else c
            }
        }

        _business.update { biz ->
            biz.copy(
                ingresosHoy = biz.ingresosHoy + nuevoTotal,
                balanceNeto = biz.balanceNeto + nuevoTotal,
                dineroRecibido = biz.dineroRecibido + nuevoTotal,
                citasUsadasMes = biz.citasUsadasMes + 1
            )
        }

        if (ventasAdicionales.isNotEmpty()) {
            _inventario.update { invList ->
                invList.map { p ->
                    val vendidas = ventasAdicionales.filter { it.productoNombre == p.nombre }.sumOf { it.cantidad }
                    if (vendidas > 0) p.copy(stock = (p.stock - vendidas).coerceAtLeast(0)) else p
                }
            }
        }

        _barberos.update { barberoList ->
            barberoList.map { b ->
                if (b.id == citaTarget.barberoId) {
                    b.copy(
                        citasHoy = b.citasHoy + 1,
                        ingresosHoy = b.ingresosHoy + nuevoTotal
                    )
                } else b
            }
        }

        val bizId = _business.value.id.ifBlank { auth.currentUser?.uid }
        if (!bizId.isNullOrBlank()) {
            repositoryScope.launch {
                firebaseService.actualizarEstadoCita(
                    negocioId = bizId,
                    citaId = citaId,
                    estado = EstadoCita.SI_VINO,
                    totalCobrado = nuevoTotal,
                    ventasAdicionales = ventasAdicionales
                )
                firestore.collection("negocios").document(bizId).update(
                    mapOf(
                        "ingresosHoy" to _business.value.ingresosHoy,
                        "citasUsadasMes" to FieldValue.increment(1)
                    )
                )
            }
        }

        return ResultadoOperacionCita.Exito
    }

    fun registrarNoVino(citaId: String) {
        _citas.update { list ->
            list.map { c ->
                if (c.id == citaId) c.copy(estado = EstadoCita.NO_VINO) else c
            }
        }
        _business.update { biz ->
            biz.copy(tasaAusencias = (biz.tasaAusencias + 1).coerceAtMost(100))
        }

        val bizId = _business.value.id.ifBlank { auth.currentUser?.uid }
        if (!bizId.isNullOrBlank()) {
            repositoryScope.launch {
                firebaseService.actualizarEstadoCita(
                    negocioId = bizId,
                    citaId = citaId,
                    estado = EstadoCita.NO_VINO,
                    totalCobrado = 0.0
                )
            }
        }
    }

    fun crearCitaWalkIn(
        clienteNombre: String,
        clienteTelefono: String,
        barberoId: String,
        servicio: String,
        precio: Double,
        hora: String
    ): ResultadoOperacionCita {
        val limitCheck = validarLimitePlan()
        if (limitCheck is ResultadoOperacionCita.LimiteAlcanzado) {
            return limitCheck
        }

        val hayConflicto = _citas.value.any {
            it.barberoId == barberoId && it.hora.equals(hora, ignoreCase = true) &&
                    (it.estado == EstadoCita.CONFIRMADA || it.estado == EstadoCita.PENDIENTE)
        }
        if (hayConflicto) {
            val barbero = _barberos.value.find { it.id == barberoId }?.nombre ?: "El barbero"
            return ResultadoOperacionCita.ConflictoHorario(
                "⚠️ Conflicto de horario: $barbero ya tiene una cita reservada a las $hora."
            )
        }

        val barberoNombre = _barberos.value.find { it.id == barberoId }?.nombre ?: "Barbero Asignado"
        val nuevaCita = Cita(
            id = "c_walkin_${System.currentTimeMillis()}",
            clienteNombre = clienteNombre.ifBlank { "Cliente Walk-in" },
            clienteTelefono = clienteTelefono.ifBlank { "809-000-0000" },
            barberoId = barberoId,
            barberoNombre = barberoNombre,
            servicio = servicio,
            precio = precio,
            fecha = "Hoy",
            hora = hora,
            duracionMinutos = 45,
            estado = EstadoCita.CONFIRMADA,
            esWalkIn = true
        )

        _citas.update { listOf(nuevaCita) + it }

        // Agregar también al CRM si no existe
        val clienteExistente = _clientes.value.find { it.nombre.equals(clienteNombre, ignoreCase = true) }
        if (clienteExistente == null) {
            val nuevoCliente = Cliente(
                id = "cl_${System.currentTimeMillis()}",
                nombre = nuevaCita.clienteNombre,
                correo = "",
                telefono = nuevaCita.clienteTelefono,
                categoria = CategoriaCliente.NUEVO,
                totalGastado = 0.0,
                ultimaVisita = "Hoy",
                visitasCount = 1,
                servicioFavorito = servicio,
                barberoFavorito = barberoNombre
            )
            _clientes.update { listOf(nuevoCliente) + it }
        }

        val bizId = _business.value.id.ifBlank { auth.currentUser?.uid }
        if (!bizId.isNullOrBlank()) {
            repositoryScope.launch {
                firebaseService.guardarCita(bizId, nuevaCita)
            }
        }

        return ResultadoOperacionCita.Exito
    }

    suspend fun crearCitaDesdeCliente(
        negocioId: String,
        clienteNombre: String,
        clienteTelefono: String,
        barberoId: String,
        barberoNombre: String,
        servicio: String,
        precio: Double,
        hora: String
    ): ResultadoOperacionCita {
        return withContext(Dispatchers.IO) {
            val citasActuales = firebaseService.cargarCitas(negocioId)
            val hayConflicto = citasActuales.any {
                it.barberoId == barberoId && it.hora.equals(hora, ignoreCase = true) &&
                        (it.estado == EstadoCita.CONFIRMADA || it.estado == EstadoCita.PENDIENTE)
            }
            if (hayConflicto) {
                return@withContext ResultadoOperacionCita.ConflictoHorario(
                    "⚠️ Conflicto de horario: $barberoNombre ya tiene una cita agendada a las $hora."
                )
            }

            val nuevaCita = Cita(
                id = "c_client_${System.currentTimeMillis()}",
                clienteNombre = clienteNombre.ifBlank { "Cliente CitaYa" },
                clienteTelefono = clienteTelefono.ifBlank { "809-000-0000" },
                barberoId = barberoId,
                barberoNombre = barberoNombre,
                servicio = servicio,
                precio = precio,
                fecha = "Hoy",
                hora = hora,
                duracionMinutos = 45,
                estado = EstadoCita.CONFIRMADA,
                esWalkIn = false
            )

            firebaseService.guardarCita(negocioId, nuevaCita)
            ResultadoOperacionCita.Exito
        }
    }

    fun agregarBarbero(nombre: String, apodo: String, telefono: String, especialidad: String, comision: Double) {
        val initials = nombre.split(" ").filter { it.isNotBlank() }.take(2).map { it.first() }.joinToString("").uppercase()
        val nuevoBarbero = Barbero(
            id = "b_${System.currentTimeMillis()}",
            nombre = nombre,
            apodo = apodo,
            telefono = telefono,
            especialidad = especialidad.ifBlank { "Cortes & Barba" },
            activo = true,
            comisionPorcentaje = comision,
            citasHoy = 0,
            ingresosHoy = 0.0,
            rating = 5.0,
            avatarInitials = initials.ifBlank { "B" }
        )

        _barberos.update { it + nuevoBarbero }

        val bizId = _business.value.id.ifBlank { auth.currentUser?.uid }
        if (!bizId.isNullOrBlank()) {
            repositoryScope.launch {
                firebaseService.guardarBarbero(bizId, nuevoBarbero)
            }
        }
    }

    fun registrarNegocio(
        nombre: String,
        telefono: String,
        correo: String,
        categoria: String = "Barbería",
        ciudad: String = "Santo Domingo",
        direccion: String = "Distrito Nacional"
    ) {
        val codigo = "CY" + (1000..9999).random()
        val uid = auth.currentUser?.uid ?: "biz_${System.currentTimeMillis()}"

        val nuevoBiz = Business(
            id = uid,
            nombre = nombre.ifBlank { "Mi Negocio" },
            telefono = telefono.ifBlank { "809-555-0000" },
            correo = correo,
            categoria = categoria,
            ciudad = ciudad,
            direccion = direccion,
            codigoUnico = codigo,
            plan = PlanType.VIP_TRIAL,
            citasUsadasMes = 0,
            fechaInicioPrueba = System.currentTimeMillis(),
            fechaFinPrueba = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
            ingresosHoy = 0.0,
            balanceNeto = 0.0,
            dineroRecibido = 0.0,
            dineroEnEspera = 0.0,
            gastosMes = 0.0,
            tasaOcupacion = 0,
            tasaAusencias = 0,
            productividad = 0
        )
        _business.value = nuevoBiz
        _barberos.value = emptyList()
        _citas.value = emptyList()
        _clientes.value = emptyList()
        _inventario.value = emptyList()

        auth.currentUser?.uid?.let { currentUid ->
            repositoryScope.launch {
                firebaseService.inicializarNegocioVIP(
                    uid = currentUid,
                    nombreNegocio = nuevoBiz.nombre,
                    telefono = nuevoBiz.telefono,
                    email = correo,
                    codigoUnico = codigo,
                    categoria = categoria,
                    ciudad = ciudad,
                    direccion = direccion
                )
            }
        }
    }

    fun actualizarPlan(nuevoPlan: PlanType) {
        _business.update { it.copy(plan = nuevoPlan) }
        auth.currentUser?.uid?.let { uid ->
            val docRef = firestore.collection("negocios").document(uid)
            docRef.update("plan", nuevoPlan.name)
        }
    }

    /**
     * Confirma el pago de suscripción con Stripe y actualiza el plan a VIP_ACTIVO en Firestore
     * extendiendo la fecha de vigencia por 30 días adicionales.
     */
    fun activarSuscripcionVip(uid: String, onComplete: (Boolean) -> Unit = {}) {
        repositoryScope.launch {
            val res = firebaseService.activarPlanVipSuscripcion(uid)
            if (res.isSuccess) {
                val nuevaFecha = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
                _business.update {
                    it.copy(
                        plan = PlanType.VIP_ACTIVO,
                        fechaFinPrueba = nuevaFecha
                    )
                }
                withContext(Dispatchers.Main) { onComplete(true) }
            } else {
                withContext(Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun agregarProducto(nombre: String, categoria: String, precioVenta: Double, costo: Double, stock: Int) {
        val prod = ProductoInventario(
            id = "p_${System.currentTimeMillis()}",
            nombre = nombre,
            categoria = categoria.ifBlank { "General" },
            precioVenta = precioVenta,
            costo = costo,
            stock = stock,
            stockMinimo = 2
        )
        _inventario.update { it + prod }

        val bizId = _business.value.id.ifBlank { auth.currentUser?.uid }
        if (!bizId.isNullOrBlank()) {
            repositoryScope.launch {
                firebaseService.guardarProducto(bizId, prod)
            }
        }
    }

    fun venderProducto(productoId: String, cantidad: Int) {
        val prod = _inventario.value.find { it.id == productoId } ?: return
        val totalVenta = prod.precioVenta * cantidad

        _inventario.update { list ->
            list.map {
                if (it.id == productoId) it.copy(stock = (it.stock - cantidad).coerceAtLeast(0)) else it
            }
        }
        _business.update { biz ->
            biz.copy(
                ingresosHoy = biz.ingresosHoy + totalVenta,
                balanceNeto = biz.balanceNeto + totalVenta
            )
        }
    }

    fun reabastecerProducto(productoId: String, cantidad: Int) {
        _inventario.update { list ->
            list.map {
                if (it.id == productoId) it.copy(stock = it.stock + cantidad) else it
            }
        }
    }

    fun enviarMensajeWhatsApp(context: Context, telefono: String, mensaje: String) {
        try {
            val telLimpio = telefono.replace(Regex("[^0-9]"), "")
            val telFinal = if (telLimpio.length == 10) "1$telLimpio" else telLimpio
            val mensajeCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8.toString())
            val uri = Uri.parse("https://wa.me/$telFinal?text=$mensajeCodificado")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    companion object {
        @Volatile
        private var instance: CitaYaRepository? = null

        fun getInstance(context: Context): CitaYaRepository {
            return instance ?: synchronized(this) {
                instance ?: CitaYaRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
