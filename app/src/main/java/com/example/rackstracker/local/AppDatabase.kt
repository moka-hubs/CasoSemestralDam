package com.example.rackstracker.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rackstracker.model.Usuario
import com.example.rackstracker.model.Racks


@Database(
    entities = [Racks::class, Usuario::class, SesionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
}
