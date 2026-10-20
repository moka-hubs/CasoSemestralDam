package com.example.rackstracker.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "sesion")
data class SesionEntity(
    @PrimaryKey val id: Int = ID_UNICO,
    val correo: String
) {
    companion object {
        const val ID_UNICO = 1
    }
}