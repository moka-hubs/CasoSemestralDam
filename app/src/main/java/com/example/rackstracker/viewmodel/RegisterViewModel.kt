package com.example.rackstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rackstracker.repository.AuthRepository
import com.example.rackstracker.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val registroExitoso: Boolean = false
) {
    val formularioValido: Boolean
        get() = nombre.isNotBlank() && apellido.isNotBlank() && rut.isNotBlank() &&
                correo.isNotBlank() && contrasena.length >= 8
}

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v, error = null) }
    fun onApellidoChange(v: String) = _uiState.update { it.copy(apellido = v, error = null) }
    fun onRutChange(v: String) = _uiState.update { it.copy(rut = v, error = null) }
    fun onCorreoChange(v: String) = _uiState.update { it.copy(correo = v, error = null) }
    fun onContrasenaChange(v: String) = _uiState.update { it.copy(contrasena = v, error = null) }
    fun toggleContrasenaVisibilidad() =
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }

    fun consumirRegistro() {
        _uiState.value = RegisterUiState()
    }

    fun crearCuenta() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            authRepository.crearCuenta(
                s.nombre.trim(), s.apellido.trim(), s.rut.trim(), s.correo.trim(), s.contrasena
            ).onSuccess { usuario ->
                SessionManager.iniciarSesion(usuario)
                _uiState.update { it.copy(cargando = false, registroExitoso = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }
}