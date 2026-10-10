package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.TextMuted

enum class AppTab(val label: String) {
    PANEL("Panel"),
    DASHBOARD("Dashboard"),
    INVENTARIO("Inventario"),
    CRM("CRM"),
    OPERACIONES("Operaciones")
}

@Composable
fun BottomCapsuleNavigation(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.12f)
                )
                .border(1.dp, BorderLight, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            color = PureWhite
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem(
                    tab = AppTab.PANEL,
                    label = "Panel",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    isSelected = currentTab == AppTab.PANEL,
                    onClick = { onTabSelected(AppTab.PANEL) }
                )
                NavigationItem(
                    tab = AppTab.DASHBOARD,
                    label = "Dashboard",
                    selectedIcon = Icons.Filled.BarChart,
                    unselectedIcon = Icons.Outlined.BarChart,
                    isSelected = currentTab == AppTab.DASHBOARD,
                    onClick = { onTabSelected(AppTab.DASHBOARD) }
                )
                NavigationItem(
                    tab = AppTab.INVENTARIO,
                    label = "Inventario",
                    selectedIcon = Icons.Filled.Inventory2,
                    unselectedIcon = Icons.Outlined.Inventory2,
                    isSelected = currentTab == AppTab.INVENTARIO,
                    onClick = { onTabSelected(AppTab.INVENTARIO) }
                )
                NavigationItem(
                    tab = AppTab.CRM,
                    label = "CRM",
                    selectedIcon = Icons.Filled.People,
                    unselectedIcon = Icons.Outlined.People,
                    isSelected = currentTab == AppTab.CRM,
                    onClick = { onTabSelected(AppTab.CRM) }
                )
                NavigationItem(
                    tab = AppTab.OPERACIONES,
                    label = "Operaciones",
                    selectedIcon = Icons.Filled.Tune,
                    unselectedIcon = Icons.Outlined.Tune,
                    isSelected = currentTab == AppTab.OPERACIONES,
                    onClick = { onTabSelected(AppTab.OPERACIONES) }
                )
            }
        }
    }
}

@Composable
private fun NavigationItem(
    tab: AppTab,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("nav_tab_${tab.name.lowercase()}")
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) SunsetOrange else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = if (isSelected) SunsetOrange else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
