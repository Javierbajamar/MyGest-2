package com.atecresa.gestionLineasComanda

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LineasDao {

    @get:Query("SELECT * from LINEAS_COMANDA WHERE ESTADO=101")
    val lineasPendientes: LiveData<List<LineasComanda>>  //ACCESO DESDE EL VIEW MODEL

    @get:Query("SELECT * FROM LINEAS_COMANDA WHERE ESTADO=100")
    val lineasRetenidas: LiveData<List<LineasComanda>>  //ACCESO DESDE EL VIEW MODEL

    //INSERTAMOS CON ESTE MÉTODO. FILTRAMOS EL ESTADO DESDE FUERA
    @Insert(onConflict = OnConflictStrategy.REPLACE) //ESTRATEGIA A SEGUIR CUANDO EL REGISTRO YA EXISTA -> ABORT FAIL IGNORE REPLACE ROLLBACK (por defecto es abort)
    fun insertLinea(linea: LineasComanda)

    //region FUNCIONES LINEAS PENDIENTES DE ENVIAR

    @Query("DELETE FROM LINEAS_COMANDA WHERE ESTADO=101")
    fun deleteLineasPendientes()

    //endregion

    //region FUNCIONES LINEAS RETENIDAS

    @Query("DELETE FROM LINEAS_COMANDA WHERE ESTADO=100 AND MESA=:mesa")
    fun deleteLineasRetenidas(mesa: String)

    @Query("SELECT * from LINEAS_COMANDA WHERE MESA=:mesa AND ESTADO=100")
    fun getLineasRetenidasPorMesa(mesa: String): LiveData<List<LineasComanda>>   //ACCESO DESDE EL VIEW MODEL

    //endregion
}

