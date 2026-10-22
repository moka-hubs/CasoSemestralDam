package com.example.rackstracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.rackstracker.R
import com.example.rackstracker.model.Usuario
import com.example.rackstracker.components.LinkText
import com.example.rackstracker.components.PasswordField
import com.example.rackstracker.components.PrimaryButton
import com.example.rackstracker.components.RacksTextField
import com.example.rackstracker.components.RacksTopBar
import com.example.rackstracker.ui.theme.CardBorder
import com.example.rackstracker.ui.theme.Mint
import com.example.rackstracker.ui.theme.SurfaceElevated
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.navigation.Routes
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.loginExitoso) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = SurfaceElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Mint,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = { Text("Inicio de sesión exitoso") },
            text = {
                Text("Bienvenido, ${state.usuario?.nombre ?: "usuario"}. Ya puedes gestionar tus racks.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate(Routes.RACKS) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                ) {
                    Text("Ir a mis racks", color = Mint, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        RacksTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo de Racks Tracker",
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Tu infraestructura,",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "bajo control.",
                color = Mint,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Inicia sesión para gestionar tus racks y supervisar cada servidor.",
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "Usuarios de prueba",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Usuario.DEMO.forEach { usuario ->
                    Text(
                        text = "${usuario.rol.label}: ${usuario.correo} / ${usuario.contrasena}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
            RacksTextField(
                value = state.correo,
                onValueChange = viewModel::onCorreoChange,
                label = "Correo electrónico",
                placeholder = "nombre@empresa.com",
                keyboardType = KeyboardType.Email,
                leadingIcon = {
                    Icon(Icons.Filled.Mail, contentDescription = null, tint = TextMuted)
                }
            )

            Spacer(Modifier.height(20.dp))
            PasswordField(
                value = state.contrasena,
                onValueChange = viewModel::onContrasenaChange,
                label = "Contraseña",
                placeholder = "Introduce tu contraseña",
                visible = state.contrasenaVisible,
                onToggleVisibility = viewModel::toggleContrasenaVisibilidad
            )

            if (state.error != null) {
                Spacer(Modifier.height(16.dp))
                Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = if (state.cargando) "Ingresando…" else "Iniciar sesión",
                onClick = viewModel::iniciarSesion,
                enabled = !state.cargando
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LinkText(
                    text = "Recuperar contraseña",
                    onClick = { navController.navigate(Routes.RECUPERAR) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LinkText(
                    text = "Crear cuenta",
                    onClick = { navController.navigate(Routes.CREAR_CUENTA) }
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Acceso seguro a tu infraestructura",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
