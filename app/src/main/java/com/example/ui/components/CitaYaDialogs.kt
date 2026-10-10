package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Barbero
import com.example.model.Cita
import com.example.model.Cliente
import com.example.model.PlanType
import com.example.model.ProductoInventario
import com.example.model.VentaAdicional
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CobaltBlueLight
import com.example.ui.theme.GoldVip
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenText
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SunsetOrangeLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarmSand

/**
 * Modal de Venta Cruzada al pulsar [✓ Sí Vino] (Módulo VIP CitaYa)
 */
@Composable
fun CrossSellDialog(
    cita: Cita,
    inventario: List<ProductoInventario>,
    onDismiss: () -> Unit,
    onConfirm: (List<VentaAdicional>) -> Unit
) {
    val selectedProducts = remember { mutableStateMapOf<String, Int>() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GoldVip.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = GoldVip, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Venta Cruzada VIP", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
                            Text("Cliente: ${cita.clienteNombre}", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Añade productos de cuidado o bebidas a este cobro antes de finalizar:",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inventario) { prod ->
                        val qty = selectedProducts[prod.id] ?: 0
                        val isSelected = qty > 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) SunsetOrange else BorderLight,
                                    RoundedCornerShape(14.dp)
                                )
                                .background(if (isSelected) SunsetOrangeLight else WarmSand)
                                .clickable {
                                    if (qty == 0) selectedProducts[prod.id] = 1 else selectedProducts.remove(prod.id)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.nombre, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                                Text("Stock: ${prod.stock} disp.", fontSize = 11.sp, color = TextMuted)
                            }

                            Text(
                                "RD$ ${String.format("%,.2f", prod.precioVenta)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SunsetOrange else TextDark
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .background(SunsetOrange, CircleShape)
                                            .clickable {
                                                selectedProducts[prod.id] = qty + 1
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                val totalAdicional = selectedProducts.entries.sumOf { entry ->
                    val prod = inventario.find { it.id == entry.key }
                    (prod?.precioVenta ?: 0.0) * entry.value
                }
                val totalFinal = cita.precio + totalAdicional

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total a cobrar:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text(
                        "RD$ ${String.format("%,.2f", totalFinal)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunsetOrange
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onConfirm(emptyList()) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Solo Servicio", fontSize = 12.sp, color = TextMuted)
                    }

                    Button(
                        onClick = {
                            val ventas = selectedProducts.mapNotNull { entry ->
                                val p = inventario.find { it.id == entry.key } ?: return@mapNotNull null
                                VentaAdicional(
                                    id = "va_${System.currentTimeMillis()}_${p.id}",
                                    productoNombre = p.nombre,
                                    precio = p.precioVenta,
                                    cantidad = entry.value
                                )
                            }
                            onConfirm(ventas)
                        },
                        modifier = Modifier.weight(1.3f).testTag("btn_confirm_cross_sell"),
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Confirmar Cobro", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Modal rápido para registrar cita presencial Walk-In
 */
@Composable
fun WalkInDialog(
    barberos: List<Barbero>,
    onDismiss: () -> Unit,
    onCrearCita: (cliente: String, tel: String, barberoId: String, servicio: String, precio: Double, hora: String) -> Unit
) {
    var clienteNombre by remember { mutableStateOf("") }
    var clienteTelefono by remember { mutableStateOf("") }
    var selectedBarberoId by remember { mutableStateOf(barberos.firstOrNull { it.activo }?.id ?: "") }
    var servicio by remember { mutableStateOf("Corte Clásico & Barba") }
    var precioTexto by remember { mutableStateOf("600") }
    var hora by remember { mutableStateOf("10:30 AM") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SunsetOrangeLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.ContentCut, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Cita Presencial (Walk-In)", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = clienteNombre,
                    onValueChange = { clienteNombre = it },
                    label = { Text("Nombre del Cliente") },
                    placeholder = { Text("Ej. Juan Rodríguez") },
                    modifier = Modifier.fillMaxWidth().testTag("input_walkin_client"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = clienteTelefono,
                    onValueChange = { clienteTelefono = it },
                    label = { Text("Teléfono WhatsApp") },
                    placeholder = { Text("Ej. 8295551234") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Barbero Asignado:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    barberos.filter { it.activo }.take(3).forEach { b ->
                        val isSelected = b.id == selectedBarberoId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ObsidianDeep else WarmSand)
                                .clickable { selectedBarberoId = b.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(b.nombre.split(" ").first(), color = if (isSelected) Color.White else TextDark, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = hora,
                        onValueChange = { hora = it },
                        label = { Text("Hora") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = precioTexto,
                        onValueChange = { precioTexto = it },
                        label = { Text("Precio (RD$)") },
                        modifier = Modifier.weight(1f).testTag("input_walkin_price"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val precio = precioTexto.toDoubleOrNull() ?: 500.0
                        onCrearCita(clienteNombre, clienteTelefono, selectedBarberoId, servicio, precio, hora)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_save_walkin"),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Registrar Cita Ahora", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

/**
 * Paywall bloqueante cuando se alcanza el límite de 20 citas en Plan Gratis
 */
@Composable
fun PaywallDialog(
    mensaje: String,
    onDismiss: () -> Unit,
    onUpgrade: (PlanType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = PureWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(SunsetOrangeLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.RocketLaunch,
                        contentDescription = null,
                        tint = SunsetOrange,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Límite Mensual Alcanzado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mensaje,
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Card Plan Pro
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUpgrade(PlanType.PRO) },
                    colors = CardDefaults.cardColors(containerColor = WarmSand),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Plan Pro CitaYa", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Text("Citas ilimitadas • Hasta 4 barberos", fontSize = 11.sp, color = TextMuted)
                        }
                        Text("RD$ 1,499/m", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CobaltBlue)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card Plan VIP
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUpgrade(PlanType.VIP) },
                    colors = CardDefaults.cardColors(containerColor = ObsidianDeep),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Plan VIP Completo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("POPULAR", fontSize = 9.sp, color = GoldVip, fontWeight = FontWeight.Bold)
                            }
                            Text("Barberos ilimitados • Venta cruzada", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                        Text("RD$ 2,999/m", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GoldVip)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onUpgrade(PlanType.PRO) },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_upgrade_paywall"),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Actualizar a Plan Pro", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Continuar en Modo Consulta", fontSize = 12.sp, color = TextMuted)
                }
            }
        }
    }
}

/**
 * Ficha detallada por cliente en CRM con historial y botón WhatsApp
 */
@Composable
fun ClientDetailDialog(
    cliente: Cliente,
    onDismiss: () -> Unit,
    onSendWhatsApp: (tel: String, msg: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(CobaltBlueLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cliente.nombre.take(1),
                                color = CobaltBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(cliente.nombre, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
                            Text(cliente.correo, fontSize = 12.sp, color = TextMuted)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarmSand, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Gastado", fontSize = 11.sp, color = TextMuted)
                        Text("RD$ ${String.format("%,.0f", cliente.totalGastado)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Visitas", fontSize = 11.sp, color = TextMuted)
                        Text("${cliente.visitasCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Estado", fontSize = 11.sp, color = TextMuted)
                        Text(cliente.categoria.label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SunsetOrange)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Detalles de preferencia:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Text("• Barbero preferido: ${cliente.barberoFavorito}", fontSize = 12.sp, color = TextMuted)
                Text("• Servicio habitual: ${cliente.servicioFavorito}", fontSize = 12.sp, color = TextMuted)
                Text("• Última cita: ${cliente.ultimaVisita}", fontSize = 12.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val mensaje = "¡Hola ${cliente.nombre}! Te saludamos de King Barbershop. Hace días que no te vemos y queremos que luzcas impecable. ¿Te agendamos un espacio esta semana con ${cliente.barberoFavorito}?"
                        onSendWhatsApp(cliente.telefono, mensaje)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_client_whatsapp"),
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreenText),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reactivar por WhatsApp ($0)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
