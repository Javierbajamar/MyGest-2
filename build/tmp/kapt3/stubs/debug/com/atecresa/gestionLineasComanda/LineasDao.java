package com.atecresa.gestionLineasComanda;

import java.lang.System;

@androidx.room.Dao
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\b\u0010\n\u001a\u00020\u000bH\'J\u0010\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\'J\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\r\u001a\u00020\u000eH\'J\u0010\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u0005H\'R \u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00038gX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R \u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00038gX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/atecresa/gestionLineasComanda/LineasDao;", "", "lineasPendientes", "Landroidx/lifecycle/LiveData;", "", "Lcom/atecresa/gestionLineasComanda/LineasComanda;", "getLineasPendientes", "()Landroidx/lifecycle/LiveData;", "lineasRetenidas", "getLineasRetenidas", "deleteLineasPendientes", "", "deleteLineasRetenidas", "mesa", "", "getLineasRetenidasPorMesa", "insertLinea", "linea", "MyGest 2_debug"})
public abstract interface LineasDao {
    
    @org.jetbrains.annotations.NotNull
    @androidx.room.Query(value = "SELECT * from LINEAS_COMANDA WHERE ESTADO=101")
    public abstract androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> getLineasPendientes();
    
    @org.jetbrains.annotations.NotNull
    @androidx.room.Query(value = "SELECT * FROM LINEAS_COMANDA WHERE ESTADO=100")
    public abstract androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> getLineasRetenidas();
    
    @androidx.room.Insert(onConflict = 1)
    public abstract void insertLinea(@org.jetbrains.annotations.NotNull
    com.atecresa.gestionLineasComanda.LineasComanda linea);
    
    @androidx.room.Query(value = "DELETE FROM LINEAS_COMANDA WHERE ESTADO=101")
    public abstract void deleteLineasPendientes();
    
    @androidx.room.Query(value = "DELETE FROM LINEAS_COMANDA WHERE ESTADO=100 AND MESA=:mesa")
    public abstract void deleteLineasRetenidas(@org.jetbrains.annotations.NotNull
    java.lang.String mesa);
    
    @org.jetbrains.annotations.NotNull
    @androidx.room.Query(value = "SELECT * from LINEAS_COMANDA WHERE MESA=:mesa AND ESTADO=100")
    public abstract androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> getLineasRetenidasPorMesa(@org.jetbrains.annotations.NotNull
    java.lang.String mesa);
}