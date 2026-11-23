package com.duoc.rackstracker.repository

import com.duoc.rackstracker.data.remote.ApiService
import com.duoc.rackstracker.data.remote.LoginRequest
import com.duoc.rackstracker.data.remote.RecuperarContrasenaRequest
import com.duoc.rackstracker.data.remote.RegistroRequest
import com.duoc.rackstracker.data.remote.RetrofitInstance
import com.duoc.rackstracker.data.remote.TokenStore
import com.duoc.rackstracker.data.remote.UsuarioUpdateRequest
import com.duoc.rackstracker.data.remote.aDominio
import com.duoc.rackstracker.model.RolUsuario
import com.duoc.rackstracker.model.Usuario
import retrofit2.HttpException
import java.io.IOException

/** Autenticación y perfil contra Xano, sin base de datos local. */
class XanoAuthRepository(
    private val api: ApiService = RetrofitInstance.api
) : AuthRepository {

    override suspend fun login(correo: String, contrasena: String): Result<Usuario> {
        if (correo.isBlank() || contrasena.isBlank()) {
            return Result.failure(IllegalArgumentException("Completa todos los campos"))
        }
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }

        return try {
            val respuesta = api.login(LoginRequest(correo.trim().lowercase(), contrasena))
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

    override suspend fun actualizarUsuario(
        correoOriginal: String,
        usuario: Usuario
    ): Result<Unit> = try {
        val id = api.me().id
        api.actualizarUsuario(
            id = id,
            body = UsuarioUpdateRequest(
                email = usuario.correo.trim().lowercase(),
                nombre = usuario.nombre.trim(),
                apellido = usuario.apellido.trim(),
                rut = usuario.rut.trim(),
                rol = if (usuario.rol == RolUsuario.ADMIN) "ADMIN" else "USUARIO"
            )
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(mensajeError(e), e))
    }

    override suspend fun buscarPorCorreo(correo: String): Usuario? = try {
        api.getUsuarios()
            .firstOrNull { it.email.equals(correo.trim(), ignoreCase = true) }
            ?.aDominio()
    } catch (e: Exception) {
        null
    }

    override suspend fun recuperarContrasena(correo: String): Result<Unit> {
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }
        return try {
            api.recuperarContrasena(RecuperarContrasenaRequest(correo.trim().lowercase()))
            Result.success(Unit)
        } catch (e: Exception) {
            val mensaje = if (e is HttpException && e.code() == 401) {
                "No se pudo enviar la solicitud. Inténtalo más tarde."
            } else {
                mensajeError(e)
            }
            Result.failure(Exception(mensaje, e))
        }
    }

    private fun mensajeError(e: Exception): String = when (e) {
        is HttpException -> when (e.code()) {
            400 -> "Revisa los datos ingresados."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            409 -> "Ya existe una cuenta con ese correo."
            in 500..599 -> "El servidor no está disponible."
            else -> "Error del servidor (${e.code()})."
        }
        is IOException -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
        else -> e.message ?: "No se pudo completar la operación."
    }
}
