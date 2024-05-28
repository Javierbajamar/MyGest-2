package com.atecresa.gestionCobros.tpv;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2 = {"Lcom/atecresa/gestionCobros/tpv/RepositoryCobrosTPV;", "", "()V", "Companion", "MyGest 2_debug"})
public final class RepositoryCobrosTPV {
    @org.jetbrains.annotations.NotNull
    public static final com.atecresa.gestionCobros.tpv.RepositoryCobrosTPV.Companion Companion = null;
    private static java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV> listaFormasDePagoTPVS;
    @org.jetbrains.annotations.NotNull
    private static final androidx.lifecycle.MutableLiveData<java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV>> items = null;
    
    public RepositoryCobrosTPV() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J0\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u00102\b\u0010\u0015\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0016\u001a\u00020\u0017R\u001d\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001e\u0010\t\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/atecresa/gestionCobros/tpv/RepositoryCobrosTPV$Companion;", "", "()V", "items", "Landroidx/lifecycle/MutableLiveData;", "Ljava/util/ArrayList;", "Lcom/atecresa/gestionCobros/tpv/FormaPagoTPV;", "getItems", "()Landroidx/lifecycle/MutableLiveData;", "listaFormasDePagoTPVS", "Lkotlin/collections/ArrayList;", "getFormasPagoTPV", "", "guardarFormasPagoTPV", "", "jsonArray", "", "sendPayment", "_total", "_idTratamiento", "_codeTransaction", "firma", "listenerCobrosTPV", "Lcom/atecresa/gestionCobros/tpv/ListenerCobrosTPV;", "MyGest 2_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final androidx.lifecycle.MutableLiveData<java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV>> getItems() {
            return null;
        }
        
        private final void getFormasPagoTPV() {
        }
        
        public final boolean guardarFormasPagoTPV(@org.jetbrains.annotations.NotNull
        java.lang.String jsonArray) {
            return false;
        }
        
        public final void sendPayment(@org.jetbrains.annotations.NotNull
        java.lang.String _total, @org.jetbrains.annotations.NotNull
        java.lang.String _idTratamiento, @org.jetbrains.annotations.NotNull
        java.lang.String _codeTransaction, @org.jetbrains.annotations.Nullable
        java.lang.String firma, @org.jetbrains.annotations.NotNull
        com.atecresa.gestionCobros.tpv.ListenerCobrosTPV listenerCobrosTPV) {
        }
    }
}