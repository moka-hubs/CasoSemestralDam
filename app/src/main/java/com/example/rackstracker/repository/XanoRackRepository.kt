package com.example.rackstracker.repository

import com.example.rackstracker.data.remote.ApiService
import com.example.rackstracker.data.remote.RetrofitInstance
import com.example.rackstracker.data.remote.aDominio
import retrofit2.HttpException
import java.io.IOException

/**
 * Racks obtenidos desde Xano. La caché local sigue siendo la fuente que
 * observa la interfaz; las operaciones sin endpoint remoto se delegan a ella.
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

    private fun mensajeError(e: Exception): String = when (e) {
        is HttpException -> when (e.code()) {
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            in 500..599 -> "El servidor no está disponible."
            else -> "Error del servidor (${e.code()})."
        }
        is IOException -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
        else -> e.message ?: "No se pudieron cargar los racks."
    }
}
