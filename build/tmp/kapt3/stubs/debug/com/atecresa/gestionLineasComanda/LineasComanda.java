package com.atecresa.gestionLineasComanda;

import java.lang.System;

@androidx.room.Entity(tableName = "LINEAS_COMANDA")
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0087\b\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB#\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\'\u0010\u0014\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u001e\u0010\b\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f\u00a8\u0006\u001b"}, d2 = {"Lcom/atecresa/gestionLineasComanda/LineasComanda;", "", "mesa", "", "estado", "", "json", "(Ljava/lang/String;ILjava/lang/String;)V", "contactId", "getContactId", "()I", "setContactId", "(I)V", "getEstado", "getJson", "()Ljava/lang/String;", "getMesa", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "Companion", "MyGest 2_debug"})
public final class LineasComanda {
    @org.jetbrains.annotations.NotNull
    @androidx.room.ColumnInfo(name = "MESA")
    private final java.lang.String mesa = null;
    @androidx.room.ColumnInfo(name = "ESTADO")
    private final int estado = 0;
    @org.jetbrains.annotations.NotNull
    @androidx.room.ColumnInfo(name = "JSON")
    private final java.lang.String json = null;
    @org.jetbrains.annotations.NotNull
    public static final com.atecresa.gestionLineasComanda.LineasComanda.Companion Companion = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TABLE_NAME = "LINEAS_COMANDA";
    @androidx.room.ColumnInfo(name = "id")
    @androidx.room.PrimaryKey(autoGenerate = true)
    private int contactId = 0;
    
    @org.jetbrains.annotations.NotNull
    public final com.atecresa.gestionLineasComanda.LineasComanda copy(@org.jetbrains.annotations.NotNull
    java.lang.String mesa, @org.jetbrains.annotations.NotNull
    int estado, @org.jetbrains.annotations.NotNull
    java.lang.String json) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public LineasComanda(@org.jetbrains.annotations.NotNull
    java.lang.String mesa, @org.jetbrains.annotations.NotNull
    int estado, @org.jetbrains.annotations.NotNull
    java.lang.String json) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getMesa() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int getEstado() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getJson() {
        return null;
    }
    
    public final int getContactId() {
        return 0;
    }
    
    public final void setContactId(int p0) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/atecresa/gestionLineasComanda/LineasComanda$Companion;", "", "()V", "TABLE_NAME", "", "MyGest 2_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}