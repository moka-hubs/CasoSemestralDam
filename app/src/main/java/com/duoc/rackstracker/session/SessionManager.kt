package com.duoc.rackstracker.session

import com.duoc.rackstracker.data.remote.ApiService
import com.duoc.rackstracker.data.remote.TokenStore
import com.duoc.rackstracker.data.remote.aDominio
import com.duoc.rackstracker.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private var api: ApiService? = null

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    val correoActual: String?
        get() = _usuario.value?.correo

    val esAdmin: Boolean
        get() = _usuario.value?.esAdmin == true

    fun attach(apiService: ApiService) {
        api = apiService
    }

    /** Sin token no hay sesión válida; con token se valida siempre contra Xano. */
    suspend fun restaurar() {
        val token = TokenStore.token
        if (token.isNullOrBlank()) {
            _usuario.value = null
            return
        }

        val remoto = try {
            api?.me()?.aDominio()
        } catch (e: Exception) {
            null
        }

        if (remoto == null) {
            cerrarSesion()
            return
        }

        _usuario.value = remoto
    }

    suspend fun iniciarSesion(usuario: Usuario) {
        _usuario.value = usuario
    }

    suspend fun cerrarSesion() {
        _usuario.value = null
        TokenStore.limpiar()
    }
}
