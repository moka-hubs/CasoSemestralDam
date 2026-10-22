package com.example.rackstracker.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rackstracker.components.LinkText
import com.example.rackstracker.components.PasswordField
import com.example.rackstracker.components.PrimaryButton
import com.example.rackstracker.components.RacksTextField
import com.example.rackstracker.components.RacksTopBar
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.navigation.Routes
import com.example.rackstracker.viewmodel.RegisterViewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) {
            navController.navigate(Routes.RACKS) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
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
            Text(
                text = "Crear cuenta",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Completa tus datos para empezar a gestionar tu infraestructura.",
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RacksTextField(
                    value = state.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = "Nombre",
                    placeholder = "Catalina",
                    modifier = Modifier.weight(1f)
                )
                RacksTextField(
                    value = state.apellido,
                    onValueChange = viewModel::onApellidoChange,
                    label = "Apellido",
                    placeholder = "Soto",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))
            RacksTextField(
                value = state.rut,
                onValueChange = viewModel::onRutChange,
                label = "RUT",
                placeholder = "12.345.678-5",
                leadingIcon = {
                    Icon(Icons.Filled.Badge, contentDescription = null, tint = TextMuted)
                }
            )

            Spacer(Modifier.height(20.dp))
            RacksTextField(
                value = state.correo,
                onValueChange = viewModel::onCorreoChange,
                label = "Correo electrónico",
                placeholder = "catalina@empresa.com",
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
                placeholder = "••••••••",
                visible = state.contrasenaVisible,
                onToggleVisibility = viewModel::toggleContrasenaVisibilidad,
                helperText = "Usa al menos 8 caracteres, letras y números."
            )

            if (state.error != null) {
                Spacer(Modifier.height(16.dp))
                Text(state.error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = if (state.cargando) "Creando cuenta…" else "Crear cuenta",
                onClick = viewModel::crearCuenta,
                enabled = !state.cargando && state.formularioValido
            )

            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                LinkText(
                    text = "Volver a iniciar sesión",
                    onClick = { navController.popBackStack() }
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
            Text(
                text = "Acceso seguro a tu infraestructura",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
