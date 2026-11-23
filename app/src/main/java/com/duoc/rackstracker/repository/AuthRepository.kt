package com.duoc.rackstracker.repository

import com.duoc.rackstracker.model.Usuario

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
    suspend fun actualizarUsuario(correoOriginal: String, usuario: Usuario): Result<Unit>
    suspend fun buscarPorCorreo(correo: String): Usuario?
}
