package com.example.rackstracker.session

import com.example.rackstracker.model.Usuario
import com.example.rackstracker.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private var dao: AppDao? = null
    private var authRepository: AuthRepository? = null

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    val correoActual: String?
        get() = _usuario.value?.correo

    val esAdmin: Boolean
        get() = _usuario.value?.esAdmin == true

    fun attach(appDao: AppDao, auth: AuthRepository) {
        dao = appDao
        authRepository = auth
    }

    /**
     * Restaura la sesión guardada en Room. Debe llamarse una sola vez al iniciar la app.
     */
    suspend fun restaurar() {
        val guardada = dao?.obtenerSesion() ?: return
        val usuario = authRepository?.buscarPorCorreo(guardada.correo) ?: return
        _usuario.value = usuario
    }

    suspend fun iniciarSesion(usuario: Usuario) {
        _usuario.value = usuario
        dao?.guardarSesion(SesionEntity(correo = usuario.correo))
    }

    suspend fun cerrarSesion() {
        _usuario.value = null
        dao?.limpiarSesion()
    }
}
