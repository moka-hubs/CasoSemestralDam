package com.example.rackstracker.session

import com.example.rackstracker.data.remote.ApiService
import com.example.rackstracker.data.remote.TokenStore
import com.example.rackstracker.data.remote.aDominio
import com.example.rackstracker.local.AppDao
import com.example.rackstracker.local.SesionEntity
import com.example.rackstracker.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private var dao: AppDao? = null
    private var api: ApiService? = null

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    val correoActual: String?
        get() = _usuario.value?.correo

    val esAdmin: Boolean
        get() = _usuario.value?.esAdmin == true

    fun attach(appDao: AppDao, apiService: ApiService) {
        dao = appDao
        api = apiService
    }

    /**
     * Restaura la sesión. Sin token no hay sesión válida: se limpia también
     * la sesión local antigua para no dejar un usuario "fantasma".
     */
    suspend fun restaurar() {
        val token = TokenStore.token
        if (token.isNullOrBlank()) {
            dao?.limpiarSesion()
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
        dao?.guardarSesion(SesionEntity(correo = remoto.correo))
    }

    suspend fun iniciarSesion(usuario: Usuario) {
        _usuario.value = usuario
        dao?.guardarSesion(SesionEntity(correo = usuario.correo))
    }

    suspend fun cerrarSesion() {
        _usuario.value = null
        TokenStore.limpiar()
        dao?.limpiarSesion()
    }
}
