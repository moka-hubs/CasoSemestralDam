package com.example.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Racks
import com.example.rackstracker.repository.RackRepository
import com.example.rackstracker.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddRackUiState(
    val nombre: String = "",
    val codigo: String = "",
    val sede: String = "",
    val sala: String = "",
    val posicion: String = "",
    val capacidad: String = "",
    val estado: RackStatus = RackStatus.ACTIVO,
    val notas: String = "",
    val imagenUri: String? = null,
    val guardado: Boolean = false
) {
    val formularioValido: Boolean
        get() = nombre.isNotBlank() && codigo.isNotBlank() &&
                sede.isNotBlank() && posicion.isNotBlank() &&
                capacidad.toIntOrNull()?.let { it > 0 } == true
}

class AddRackViewModel(
    private val repository: RackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRackUiState())
    val uiState: StateFlow<AddRackUiState> = _uiState.asStateFlow()

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onCodigoChange(v: String) = _uiState.update { it.copy(codigo = v) }
    fun onSedeChange(v: String) = _uiState.update { it.copy(sede = v) }
    fun onSalaChange(v: String) = _uiState.update { it.copy(sala = v) }
    fun onPosicionChange(v: String) = _uiState.update { it.copy(posicion = v) }
    fun onCapacidadChange(v: String) =
        _uiState.update { it.copy(capacidad = v.filter { c -> c.isDigit() }) }
    fun onEstadoChange(v: RackStatus) = _uiState.update { it.copy(estado = v) }
    fun onNotasChange(v: String) =
        _uiState.update { if (v.length <= 500) it.copy(notas = v) else it }
    fun onImagenChange(uri: String?) = _uiState.update { it.copy(imagenUri = uri) }
    fun quitarImagen() = _uiState.update { it.copy(imagenUri = null) }

    fun registrar() {
        val s = _uiState.value
        if (!s.formularioValido) return
        viewModelScope.launch {
            repository.addRack(
                Racks(
                    nombre = s.nombre.trim(),
                    codigo = s.codigo.trim().uppercase(),
                    sede = s.sede,
                    sala = s.sala,
                    posicion = s.posicion.trim(),
                    capacidadU = s.capacidad.toIntOrNull() ?: 0,
                    ocupadasU = 0,
                    equipos = 0,
                    estado = s.estado,
                    creadoPor = SessionManager.correoActual ?: "",
                    notas = s.notas.trim(),
                    imagenUri = s.imagenUri
                )
            )
            _uiState.update { it.copy(guardado = true) }
        }
    }

    companion object {
        val sedes = listOf("Santiago", "Arica", "Valdivia", "Valparaiso")
        val salas = listOf("Sala A", "Sala B", "Sala C", "Sala D")
    }
}
