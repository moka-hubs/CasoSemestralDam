package com.example.rackstracker.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rackstracker.model.RackHistorial
import com.example.rackstracker.model.Racks
import com.example.rackstracker.model.Usuario


@Database(
    entities = [Racks::class, Usuario::class, SesionEntity::class, RackHistorial::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `historial_racks` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rackId` INTEGER NOT NULL,
                        `tipo` TEXT NOT NULL,
                        `descripcion` TEXT NOT NULL,
                        `estadoAnterior` TEXT,
                        `estadoNuevo` TEXT,
                        `usuarioCorreo` TEXT NOT NULL,
                        `fecha` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_historial_racks_rackId` " +
                        "ON `historial_racks` (`rackId`)"
                )
            }
        }
    }
}
