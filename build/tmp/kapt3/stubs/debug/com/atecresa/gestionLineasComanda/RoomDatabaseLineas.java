package com.atecresa.gestionLineasComanda;

import java.lang.System;

@androidx.room.Database(entities = {com.atecresa.gestionLineasComanda.LineasComanda.class}, version = 1, exportSchema = false)
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b!\u0018\u0000 \u00062\u00020\u0001:\u0001\u0006B\u0005\u00a2\u0006\u0002\u0010\u0002J\r\u0010\u0003\u001a\u00020\u0004H \u00a2\u0006\u0002\b\u0005\u00a8\u0006\u0007"}, d2 = {"Lcom/atecresa/gestionLineasComanda/RoomDatabaseLineas;", "Landroidx/room/RoomDatabase;", "()V", "lineasDao", "Lcom/atecresa/gestionLineasComanda/LineasDao;", "lineasDao$MyGest_2_debug", "Companion", "MyGest 2_debug"})
public abstract class RoomDatabaseLineas extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull
    public static final com.atecresa.gestionLineasComanda.RoomDatabaseLineas.Companion Companion = null;
    @kotlin.jvm.Volatile
    private static volatile com.atecresa.gestionLineasComanda.RoomDatabaseLineas INSTANCE;
    
    public RoomDatabaseLineas() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public abstract com.atecresa.gestionLineasComanda.LineasDao lineasDao$MyGest_2_debug();
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/atecresa/gestionLineasComanda/RoomDatabaseLineas$Companion;", "", "()V", "INSTANCE", "Lcom/atecresa/gestionLineasComanda/RoomDatabaseLineas;", "getDatabase", "context", "Landroid/content/Context;", "MyGest 2_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        public final com.atecresa.gestionLineasComanda.RoomDatabaseLineas getDatabase(@org.jetbrains.annotations.NotNull
        android.content.Context context) {
            return null;
        }
    }
}