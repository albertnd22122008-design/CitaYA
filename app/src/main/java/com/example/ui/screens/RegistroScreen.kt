package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CitaYaRepository
import com.example.ui.components.CitaYaBrandHeader
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CobaltBlue
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

@Composable
fun RegistroScreen(
    repository: CitaYaRepository,
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombreNegocio by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmSand)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo Header
            CitaYaBrandHeader(logoSize = 36.dp, fontSize = 28)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu cita, al instante",
                fontSize = 14.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // VIP Trial Banner (30 días gratis cortesía)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = ObsidianDeep
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SunsetOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.RocketLaunch, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("30 Días VIP de Cortesía", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Sin tarjeta de crédito • Activación instantánea", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Crea tu cuenta de Negocio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("Configura tu salón o barbería en 30 segundos", fontSize = 12.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = nombreNegocio,
                        onValueChange = { nombreNegocio = it; errorMensaje = "" },
                        label = { Text("Nombre del Negocio") },
                        placeholder = { Text("Ej. Imperio Barbershop") },
                        leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_business_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it; errorMensaje = "" },
                        label = { Text("Teléfono / WhatsApp") },
                        placeholder = { Text("Ej. 809-555-1234") },
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_phone"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; errorMensaje = "" },
                        label = { Text("Correo Electrónico") },
                        placeholder = { Text("negocio@correo.com") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_email"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it; errorMensaje = "" },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_password"),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    if (errorMensaje.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMensaje, color = SunsetOrange, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (nombreNegocio.isBlank()) {
                                errorMensaje = "Por favor ingresa el nombre de tu negocio"
                                return@Button
                            }
                            repository.registrarNegocio(nombreNegocio, telefono, correo)
                            onRegistroExitoso()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_registration"),
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Iniciar 30 Días VIP Gratis", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Volver al Panel",
                color = CobaltBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onVolver)
                    .padding(8.dp)
            )
        }
    }
}
