package com.example.rackstracker.repository

import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface RackRepository {
    val racks: StateFlow<List<Racks>>
    fun getRack(id: String): Racks?
    fun addRack(rack: Racks)
    fun updateStatus(id: String, status: RackStatus)
}

object InMemoryRackRepository : RackRepository {

    private val correoGenerico = Usuario.GENERICO.correo
    private val correoAna = "ana@racks.com"
    private val correoCarlos = "carlos@racks.com"

    private val _racks = MutableStateFlow(
        listOf(
            Racks(
                id = "1",
                nombre = "Rack A-01",
                codigo = "MAD-A-01",
                sede = "Madrid",
                sala = "Sala A",
                posicion = "Fila A • Posición 01",
                capacidadU = 42,
                ocupadasU = 28,
                equipos = 16,
                estado = RackStatus.ACTIVO,
                creadoPor = correoGenerico
            ),
            Racks(
                id = "2",
                nombre = "Rack A-02",
                codigo = "MAD-A-02",
                sede = "Madrid",
                sala = "Sala A",
                posicion = "Fila A • Posición 02",
                capacidadU = 42,
                ocupadasU = 18,
                equipos = 10,
                estado = RackStatus.INACTIVO,
                creadoPor = correoGenerico
            ),
            Racks(
                id = "3",
                nombre = "Rack B-01",
                codigo = "MAD-B-01",
                sede = "Madrid",
                sala = "Sala B",
                posicion = "Fila B • Posición 01",
                capacidadU = 24,
                ocupadasU = 10,
                equipos = 6,
                estado = RackStatus.MANTENIMIENTO,
                creadoPor = correoGenerico
            ),
            Racks(
                id = "4",
                nombre = "Rack B-02",
                codigo = "MAD-B-02",
                sede = "Madrid",
                sala = "Sala B",
                posicion = "Fila B • Posición 02",
                capacidadU = 24,
                ocupadasU = 20,
                equipos = 12,
                estado = RackStatus.ACTIVO,
                creadoPor = correoGenerico
            ),
            Racks(
                id = "5",
                nombre = "Rack C-01",
                codigo = "BCN-C-01",
                sede = "Barcelona",
                sala = "Sala C",
                posicion = "Fila A • Posición 01",
                capacidadU = 48,
                ocupadasU = 12,
                equipos = 8,
                estado = RackStatus.ACTIVO,
                creadoPor = correoAna
            ),
            Racks(
                id = "6",
                nombre = "Rack C-02",
                codigo = "BCN-C-02",
                sede = "Barcelona",
                sala = "Sala C",
                posicion = "Fila A • Posición 02",
                capacidadU = 48,
                ocupadasU = 40,
                equipos = 22,
                estado = RackStatus.MANTENIMIENTO,
                creadoPor = correoAna
            ),
            Racks(
                id = "7",
                nombre = "Rack D-01",
                codigo = "VLC-D-01",
                sede = "Valencia",
                sala = "Sala D",
                posicion = "Fila C • Posición 05",
                capacidadU = 42,
                ocupadasU = 0,
                equipos = 0,
                estado = RackStatus.INACTIVO,
                creadoPor = correoCarlos
            ),
            Racks(
                id = "8",
                nombre = "Rack D-02",
                codigo = "VLC-D-02",
                sede = "Valencia",
                sala = "Sala D",
                posicion = "Fila C • Posición 06",
                capacidadU = 42,
                ocupadasU = 30,
                equipos = 18,
                estado = RackStatus.ACTIVO,
                creadoPor = correoCarlos
            )
        )
    )

    override val racks: StateFlow<List<Racks>> = _racks.asStateFlow()

    override fun getRack(id: String): Racks? = _racks.value.firstOrNull { it.id == id }

    override fun addRack(rack: Racks) {
        _racks.update { it + rack }
    }

    override fun updateStatus(id: String, status: RackStatus) {
        _racks.update { racks ->
            racks.map { if (it.id == id) it.copy(estado = status) else it }
        }
    }
}
