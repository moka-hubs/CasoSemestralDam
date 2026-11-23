package com.duoc.rackstracker.repository

import com.duoc.rackstracker.model.RackHistorial
import com.duoc.rackstracker.model.RackStatus
import com.duoc.rackstracker.model.Racks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface RackRepository {
    val racks: Flow<List<Racks>>
    suspend fun getRack(id: Int): Racks?
    suspend fun addRack(rack: Racks): Result<Long>
    suspend fun updateStatus(id: Int, status: RackStatus): Result<Unit>
    fun historial(rackId: Int): Flow<List<RackHistorial>>
    suspend fun addHistorial(evento: RackHistorial): Result<Unit>

    /** Sincroniza los racks desde Xano a la caché en memoria. */
    suspend fun refreshRacks(): Result<Unit> = Result.success(Unit)

    /** Reemplaza por completo la caché en memoria de racks. */
    suspend fun reemplazarRacks(racks: List<Racks>) = Unit

    /** Sincroniza el historial de un rack desde Xano a la caché en memoria. */
    suspend fun refreshHistorial(rackId: Int): Result<Unit> = Result.success(Unit)

    /** Reemplaza el historial en memoria de un rack. */
    suspend fun reemplazarHistorial(rackId: Int, eventos: List<RackHistorial>) = Unit
}

/**
 * Caché en memoria: mantiene la reactividad de la interfaz sin base de datos local.
 * Xano es la única fuente de verdad; estos datos se descartan al cerrar la app.
 */
class InMemoryRackRepository : RackRepository {

    private val _racks = MutableStateFlow<List<Racks>>(emptyList())
    override val racks: StateFlow<List<Racks>> = _racks.asStateFlow()

    private val _historial = MutableStateFlow<Map<Int, List<RackHistorial>>>(emptyMap())

    override suspend fun getRack(id: Int): Racks? = _racks.value.firstOrNull { it.id == id }

    override suspend fun addRack(rack: Racks): Result<Long> = runCatching {
        _racks.update { actual ->
            (actual.filterNot { it.id == rack.id } + rack).sortedBy { it.codigo }
        }
        rack.id.toLong()
    }

    override suspend fun updateStatus(id: Int, status: RackStatus): Result<Unit> = runCatching {
        _racks.update { actual ->
            actual.map { if (it.id == id) it.copy(estado = status) else it }
        }
    }

    override fun historial(rackId: Int): Flow<List<RackHistorial>> =
        _historial.map { map ->
            map[rackId].orEmpty().sortedWith(
                compareByDescending<RackHistorial> { it.fecha }.thenByDescending { it.id }
            )
        }

    override suspend fun addHistorial(evento: RackHistorial): Result<Unit> = runCatching {
        _historial.update { map ->
            map + (evento.rackId to (map[evento.rackId].orEmpty() + evento))
        }
    }

    override suspend fun reemplazarRacks(racks: List<Racks>) {
        _racks.value = racks.sortedBy { it.codigo }
    }

    override suspend fun reemplazarHistorial(rackId: Int, eventos: List<RackHistorial>) {
        _historial.update { it + (rackId to eventos) }
    }
}
