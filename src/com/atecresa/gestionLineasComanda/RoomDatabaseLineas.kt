package com.atecresa.gestionLineasComanda

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//EN LAS ENTITIES LE PASAMOS LAS TABLAS Y LA VERSIÓN
@Database(entities = [LineasComanda::class], version = 1, exportSchema = false)
internal abstract//SCHEMA A FALSE PARA EVITAR ERROR DE COMPILACIÓN
class RoomDatabaseLineas : RoomDatabase() {

    internal abstract fun lineasDao(): LineasDao  //POR AHORA NO SE USA. EN EL EJEMPLO VIENE PARA LLENAR LA BASE DE DATOS DE ENTRADA

    companion object {

        @Volatile
        private var INSTANCE: RoomDatabaseLineas? = null

        //CREACIÓN DE LA BASE DE DATOS
        fun getDatabase(context: Context): RoomDatabaseLineas? {
            if (INSTANCE == null) {
                synchronized(RoomDatabaseLineas::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                RoomDatabaseLineas::class.java, "lineas_database")
                                .build()
                    }
                }
            }
            return INSTANCE
        }

    }

}
