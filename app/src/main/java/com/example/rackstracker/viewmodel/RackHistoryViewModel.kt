package com.example.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.Racks
import com.example.rackstracker.repository.RackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RackHistoryUiState(
    val rack: Racks? = null,
    val eventos: List<RackHistorial> = emptyList(),
    val cargando: Boolean = true
)

class RackHistoryViewModel(
    private val repository: RackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RackHistoryUiState())
    val uiState: StateFlow<RackHistoryUiState> = _uiState.asStateFlow()

    private var rackId: Int? = null
    private var trabajo: Job? = null

    fun cargar(id: Int) {
        if (rackId == id && trabajo?.isActive == true) return
        rackId = id
        trabajo?.cancel()
        trabajo = viewModelScope.launch {
            _uiState.value = RackHistoryUiState(cargando = true)
            _uiState.update { it.copy(rack = repository.getRack(id)) }
            repository.historial(id).collect { eventos ->
                _uiState.update { it.copy(eventos = eventos, cargando = false) }
            }
        }
    }
}
