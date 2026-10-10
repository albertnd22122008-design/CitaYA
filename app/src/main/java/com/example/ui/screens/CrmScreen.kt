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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.model.CategoriaCliente
import com.example.model.Cliente
import com.example.ui.components.CitaYaBrandHeader
import com.example.ui.components.ClientDetailDialog
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CobaltBlueLight
import com.example.ui.theme.MintGreenText
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SunsetOrangeLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandDarker

@Composable
fun CrmScreen(
    repository: CitaYaRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val business by repository.business.collectAsState()
    val clientes by repository.clientes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CategoriaCliente.TODOS) }
    var selectedSubFilter by remember { mutableStateOf("Clientes") }
    var selectedClientDetail by remember { mutableStateOf<Cliente?>(null) }

    val subFilters = listOf("Clientes", "Recuperar", "Cumpleaños", "Mejores", "Crédito", "Historial")
    val categoryChips = listOf(
        CategoriaCliente.TODOS,
        CategoriaCliente.VIP,
        CategoriaCliente.NUEVO,
        CategoriaCliente.ACTIVO,
        CategoriaCliente.EN_RIESGO,
        CategoriaCliente.INACTIVO,
        CategoriaCliente.LE_TOCA_VOLVER
    )

    val clientesFiltrados = remember(clientes, searchQuery, selectedCategory, selectedSubFilter) {
        clientes.filter { cl ->
            val matchCategory = when (selectedCategory) {
                CategoriaCliente.TODOS -> true
                else -> cl.categoria == selectedCategory
            }
            val matchSub = when (selectedSubFilter) {
                "Recuperar" -> cl.categoria == CategoriaCliente.EN_RIESGO || cl.categoria == CategoriaCliente.INACTIVO
                "Mejores" -> cl.categoria == CategoriaCliente.VIP || cl.totalGastado > 20000
                else -> true
            }
            val matchQuery = searchQuery.isBlank() ||
                    cl.nombre.contains(searchQuery, ignoreCase = true) ||
                    cl.correo.contains(searchQuery, ignoreCase = true) ||
                    cl.telefono.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSub && matchQuery
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
            // Header Top Bar: CitaYa logo + "CRM Clientes"
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
                            text = "CRM",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }
            }

            // Title: Clientes
            item {
                Text(
                    text = "Clientes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    letterSpacing = (-0.5).sp
                )
            }

            // 4 Metrics Grid (2x2) matching Screen 3 in reference image
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: Clientes (33) & Activos (12) in Obsidian Deep
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 1: 33 Clientes (Obsidian)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(4.dp, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = ObsidianCard
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("33", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Clientes", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        // Card 2: 12 Activos (Obsidian)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(4.dp, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = ObsidianCard
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("12", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Activos", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }

                    // Row 2: En riesgo (17) & Inactivos (4) in Pure White with Sunset Orange numbers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 3: 17 En riesgo
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(3.dp, RoundedCornerShape(18.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = PureWhite
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("17", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SunsetOrange)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("En riesgo", fontSize = 12.sp, color = TextMuted)
                            }
                        }

                        // Card 4: 4 Inactivos
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(3.dp, RoundedCornerShape(18.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = PureWhite
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("4", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SunsetOrange)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Inactivos", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }

            // Retention Banner in Sunset Orange (Screen 3 Reference)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedCategory = CategoriaCliente.EN_RIESGO
                            selectedSubFilter = "Recuperar"
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = SunsetOrange
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "¡Atención!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tienes 14 clientes que podrías recuperar antes de perderlos.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.weight(1f),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ver detalles >",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Sub-filter Pills: [Clientes], [Recuperar], [Cumpleaños], [Mejores], [Crédito], [Historial]
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subFilters) { sub ->
                        val isSelected = sub == selectedSubFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ObsidianDeep else PureWhite)
                                .border(1.dp, if (isSelected) ObsidianDeep else BorderLight, RoundedCornerShape(16.dp))
                                .clickable { selectedSubFilter = sub }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = sub,
                                color = if (isSelected) Color.White else TextDark,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Search input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Buscar por nombre, correo o teléfono...", fontSize = 12.sp, color = TextMuted)
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_crm_clients"),
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

            // Category tag pills: [Todos], [VIP], [Nuevos], [Activos], [En riesgo], [Inactivos], [Le toca volver]
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categoryChips) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) ObsidianDeep else WarmSandDarker)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat.label,
                                color = if (isSelected) Color.White else TextDark,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // List of Clients
            if (clientesFiltrados.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = ObsidianCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(38.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No hay clientes registrados aún",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A medida que tus clientes reserven citas o crees citas walk-in, sus fichas CRM con historial y frecuencia aparecerán aquí.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(clientesFiltrados, key = { it.id }) { cliente ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(18.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(18.dp))
                        .clickable { selectedClientDetail = cliente },
                    shape = RoundedCornerShape(18.dp),
                    color = PureWhite
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    when (cliente.categoria) {
                                        CategoriaCliente.ACTIVO -> CobaltBlueLight
                                        CategoriaCliente.VIP -> SunsetOrangeLight
                                        else -> WarmSandDarker
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cliente.nombre.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = when (cliente.categoria) {
                                    CategoriaCliente.ACTIVO -> CobaltBlue
                                    CategoriaCliente.VIP -> SunsetOrange
                                    else -> TextDark
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Info Column
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cliente.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextDark
                            )
                            Text(
                                text = cliente.correo,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        // Right side stats & WhatsApp trigger
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "RD$ ${String.format("%,.2f", cliente.totalGastado)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextDark
                                )
                                Text(
                                    text = cliente.ultimaVisita,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // One tap WhatsApp button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MintGreenText.copy(alpha = 0.12f))
                                    .clickable {
                                        val msg = "¡Hola ${cliente.nombre}! Te saludamos de ${business.nombre}. ¿Te gustaría apartar tu cita esta semana para tu ${cliente.servicioFavorito}?"
                                        repository.enviarMensajeWhatsApp(context, cliente.telefono, msg)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Chat,
                                    contentDescription = "WhatsApp",
                                    tint = MintGreenText,
                                    modifier = Modifier.size(16.dp)
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

        // Client Details Dialog
        selectedClientDetail?.let { cl ->
            ClientDetailDialog(
                cliente = cl,
                onDismiss = { selectedClientDetail = null },
                onSendWhatsApp = { tel, msg ->
                    repository.enviarMensajeWhatsApp(context, tel, msg)
                    selectedClientDetail = null
                }
            )
        }
    }
}
