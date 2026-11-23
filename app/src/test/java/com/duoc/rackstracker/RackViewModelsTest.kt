package com.duoc.rackstracker

import com.duoc.rackstracker.model.RolUsuario
import com.duoc.rackstracker.model.RackStatus
import com.duoc.rackstracker.model.TipoEventoRack
import com.duoc.rackstracker.model.Usuario
import com.duoc.rackstracker.viewmodel.AddRackViewModel
import com.duoc.rackstracker.viewmodel.RackDetailViewModel
import com.duoc.rackstracker.viewmodel.RackFiltro
import com.duoc.rackstracker.viewmodel.RackHistoryViewModel
import com.duoc.rackstracker.viewmodel.RacksUiState
import com.duoc.rackstracker.viewmodel.RacksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RackViewModelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        TestSession.reset()
    }

    @Test
    fun `filtro mis racks muestra propios y todos muestra todos`() = runTest {
        val usuario = Usuario.GENERICO.copy(correo = "admin@racks.com")
        TestSession.iniciar(usuario)
        val propio = rackDePrueba(id = 1, codigo = "R-001", creadoPor = usuario.correo, equipos = 3)
        val ajeno = rackDePrueba(id = 2, codigo = "R-002", creadoPor = "otro@racks.com", equipos = 5)
        val repositorio = FakeRackRepository(racksIniciales = listOf(propio, ajeno))
        val viewModel = RacksViewModel(repositorio)

        val estados = mutableListOf<RacksUiState>()
        val colector: Job = launch { viewModel.uiState.collect { estados += it } }
        advanceUntilIdle()

        assertEquals(1, estados.last().racks.size)
        assertEquals("R-001", estados.last().racks.first().codigo)
        assertEquals(3, estados.last().totalEquipos)
        assertTrue(repositorio.refreshes > 0)

        viewModel.onFiltroChange(RackFiltro.TODOS)
        advanceUntilIdle()

        assertEquals(2, estados.last().racks.size)
        assertEquals(8, estados.last().totalEquipos)
        colector.cancel()
    }

    @Test
    fun `registrar rack crea evento de creacion`() = runTest {
        TestSession.iniciar(Usuario.GENERICO)
        val repositorio = FakeRackRepository()
        val viewModel = AddRackViewModel(repositorio)

        viewModel.onNombreChange("Rack nuevo")
        viewModel.onCodigoChange("r-010")
        viewModel.onSedeChange("Santiago")
        viewModel.onSalaChange("Sala B")
        viewModel.onPosicionChange("Fila 2")
        viewModel.onCapacidadChange("45")
        viewModel.onNotasChange("Nota de prueba")
        assertTrue(viewModel.uiState.value.formularioValido)

        viewModel.registrar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.guardado)
        assertEquals("R-010", repositorio.ultimoRackAgregado?.codigo)
        assertEquals(Usuario.GENERICO.correo, repositorio.ultimoRackAgregado?.creadoPor)
        assertNotNull(repositorio.ultimoEventoAgregado)
        assertEquals(42, repositorio.ultimoEventoAgregado?.rackId)
        assertEquals(TipoEventoRack.CREACION, repositorio.ultimoEventoAgregado?.tipo)
    }

    @Test
    fun `registrar rack fallido muestra error y no crea historial`() = runTest {
        TestSession.iniciar(Usuario.GENERICO)
        val repositorio = FakeRackRepository().apply {
            resultadoAddRack = { Result.failure(Exception("No se pudo guardar")) }
        }
        val viewModel = AddRackViewModel(repositorio)

        viewModel.onNombreChange("Rack")
        viewModel.onCodigoChange("R-011")
        viewModel.onSedeChange("Santiago")
        viewModel.onPosicionChange("Fila 3")
        viewModel.onCapacidadChange("20")
        viewModel.registrar()
        advanceUntilIdle()

        assertEquals("No se pudo guardar", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.guardado)
        assertNull(repositorio.ultimoEventoAgregado)
    }

    @Test
    fun `usuario no puede editar rack ajeno`() = runTest {
        val usuario = Usuario.GENERICO.copy(
            correo = "user@racks.com",
            rol = RolUsuario.USUARIO
        )
        TestSession.iniciar(usuario)
        val ajeno = rackDePrueba(id = 1, creadoPor = "otro@racks.com")
        val repositorio = FakeRackRepository(racksIniciales = listOf(ajeno))
        val viewModel = RackDetailViewModel(repositorio)

        viewModel.cargar(1)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.puedeEditar)
        viewModel.onEstadoSeleccionado(RackStatus.MANTENIMIENTO)
        assertEquals(RackStatus.ACTIVO, viewModel.uiState.value.estadoSeleccionado)
        viewModel.guardarCambios()
        advanceUntilIdle()
        assertNull(repositorio.ultimoCambioEstado)
    }

    @Test
    fun `admin puede cambiar estado y registra historial`() = runTest {
        TestSession.iniciar(Usuario.GENERICO)
        val rack = rackDePrueba(
            id = 7,
            creadoPor = "otro@racks.com",
            estado = RackStatus.ACTIVO
        )
        val repositorio = FakeRackRepository(racksIniciales = listOf(rack))
        val viewModel = RackDetailViewModel(repositorio)

        viewModel.cargar(7)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.puedeEditar)

        viewModel.onEstadoSeleccionado(RackStatus.INACTIVO)
        viewModel.guardarCambios()
        advanceUntilIdle()

        assertEquals(7 to RackStatus.INACTIVO, repositorio.ultimoCambioEstado)
        assertEquals(TipoEventoRack.CAMBIO_ESTADO, repositorio.ultimoEventoAgregado?.tipo)
        assertTrue(viewModel.uiState.value.guardado)
    }

    @Test
    fun `historial carga rack y eventos`() = runTest {
        val rack = rackDePrueba(id = 3)
        val eventos = listOf(
            eventoDePrueba(id = 1, rackId = 3, fecha = 1_000L),
            eventoDePrueba(id = 2, rackId = 3, fecha = 2_000L, tipo = TipoEventoRack.CAMBIO_ESTADO)
        )
        val repositorio = FakeRackRepository(
            racksIniciales = listOf(rack),
            historialInicial = mapOf(3 to eventos)
        )
        val viewModel = RackHistoryViewModel(repositorio)

        viewModel.cargar(3)
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.rack?.id)
        assertEquals(2, viewModel.uiState.value.eventos.size)
        assertFalse(viewModel.uiState.value.cargando)
        assertTrue(3 in repositorio.historialesRefrescados)
    }
}
