package com.example.model

enum class PlanType(val label: String, val precioMensualRd: Double, val limiteCitas: Int?) {
    VIP_TRIAL("VIP Prueba (30 días)", 0.0, null),
    VIP_ACTIVO("Plan VIP Activo", 2999.0, null),
    GRATIS("Plan Gratis", 0.0, 20),
    PRO("Plan Pro", 1499.0, null),
    VIP("Plan VIP", 2999.0, null)
}

enum class EstadoCita(val label: String) {
    CONFIRMADA("Confirmada"),
    PENDIENTE("Pendiente"),
    SI_VINO("Sí Vino"),
    NO_VINO("No Vino"),
    CANCELADA("Cancelada")
}

enum class CategoriaCliente(val label: String) {
    TODOS("Todos"),
    VIP("VIP"),
    ACTIVO("Activos"),
    EN_RIESGO("En riesgo"),
    INACTIVO("Inactivos"),
    LE_TOCA_VOLVER("Le toca volver"),
    NUEVO("Nuevos")
}

data class VentaAdicional(
    val id: String,
    val productoNombre: String,
    val precio: Double,
    val cantidad: Int = 1
)

data class Cita(
    val id: String,
    val clienteNombre: String,
    val clienteTelefono: String,
    val clienteCorreo: String = "",
    val barberoId: String,
    val barberoNombre: String,
    val servicio: String,
    val precio: Double,
    val fecha: String,
    val hora: String,
    val duracionMinutos: Int = 60,
    val estado: EstadoCita = EstadoCita.CONFIRMADA,
    val ventasAdicionales: List<VentaAdicional> = emptyList(),
    val totalCobrado: Double = precio,
    val esWalkIn: Boolean = false,
    val notas: String = ""
)

data class Barbero(
    val id: String,
    val nombre: String,
    val apodo: String = "",
    val telefono: String = "",
    val especialidad: String = "Cortes & Barba",
    val activo: Boolean = true,
    val comisionPorcentaje: Double = 50.0,
    val citasHoy: Int = 0,
    val ingresosHoy: Double = 0.0,
    val rating: Double = 4.9,
    val avatarInitials: String = ""
)

data class Cliente(
    val id: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val categoria: CategoriaCliente,
    val totalGastado: Double,
    val ultimaVisita: String,
    val visitasCount: Int,
    val servicioFavorito: String = "Corte & Barba",
    val barberoFavorito: String = "José Límoz",
    val notas: String = ""
)

data class ProductoInventario(
    val id: String,
    val nombre: String,
    val categoria: String,
    val precioVenta: Double,
    val costo: Double,
    val stock: Int,
    val stockMinimo: Int = 3
)

data class FlujoCajaMes(
    val mes: String,
    val ingresos: Double,
    val gastos: Double
)

data class Business(
    val id: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val correo: String = "",
    val categoria: String = "Barbería",
    val ciudad: String = "Santo Domingo",
    val direccion: String = "",
    val codigoUnico: String = "",
    val plan: PlanType = PlanType.VIP_TRIAL,
    val citasUsadasMes: Int = 0,
    val fechaInicioPrueba: Long = System.currentTimeMillis(),
    val fechaFinPrueba: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val ingresosHoy: Double = 0.0,
    val balanceNeto: Double = 0.0,
    val dineroRecibido: Double = 0.0,
    val dineroEnEspera: Double = 0.0,
    val gastosMes: Double = 0.0,
    val tasaOcupacion: Int = 0,
    val tasaAusencias: Int = 0,
    val productividad: Int = 0
)
