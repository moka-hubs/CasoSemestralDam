package com.example.rackstracker.local
import androidx.room.TypeConverter
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.model.RolUsuario
import com.example.rackstracker.model.TipoEventoRack


class Converters {
    @TypeConverter
    fun rolAString(rol: RolUsuario): String = rol.name

    @TypeConverter
    fun stringARol(valor:String): RolUsuario =
        runCatching { RolUsuario.valueOf(valor) }.getOrDefault(RolUsuario.USUARIO)

    @TypeConverter
    fun estadoAString(estado: RackStatus): String = estado.name

    @TypeConverter
    fun stringAEstado(valor: String): RackStatus =
        runCatching { RackStatus.valueOf(valor) }.getOrDefault(RackStatus.ACTIVO)

    @TypeConverter
    fun tipoEventoAString(tipo: TipoEventoRack): String = tipo.name

    @TypeConverter
    fun stringATipoEvento(valor: String): TipoEventoRack =
        runCatching { TipoEventoRack.valueOf(valor) }.getOrDefault(TipoEventoRack.ACTUALIZACION)
}