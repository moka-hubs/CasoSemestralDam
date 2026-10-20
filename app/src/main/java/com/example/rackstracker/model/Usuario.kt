package com.example.rackstracker.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey


enum class RolUsuario(val label: String) {
    ADMIN("Administrador"),
    USUARIO("Usuario")
}

@Entity(tableName = "usuarios")
data class Usuario(
    val nombre: String,
    val apellido: String,
    val rut: String,
    @PrimaryKey val correo: String,
    val contrasena: String,
    val rol: RolUsuario = RolUsuario.USUARIO
) {
    val esAdmin: Boolean
        get() = rol == RolUsuario.ADMIN

    val nombreCompleto: String
        get() = listOf(nombre, apellido).filter { it.isNotBlank() }.joinToString(" ")

    companion object {
        val GENERICO = Usuario(
            nombre = "Administrador",
            apellido = "Sistema",
            rut = "12.345.678-5",
            correo = "admin@racks.com",
            contrasena = "admin1234",
            rol = RolUsuario.ADMIN
        )

        val DEMO = listOf(
            GENERICO,
            Usuario(
                nombre = "Ana",
                apellido = "Torres",
                rut = "15.678.912-3",
                correo = "ana@racks.com",
                contrasena = "ana12345",
                rol = RolUsuario.USUARIO
            )
        )
    }
}
