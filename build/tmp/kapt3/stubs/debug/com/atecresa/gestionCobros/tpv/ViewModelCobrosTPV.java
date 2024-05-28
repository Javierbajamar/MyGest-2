package com.atecresa.gestionCobros.tpv;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\u001e\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0012R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00068F\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/atecresa/gestionCobros/tpv/ViewModelCobrosTPV;", "Landroidx/lifecycle/ViewModel;", "_pl", "Lcom/atecresa/gestionCobros/tpv/ListenerCobrosTPV;", "(Lcom/atecresa/gestionCobros/tpv/ListenerCobrosTPV;)V", "data", "Landroidx/lifecycle/MutableLiveData;", "Ljava/util/ArrayList;", "Lcom/atecresa/gestionCobros/tpv/FormaPagoTPV;", "items", "getItems", "()Landroidx/lifecycle/MutableLiveData;", "listaFormasDePagoTPVS", "listenerCobrosTPV", "loadPayments", "", "sendPayment", "_total", "", "_idTratamiento", "_codeTransaction", "MyGest 2_debug"})
public final class ViewModelCobrosTPV extends androidx.lifecycle.ViewModel {
    private com.atecresa.gestionCobros.tpv.ListenerCobrosTPV listenerCobrosTPV;
    private java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV> listaFormasDePagoTPVS;
    private androidx.lifecycle.MutableLiveData<java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV>> data;
    
    public ViewModelCobrosTPV(@org.jetbrains.annotations.NotNull
    com.atecresa.gestionCobros.tpv.ListenerCobrosTPV _pl) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.MutableLiveData<java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV>> getItems() {
        return null;
    }
    
    private final void loadPayments() {
    }
    
    public final void sendPayment(@org.jetbrains.annotations.NotNull
    java.lang.String _total, @org.jetbrains.annotations.NotNull
    java.lang.String _idTratamiento, @org.jetbrains.annotations.NotNull
    java.lang.String _codeTransaction) {
    }
}