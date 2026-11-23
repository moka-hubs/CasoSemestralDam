package com.duoc.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.rackstracker.model.Racks
import com.duoc.rackstracker.repository.RackRepository
import com.duoc.rackstracker.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RackFiltro(val label: String) {
    MIS_RACKS("Mis racks"),
    TODOS("Todos los racks")
}

data class RacksUiState(
    val racks: List<Racks> = emptyList(),
    val totalEquipos: Int = 0,
    val filtro: RackFiltro = RackFiltro.MIS_RACKS,
    val cargando: Boolean = false,
    val error: String? = null
)

class RacksViewModel(
    private val repository: RackRepository
) : ViewModel() {

    private val filtro = MutableStateFlow(RackFiltro.MIS_RACKS)
    private val cargando = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<RacksUiState> =
        combine(
            repository.racks,
            filtro,
            SessionManager.usuario,
            cargando,
            error
        ) { racks, filtro, usuario, cargando, error ->
            val visibles = when (filtro) {
                RackFiltro.MIS_RACKS ->
                    racks.filter { it.creadoPor.equals(usuario?.correo, ignoreCase = true) }
                RackFiltro.TODOS -> racks
            }
            RacksUiState(
                racks = visibles,
                totalEquipos = visibles.sumOf { it.equipos },
                filtro = filtro,
                cargando = cargando,
                error = error
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RacksUiState()
        )

    init {
        // Carga al iniciar sesión y al restaurar una sesión guardada.
        viewModelScope.launch {
            SessionManager.usuario.collect { usuario ->
                if (usuario != null) cargarRacks()
            }
        }
    }

    fun cargarRacks() {
        if (cargando.value) return
        cargando.value = true
        viewModelScope.launch {
            error.value = null
            repository.refreshRacks()
                .onFailure { error.value = it.message ?: "No se pudieron cargar los racks." }
            cargando.value = false
        }
    }

    fun onFiltroChange(nuevo: RackFiltro) {
        filtro.update { nuevo }
    }
}
