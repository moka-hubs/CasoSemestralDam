package com.example.rackstracker.repository

import com.example.rackstracker.local.AppDao
import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Racks
import kotlinx.coroutines.flow.Flow

interface RackRepository {
    val racks: Flow<List<Racks>>
    suspend fun getRack(id: Int): Racks?
    suspend fun addRack(rack: Racks): Long
    suspend fun updateStatus(id: Int, status: RackStatus)
    fun historial(rackId: Int): Flow<List<RackHistorial>>
    suspend fun addHistorial(evento: RackHistorial)
}

class RoomRackRepository(
    private val dao: AppDao
) : RackRepository {

    override val racks: Flow<List<Racks>> = dao.observarRacks()

    override suspend fun getRack(id: Int): Racks? = dao.obtenerRack(id)

    override suspend fun addRack(rack: Racks): Long = dao.insertarRack(rack)

    override suspend fun updateStatus(id: Int, status: RackStatus) {
        dao.actualizarEstadoRack(id, status)
    }

    override fun historial(rackId: Int): Flow<List<RackHistorial>> =
        dao.observarHistorial(rackId)

    override suspend fun addHistorial(evento: RackHistorial) {
        dao.insertarHistorial(evento)
    }
}
