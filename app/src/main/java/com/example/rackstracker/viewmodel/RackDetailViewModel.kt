package com.example.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.TipoEventoRack
import com.example.rackstracker.repository.RackRepository
import com.example.rackstracker.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RackDetailUiState(
    val rack: Racks? = null,
    val estadoSeleccionado: RackStatus = RackStatus.ACTIVO,
    val guardado: Boolean = false,
    val puedeEditar: Boolean = false
)

class RackDetailViewModel(
    private val repository: RackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RackDetailUiState())
    val uiState: StateFlow<RackDetailUiState> = _uiState.asStateFlow()

    private var rackId: Int? = null

    init {
        viewModelScope.launch {
            repository.racks.collect { racks ->
                val rack = rackId?.let { id -> racks.firstOrNull { it.id == id }}
                if (rack != null) {
                    _uiState.update { it.copy(rack = rack, puedeEditar = puedeEditar(rack)) }
                }
            }
        }
    }

    fun cargar(id: Int) {
        if (rackId == id) return
        rackId = id
        viewModelScope.launch {
            repository.getRack(id)?.let { racks ->
                _uiState.value = RackDetailUiState(
                    rack = racks,
                    estadoSeleccionado = racks.estado,
                    puedeEditar = puedeEditar(racks)
                )
            }
        }
    }

    private fun puedeEditar(racks: Racks): Boolean {
        val usuario = SessionManager.usuario.value ?: return false
        return usuario.esAdmin ||
                racks.creadoPor.equals(usuario.correo, ignoreCase = true)
    }

    fun onEstadoSeleccionado(status: RackStatus) = _uiState.update {
        if (it.puedeEditar) it.copy(estadoSeleccionado = status, guardado = false) else it
    }

    fun guardarCambios() {
        val s = _uiState.value
        val rack = s.rack ?: return
        if (!s.puedeEditar) return
        viewModelScope.launch {
            repository.updateStatus(rack.id, s.estadoSeleccionado)
            if (rack.estado != s.estadoSeleccionado) {
                repository.addHistorial(
                    RackHistorial(
                        rackId = rack.id,
                        tipo = TipoEventoRack.CAMBIO_ESTADO,
                        descripcion = "Estado actualizado de ${rack.estado.label} a ${s.estadoSeleccionado.label}",
                        estadoAnterior = rack.estado,
                        estadoNuevo = s.estadoSeleccionado,
                        usuarioCorreo = SessionManager.correoActual ?: ""
                    )
                )
            }
            _uiState.update { it.copy(guardado = true) }
        }
    }
}