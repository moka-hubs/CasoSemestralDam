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
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rackstracker.components.BackLink
import com.example.rackstracker.components.LinkText
import com.example.rackstracker.components.PrimaryButton
import com.example.rackstracker.components.RacksTextField
import com.example.rackstracker.ui.theme.Mint
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.viewmodel.RecoveryViewModel
import androidx.navigation.NavController

@Composable
fun RecoveryScreen(
    navController: NavController,
    viewModel: RecoveryViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        BackLink(text = "Volver", onClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Recuperar contraseña",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Introduce el correo asociado a tu cuenta. Te enviaremos una clave temporal para volver a acceder a tus racks.",
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))
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

            if (state.error != null) {
                Spacer(Modifier.height(16.dp))
                Text(state.error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            if (state.enviado) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Te enviamos una clave temporal a tu correo.",
                    color = Mint,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = if (state.cargando) "Enviando…" else "Enviar clave temporal al correo",
                onClick = viewModel::enviarClave,
                enabled = !state.cargando
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LinkText(
                    text = "Volver a iniciar sesión",
                    onClick = { navController.popBackStack() }
                )
            }
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
