package com.atecresa.comunicaciones.v3;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u0000 \r2\u00020\u0001:\u0002\r\u000eB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tJ\b\u0010\u000b\u001a\u00020\u0007H\u0002J\u0006\u0010\f\u001a\u00020\u0007R\u000e\u0010\u0005\u001a\u00020\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/atecresa/comunicaciones/v3/JsonTPVQueryK;", "", "cnxListenerK", "Lcom/atecresa/comunicaciones/v3/ListenerCnx;", "(Lcom/atecresa/comunicaciones/v3/ListenerCnx;)V", "listener", "agregarFuncion", "", "nombre", "", "valor", "inicializarJSONFunciones", "sendQuery", "Companion", "ExecuteSocketQuery", "MyGest 2_debug"})
public final class JsonTPVQueryK {
    private com.atecresa.comunicaciones.v3.ListenerCnx listener;
    @org.jetbrains.annotations.NotNull
    public static final com.atecresa.comunicaciones.v3.JsonTPVQueryK.Companion Companion = null;
    private static final org.json.JSONObject jsFunciones = null;
    
    public JsonTPVQueryK(@org.jetbrains.annotations.NotNull
    com.atecresa.comunicaciones.v3.ListenerCnx cnxListenerK) {
        super();
    }
    
    private final void inicializarJSONFunciones() {
    }
    
    public final void agregarFuncion(@org.jetbrains.annotations.NotNull
    java.lang.String nombre, @org.jetbrains.annotations.NotNull
    java.lang.String valor) {
    }
    
    public final void sendQuery() {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J#\u0010\u0012\u001a\u0004\u0018\u00010\u00022\u0012\u0010\u0013\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0014\"\u00020\u0002H\u0014\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0002H\u0014R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001c\u0010\f\u001a\u0004\u0018\u00010\rX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0019"}, d2 = {"Lcom/atecresa/comunicaciones/v3/JsonTPVQueryK$ExecuteSocketQuery;", "Landroid/os/AsyncTask;", "Ljava/lang/Void;", "listenerK", "Lcom/atecresa/comunicaciones/v3/ListenerCnx;", "jsFunciones", "Lorg/json/JSONObject;", "(Lcom/atecresa/comunicaciones/v3/ListenerCnx;Lorg/json/JSONObject;)V", "getJsFunciones", "()Lorg/json/JSONObject;", "getListenerK", "()Lcom/atecresa/comunicaciones/v3/ListenerCnx;", "response", "Lcom/atecresa/comunicaciones/v3/CnxResponseK;", "getResponse$MyGest_2_debug", "()Lcom/atecresa/comunicaciones/v3/CnxResponseK;", "setResponse$MyGest_2_debug", "(Lcom/atecresa/comunicaciones/v3/CnxResponseK;)V", "doInBackground", "voids", "", "([Ljava/lang/Void;)Ljava/lang/Void;", "onPostExecute", "", "aVoid", "MyGest 2_debug"})
    static final class ExecuteSocketQuery extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        @org.jetbrains.annotations.NotNull
        private final com.atecresa.comunicaciones.v3.ListenerCnx listenerK = null;
        @org.jetbrains.annotations.NotNull
        private final org.json.JSONObject jsFunciones = null;
        @org.jetbrains.annotations.Nullable
        private com.atecresa.comunicaciones.v3.CnxResponseK response;
        
        public ExecuteSocketQuery(@org.jetbrains.annotations.NotNull
        com.atecresa.comunicaciones.v3.ListenerCnx listenerK, @org.jetbrains.annotations.NotNull
        org.json.JSONObject jsFunciones) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.atecresa.comunicaciones.v3.ListenerCnx getListenerK() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.json.JSONObject getJsFunciones() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final com.atecresa.comunicaciones.v3.CnxResponseK getResponse$MyGest_2_debug() {
            return null;
        }
        
        public final void setResponse$MyGest_2_debug(@org.jetbrains.annotations.Nullable
        com.atecresa.comunicaciones.v3.CnxResponseK p0) {
        }
        
        @org.jetbrains.annotations.Nullable
        @java.lang.Override
        protected java.lang.Void doInBackground(@org.jetbrains.annotations.NotNull
        java.lang.Void... voids) {
            return null;
        }
        
        @java.lang.Override
        protected void onPostExecute(@org.jetbrains.annotations.NotNull
        java.lang.Void aVoid) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/atecresa/comunicaciones/v3/JsonTPVQueryK$Companion;", "", "()V", "jsFunciones", "Lorg/json/JSONObject;", "executeHTTPQuery", "", "_ctx", "Landroid/content/Context;", "listener", "Lcom/atecresa/comunicaciones/v3/ListenerCnx;", "MyGest 2_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        private final void executeHTTPQuery(android.content.Context _ctx, com.atecresa.comunicaciones.v3.ListenerCnx listener) {
        }
    }
}