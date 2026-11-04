package com.example.rackstracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoEventoRack(val label: String) {
    CREACION("Rack registrado"),
    CAMBIO_ESTADO("Cambio de estado"),
    ACTUALIZACION("Actualización")
}

@Entity(
    tableName = "historial_racks",
    indices = [Index("rackId")]
)
data class RackHistorial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rackId: Int,
    val tipo: TipoEventoRack,
    val descripcion: String,
    val estadoAnterior: RackStatus? = null,
    val estadoNuevo: RackStatus? = null,
    val usuarioCorreo: String = "",
    val fecha: Long = System.currentTimeMillis()
)
