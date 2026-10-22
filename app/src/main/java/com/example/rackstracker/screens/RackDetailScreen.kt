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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.components.BackLink
import com.example.rackstracker.components.PrimaryButton
import com.example.rackstracker.components.SectionLabel
import com.example.rackstracker.components.StatusChip
import com.example.rackstracker.ui.theme.CardBorder
import com.example.rackstracker.ui.theme.Mint
import com.example.rackstracker.ui.theme.SurfaceElevated
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.viewmodel.RackDetailViewModel
import androidx.navigation.NavController

@Composable
fun RackDetailScreen(
    navController: NavController,
    rackId: Int,
    viewModel: RackDetailViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(rackId) {
        viewModel.cargar(rackId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        BackLink(text = "Volver a mis racks", onClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            Text("Detalle del rack", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(
                "Consulta su información y actualiza su estado.",
                color = TextSecondary,
                fontSize = 14.sp
            )

            val rack = state.rack
            if (rack == null) {
                Spacer(Modifier.height(40.dp))
                Text("Rack no encontrado.", color = TextMuted, fontSize = 14.sp)
                return@Column
            }

            Spacer(Modifier.height(24.dp))
            RackSummaryCard(rack)

            Spacer(Modifier.height(28.dp))
            Text("Estado del rack", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Selecciona el estado que quieres guardar.",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(16.dp))

            RackStatus.entries.forEach { status ->
                StatusOptionRow(
                    status = status,
                    seleccionado = state.estadoSeleccionado == status,
                    enabled = state.puedeEditar,
                    onClick = { viewModel.onEstadoSeleccionado(status) }
                )
                Spacer(Modifier.height(10.dp))
            }

            if (!state.puedeEditar) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Mint.copy(alpha = 0.08f))
                        .border(1.dp, Mint.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Lock, null, tint = Mint, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Solo el administrador o el propietario del rack puede modificar su estado.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = "Guardar cambios",
                onClick = viewModel::guardarCambios,
                enabled = state.puedeEditar
            )
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = when {
                        state.guardado -> "Cambios guardados correctamente."
                        !state.puedeEditar -> "No tienes permisos para modificar este rack."
                        else -> "El cambio se aplicará al guardar."
                    },
                    color = if (state.guardado) Mint else TextMuted,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun RackSummaryCard(rack: Racks) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(rack.nombre, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(rack.ubicacionResumen, color = TextSecondary, fontSize = 13.sp)
            }
            StatusChip(status = rack.estado)
        }

        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("Equipos")
                Spacer(Modifier.height(6.dp))
                Text("${rack.equipos} equipos", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text("${rack.ocupadasU} / ${rack.capacidadU} U ocupadas", color = TextSecondary, fontSize = 12.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("Capacidad")
                Spacer(Modifier.height(6.dp))
                Text("${rack.capacidadU} U", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ocupación", color = TextSecondary, fontSize = 12.sp)
            Text("${rack.ocupacionPorcentaje}%", color = Mint, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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

@Composable
private fun StatusOptionRow(
    status: RackStatus,
    seleccionado: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.4f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceElevated)
            .border(
                width = 1.dp,
                color = if (seleccionado && enabled) Mint else CardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (seleccionado && enabled) Mint else CardBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (seleccionado) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (enabled) Mint else TextMuted)
                )
            }
        }
        Spacer(Modifier.size(14.dp))
        Text(
            text = status.label,
            color = (if (seleccionado) TextPrimary else TextSecondary).copy(alpha = alpha),
            fontSize = 15.sp,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
