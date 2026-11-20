package com.example.rackstracker.data.remote

import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.RolUsuario
import com.example.rackstracker.model.TipoEventoRack
import com.example.rackstracker.model.Usuario
import com.google.gson.annotations.SerializedName

// ----- Autenticación -----

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegistroRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val apellido: String,
    val rut: String = ""
)

data class LoginResponse(
    val authToken: String = ""
)

data class UsuarioDto(
    val id: Int = 0,
    val email: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val rol: String = "USUARIO",
    @SerializedName("created_at") val createdAt: Long? = null
)

fun UsuarioDto.aDominio(): Usuario = Usuario(
    nombre = nombre,
    apellido = apellido,
    rut = rut,
    correo = email.trim().lowercase(),
    contrasena = "",
    rol = if (rol.equals("ADMIN", ignoreCase = true)) RolUsuario.ADMIN else RolUsuario.USUARIO
)

// ----- Racks -----

data class RackDto(
    val id: Int = 0,
    val nombre: String = "",
    val codigo: String = "",
    val sede: String = "",
    val sala: String = "",
    val posicion: String = "",
    @SerializedName("capacidad_u") val capacidadU: Int = 0,
    @SerializedName("ocupadas_u") val ocupadasU: Int = 0,
    val equipos: Int = 0,
    val estado: String = "ACTIVO",
    @SerializedName("creador_por") val creadoPor: String? = null,
    val notas: String? = null,
    @SerializedName("imagen_url") val imagenUrl: String? = null,
    @SerializedName("created_at") val createdAt: Long? = null
)

fun RackDto.aDominio(): Racks = Racks(
    id = id,
    nombre = nombre,
    codigo = codigo,
    sede = sede,
    sala = sala,
    posicion = posicion,
    capacidadU = capacidadU,
    ocupadasU = ocupadasU,
    equipos = equipos,
    estado = estadoApi(estado),
    creadoPor = creadoPor.orEmpty(),
    notas = notas.orEmpty(),
    imagenUri = imagenUrl
)

data class RackCreateRequest(
    val nombre: String,
    val codigo: String,
    val sede: String,
    val sala: String = "",
    val posicion: String = "",
    @SerializedName("capacidad_u") val capacidadU: Int = 0,
    @SerializedName("ocupadas_u") val ocupadasU: Int = 0,
    val equipos: Int = 0,
    val estado: String = "ACTIVO",
    @SerializedName("creador_por") val creadorPor: String? = null,
    val notas: String = "",
    @SerializedName("imagen_url") val imagenUrl: String? = null
)

data class EstadoRequest(
    val estado: String
)

// ----- Historial -----

data class HistorialDto(
    val id: Int = 0,
    @SerializedName("rack_id") val rackId: Int = 0,
    @SerializedName("rack_codigo") val rackCodigo: Int? = null,
    val tipo: String = "ACTUALIZACION",
    val descripcion: String = "",
    @SerializedName("estado_anterior") val estadoAnterior: String? = null,
    @SerializedName("estado_nuevo") val estadoNuevo: String? = null,
    @SerializedName("usuario_correo") val usuarioCorreo: String? = null,
    @SerializedName("created_at") val createdAt: Long? = null
)

fun HistorialDto.aDominio(): RackHistorial = RackHistorial(
    id = id,
    rackId = rackId,
    tipo = tipoApi(tipo),
    descripcion = descripcion,
    estadoAnterior = estadoAnterior?.let(::estadoApi),
    estadoNuevo = estadoNuevo?.let(::estadoApi),
    usuarioCorreo = usuarioCorreo.orEmpty(),
    fecha = createdAt ?: 0L
)

private fun estadoApi(valor: String?): RackStatus = when (valor?.uppercase()?.trim()) {
    "ACTIVO", "ACTIVE", "1" -> RackStatus.ACTIVO
    "INACTIVO", "INACTIVE", "0" -> RackStatus.INACTIVO
    "MANTENIMIENTO", "MAINTENANCE", "2" -> RackStatus.MANTENIMIENTO
    else -> RackStatus.ACTIVO
}

private fun tipoApi(valor: String?): TipoEventoRack = when (valor?.uppercase()?.trim()) {
    "CREACION" -> TipoEventoRack.CREACION
    "CAMBIO_ESTADO" -> TipoEventoRack.CAMBIO_ESTADO
    else -> TipoEventoRack.ACTUALIZACION
}
