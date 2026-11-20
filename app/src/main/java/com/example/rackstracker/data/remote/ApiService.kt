package com.example.rackstracker.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ----- Autenticación -----

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("auth/signup")
    suspend fun registro(@Body body: RegistroRequest): LoginResponse

    @GET("auth/me")
    suspend fun me(): UsuarioDto

    // ----- Racks -----

    @GET("rack")
    suspend fun getRacks(): List<RackDto>

    @GET("rack/{id}")
    suspend fun getRack(@Path("id") id: Int): RackDto

    @POST("rack")
    suspend fun crearRack(@Body rack: RackCreateRequest): RackDto

    @PATCH("rack/{id}")
    suspend fun actualizarEstado(
        @Path("id") id: Int,
        @Body body: EstadoRequest
    ): RackDto

    // ----- Historial -----

    @GET("historial_racks")
    suspend fun historial(@Query("rack_id") rackId: Int): List<HistorialDto>
}
