package com.example.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rackstracker.model.Racks
import com.example.rackstracker.repository.RackRepository
import com.example.rackstracker.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class RackFiltro(val label: String) {
    MIS_RACKS("Mis racks"),
    TODOS("Todos los racks")
}

data class RacksUiState(
    val racks: List<Racks> = emptyList(),
    val totalEquipos: Int = 0,
    val filtro: RackFiltro = RackFiltro.MIS_RACKS
)

class RacksViewModel(
    private val repository: RackRepository
) : ViewModel() {

    private val filtro = MutableStateFlow(RackFiltro.MIS_RACKS)

    val uiState: StateFlow<RacksUiState> =
        combine(repository.racks, filtro, SessionManager.usuario) { racks, filtro, usuario ->
            val visibles = when (filtro) {
                RackFiltro.MIS_RACKS ->
                    racks.filter { it.creadoPor.equals(usuario?.correo, ignoreCase = true) }
                RackFiltro.TODOS -> racks
            }
            RacksUiState(
                racks = visibles,
                totalEquipos = visibles.sumOf { it.equipos },
                filtro = filtro
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RacksUiState()
        )

    fun onFiltroChange(nuevo: RackFiltro) {
        filtro.update { nuevo }
    }
}