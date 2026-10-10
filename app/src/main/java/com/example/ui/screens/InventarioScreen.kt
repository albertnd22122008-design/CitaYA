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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CitaYaRepository
import com.example.model.ProductoInventario
import com.example.ui.components.CitaYaBrandHeader
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CobaltBlueLight
import com.example.ui.theme.GoldVip
import com.example.ui.theme.MintGreenText
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SunsetOrangeLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandDarker

@Composable
fun InventarioScreen(
    repository: CitaYaRepository,
    modifier: Modifier = Modifier
) {
    val inventario by repository.inventario.collectAsState()
    var showRestockDialogFor by remember { mutableStateOf<ProductoInventario?>(null) }
    var showSellDialogFor by remember { mutableStateOf<ProductoInventario?>(null) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    val valorTotalStock = remember(inventario) {
        inventario.sumOf { it.precioVenta * it.stock }
    }
    val productosBajoStock = remember(inventario) {
        inventario.filter { it.stock <= it.stockMinimo }
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
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CitaYaBrandHeader(logoSize = 28.dp, fontSize = 22)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldVip.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Módulo VIP ($0)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                    }
                }
            }

            item {
                Text(
                    text = "Control de Inventario",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    letterSpacing = (-0.5).sp
                )
            }

            // Valuation Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = ObsidianCard
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "VALORACIÓN TOTAL DEL STOCK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "RD$ ${String.format("%,.2f", valorTotalStock)}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldVip
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${inventario.sumOf { it.stock }} unidades disponibles en tienda",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Low Stock Warning if any
            if (productosBajoStock.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SunsetOrange, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = SunsetOrangeLight
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Alerta de Stock Mínimo", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SunsetOrange)
                                Text(
                                    "${productosBajoStock.size} productos están por agotarse: ${productosBajoStock.joinToString { it.nombre }}",
                                    fontSize = 11.sp,
                                    color = TextDark
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Productos en Stock",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SunsetOrange)
                            .clickable { showAddProductDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("+ Producto", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (inventario.isEmpty()) {
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
                            Icon(Icons.Filled.Inventory2, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Aún no tienes productos en inventario",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Agrega ceras, geles, aceites o bebidas para ofrecer venta cruzada a tus clientes en cada cita.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showAddProductDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Agregar Primer Producto", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(inventario) { prod ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(18.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        color = ObsidianCard
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(WarmSand, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Inventory2, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Text(prod.categoria, fontSize = 11.sp, color = TextMuted)
                            Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Stock: ${prod.stock} u.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (prod.stock <= prod.stockMinimo) SunsetOrange else MintGreenText
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Costo: RD$ ${prod.costo.toInt()}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "RD$ ${String.format("%,.0f", prod.precioVenta)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SunsetOrangeLight)
                                        .clickable { showSellDialogFor = prod }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Vender", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SunsetOrange)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CobaltBlueLight)
                                        .clickable { showRestockDialogFor = prod }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+ Stock", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CobaltBlue)
                                }
                            }
                        }
                    }
                }
            }
        }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        // Sell quick dialog
        showSellDialogFor?.let { prod ->
            Dialog(onDismissRequest = { showSellDialogFor = null }) {
                Surface(shape = RoundedCornerShape(20.dp), color = PureWhite, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Venta Rápida de Producto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Text(prod.nombre, fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Precio a cobrar: RD$ ${prod.precioVenta}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SunsetOrange)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                repository.venderProducto(prod.id, 1)
                                showSellDialogFor = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirmar Venta (1 u.)", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Restock quick dialog
        showRestockDialogFor?.let { prod ->
            Dialog(onDismissRequest = { showRestockDialogFor = null }) {
                Surface(shape = RoundedCornerShape(20.dp), color = PureWhite, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Reabastecer Producto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Text(prod.nombre, fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                repository.reabastecerProducto(prod.id, 5)
                                showRestockDialogFor = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CobaltBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Añadir +5 Unidades", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        if (showAddProductDialog) {
            AddProductDialog(
                onDismiss = { showAddProductDialog = false },
                onAddProduct = { nombre, cat, precio, costo, stock ->
                    repository.agregarProducto(nombre, cat, precio, costo, stock)
                    showAddProductDialog = false
                }
            )
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddProduct: (String, String, Double, Double, Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Cuidado Capilar") }
    var precioStr by remember { mutableStateOf("") }
    var costoStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("10") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = ObsidianCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Agregar Producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = null },
                    label = { Text("Nombre del Producto") },
                    placeholder = { Text("Ej. Cera Mate Fijadora") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = precioStr,
                        onValueChange = { precioStr = it },
                        label = { Text("Precio RD$") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = costoStr,
                        onValueChange = { costoStr = it },
                        label = { Text("Costo RD$") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text("Stock Inicial") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = SunsetOrange, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (nombre.isBlank() || precioStr.isBlank()) {
                            error = "Ingresa al menos el nombre y precio"
                            return@Button
                        }
                        val precio = precioStr.toDoubleOrNull() ?: 0.0
                        val costo = costoStr.toDoubleOrNull() ?: (precio * 0.5)
                        val stock = stockStr.toIntOrNull() ?: 5
                        onAddProduct(nombre, categoria, precio, costo, stock)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar Producto", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
