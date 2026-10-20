package com.example.rackstracker.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesion")
data class SesionEntity(
    @PrimaryKey val id: Int = ID_UNICO,
    val correo: String
) {
    companion object {
        const val ID_UNICO = 1
    }
}