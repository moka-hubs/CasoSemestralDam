package com.example.rackstracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rackstracker.components.BackLink
import com.example.rackstracker.components.PrimaryButton
import com.example.rackstracker.components.RacksTextField
import com.example.rackstracker.components.RacksTopBar
import com.example.rackstracker.components.SecondaryButton
import com.example.rackstracker.components.SectionLabel
import com.example.rackstracker.ui.theme.CardBorder
import com.example.rackstracker.ui.theme.Mint
import com.example.rackstracker.ui.theme.MintDark
import com.example.rackstracker.ui.theme.SurfaceElevated
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.navigation.Routes
import com.example.rackstracker.viewmodel.ProfileViewModel
import androidx.navigation.NavController

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmarSalida by remember { mutableStateOf(false) }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    viewModel.cerrarSesion()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("Cerrar sesión", color = Mint, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }

    val iniciales = buildString {
        state.nombre.firstOrNull()?.let { append(it.uppercaseChar()) }
        state.apellido.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { "U" }

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
            Spacer(Modifier.height(24.dp))
            Text("Mi perfil", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Consulta los datos de tu cuenta.", color = TextSecondary, fontSize = 14.sp)

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MintDark)
                        .border(1.dp, Mint.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iniciales, color = Mint, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.size(16.dp))
                Column {
                    Text(
                        text = listOf(state.nombre, state.apellido)
                            .filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Usuario" },
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Mint)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text("Sesión iniciada", color = TextSecondary, fontSize = 13.sp)
                    }
                    state.usuario?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(it.rol.label, color = TextMuted, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Text("Información personal", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            if (state.editando) {
                RacksTextField(
                    value = state.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = "Nombre"
                )
                Spacer(Modifier.height(18.dp))
                RacksTextField(
                    value = state.apellido,
                    onValueChange = viewModel::onApellidoChange,
                    label = "Apellido"
                )
                Spacer(Modifier.height(18.dp))
                RacksTextField(
                    value = state.rut,
                    onValueChange = {},
                    label = "RUT",
                    placeholder = "12.345.678-5",
                    enabled = false,
                    helperText = "El RUT no se puede modificar."
                )
                Spacer(Modifier.height(18.dp))
                RacksTextField(
                    value = state.correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = "Correo electrónico",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    ProfileField("Nombre", state.nombre.ifBlank { "—" })
                    Divider()
                    ProfileField("Apellido", state.apellido.ifBlank { "—" })
                    Divider()
                    ProfileField("RUT", state.rut.ifBlank { "—" })
                    Divider()
                    ProfileField("Correo electrónico", state.correo)
                }
            }

            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(state.error ?: "", color = androidx.compose.material3.MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            if (state.guardado) {
                Spacer(Modifier.height(12.dp))
                Text("Datos actualizados correctamente.", color = Mint, fontSize = 13.sp)
            }

            Spacer(Modifier.height(28.dp))
            if (state.editando) {
                PrimaryButton(
                    text = "Guardar cambios",
                    onClick = viewModel::guardar,
                    enabled = state.formularioValido
                )
                Spacer(Modifier.height(12.dp))
                SecondaryButton(text = "Cancelar", onClick = viewModel::cancelarEdicion)
            } else {
                PrimaryButton(text = "Editar datos", onClick = viewModel::iniciarEdicion)
                Spacer(Modifier.height(12.dp))
                SecondaryButton(text = "Cerrar sesión", onClick = { confirmarSalida = true })
            }

            Spacer(Modifier.height(20.dp))
            BackLink(text = "Volver a mis racks", onClick = { navController.popBackStack() })

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Shield,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Tus datos personales están protegidos.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 14.dp)) {
        SectionLabel(label)
        Spacer(Modifier.height(6.dp))
        Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(CardBorder)
    )
}
