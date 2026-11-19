package com.example.rackstracker.repository

import com.example.rackstracker.data.remote.ApiService
import com.example.rackstracker.data.remote.LoginRequest
import com.example.rackstracker.data.remote.RegistroRequest
import com.example.rackstracker.data.remote.RetrofitInstance
import com.example.rackstracker.data.remote.TokenStore
import com.example.rackstracker.data.remote.aDominio
import com.example.rackstracker.model.Usuario
import retrofit2.HttpException
import java.io.IOException

/**
 * Autenticación contra Xano. Las operaciones que aún no tienen endpoint remoto
 * se delegan al repositorio local para no romper las pantallas existentes.
 */
class XanoAuthRepository(
    private val api: ApiService = RetrofitInstance.api,
    private val local: AuthRepository
) : AuthRepository {

    override suspend fun login(correo: String, contrasena: String): Result<Usuario> {
        if (correo.isBlank() || contrasena.isBlank()) {
            return Result.failure(IllegalArgumentException("Completa todos los campos"))
        }
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }

        return try {
            val respuesta = api.login(
                LoginRequest(correo.trim().lowercase(), contrasena)
            )
            guardarSesion(respuesta.authToken)
            Result.success(api.me().aDominio())
        } catch (e: Exception) {
            TokenStore.limpiar()
            Result.failure(Exception(mensajeError(e), e))
        }
    }

    override suspend fun crearCuenta(
        nombre: String,
        apellido: String,
        rut: String,
        correo: String,
        contrasena: String
    ): Result<Usuario> {
        if (nombre.isBlank() || apellido.isBlank() || rut.isBlank() ||
            correo.isBlank() || contrasena.isBlank()
        ) {
            return Result.failure(IllegalArgumentException("Completa todos los campos obligatorios"))
        }
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }
        if (contrasena.length < 8) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 8 caracteres"))
        }

        return try {
            val respuesta = api.registro(
                RegistroRequest(
                    email = correo.trim().lowercase(),
                    password = contrasena,
                    nombre = nombre.trim(),
                    apellido = apellido.trim(),
                    rut = rut.trim()
                )
            )
            guardarSesion(respuesta.authToken)
            Result.success(api.me().aDominio())
        } catch (e: Exception) {
            TokenStore.limpiar()
            Result.failure(Exception(mensajeError(e), e))
        }
    }

    private fun guardarSesion(token: String) {
        if (token.isBlank()) {
            throw IllegalStateException("Xano no devolvió un token de autenticación")
        }
        TokenStore.token = token
    }

    private fun mensajeError(e: Exception): String = when (e) {
        is HttpException -> when (e.code()) {
            400 -> "Revisa los datos ingresados."
            401 -> "Correo o contraseña incorrectos."
            409 -> "Ya existe una cuenta con ese correo."
            in 500..599 -> "El servidor no está disponible."
            else -> "Error del servidor (${e.code()})."
        }
        is IOException -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
        else -> e.message ?: "No se pudo completar la operación."
    }

    override suspend fun recuperarContrasena(correo: String): Result<Unit> =
        local.recuperarContrasena(correo)

    override suspend fun actualizarUsuario(correoOriginal: String, usuario: Usuario) =
        local.actualizarUsuario(correoOriginal, usuario)

    override suspend fun buscarPorCorreo(correo: String): Usuario? =
        local.buscarPorCorreo(correo)
}
