package com.example.rackstracker.repository

import com.example.rackstracker.local.AppDao
import com.example.rackstracker.model.Usuario

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
    suspend fun actualizarUsuario(correoOriginal: String, usuario: Usuario)
    suspend fun buscarPorCorreo(correo: String): Usuario?
}

class RoomAuthRepository(
    private val dao: AppDao
) : AuthRepository {

    override suspend fun login(correo: String, contrasena: String): Result<Usuario> {
        if (correo.isBlank() || contrasena.isBlank()) {
            return Result.failure(IllegalArgumentException("Completa todos los campos"))
        }
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }
        val usuario = dao.buscarUsuario(correo.trim().lowercase())
        return if (usuario != null && usuario.contrasena == contrasena) {
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
        if (dao.buscarUsuario(correoLimpio) != null) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta con ese correo"))
        }
        val nuevo = Usuario(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            rut = rut.trim(),
            correo = correoLimpio,
            contrasena = contrasena
        )
        dao.insertarUsuario(nuevo)
        return Result.success(nuevo)
    }

    override suspend fun recuperarContrasena(correo: String): Result<Unit> {
        if (!correo.contains("@")) {
            return Result.failure(IllegalArgumentException("Correo electrónico no válido"))
        }
        return Result.success(Unit)
    }

    override suspend fun actualizarUsuario(correoOriginal: String, usuario: Usuario) {
        if (!correoOriginal.equals(usuario.correo, ignoreCase = true)) {
            dao.eliminarUsuario(correoOriginal)
        }
        dao.insertarUsuario(usuario)
    }

    override suspend fun buscarPorCorreo(correo: String): Usuario? =
        dao.buscarUsuario(correo)
}