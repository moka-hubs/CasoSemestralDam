package com.example.rackstracker.local
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Usuario
import com.example.rackstracker.model.Racks

object DatabaseSeeder {

    private const val CORREO_GENERICO = "admin@racks.com"
    private const val CORREO_ANA = "ana@racks.com"
    private const val CORREO_CARLOS = "carlos@racks.com"

    suspend fun sembrarSiEsNecesario(database: AppDatabase) {
        val dao = database.appDao()
        if (dao.contarUsuarios() == 0) {
            dao.insertarUsuarios(Usuario.DEMO)
        }
        if (dao.contarRacks() == 0) {
            dao.insertarRacks(racksIniciales())
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
