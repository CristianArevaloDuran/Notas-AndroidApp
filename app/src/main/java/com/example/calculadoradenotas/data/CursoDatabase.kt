package com.example.calculadoradenotas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calculadoradenotas.data.curso.CursoDao
import com.example.calculadoradenotas.data.curso.CursoEntity
import com.example.calculadoradenotas.data.nota.NotaDao
import com.example.calculadoradenotas.data.nota.NotaEntity

@Database(entities = [CursoEntity::class, NotaEntity::class], version = 2)
abstract class CursoDatabase: RoomDatabase() {
    abstract fun cursoDao(): CursoDao
    abstract fun notaDao(): NotaDao

    companion object {
        @Volatile
        private var INSTANCE: CursoDatabase? = null

        fun getDatabase(context: Context): CursoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CursoDatabase::class.java,
                    "cursos_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}