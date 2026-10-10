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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CitaYaRepository
import com.example.data.ResultadoOperacionCita
import com.example.model.Cita
import com.example.model.EstadoCita
import com.example.model.PlanType
import com.example.ui.components.CitaYaBrandHeader
import com.example.ui.components.CrossSellDialog
import com.example.ui.components.PaywallDialog
import com.example.ui.components.WalkInDialog
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenText
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftBlueBtn
import com.example.ui.theme.SoftBlueBtnText
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SunsetOrangeLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandDarker

@Composable
fun PanelScreen(
    repository: CitaYaRepository,
    modifier: Modifier = Modifier,
    onNavigateToPlan: () -> Unit = {}
) {
    val context = LocalContext.current
    val business by repository.business.collectAsState()
    val citas by repository.citas.collectAsState()
    val barberos by repository.barberos.collectAsState()
    val inventario by repository.inventario.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf("Hoy") }

    // Dialog States
    var showWalkInDialog by remember { mutableStateOf(false) }
    var crossSellCitaTarget by remember { mutableStateOf<Cita?>(null) }
    var paywallErrorMessage by remember { mutableStateOf<String?>(null) }
    var showBusinessCodeDialog by remember { mutableStateOf(false) }

    val dateFilters = listOf("Mes", "Hoy", "Semana", "Semana +1", "Fecha")

    val citasFiltradas = remember(citas, searchQuery, selectedDateFilter) {
        citas.filter { cita ->
            val matchQuery = searchQuery.isBlank() ||
                    cita.clienteNombre.contains(searchQuery, ignoreCase = true) ||
                    cita.barberoNombre.contains(searchQuery, ignoreCase = true) ||
                    cita.clienteTelefono.contains(searchQuery, ignoreCase = true)
            matchQuery
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmSand)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Top Bar: CitaYa brand + Notification & Business Code Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CitaYaBrandHeader(logoSize = 28.dp, fontSize = 24)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick 6-char Code Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(WarmSandDarker)
                                .clickable { showBusinessCodeDialog = true }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.QrCode, contentDescription = null, tint = TextDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(business.codigoUnico, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PureWhite, CircleShape)
                                .border(1.dp, BorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = TextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Subtitle greeting
            item {
                Column {
                    Text(
                        text = "Hola, ${business.nombre}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "${barberos.count { it.activo }} barberos activos • Plan: ${business.plan.label}",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Hero Widget: INGRESOS HOY (Obsidian Deep Card)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = ObsidianCard
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INGRESOS HOY:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.6f),
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(CobaltBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.BarChart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "RD$ ${String.format("%,.2f", business.ingresosHoy)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunsetOrange,
                            letterSpacing = (-0.5).sp
                        )
                    }
                }
            }

            // Section Header: Próximas citas & Dropdown pill
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Próximas citas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(WarmSandDarker)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Todas citas...",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Buscar por nombre, teléfono, correo o código...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_universal"),
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = SunsetOrange,
                        unfocusedBorderColor = BorderLight
                    ),
                    singleLine = true
                )
            }

            // Date Filters Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateFilters) { filter ->
                        val isSelected = filter == selectedDateFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) ObsidianDeep else PureWhite)
                                .border(1.dp, if (isSelected) ObsidianDeep else BorderLight, RoundedCornerShape(18.dp))
                                .clickable { selectedDateFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else TextDark,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Appointments List
            if (citasFiltradas.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = ObsidianSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = SunsetOrange,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No hay citas registradas hoy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Toca el botón + para registrar una cita walk-in o comparte tu código ${business.codigoUnico} con tus clientes para que reserven desde su celular.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                items(citasFiltradas, key = { it.id }) { cita ->
                    AppointmentCard(
                        cita = cita,
                        onSiVino = {
                            if (business.plan == PlanType.VIP || business.plan == PlanType.VIP_TRIAL) {
                                crossSellCitaTarget = cita
                            } else {
                                val res = repository.registrarSiVino(cita.id)
                                if (res is ResultadoOperacionCita.LimiteAlcanzado) {
                                    paywallErrorMessage = res.mensaje
                                }
                            }
                        },
                        onNoVino = {
                            repository.registrarNoVino(cita.id)
                        },
                        onWhatsApp = {
                            val msg = "¡Hola ${cita.clienteNombre}! Tu cita en ${business.nombre} para ${cita.servicio} con ${cita.barberoNombre} el ${cita.fecha} a las ${cita.hora} está confirmada. ¡Te esperamos!"
                            repository.enviarMensajeWhatsApp(context, cita.clienteTelefono, msg)
                        }
                    )
                }
            }

            // Bottom Spacing for floating capsule navigation
            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        // Floating Action Button in Sunset Orange for Walk-In (Bottom Right)
        FloatingActionButton(
            onClick = { showWalkInDialog = true },
            containerColor = SunsetOrange,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 86.dp)
                .shadow(6.dp, CircleShape)
                .testTag("fab_walkin")
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Registrar Walk-In", modifier = Modifier.size(26.dp))
        }

        // Dialogs
        if (showWalkInDialog) {
            WalkInDialog(
                barberos = barberos,
                onDismiss = { showWalkInDialog = false },
                onCrearCita = { cliente, tel, barberoId, serv, precio, hora ->
                    val res = repository.crearCitaWalkIn(cliente, tel, barberoId, serv, precio, hora)
                    if (res is ResultadoOperacionCita.LimiteAlcanzado) {
                        paywallErrorMessage = res.mensaje
                    } else if (res is ResultadoOperacionCita.ConflictoHorario) {
                        paywallErrorMessage = res.mensaje
                    }
                    showWalkInDialog = false
                }
            )
        }

        crossSellCitaTarget?.let { targetCita ->
            CrossSellDialog(
                cita = targetCita,
                inventario = inventario,
                onDismiss = { crossSellCitaTarget = null },
                onConfirm = { ventasAdicionales ->
                    val res = repository.registrarSiVino(targetCita.id, ventasAdicionales)
                    if (res is ResultadoOperacionCita.LimiteAlcanzado) {
                        paywallErrorMessage = res.mensaje
                    }
                    crossSellCitaTarget = null
                }
            )
        }

        paywallErrorMessage?.let { msg ->
            PaywallDialog(
                mensaje = msg,
                onDismiss = { paywallErrorMessage = null },
                onUpgrade = { plan ->
                    repository.actualizarPlan(plan)
                    paywallErrorMessage = null
                }
            )
        }

        if (showBusinessCodeDialog) {
            BusinessCodeModal(
                business = business,
                onDismiss = { showBusinessCodeDialog = false }
            )
        }
    }
}

@Composable
private fun AppointmentCard(
    cita: Cita,
    onSiVino: () -> Unit,
    onNoVino: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = PureWhite
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time box (Warm greyish container on left)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarmSand)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = cita.hora,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "${cita.duracionMinutos}m",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Avatar initials circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(CobaltBlue.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cita.clienteNombre.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        color = CobaltBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Name and Barber
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cita.clienteNombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextDark
                    )
                    Text(
                        text = cita.barberoNombre,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Price & Status Badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RD$ ${String.format("%,.2f", cita.totalCobrado)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    val (bgColor, textColor) = when (cita.estado) {
                        EstadoCita.CONFIRMADA -> MintGreen to MintGreenText
                        EstadoCita.PENDIENTE -> WarmSandDarker to TextMuted
                        EstadoCita.SI_VINO -> MintGreen to MintGreenText
                        EstadoCita.NO_VINO -> SoftBlueBtn to SoftBlueBtnText
                        EstadoCita.CANCELADA -> SunsetOrangeLight to SunsetOrange
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = cita.estado.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }
                }
            }

            // Quick Actions if pending or confirmed
            if (cita.estado == EstadoCita.CONFIRMADA || cita.estado == EstadoCita.PENDIENTE) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // [✓ Sí Vino] Button (Mint Green)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MintGreen)
                            .clickable(onClick = onSiVino)
                            .testTag("btn_si_vino_${cita.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = MintGreenText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✓ Sí Vino",
                                color = MintGreenText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // [✗ No Vino] Button (Soft Blue)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftBlueBtn)
                            .clickable(onClick = onNoVino)
                            .testTag("btn_no_vino_${cita.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = SoftBlueBtnText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✕ No Vino",
                                color = SoftBlueBtnText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Quick WhatsApp wa.me trigger ($0 Cost)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarmSandDarker)
                            .clickable(onClick = onWhatsApp)
                            .testTag("btn_wa_${cita.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Chat,
                            contentDescription = "WhatsApp",
                            tint = MintGreenText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else if (cita.ventasAdicionales.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "+ Venta Cruzada: " + cita.ventasAdicionales.joinToString(", ") { "${it.productoNombre} x${it.cantidad}" },
                    fontSize = 11.sp,
                    color = SunsetOrange,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BusinessCodeModal(
    business: com.example.model.Business,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vinculación de Negocio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(WarmSand, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.QrCode, contentDescription = "QR Code", tint = ObsidianDeep, modifier = Modifier.size(64.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("CÓDIGO ÚNICO (6 CARACTERES):", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Text(
                    text = business.codigoUnico,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SunsetOrange,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tus clientes pueden ingresar este código en la pantalla de inicio o escanear el QR para reservar al instante.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
