package com.example.rackstracker.model

enum class TipoEventoRack(val label: String) {
    CREACION("Rack registrado"),
    CAMBIO_ESTADO("Cambio de estado"),
    ACTUALIZACION("Actualización")
}

data class RackHistorial(
    val id: Int = 0,
    val rackId: Int,
    val tipo: TipoEventoRack,
    val descripcion: String,
    val estadoAnterior: RackStatus? = null,
    val estadoNuevo: RackStatus? = null,
    val usuarioCorreo: String = "",
    val fecha: Long = System.currentTimeMillis()
)
