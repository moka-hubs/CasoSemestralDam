package com.example.rackstracker.repository

import com.example.rackstracker.local.AppDao
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface RackRepository {
    val racks: Flow<List<Racks>>
    suspend fun getRack(id: Int): Racks?
    suspend fun addRack(rack: Racks)
    suspend fun updateStatus(id: Int, status: RackStatus)
}

class RoomRackRepository(
    private val dao: AppDao
) : RackRepository {

    override val racks: Flow<List<Racks>> = dao.observarRacks()

    override suspend fun getRack(id: Int): Racks? = dao.obtenerRack(id)

    override suspend fun addRack(rack: Racks) {
        dao.insertarRack(rack)
    }

    override suspend fun updateStatus(id: Int, status: RackStatus) {
        dao.actualizarEstadoRack(id, status)
    }
}