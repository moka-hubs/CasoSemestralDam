package com.duoc.rackstracker.repository

import com.duoc.rackstracker.data.remote.ApiService
import com.duoc.rackstracker.data.remote.EstadoRequest
import com.duoc.rackstracker.data.remote.HistorialCreateRequest
import com.duoc.rackstracker.data.remote.RetrofitInstance
import com.duoc.rackstracker.data.remote.aDominio
import com.duoc.rackstracker.data.remote.aRequest
import com.duoc.rackstracker.model.RackHistorial
import com.duoc.rackstracker.model.RackStatus
import com.duoc.rackstracker.model.Racks
import retrofit2.HttpException
import java.io.IOException

/**
 * Racks e historial obtenidos desde Xano. La caché local sigue siendo la fuente
 * que observa la interfaz; las operaciones sin endpoint remoto se delegan a ella.
 */
class XanoRackRepository(
    private val api: ApiService = RetrofitInstance.api,
    private val local: RackRepository
) : RackRepository by local {

    override suspend fun refreshRacks(): Result<Unit> = try {
        val remotos = api.getRacks().map { it.aDominio() }
        local.reemplazarRacks(remotos)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(mensajeError(e), e))
    }

    override suspend fun refreshHistorial(rackId: Int): Result<Unit> = try {
        // Xano no filtra en el servidor: traemos todo y filtramos acá.
        val eventos = api.historial()
            .filter { it.rackId == rackId }
            .map { it.aDominio() }
        local.reemplazarHistorial(rackId, eventos)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(mensajeError(e), e))
    }

    override suspend fun addRack(rack: Racks): Result<Long> = try {
        val creado = api.crearRack(rack.aRequest()).aDominio()
        local.addRack(creado)
        Result.success(creado.id.toLong())
    } catch (e: Exception) {
        Result.failure(Exception(mensajeError(e), e))
    }

    override suspend fun updateStatus(id: Int, status: RackStatus): Result<Unit> = try {
        api.actualizarEstado(id, EstadoRequest(status.name))
        local.updateStatus(id, status)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(mensajeError(e), e))
    }

    override suspend fun addHistorial(evento: RackHistorial): Result<Unit> = try {
        api.crearHistorial(
            HistorialCreateRequest(
                rackId = evento.rackId,
                rackCodigo = evento.rackId,
                tipo = evento.tipo.name,
                descripcion = evento.descripcion,
                estadoAnterior = evento.estadoAnterior?.name.orEmpty(),
                estadoNuevo = evento.estadoNuevo?.name.orEmpty(),
                usuarioCorreo = evento.usuarioCorreo
            )
        )
        local.addHistorial(evento)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(mensajeError(e), e))
    }

    private fun mensajeError(e: Exception): String = when (e) {
        is HttpException -> when (e.code()) {
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            in 500..599 -> "El servidor no está disponible."
            else -> "Error del servidor (${e.code()})."
        }
        is IOException -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
        else -> e.message ?: "No se pudo completar la operación."
    }
}
