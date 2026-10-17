package com.example.rackstracker.repository

import com.example.rackstracker.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface AuthRepository {
    suspend fun login(correo: String, contrasena: String): Result<Usuario>
    suspend fun crearCuenta(
        nombre: String,
        apellido: String,
        rut: String,
        correo: String,
        contrasena: String
    ): Result<Usuario>
    suspend fun recuperarContrasena(correo: String): Result<Unit>
    fun actualizarUsuario(correoOriginal: String, usuario: Usuario)
}

object FakeAuthRepository : AuthRepository {

    private val usuarios = MutableStateFlow(Usuario.DEMO)

    override suspend fun login(correo: String, contrasena: String): Result<Usuario> {
        if (correo.isBlank() || contrasena.isBlank()) {
            return Result.failure(IllegalArgumentException("Completa todos los campos"))
        }
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }
        val usuario = usuarios.value.firstOrNull {
            it.correo.equals(correo, ignoreCase = true) && it.contrasena == contrasena
        }
        return if (usuario != null) {
            Result.success(usuario)
        } else {
            Result.failure(IllegalArgumentException("Correo o contraseña incorrectos"))
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
        val correoLimpio = correo.trim().lowercase()
        if (usuarios.value.any { it.correo.equals(correoLimpio, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta con ese correo"))
        }
        val nuevo = Usuario(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            rut = rut.trim(),
            correo = correoLimpio,
            contrasena = contrasena
        )
        usuarios.update { it + nuevo }
        return Result.success(nuevo)
    }

    override suspend fun recuperarContrasena(correo: String): Result<Unit> {
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }
        return Result.success(Unit)
    }

    override fun actualizarUsuario(correoOriginal: String, usuario: Usuario) {
        usuarios.update { lista ->
            lista.map { if (it.correo.equals(correoOriginal, ignoreCase = true)) usuario else it }
        }
    }
}