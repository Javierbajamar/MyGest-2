package com.atecresa.gestionLineasComanda

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
/*
LA ESTRUCTURA NO QUEDA CLARA TODAVÍA
 */
@Entity(tableName = LineasComanda.TABLE_NAME)
data class LineasComanda(
        @ColumnInfo(name = "MESA") @NotNull val mesa: String, //número
        @ColumnInfo(name = "ESTADO") @NotNull val estado: Int,  //estado pendiente-retenida y futuro tipo borrador
        @ColumnInfo(name = "JSON") @NotNull val json: String  //contenido de la misma
) {
    companion object {
        const val TABLE_NAME = "LINEAS_COMANDA"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var contactId: Int = 0
}

