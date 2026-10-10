package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CitaYaRepository
import com.example.data.StripePaymentService
import com.example.ui.components.AppTab
import com.example.ui.components.BottomCapsuleNavigation
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CrmScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventarioScreen
import com.example.ui.screens.MenuInicioScreen
import com.example.ui.screens.ModoClienteScreen
import com.example.ui.screens.OperacionesScreen
import com.example.ui.screens.PanelScreen
import com.example.ui.screens.RegistroScreen
import com.example.ui.theme.BorderLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.google.firebase.auth.FirebaseAuth
import com.stripe.android.PaymentConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Stripe usa SOLO la clave pública en la app (la secreta vive en el backend).
        try {
            PaymentConfiguration.init(applicationContext, StripePaymentService.publishableKey)
        } catch (_: Exception) {
        }
        val repository = CitaYaRepository.getInstance(this)

        setContent {
            MyApplicationTheme {
                AppRootContent(repository = repository)
            }
        }
    }
}

enum class RootScreen {
    MENU,
    AUTH,
    CLIENTE,
    REGISTRO_NEGOCIO,
    MAIN_TABS
}

@Composable
fun AppRootContent(repository: CitaYaRepository) {
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var currentScreen by remember { mutableStateOf(if (currentUser != null) RootScreen.MAIN_TABS else RootScreen.MENU) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null && (currentScreen == RootScreen.MENU || currentScreen == RootScreen.AUTH)) {
                currentScreen = RootScreen.MAIN_TABS
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(listener)
        }
    }

    when (currentScreen) {
        RootScreen.MENU -> {
            MenuInicioScreen(
                onCliente = {
                    currentScreen = RootScreen.CLIENTE
                },
                onNegocio = {
                    currentScreen = RootScreen.AUTH
                }
            )
        }

        RootScreen.AUTH -> {
            BackHandler {
                currentScreen = RootScreen.MENU
            }
            AuthScreen(
                onContinueAsClient = {
                    currentScreen = RootScreen.CLIENTE
                },
                onAuthSuccess = {
                    currentUser = FirebaseAuth.getInstance().currentUser
                    currentScreen = RootScreen.MAIN_TABS
                },
                onRegistrarNuevoNegocio = {
                    currentScreen = RootScreen.REGISTRO_NEGOCIO
                }
            )
        }

        RootScreen.CLIENTE -> {
            BackHandler {
                currentScreen = if (currentUser != null) RootScreen.MAIN_TABS else RootScreen.MENU
            }
            ModoClienteScreen(
                repository = repository,
                onVolverModoNegocio = {
                    currentScreen = if (currentUser != null) RootScreen.MAIN_TABS else RootScreen.MENU
                }
            )
        }

        RootScreen.REGISTRO_NEGOCIO -> {
            BackHandler {
                currentScreen = if (currentUser != null) RootScreen.MAIN_TABS else RootScreen.MENU
            }
            RegistroScreen(
                repository = repository,
                onRegistroExitoso = {
                    currentScreen = RootScreen.MAIN_TABS
                },
                onVolver = {
                    currentScreen = if (currentUser != null) RootScreen.MAIN_TABS else RootScreen.MENU
                }
            )
        }

        RootScreen.MAIN_TABS -> {
            CitaYaMainApp(
                repository = repository,
                onOpenClientMode = {
                    currentScreen = RootScreen.CLIENTE
                },
                onOpenRegister = {
                    currentScreen = RootScreen.REGISTRO_NEGOCIO
                },
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    currentUser = null
                    repository.limpiarDatosLocales()
                    currentScreen = RootScreen.MENU
                }
            )
        }
    }
}

@Composable
fun CitaYaMainApp(
    repository: CitaYaRepository,
    onOpenClientMode: () -> Unit,
    onOpenRegister: () -> Unit,
    onSignOut: () -> Unit
) {
    var currentTab by remember { mutableStateOf(AppTab.PANEL) }
    val business by repository.business.collectAsState()

    BackHandler(enabled = currentTab != AppTab.PANEL) {
        currentTab = AppTab.PANEL
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianDeep,
        bottomBar = {
            BottomCapsuleNavigation(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Barra superior de estado / cambio de modo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info negocio actual
                Text(
                    text = business.nombre.ifBlank { "Mi Barbería" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunsetOrange
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Botón para probar Modo Cliente
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianCard)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenClientMode)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("btn_switch_client_mode"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Modo Cliente", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón Registrar Negocio
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianCard)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenRegister)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("btn_switch_register"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AppRegistration, contentDescription = null, tint = TextLight, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Negocio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón Cerrar Sesión
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianCard)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .clickable(onClick = onSignOut)
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión", tint = TextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Contenido de la pestaña activa
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    AppTab.PANEL -> PanelScreen(repository = repository)
                    AppTab.DASHBOARD -> DashboardScreen(repository = repository)
                    AppTab.INVENTARIO -> InventarioScreen(repository = repository)
                    AppTab.CRM -> CrmScreen(repository = repository)
                    AppTab.OPERACIONES -> OperacionesScreen(repository = repository)
                }
            }
        }
    }
}
