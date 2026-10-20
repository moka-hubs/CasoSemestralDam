package com.example.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.rackstracker.model.Usuario
import com.example.rackstracker.repository.AuthRepository
import com.example.rackstracker.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = Usuario.GENERICO.correo,
    val contrasena: String = Usuario.GENERICO.contrasena,
    val contrasenaVisible: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val loginExitoso: Boolean = false,
    val usuario: Usuario? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(value: String) = _uiState.update { it.copy(correo = value, error = null) }

    fun onContrasenaChange(value: String) =
        _uiState.update { it.copy(contrasena = value, error = null) }

    fun toggleContrasenaVisibilidad() =
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }

    fun iniciarSesion() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            val resultado = authRepository.login(state.correo.trim(), state.contrasena)
            resultado.onSuccess { usuario ->
                SessionManager.iniciarSesion(usuario)
                _uiState.update {
                    it.copy(cargando = false, loginExitoso = true, usuario = usuario)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }
}
