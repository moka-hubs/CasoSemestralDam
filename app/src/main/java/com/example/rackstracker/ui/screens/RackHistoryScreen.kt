package com.example.rackstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.rackstracker.components.BackLink
import com.example.rackstracker.components.SectionLabel
import com.example.rackstracker.components.StatusChip
import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.TipoEventoRack
import com.example.rackstracker.ui.theme.CardBorder
import com.example.rackstracker.ui.theme.Mint
import com.example.rackstracker.ui.theme.StatusActive
import com.example.rackstracker.ui.theme.StatusMaintenance
import com.example.rackstracker.ui.theme.SurfaceElevated
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.viewmodel.RackHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RackHistoryScreen(
    navController: NavController,
    rackId: Int,
    viewModel: RackHistoryViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val formatoFecha = remember { SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.getDefault()) }

    LaunchedEffect(rackId) {
        viewModel.cargar(rackId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        BackLink(text = "Volver al detalle", onClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Historial del rack",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Consulta todos los cambios registrados para este rack.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            val rack = state.rack
            if (rack == null) {
                Spacer(Modifier.height(40.dp))
                Text(
                    text = if (state.cargando) "Cargando historial…" else "Rack no encontrado.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                return@Column
            }

            Spacer(Modifier.height(24.dp))
            RackHistoryHeader(rack)

            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Línea de tiempo",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (state.eventos.isEmpty()) "Sin movimientos" else "${state.eventos.size} movimientos",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))

            if (state.eventos.isEmpty()) {
                HistorialVacio(cargando = state.cargando)
            } else {
                state.eventos.forEach { evento ->
                    EventoTimeline(evento = evento, fecha = formatoFecha.format(Date(evento.fecha)))
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun RackHistoryHeader(rack: Racks) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rack.nombre,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${rack.codigo} · ${rack.ubicacionResumen}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            StatusChip(status = rack.estado)
        }

        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(CardBorder)
        )
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("Registrado por")
                Spacer(Modifier.height(6.dp))
                Text(
                    text = rack.creadoPor.ifBlank { "—" },
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("Capacidad")
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${rack.capacidadU} U · ${rack.equipos} equipos",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EventoTimeline(evento: RackHistorial, fecha: String) {
    val color = colorEvento(evento.tipo)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceElevated)
                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = evento.tipo.label.uppercase(),
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp
                    )
                }
                Text(fecha, color = TextMuted, fontSize = 11.sp)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = evento.descripcion,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.sp
            )

            val anterior = evento.estadoAnterior
            val nuevo = evento.estadoNuevo
            if (anterior != null || nuevo != null) {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (anterior != null) {
                        StatusChip(status = anterior)
                        Spacer(Modifier.width(8.dp))
                        Text("→", color = TextMuted, fontSize = 13.sp)
                        Spacer(Modifier.width(8.dp))
                    }
                    if (nuevo != null) StatusChip(status = nuevo)
                }
            }

            if (evento.usuarioCorreo.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Por ${evento.usuarioCorreo}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun HistorialVacio(cargando: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (cargando) {
                "Cargando movimientos…"
            } else {
                "Este rack todavía no tiene movimientos registrados. Los cambios de estado aparecerán aquí."
            },
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}

private fun colorEvento(tipo: TipoEventoRack): Color = when (tipo) {
    TipoEventoRack.CREACION -> Mint
    TipoEventoRack.CAMBIO_ESTADO -> StatusMaintenance
    TipoEventoRack.ACTUALIZACION -> StatusActive
}
