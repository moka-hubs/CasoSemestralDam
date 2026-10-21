package com.example.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rackstracker.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecoveryUiState(
    val correo: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val enviado: Boolean = false
)

class RecoveryViewModel(
    private  val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    fun onCorreoChange(v: String) = _uiState.update { it.copy(correo = v, error = null) }

    fun enviarClave() {
        val correo = _uiState.value.correo.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            authRepository.recuperarContrasena(correo)
                .onSuccess { _uiState.update { it.copy(cargando = false, enviado = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(cargando = false, error = e.message) }
                }
        }
    }
}