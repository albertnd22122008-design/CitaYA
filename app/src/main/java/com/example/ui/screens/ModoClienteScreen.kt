package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CitaYaRepository
import com.example.data.ResultadoOperacionCita
import com.example.model.Barbero
import com.example.model.Business
import com.example.ui.components.CitaYaLogoMark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CobaltBlueLight
import com.example.ui.theme.GoldVip
import com.example.ui.theme.MintGreenText
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SunsetOrangeLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.launch

@Composable
fun ModoClienteScreen(
    repository: CitaYaRepository,
    onVolverModoNegocio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var conectadoAlLocal by remember { mutableStateOf(false) }
    var nombreCliente by remember { mutableStateOf("") }
    var codigoLocal by remember { mutableStateOf("") }
    var estaBuscando by remember { mutableStateOf(false) }
    var errorBusqueda by remember { mutableStateOf<String?>(null) }

    // Negocio real cargado desde Firebase Firestore
    var negocioEncontrado by remember { mutableStateOf<Business?>(null) }
    var barberosDelNegocio by remember { mutableStateOf<List<Barbero>>(emptyList()) }

    var bookingDialogBarbero by remember { mutableStateOf<Barbero?>(null) }
    var confirmacionMensaje by remember { mutableStateOf<String?>(null) }
    var showQrScannerDialog by remember { mutableStateOf(false) }

    val serviciosDisponibles = listOf(
        "Corte Fade & Estilo" to 500.0,
        "Corte & Barba Premium" to 750.0,
        "Afeitado Clásico Navaja" to 400.0,
        "Tinte & Texturizado" to 1200.0,
        "Tratamiento Facial Exfoliante" to 850.0
    )

    if (!conectadoAlLocal) {
        // =========================================================================
        // PANTALLA LIMPIA DE INGRESO DE CLIENTE (ÚNICAMENTE NOMBRE + CÓDIGO/QR LOCAL)
        // =========================================================================
        Surface(
            modifier = modifier.fillMaxSize(),
            color = ObsidianDeep
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logotipo y Slogan insignia
                CitaYaLogoMark(size = 50.dp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "citaya",
                    style = MaterialTheme.typography.headlineLarge,
                    color = SunsetOrange,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "de quita sueño pal mundo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Tarjeta limpia de Acceso de Cliente
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = ObsidianSurface,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Acceso de Cliente",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Ingresa tus datos para agendar tu turno en tu local favorito",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 1. Campo Nombre
                        OutlinedTextField(
                            value = nombreCliente,
                            onValueChange = {
                                nombreCliente = it
                                errorBusqueda = null
                            },
                            label = { Text("Escribe tu nombre", fontSize = 14.sp) },
                            placeholder = { Text("Ej. Juan Rodríguez", color = TextMuted.copy(alpha = 0.6f)) },
                            leadingIcon = {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = SunsetOrange)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_client_name"),
                            shape = RoundedCornerShape(14.dp),
                            textStyle = TextStyle(fontSize = 15.sp, color = TextLight),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SunsetOrange,
                                unfocusedBorderColor = BorderLight,
                                focusedLabelColor = SunsetOrange,
                                unfocusedLabelColor = TextMuted
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Campo Código del Local
                        OutlinedTextField(
                            value = codigoLocal,
                            onValueChange = {
                                codigoLocal = it.uppercase().trim()
                                errorBusqueda = null
                            },
                            label = { Text("Código del local (ej. CY8942)", fontSize = 14.sp) },
                            placeholder = { Text("Pídelo en tu barbería o salón", color = TextMuted.copy(alpha = 0.6f)) },
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = null, tint = SunsetOrange)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_client_local_code"),
                            shape = RoundedCornerShape(14.dp),
                            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextLight),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SunsetOrange,
                                unfocusedBorderColor = BorderLight,
                                focusedLabelColor = SunsetOrange,
                                unfocusedLabelColor = TextMuted
                            )
                        )

                        errorBusqueda?.let { err ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = err,
                                color = SunsetOrange,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Botón Entrar al Local con búsqueda real en Firebase
                        Button(
                            onClick = {
                                if (nombreCliente.isBlank()) {
                                    errorBusqueda = "Por favor escribe tu nombre"
                                    return@Button
                                }
                                if (codigoLocal.isBlank()) {
                                    errorBusqueda = "Por favor ingresa el código del local"
                                    return@Button
                                }

                                estaBuscando = true
                                errorBusqueda = null

                                coroutineScope.launch {
                                    val biz = repository.buscarNegocioPorCodigo(codigoLocal)
                                    estaBuscando = false
                                    if (biz != null) {
                                        negocioEncontrado = biz
                                        barberosDelNegocio = repository.cargarBarberosDeNegocio(biz.id)
                                        conectadoAlLocal = true
                                    } else {
                                        errorBusqueda = "Código no encontrado. No existe ningún negocio registrado con '$codigoLocal'. Verifica el código con tu establecimiento."
                                    }
                                }
                            },
                            enabled = !estaBuscando,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_enter_local"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                        ) {
                            if (estaBuscando) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = "Entrar al Local",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Opción de Escanear Código QR
                        OutlinedButton(
                            onClick = { showQrScannerDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_scan_qr"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Escanear código QR del local",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .clickable(onClick = onVolverModoNegocio)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Volver a Modo Negocio / Inicio",
                        color = TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    } else {
        // =========================================================================
        // PANTALLA DEL LOCAL CONECTADO (DATOS 100% REALES DESDE FIREBASE FIRESTORE)
        // =========================================================================
        val biz = negocioEncontrado ?: Business(nombre = "Local Conectado")

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianDeep)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Top Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CitaYaLogoMark(size = 32.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("citaya", color = SunsetOrange, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ObsidianCard)
                                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                    .clickable {
                                        conectadoAlLocal = false
                                        negocioEncontrado = null
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Cambiar Local", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SunsetOrange)
                                    .clickable(onClick = onVolverModoNegocio)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Inicio", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Saludo al cliente
                item {
                    Column {
                        Text(
                            text = "¡Hola, $nombreCliente!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "Estás conectado a ${biz.nombre} (Código: ${biz.codigoUnico})",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }

                // Tarjeta del Negocio
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(22.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        color = ObsidianSurface
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(biz.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextLight)
                                    Text(biz.categoria, fontSize = 13.sp, color = SunsetOrange)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SunsetOrange)
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(biz.codigoUnico, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            if (biz.direccion.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${biz.direccion}, ${biz.ciudad}", fontSize = 13.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }

                // Lista de Barberos o Estado Vacío
                item {
                    Text(
                        text = "Barberos Disponibles:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                }

                if (barberosDelNegocio.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = ObsidianSurface
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Este establecimiento aún no tiene barberos activos en su equipo.",
                                    fontSize = 14.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        // Permite reservar con cita directa general
                                        bookingDialogBarbero = Barbero(
                                            id = "b_general",
                                            nombre = "Turno General / Próximo Disponible",
                                            activo = true
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Reservar Turno General", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(barberosDelNegocio.filter { it.activo }) { b ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(3.dp, RoundedCornerShape(18.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = ObsidianSurface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(ObsidianElevated, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(b.avatarInitials.ifBlank { "B" }, color = SunsetOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(b.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextLight)
                                    Text(b.especialidad, fontSize = 12.sp, color = TextMuted)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Star, contentDescription = null, tint = GoldVip, modifier = Modifier.size(13.dp))
                                        Text(" ${b.rating} • Disponible", fontSize = 12.sp, color = MintGreenText, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Button(
                                    onClick = { bookingDialogBarbero = b },
                                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("btn_book_client_${b.id}")
                                ) {
                                    Text("Reservar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Modal de Reserva con Validación de Choque en Firebase
    bookingDialogBarbero?.let { barbero ->
        val currentBiz = negocioEncontrado ?: Business()
        var clienteTel by remember { mutableStateOf("") }
        var servicioSeleccionado by remember { mutableStateOf(serviciosDisponibles.first()) }
        var horaSeleccionada by remember { mutableStateOf("02:00 PM") }
        var errorConflicto by remember { mutableStateOf<String?>(null) }
        var guardandoCita by remember { mutableStateOf(false) }

        val horasDisponibles = listOf("09:30 AM", "11:30 AM", "01:30 PM", "02:00 PM", "03:30 PM", "04:30 PM", "06:00 PM")

        Dialog(onDismissRequest = { bookingDialogBarbero = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ObsidianSurface,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Reservar en ${currentBiz.nombre}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextLight)
                            Text("Barbero: ${barbero.nombre}", fontSize = 13.sp, color = TextMuted)
                        }
                        IconButton(onClick = { bookingDialogBarbero = null }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = clienteTel,
                        onValueChange = { clienteTel = it; errorConflicto = null },
                        label = { Text("Tu número de WhatsApp") },
                        placeholder = { Text("8295551234", color = TextMuted.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = BorderLight,
                            focusedLabelColor = SunsetOrange,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Selecciona Hora:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                        items(horasDisponibles) { h ->
                            val isSelected = h == horaSeleccionada
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SunsetOrange else ObsidianCard)
                                    .clickable { horaSeleccionada = h; errorConflicto = null }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(h, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Servicio: ${servicioSeleccionado.first}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
                    Text("Precio: RD$ ${String.format("%,.0f", servicioSeleccionado.second)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SunsetOrange)

                    errorConflicto?.let {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(it, color = SunsetOrange, fontSize = 12.sp, lineHeight = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (clienteTel.isBlank()) {
                                errorConflicto = "Por favor ingresa tu número de teléfono"
                                return@Button
                            }
                            guardandoCita = true
                            errorConflicto = null

                            coroutineScope.launch {
                                val res = repository.crearCitaDesdeCliente(
                                    negocioId = currentBiz.id,
                                    clienteNombre = nombreCliente.ifBlank { "Cliente CitaYa" },
                                    clienteTelefono = clienteTel,
                                    barberoId = barbero.id,
                                    barberoNombre = barbero.nombre,
                                    servicio = servicioSeleccionado.first,
                                    precio = servicioSeleccionado.second,
                                    hora = horaSeleccionada
                                )
                                guardandoCita = false

                                when (res) {
                                    is ResultadoOperacionCita.ConflictoHorario -> {
                                        errorConflicto = res.mensaje
                                    }
                                    is ResultadoOperacionCita.LimiteAlcanzado -> {
                                        errorConflicto = res.mensaje
                                    }
                                    ResultadoOperacionCita.Exito -> {
                                        val mensajeWa = "¡Hola ${nombreCliente.ifBlank { "Cliente" }}! Tu cita en ${currentBiz.nombre} para ${servicioSeleccionado.first} con ${barbero.nombre} a las $horaSeleccionada ha sido agendada con éxito."
                                        repository.enviarMensajeWhatsApp(context, clienteTel, mensajeWa)
                                        confirmacionMensaje = "¡Cita reservada y guardada en Firebase Firestore! Mensaje enviado a WhatsApp ($0 costo)."
                                        bookingDialogBarbero = null
                                    }
                                }
                            }
                        },
                        enabled = !guardandoCita,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (guardandoCita) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Confirmar y Enviar a WhatsApp", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }

    // Modal de Escaneo QR
    if (showQrScannerDialog) {
        var qrInputSimulado by remember { mutableStateOf("") }
        var escaneandoError by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = { showQrScannerDialog = false }) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = ObsidianSurface,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.QrCode, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Escanear Código QR", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextLight)
                    Text("Apunta tu cámara al código QR del salón o ingresa el código detectado:", fontSize = 13.sp, color = TextMuted, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = qrInputSimulado,
                        onValueChange = { qrInputSimulado = it.uppercase().trim() },
                        label = { Text("Código Detectado por QR") },
                        placeholder = { Text("Ej. CY1234") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = BorderLight,
                            focusedLabelColor = SunsetOrange,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    escaneandoError?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(err, color = SunsetOrange, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (qrInputSimulado.isBlank()) {
                                escaneandoError = "Por favor ingresa o escanea un código"
                                return@Button
                            }
                            coroutineScope.launch {
                                val biz = repository.buscarNegocioPorCodigo(qrInputSimulado)
                                if (biz != null) {
                                    codigoLocal = qrInputSimulado
                                    negocioEncontrado = biz
                                    barberosDelNegocio = repository.cargarBarberosDeNegocio(biz.id)
                                    conectadoAlLocal = true
                                    showQrScannerDialog = false
                                } else {
                                    escaneandoError = "Código QR no válido. No existe ningún local con '$qrInputSimulado'."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Verificar y Conectar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Mensaje de Confirmación
    confirmacionMensaje?.let { msg ->
        Dialog(onDismissRequest = { confirmacionMensaje = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ObsidianSurface,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MintGreenText, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(msg, fontSize = 14.sp, color = TextLight, textAlign = TextAlign.Center, lineHeight = 19.sp)
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { confirmacionMensaje = null },
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
