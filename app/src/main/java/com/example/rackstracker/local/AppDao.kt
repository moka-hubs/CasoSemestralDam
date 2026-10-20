package com.example.rackstracker.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // ----- Racks -----

    @Query("SELECT * FROM racks ORDER BY codigo ASC")
    fun observarRacks(): Flow<List<Racks>>

    @Query("SELECT * FROM racks WHERE id = :id LIMIT 1")
    suspend fun obtenerRack(id: Int): Racks?

    @Query("SELECT COUNT(*) FROM racks")
    suspend fun contarRacks(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRack(rack: Racks): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRacks(racks: List<Racks>)

    @Query("UPDATE racks SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstadoRack(id: Int, estado: RackStatus)

    // ----- Usuarios -----

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarUsuario(correo: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuarios(usuarios: List<Usuario>)

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Query("DELETE FROM usuarios WHERE correo = :correo")
    suspend fun eliminarUsuario(correo: String)

    // ----- Sesión -----

    @Query("SELECT * FROM sesion WHERE id = :id LIMIT 1")
    suspend fun obtenerSesion(id: Int = SesionEntity.ID_UNICO): SesionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSesion(sesion: SesionEntity)

    @Query("DELETE FROM sesion")
    suspend fun limpiarSesion()
}
