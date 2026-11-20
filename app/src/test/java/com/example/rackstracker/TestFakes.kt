package com.example.rackstracker

import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.TipoEventoRack
import com.example.rackstracker.model.Usuario
import com.example.rackstracker.repository.AuthRepository
import com.example.rackstracker.repository.RackRepository
import com.example.rackstracker.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

data class RegistroArgs(
    val nombre: String,
    val apellido: String,
    val rut: String,
    val correo: String,
    val contrasena: String
)

class FakeAuthRepository : AuthRepository {

    var loginResult: Result<Usuario> = Result.success(Usuario.GENERICO)
    var registroResult: Result<Usuario> = Result.success(Usuario.GENERICO)
    var recuperacionResult: Result<Unit> = Result.success(Unit)
    var actualizacionResult: Result<Unit> = Result.success(Unit)
    var usuarioPorCorreo: Usuario? = Usuario.GENERICO

    var ultimoLogin: Pair<String, String>? = null
        private set
    var ultimoRegistro: RegistroArgs? = null
        private set
    var ultimoCorreoRecuperacion: String? = null
        private set
    var ultimaActualizacion: Pair<String, Usuario>? = null
        private set

    override suspend fun login(correo: String, contrasena: String): Result<Usuario> {
        ultimoLogin = correo to contrasena
        return loginResult
    }

    override suspend fun crearCuenta(
        nombre: String,
        apellido: String,
        rut: String,
        correo: String,
        contrasena: String
    ): Result<Usuario> {
        ultimoRegistro = RegistroArgs(nombre, apellido, rut, correo, contrasena)
        return registroResult
    }

    override suspend fun recuperarContrasena(correo: String): Result<Unit> {
        ultimoCorreoRecuperacion = correo
        return recuperacionResult
    }

    override suspend fun actualizarUsuario(correoOriginal: String, usuario: Usuario): Result<Unit> {
        ultimaActualizacion = correoOriginal to usuario
        return actualizacionResult
    }

    override suspend fun buscarPorCorreo(correo: String): Usuario? = usuarioPorCorreo
}

class FakeRackRepository(
    racksIniciales: List<Racks> = emptyList(),
    historialInicial: Map<Int, List<RackHistorial>> = emptyMap(),
    var resultadoRefresh: Result<Unit> = Result.success(Unit),
    var resultadoAddRack: (Racks) -> Result<Long> = { Result.success(42) },
    var resultadoUpdateStatus: Result<Unit> = Result.success(Unit),
    var resultadoAddHistorial: Result<Unit> = Result.success(Unit),
    var resultadoRefreshHistorial: Result<Unit> = Result.success(Unit)
) : RackRepository {

    private val _racks = MutableStateFlow(racksIniciales)
    override val racks: StateFlow<List<Racks>> = _racks.asStateFlow()

    private val _historial = MutableStateFlow(historialInicial)

    var refreshes: Int = 0
        private set
    val historialesRefrescados = mutableListOf<Int>()
    var ultimoRackAgregado: Racks? = null
        private set
    var ultimoEventoAgregado: RackHistorial? = null
        private set
    var ultimoCambioEstado: Pair<Int, RackStatus>? = null
        private set

    override suspend fun getRack(id: Int): Racks? = _racks.value.firstOrNull { it.id == id }

    override suspend fun addRack(rack: Racks): Result<Long> {
        ultimoRackAgregado = rack
        val resultado = resultadoAddRack(rack)
        resultado.onSuccess { id ->
            if (id > 0) _racks.value = _racks.value + rack.copy(id = id.toInt())
        }
        return resultado
    }

    override suspend fun updateStatus(id: Int, status: RackStatus): Result<Unit> {
        ultimoCambioEstado = id to status
        val resultado = resultadoUpdateStatus
        resultado.onSuccess {
            _racks.value = _racks.value.map { if (it.id == id) it.copy(estado = status) else it }
        }
        return resultado
    }

    override fun historial(rackId: Int): Flow<List<RackHistorial>> =
        _historial.map { mapa -> mapa[rackId].orEmpty() }

    override suspend fun addHistorial(evento: RackHistorial): Result<Unit> {
        ultimoEventoAgregado = evento
        val resultado = resultadoAddHistorial
        resultado.onSuccess {
            val actual = _historial.value[evento.rackId].orEmpty()
            _historial.value = _historial.value + (evento.rackId to (actual + evento))
        }
        return resultado
    }

    override suspend fun refreshRacks(): Result<Unit> {
        refreshes++
        return resultadoRefresh
    }

    override suspend fun refreshHistorial(rackId: Int): Result<Unit> {
        historialesRefrescados += rackId
        return resultadoRefreshHistorial
    }

    override suspend fun reemplazarRacks(racks: List<Racks>) {
        _racks.value = racks
    }

    override suspend fun reemplazarHistorial(rackId: Int, eventos: List<RackHistorial>) {
        _historial.value = _historial.value + (rackId to eventos)
    }
}

object TestSession {
    fun reset() = runBlocking { SessionManager.cerrarSesion() }

    fun iniciar(usuario: Usuario) = runBlocking { SessionManager.iniciarSesion(usuario) }
}

fun rackDePrueba(
    id: Int = 1,
    codigo: String = "R-001",
    creadoPor: String = Usuario.GENERICO.correo,
    estado: RackStatus = RackStatus.ACTIVO,
    equipos: Int = 2
) = Racks(
    id = id,
    nombre = "Rack $codigo",
    codigo = codigo,
    sede = "Santiago",
    sala = "Sala A",
    posicion = "Fila 1",
    capacidadU = 42,
    ocupadasU = 10,
    equipos = equipos,
    estado = estado,
    creadoPor = creadoPor
)

fun eventoDePrueba(
    id: Int = 1,
    rackId: Int = 1,
    fecha: Long = 1_000L,
    tipo: TipoEventoRack = TipoEventoRack.CREACION
) = RackHistorial(
    id = id,
    rackId = rackId,
    tipo = tipo,
    descripcion = "Evento de prueba",
    fecha = fecha
)
