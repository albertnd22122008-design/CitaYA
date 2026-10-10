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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CitaYaRepository
import com.example.ui.components.CashFlowLineChart
import com.example.ui.components.CitaYaBrandHeader
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
fun DashboardScreen(
    repository: CitaYaRepository,
    modifier: Modifier = Modifier
) {
    val business by repository.business.collectAsState()
    val periods = listOf("Ayer", "Esta semana", "Este mes", "Este año")
    var selectedPeriod by remember { mutableStateOf("Este año") }

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
            // Header Top Bar: Dashboard Title + Business Selector + Filter Icon
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Dashboard",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ObsidianDeep)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Business, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Negocios", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        Text(
                            text = "AgendateRD • ${business.nombre}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PureWhite, CircleShape)
                            .border(1.dp, BorderLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Tune, contentDescription = "Filtros", tint = TextDark, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Period Selector Pills: [Ayer], [Esta semana], [Este mes], [Este año]
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    items(periods) { period ->
                        val isSelected = period == selectedPeriod
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) ObsidianDeep else PureWhite)
                                .border(1.dp, if (isSelected) ObsidianDeep else BorderLight, RoundedCornerShape(18.dp))
                                .clickable { selectedPeriod = period }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = period,
                                color = if (isSelected) Color.White else TextDark,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Section Label: $ FINANZAS
            item {
                Text(
                    text = "$ FINANZAS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }

            // Hero Card: BALANCE NETO (Obsidian Deep #111111)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = ObsidianCard
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "BALANCE NETO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "RD$${String.format("%,.2f", business.balanceNeto)}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = MintGreenText, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+ RD$73,262.00 vs año anterior",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MintGreenText
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Dinero recibido: RD$${String.format("%,.0f", business.dineroRecibido)}",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "En espera: RD$${String.format("%,.0f", business.dineroEnEspera)}",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Cash Flow Section (FLUJO DE CAJA Card with Vector Canvas Line Chart)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(22.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "FLUJO DE CAJA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Dinero que entra y gastos del periodo",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "RD$ 59,965.00",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Vector Canvas Line Chart with gradient fill
                        CashFlowLineChart()

                        Spacer(modifier = Modifier.height(14.dp))

                        // Legend Breakdown
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(CobaltBlue, CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Dinero recibido", fontSize = 11.sp, color = TextMuted)
                                }
                                Text("RD$ 115,665.00", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(SunsetOrange, CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Gastos operacionales", fontSize = 11.sp, color = TextMuted)
                                }
                                Text("RD$ 59,005.00", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFB0B0B0), CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Recibido - año anterior", fontSize = 11.sp, color = TextMuted)
                                }
                                Text("RD$ 0.00", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                        }
                    }
                }
            }

            // Lower Grid: INGRESOS POTENCIALES & DINERO RECIBIDO (Screen 2 reference)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card Left: INGRESOS POT.
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(3.dp, RoundedCornerShape(18.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "INGRESOS POT.",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "RD$18,325.00",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SunsetOrange
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Este año",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "▲ RD$18,325.00",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintGreenText
                            )
                        }
                    }

                    // Card Right: $ DINERO RECIBIDO
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(3.dp, RoundedCornerShape(18.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "$ DINERO RECIBIDO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "RD$59,965.00",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CobaltBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Citas abonadas • Incl.",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "RD$50,005.00 de todas",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark
                            )
                        }
                    }
                }
            }

            // Bottom Spacing for floating capsule navigation
            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}
