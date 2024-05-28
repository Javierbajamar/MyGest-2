package com.atecresa.gestionLineasComanda;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2 = {"Lcom/atecresa/gestionLineasComanda/GestorLineas;", "", "()V", "Companion", "MyGest 2_debug"})
public final class GestorLineas {
    @org.jetbrains.annotations.NotNull
    public static final com.atecresa.gestionLineasComanda.GestorLineas.Companion Companion = null;
    @org.jetbrains.annotations.NotNull
    private static final java.util.ArrayList<org.json.JSONObject> VISIBLES = null;
    @org.jetbrains.annotations.NotNull
    private static final java.util.ArrayList<java.lang.String> IDS_LINEAS_ELIMINADAS = null;
    private static java.util.ArrayList<org.json.JSONObject> PENDIENTES;
    private static java.util.ArrayList<org.json.JSONObject> RETENIDAS;
    private static android.app.Application application;
    private static com.atecresa.gestionLineasComanda.RepositoryBDLineas repositoryBDLineas;
    
    public GestorLineas() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0005J\u0016\u0010\u0015\u001a\u0012\u0012\u0004\u0012\u00020\t0\u0004j\b\u0012\u0004\u0012\u00020\t`\u0016J\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\t0\u00182\u0006\u0010\u0019\u001a\u00020\u0005J\u0012\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u00180\u001bJ\u0012\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u00180\u001bJ\u0006\u0010\u001e\u001a\u00020\u0012J\u000e\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u0005J\u0006\u0010 \u001a\u00020!J\u000e\u0010\"\u001a\u00020!2\u0006\u0010\u0014\u001a\u00020\u0005J\u000e\u0010#\u001a\u00020\u00122\u0006\u0010$\u001a\u00020%J\u000e\u0010&\u001a\u00020\u00122\u0006\u0010\'\u001a\u00020\u000eJ\u0014\u0010(\u001a\u00020!2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0018J\u0014\u0010*\u001a\u00020\u00122\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0018R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0007R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/atecresa/gestionLineasComanda/GestorLineas$Companion;", "", "()V", "IDS_LINEAS_ELIMINADAS", "Ljava/util/ArrayList;", "", "getIDS_LINEAS_ELIMINADAS", "()Ljava/util/ArrayList;", "PENDIENTES", "Lorg/json/JSONObject;", "RETENIDAS", "VISIBLES", "getVISIBLES", "application", "Landroid/app/Application;", "repositoryBDLineas", "Lcom/atecresa/gestionLineasComanda/RepositoryBDLineas;", "eliminarLineasPendientesBD", "", "eliminarLineasRetenidas", "mesa", "getLineasPendienteEnvio", "Lkotlin/collections/ArrayList;", "getLineasRetenidas", "", "_mesa", "getLiveDataPendientes", "Landroidx/lifecycle/LiveData;", "Lcom/atecresa/gestionLineasComanda/LineasComanda;", "getLiveDataRetenidas", "guardarBackupComandaBD", "guardarLineasRetenidasBD", "hayLineasNuevas", "", "hayLineasRetenidasEnMesa", "sendLineasPendientes", "listener", "Lcom/atecresa/comunicaciones/v3/ListenerCnx;", "setAplication", "_application", "updateLineasPendientesFromBD", "listaPendientes", "updateLineasRetenidasFromBD", "listaRetenidas", "MyGest 2_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.ArrayList<org.json.JSONObject> getVISIBLES() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.ArrayList<java.lang.String> getIDS_LINEAS_ELIMINADAS() {
            return null;
        }
        
        public final void setAplication(@org.jetbrains.annotations.NotNull
        android.app.Application _application) {
        }
        
        public final boolean updateLineasPendientesFromBD(@org.jetbrains.annotations.NotNull
        java.util.List<com.atecresa.gestionLineasComanda.LineasComanda> listaPendientes) {
            return false;
        }
        
        public final void updateLineasRetenidasFromBD(@org.jetbrains.annotations.NotNull
        java.util.List<com.atecresa.gestionLineasComanda.LineasComanda> listaRetenidas) {
        }
        
        public final boolean hayLineasNuevas() {
            return false;
        }
        
        public final boolean hayLineasRetenidasEnMesa(@org.jetbrains.annotations.NotNull
        java.lang.String mesa) {
            return false;
        }
        
        public final void guardarBackupComandaBD() {
        }
        
        public final void guardarLineasRetenidasBD(@org.jetbrains.annotations.NotNull
        java.lang.String _mesa) {
        }
        
        @org.jetbrains.annotations.NotNull
        public final androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> getLiveDataPendientes() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> getLiveDataRetenidas() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.ArrayList<org.json.JSONObject> getLineasPendienteEnvio() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<org.json.JSONObject> getLineasRetenidas(@org.jetbrains.annotations.NotNull
        java.lang.String _mesa) {
            return null;
        }
        
        public final void eliminarLineasPendientesBD() {
        }
        
        public final void eliminarLineasRetenidas(@org.jetbrains.annotations.NotNull
        java.lang.String mesa) {
        }
        
        public final void sendLineasPendientes(@org.jetbrains.annotations.NotNull
        com.atecresa.comunicaciones.v3.ListenerCnx listener) {
        }
    }
}