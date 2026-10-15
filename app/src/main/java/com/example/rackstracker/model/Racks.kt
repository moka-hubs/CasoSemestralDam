package com.example.rackstracker.model

enum class RackStatus(val label: String) {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    MANTENIMIENTO("En mantenimiento")
}

data class Racks(
    val id: String,
    val nombre: String,
    val codigo: String,
    val sede: String,
    val sala: String,
    val posicion: String,
    val capacidadU: Int,
    val ocupadasU: Int,
    val equipos: Int,
    val estado: RackStatus,
    val creadoPor: String = "",
    val notas: String = "",
    val imagenUri: String? = null
) {
    val ocupacionPorcentaje: Int
        get() = if (capacidadU <= 0) 0 else ((ocupadasU.toFloat() / capacidadU) * 100).toInt()

    val ubicacionResumen: String
        get() = listOf(sala, sede).filter { it.isNotBlank() }.joinToString(" • ")
}