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

data class ProfileUiState(
    val usuario: Usuario? = null,
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val correo: String = "",
    val editando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null
) {
    val formularioValido: Boolean
        get() = nombre.isNotBlank() && apellido.isNotBlank() &&
                rut.isNotBlank() && correo.contains("@")
}

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildState(SessionManager.usuario.value))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var correoOriginal: String = SessionManager.correoActual.orEmpty()

    init {
        viewModelScope.launch {
            SessionManager.usuario.collect { usuario ->
                _uiState.update { actual ->
                    if (actual.editando) actual else buildState(usuario)
                }
            }
        }
    }

    private fun buildState(usuario: Usuario?): ProfileUiState {
        if (usuario == null) return ProfileUiState()
        correoOriginal = usuario.correo
        return ProfileUiState(
            usuario = usuario,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            rut = usuario.rut,
            correo = usuario.correo
        )
    }

    fun iniciarEdicion() =
        _uiState.update { it.copy(editando = true, guardado = false, error = null) }

    fun cancelarEdicion() =
        _uiState.update { buildState(it.usuario).copy(guardado = false, error = null) }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v, error = null) }
    fun onApellidoChange(v: String) = _uiState.update { it.copy(apellido = v, error = null) }
    fun onCorreoChange(v: String) = _uiState.update { it.copy(correo = v, error = null) }

    fun guardar() {
        val s = _uiState.value
        val actual = s.usuario ?: return
        if (!s.formularioValido) {
            _uiState.update { it.copy(error = "Revisa los datos ingresados") }
            return
        }
        val actualizado = actual.copy(
            nombre = s.nombre.trim(),
            apellido = s.apellido.trim(),
            correo = s.correo.trim().lowercase()
        )
        viewModelScope.launch {
            authRepository.actualizarUsuario(correoOriginal, actualizado)
            SessionManager.iniciarSesion(actualizado)
            correoOriginal = actualizado.correo
            _uiState.update { buildState(actualizado).copy(guardado = true) }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            SessionManager.cerrarSesion()
        }
    }
}