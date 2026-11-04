package com.example.rackstracker.local
import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.TipoEventoRack
import com.example.rackstracker.model.Usuario

object DatabaseSeeder {

    private const val CORREO_GENERICO = "admin@racks.com"
    private const val CORREO_ANA = "ana@racks.com"
    private const val CORREO_CARLOS = "carlos@racks.com"

    suspend fun sembrarSiEsNecesario(database: AppDatabase) {
        val dao = database.appDao()
        if (dao.contarUsuarios() == 0) {
            dao.insertarUsuarios(Usuario.DEMO)
        }
        val baseNueva = dao.contarRacks() == 0
        if (baseNueva) {
            dao.insertarRacks(racksIniciales())
        }
        sembrarHistorial(dao, baseNueva)
    }

    private suspend fun sembrarHistorial(dao: AppDao, baseNueva: Boolean) {
        val ahora = System.currentTimeMillis()
        val racks = dao.obtenerTodosRacks()
        val eventos = mutableListOf<RackHistorial>()

        racks.forEachIndexed { indice, rack ->
            if (dao.contarHistorialRack(rack.id) > 0) return@forEachIndexed

            val fechaRegistro = if (baseNueva) {
                ahora - (racks.size - indice) * 86_400_000L
            } else {
                ahora
            }
            eventos += RackHistorial(
                rackId = rack.id,
                tipo = TipoEventoRack.CREACION,
                descripcion = "Rack registrado en el sistema",
                estadoNuevo = if (baseNueva) RackStatus.ACTIVO else rack.estado,
                usuarioCorreo = rack.creadoPor,
                fecha = fechaRegistro
            )

            if (baseNueva && rack.estado != RackStatus.ACTIVO) {
                eventos += RackHistorial(
                    rackId = rack.id,
                    tipo = TipoEventoRack.CAMBIO_ESTADO,
                    descripcion = "Estado actualizado de ${RackStatus.ACTIVO.label} a ${rack.estado.label}",
                    estadoAnterior = RackStatus.ACTIVO,
                    estadoNuevo = rack.estado,
                    usuarioCorreo = rack.creadoPor,
                    fecha = fechaRegistro + 6 * 3_600_000L
                )
            }
        }

        if (eventos.isNotEmpty()) {
            dao.insertarHistorial(eventos)
        }
    }

    private fun racksIniciales(): List<Racks> = listOf(
        Racks(
            nombre = "Rack A-01",
            codigo = "MAD-A-01",
            sede = "Madrid",
            sala = "Sala A",
            posicion = "Fila A • Posición 01",
            capacidadU = 42,
            ocupadasU = 28,
            equipos = 16,
            estado = RackStatus.ACTIVO,
            creadoPor = CORREO_GENERICO
        ),
        Racks(
            nombre = "Rack A-02",
            codigo = "MAD-A-02",
            sede = "Madrid",
            sala = "Sala A",
            posicion = "Fila A • Posición 02",
            capacidadU = 42,
            ocupadasU = 18,
            equipos = 10,
            estado = RackStatus.INACTIVO,
            creadoPor = CORREO_GENERICO
        ),
        Racks(
            nombre = "Rack B-01",
            codigo = "MAD-B-01",
            sede = "Madrid",
            sala = "Sala B",
            posicion = "Fila B • Posición 01",
            capacidadU = 24,
            ocupadasU = 10,
            equipos = 6,
            estado = RackStatus.MANTENIMIENTO,
            creadoPor = CORREO_GENERICO
        ),
        Racks(
            nombre = "Rack B-02",
            codigo = "MAD-B-02",
            sede = "Madrid",
            sala = "Sala B",
            posicion = "Fila B • Posición 02",
            capacidadU = 24,
            ocupadasU = 20,
            equipos = 12,
            estado = RackStatus.ACTIVO,
            creadoPor = CORREO_GENERICO
        ),
        Racks(
            nombre = "Rack C-01",
            codigo = "BCN-C-01",
            sede = "Barcelona",
            sala = "Sala C",
            posicion = "Fila A • Posición 01",
            capacidadU = 48,
            ocupadasU = 12,
            equipos = 8,
            estado = RackStatus.ACTIVO,
            creadoPor = CORREO_ANA
        ),
        Racks(
            nombre = "Rack C-02",
            codigo = "BCN-C-02",
            sede = "Barcelona",
            sala = "Sala C",
            posicion = "Fila A • Posición 02",
            capacidadU = 48,
            ocupadasU = 40,
            equipos = 22,
            estado = RackStatus.MANTENIMIENTO,
            creadoPor = CORREO_ANA
        ),
        Racks(
            nombre = "Rack D-01",
            codigo = "VLC-D-01",
            sede = "Valencia",
            sala = "Sala D",
            posicion = "Fila C • Posición 05",
            capacidadU = 42,
            ocupadasU = 0,
            equipos = 0,
            estado = RackStatus.INACTIVO,
            creadoPor = CORREO_CARLOS
        ),
        Racks(
            nombre = "Rack D-02",
            codigo = "VLC-D-02",
            sede = "Valencia",
            sala = "Sala D",
            posicion = "Fila C • Posición 06",
            capacidadU = 42,
            ocupadasU = 30,
            equipos = 18,
            estado = RackStatus.ACTIVO,
            creadoPor = CORREO_CARLOS
        )
    )
}
