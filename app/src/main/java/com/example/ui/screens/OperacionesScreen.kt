package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CitaYaRepository
import com.example.ui.components.AppointmentsBarChart
import com.example.ui.components.CitaYaBrandHeader
import com.example.ui.components.OccupancyRadialGauge
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CobaltBlueLight
import com.example.ui.theme.GoldVip
import com.example.ui.theme.MintGreenText
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PeachAlert
import com.example.ui.theme.PeachAlertBorder
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SunsetOrangeLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandDarker

@Composable
fun OperacionesScreen(
    repository: CitaYaRepository,
    modifier: Modifier = Modifier
) {
    val business by repository.business.collectAsState()
    val barberos by repository.barberos.collectAsState()
    var selectedSegment by remember { mutableStateOf("Summary") }
    var showAddBarberDialog by remember { mutableStateOf(false) }
    val segments = listOf("Summary", "Equipo", "Demanda")

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
            // Header: CitaYa logo + Operaciones
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CitaYaBrandHeader(logoSize = 28.dp, fontSize = 22)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Operaciones",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }
            }

            // Title: Ocupación
            item {
                Text(
                    text = "Ocupación",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    letterSpacing = (-0.5).sp
                )
            }

            // Hero Card: Ocupación Actual with Semi-Circle Gauge (Screen 4 Reference)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(22.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = PureWhite
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Semi-circular gauge (73%)
                        OccupancyRadialGauge(percentage = business.tasaOcupacion)

                        Spacer(modifier = Modifier.width(14.dp))

                        // Details & Barber avatars on right
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ocupación actual",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Tasa de ocupación en tiempo real",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Overlapping Barber Avatars row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                barberos.take(4).forEachIndexed { index, b ->
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, PureWhite, CircleShape)
                                            .background(
                                                when (index) {
                                                    0 -> CobaltBlue
                                                    1 -> SunsetOrange
                                                    2 -> GoldVip
                                                    else -> ObsidianDeep
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(b.avatarInitials, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(3.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Segmented Tab Control: [Summary], [Equipo], [Demanda]
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarmSandDarker, RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    segments.forEach { seg ->
                        val isSelected = seg == selectedSegment
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PureWhite else Color.Transparent)
                                .clickable { selectedSegment = seg }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = seg,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextDark else TextMuted
                            )
                        }
                    }
                }
            }

            // Absence Rate Alert Card (PeachAlert #FFEFEA in Screen 4)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PeachAlertBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = PeachAlert
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = SunsetOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tasa de ausencias",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SunsetOrange
                                )
                            }
                            Text(
                                text = "Ver detalles",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunsetOrange
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${business.tasaAusencias} tasa de ausencias, tasa: 50% del total de citas (50 de 100).",
                            fontSize = 11.sp,
                            color = TextDark.copy(alpha = 0.8f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Trend Card: TENDENCIA DE CITAS (Screen 4 Reference)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(20.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TENDENCIA DE CITAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Citas por día (hasta hoy)",
                            fontSize = 11.sp,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Vector Bar Chart
                        AppointmentsBarChart()
                    }
                }
            }

            // Bottom 2 KPI Cards: PRODUCTIVIDAD & AUSENCIAS (Screen 4 Reference)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: PRODUCTIVIDAD
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(2.dp, RoundedCornerShape(18.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "PRODUCTIVIDAD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${business.productividad}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "17% del total",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "▲ +1 vs mes anterior",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MintGreenText
                            )
                        }
                    }

                    // Card 2: AUSENCIAS
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(2.dp, RoundedCornerShape(18.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "AUSENCIAS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "00",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "50% del total",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "▼ -1 vs mes anterior",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CobaltBlue
                            )
                        }
                    }
                }
            }

            // Barber Performance & Commission Calculation Section (Plan VIP Feature)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Equipo de Barberos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SunsetOrange)
                            .clickable { showAddBarberDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("+ Barbero", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (barberos.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = ObsidianSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Aún no has agregado barberos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Registra a los profesionales de tu local para que tus clientes puedan agendar turnos con ellos.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showAddBarberDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Agregar Primer Barbero", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(barberos) { barbero ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = ObsidianSurface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(if (barbero.activo) CobaltBlueLight else ObsidianElevated, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(barbero.avatarInitials.ifBlank { "B" }, color = if (barbero.activo) CobaltBlue else TextMuted, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(barbero.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (barbero.activo) {
                                        Box(modifier = Modifier.size(7.dp).background(MintGreenText, CircleShape))
                                    }
                                }
                                Text(barbero.especialidad, fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "Comisión: ${barbero.comisionPorcentaje.toInt()}% • Ganancia: RD$ ${String.format("%,.0f", barbero.ingresosHoy * (barbero.comisionPorcentaje / 100))}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SunsetOrange
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${barbero.citasHoy} citas hoy",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextDark
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = GoldVip, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = " ${barbero.rating}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Spacing for floating capsule navigation
            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        if (showAddBarberDialog) {
            AddBarberDialog(
                onDismiss = { showAddBarberDialog = false },
                onAddBarber = { nombre, apodo, tel, esp, com ->
                    repository.agregarBarbero(nombre, apodo, tel, esp, com)
                    showAddBarberDialog = false
                }
            )
        }
    }
}

@Composable
fun AddBarberDialog(
    onDismiss: () -> Unit,
    onAddBarber: (String, String, String, String, Double) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apodo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("Cortes & Barba") }
    var comisionStr by remember { mutableStateOf("50") }
    var error by remember { mutableStateOf<String?>(null) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registrar Barbero",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    androidx.compose.material3.IconButton(onClick = onDismiss) {
                        Icon(androidx.compose.material.icons.Icons.Filled.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = null },
                    label = { Text("Nombre completo") },
                    placeholder = { Text("Ej. José Límoz") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = apodo,
                    onValueChange = { apodo = it },
                    label = { Text("Apodo / Sobrenombre") },
                    placeholder = { Text("Ej. El Artista") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono / WhatsApp") },
                    placeholder = { Text("Ej. 809-555-1234") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = especialidad,
                    onValueChange = { especialidad = it },
                    label = { Text("Especialidad") },
                    placeholder = { Text("Ej. Fade, Barba, Color") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = comisionStr,
                    onValueChange = { comisionStr = it },
                    label = { Text("Comisión %") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = SunsetOrange, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                androidx.compose.material3.Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            error = "Por favor ingresa el nombre del barbero"
                            return@Button
                        }
                        val comision = comisionStr.toDoubleOrNull() ?: 50.0
                        onAddBarber(nombre, apodo, telefono, especialidad, comision)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar Barbero", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
