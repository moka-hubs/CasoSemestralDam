package com.example.rackstracker.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.rackstracker.model.Racks
import com.example.rackstracker.components.PrimaryButton
import com.example.rackstracker.components.RacksTopBar
import com.example.rackstracker.components.StatusChip
import com.example.rackstracker.ui.theme.CardBorder
import com.example.rackstracker.ui.theme.Mint
import com.example.rackstracker.ui.theme.SurfaceElevated
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.navigation.Routes
import com.example.rackstracker.viewmodel.RackFiltro
import com.example.rackstracker.viewmodel.RacksViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RacksScreen(
    navController: NavController,
    viewModel: RacksViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val hora = remember { SimpleDateFormat("H:mm", Locale.getDefault()).format(Date()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RacksTopBar(modifier = Modifier.weight(1f))
            ProfileButton(onClick = { navController.navigate(Routes.PERFIL) })
        }

        Spacer(Modifier.height(28.dp))
        Text(
            text = "Mis racks",
            color = TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "tu infraestructura en un solo lugar.",
            color = TextSecondary,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatChip("${state.racks.size} racks registrados", Modifier.weight(1f))
            StatChip("${state.totalEquipos} equipos", Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Racks registrados", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text("Actualizado: $hora", color = TextMuted, fontSize = 12.sp)
        }

        Spacer(Modifier.height(12.dp))
        FilterTabs(
            seleccionado = state.filtro,
            onSelect = viewModel::onFiltroChange
        )

        Spacer(Modifier.height(16.dp))
        if (state.racks.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.filtro == RackFiltro.MIS_RACKS)
                        "Aún no has registrado ningún rack.\nUsa «Agregar rack» para crear el primero."
                    else "No hay racks registrados.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.racks, key = { it.id }) { rack ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 })
                    ) {
                        RackCard(
                            rack = rack,
                            onClick = { navController.navigate(Routes.detalleRack(rack.id)) }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            text = "+  Agregar rack",
            onClick = { navController.navigate(Routes.AGREGAR_RACK) }
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(SurfaceElevated)
            .border(1.dp, CardBorder, CircleShape)
    ) {
        Icon(
            Icons.Filled.Person,
            contentDescription = "Mi perfil",
            tint = Mint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun StatChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FilterTabs(
    seleccionado: RackFiltro,
    onSelect: (RackFiltro) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(4.dp)
    ) {
        RackFiltro.entries.forEach { filtro ->
            val activo = filtro == seleccionado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (activo) Mint else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onSelect(filtro) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filtro.label,
                    color = if (activo) androidx.compose.ui.graphics.Color(0xFF08100C) else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun RackCard(
    rack: Racks,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rack.nombre,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${rack.ubicacionResumen} • ${rack.equipos} equipos",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            StatusChip(status = rack.estado)
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${rack.ocupadasU} / ${rack.capacidadU} U ocupadas",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Text(
                text = "${rack.ocupacionPorcentaje}%",
                color = Mint,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { rack.ocupacionPorcentaje / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = Mint,
            trackColor = CardBorder
        )
    }
}
