package com.duoc.rackstracker

import com.duoc.rackstracker.model.RackStatus
import com.duoc.rackstracker.repository.InMemoryRackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryRackRepositoryTest {

    @Test
    fun `reemplazar racks ordena por codigo`() = runTest {
        val repositorio = InMemoryRackRepository()

        repositorio.reemplazarRacks(
            listOf(
                rackDePrueba(id = 2, codigo = "R-002"),
                rackDePrueba(id = 1, codigo = "R-001")
            )
        )

        assertEquals(listOf("R-001", "R-002"), repositorio.racks.first().map { it.codigo })
    }

    @Test
    fun `agregar y actualizar estado modifican la cache`() = runTest {
        val repositorio = InMemoryRackRepository()
        repositorio.reemplazarRacks(listOf(rackDePrueba(id = 5, codigo = "R-005")))

        val resultado = repositorio.addRack(
            rackDePrueba(id = 6, codigo = "R-006", estado = RackStatus.INACTIVO)
        )

        assertTrue(resultado.isSuccess)
        assertEquals(6L, resultado.getOrThrow())
        assertEquals(2, repositorio.racks.first().size)

        val actualizacion = repositorio.updateStatus(6, RackStatus.MANTENIMIENTO)
        assertTrue(actualizacion.isSuccess)
        assertEquals(RackStatus.MANTENIMIENTO, repositorio.getRack(6)?.estado)
    }

    @Test
    fun `historial se ordena de mas reciente a mas antiguo`() = runTest {
        val repositorio = InMemoryRackRepository()

        repositorio.reemplazarHistorial(
            rackId = 1,
            eventos = listOf(
                eventoDePrueba(id = 1, rackId = 1, fecha = 1_000L),
                eventoDePrueba(id = 2, rackId = 1, fecha = 3_000L),
                eventoDePrueba(id = 3, rackId = 1, fecha = 2_000L)
            )
        )

        assertEquals(listOf(2, 3, 1), repositorio.historial(1).first().map { it.id })
        assertTrue(repositorio.historial(99).first().isEmpty())
    }
}
