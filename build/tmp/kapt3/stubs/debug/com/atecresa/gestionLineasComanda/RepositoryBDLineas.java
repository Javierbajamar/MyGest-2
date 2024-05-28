package com.atecresa.gestionLineasComanda;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0000\u0018\u00002\u00020\u0001:\u0004\u001a\u001b\u001c\u001dB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000f\u001a\u00020\u0010J\u000e\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0013J\u0014\u0010\u0014\u001a\u00020\u00102\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\tJ\u001c\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u00132\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00160\tR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\f\u00a8\u0006\u001e"}, d2 = {"Lcom/atecresa/gestionLineasComanda/RepositoryBDLineas;", "", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "lDao", "Lcom/atecresa/gestionLineasComanda/LineasDao;", "lineasPendientesEnvio", "Landroidx/lifecycle/LiveData;", "", "Lcom/atecresa/gestionLineasComanda/LineasComanda;", "getLineasPendientesEnvio", "()Landroidx/lifecycle/LiveData;", "lineasRetenidas", "getLineasRetenidas", "eliminarLineasPendientesBD", "", "eliminarLineasRetenidas", "mesa", "", "guardarBDLineasPendientes", "lineasComandaPendientes", "Lorg/json/JSONObject;", "guardarBDLineasRetenidas", "num_mesa", "lineas_comanda", "EliminarLineaPendienteAsyncTask", "EliminarLineasRetenidasAsyncTask", "GuardarAllLineasAsyncTask", "GuardarLineaAsyncTask", "MyGest 2_debug"})
public final class RepositoryBDLineas {
    private final com.atecresa.gestionLineasComanda.LineasDao lDao = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> lineasPendientesEnvio = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> lineasRetenidas = null;
    
    public RepositoryBDLineas(@org.jetbrains.annotations.NotNull
    android.app.Application application) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> getLineasPendientesEnvio() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>> getLineasRetenidas() {
        return null;
    }
    
    public final void guardarBDLineasPendientes(@org.jetbrains.annotations.NotNull
    java.util.List<? extends org.json.JSONObject> lineasComandaPendientes) {
    }
    
    public final void guardarBDLineasRetenidas(@org.jetbrains.annotations.NotNull
    java.lang.String num_mesa, @org.jetbrains.annotations.NotNull
    java.util.List<? extends org.json.JSONObject> lineas_comanda) {
    }
    
    public final void eliminarLineasPendientesBD() {
    }
    
    public final void eliminarLineasRetenidas(@org.jetbrains.annotations.NotNull
    java.lang.String mesa) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0002\u0018\u00002\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0001B\u000f\b\u0000\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J/\u0010\b\u001a\u0004\u0018\u00010\u00042\u001e\u0010\t\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020\u00030\u00020\n\"\b\u0012\u0004\u0012\u00020\u00030\u0002H\u0015\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/atecresa/gestionLineasComanda/RepositoryBDLineas$GuardarAllLineasAsyncTask;", "Landroid/os/AsyncTask;", "", "Lcom/atecresa/gestionLineasComanda/LineasComanda;", "Ljava/lang/Void;", "mAsyncTaskDao", "Lcom/atecresa/gestionLineasComanda/LineasDao;", "(Lcom/atecresa/gestionLineasComanda/LineasDao;)V", "doInBackground", "listaLineas", "", "([Ljava/util/List;)Ljava/lang/Void;", "MyGest 2_debug"})
    static final class GuardarAllLineasAsyncTask extends android.os.AsyncTask<java.util.List<? extends com.atecresa.gestionLineasComanda.LineasComanda>, java.lang.Void, java.lang.Void> {
        private final com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao = null;
        
        public GuardarAllLineasAsyncTask(@org.jetbrains.annotations.NotNull
        com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        @java.lang.Override
        @java.lang.SafeVarargs
        protected java.lang.Void doInBackground(@org.jetbrains.annotations.NotNull
        java.util.List<com.atecresa.gestionLineasComanda.LineasComanda>... listaLineas) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0002\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0001B\u000f\b\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J#\u0010\u0007\u001a\u0004\u0018\u00010\u00032\u0012\u0010\b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\t\"\u00020\u0002H\u0014\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/atecresa/gestionLineasComanda/RepositoryBDLineas$GuardarLineaAsyncTask;", "Landroid/os/AsyncTask;", "Lcom/atecresa/gestionLineasComanda/LineasComanda;", "Ljava/lang/Void;", "mAsyncTaskDao", "Lcom/atecresa/gestionLineasComanda/LineasDao;", "(Lcom/atecresa/gestionLineasComanda/LineasDao;)V", "doInBackground", "linea", "", "([Lcom/atecresa/gestionLineasComanda/LineasComanda;)Ljava/lang/Void;", "MyGest 2_debug"})
    static final class GuardarLineaAsyncTask extends android.os.AsyncTask<com.atecresa.gestionLineasComanda.LineasComanda, java.lang.Void, java.lang.Void> {
        private final com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao = null;
        
        public GuardarLineaAsyncTask(@org.jetbrains.annotations.NotNull
        com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        @java.lang.Override
        protected java.lang.Void doInBackground(@org.jetbrains.annotations.NotNull
        com.atecresa.gestionLineasComanda.LineasComanda... linea) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0002\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\'\u0010\u0006\u001a\u0004\u0018\u00010\u00022\u0016\u0010\u0007\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00020\b\"\u0004\u0018\u00010\u0002H\u0014\u00a2\u0006\u0002\u0010\tR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/atecresa/gestionLineasComanda/RepositoryBDLineas$EliminarLineaPendienteAsyncTask;", "Landroid/os/AsyncTask;", "Ljava/lang/Void;", "mAsyncTaskDao", "Lcom/atecresa/gestionLineasComanda/LineasDao;", "(Lcom/atecresa/gestionLineasComanda/LineasDao;)V", "doInBackground", "p0", "", "([Ljava/lang/Void;)Ljava/lang/Void;", "MyGest 2_debug"})
    static final class EliminarLineaPendienteAsyncTask extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        private final com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao = null;
        
        public EliminarLineaPendienteAsyncTask(@org.jetbrains.annotations.NotNull
        com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        @java.lang.Override
        protected java.lang.Void doInBackground(@org.jetbrains.annotations.NotNull
        java.lang.Void... p0) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0002\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0001B\u000f\b\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J#\u0010\u0007\u001a\u0004\u0018\u00010\u00032\u0012\u0010\b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\t\"\u00020\u0002H\u0014\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/atecresa/gestionLineasComanda/RepositoryBDLineas$EliminarLineasRetenidasAsyncTask;", "Landroid/os/AsyncTask;", "", "Ljava/lang/Void;", "mAsyncTaskDao", "Lcom/atecresa/gestionLineasComanda/LineasDao;", "(Lcom/atecresa/gestionLineasComanda/LineasDao;)V", "doInBackground", "_mesa", "", "([Ljava/lang/String;)Ljava/lang/Void;", "MyGest 2_debug"})
    static final class EliminarLineasRetenidasAsyncTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        private final com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao = null;
        
        public EliminarLineasRetenidasAsyncTask(@org.jetbrains.annotations.NotNull
        com.atecresa.gestionLineasComanda.LineasDao mAsyncTaskDao) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        @java.lang.Override
        protected java.lang.Void doInBackground(@org.jetbrains.annotations.NotNull
        java.lang.String... _mesa) {
            return null;
        }
    }
}