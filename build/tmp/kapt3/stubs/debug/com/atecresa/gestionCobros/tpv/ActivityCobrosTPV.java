package com.atecresa.gestionCobros.tpv;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\f\u001a\u00020\rH\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0002J(\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\rH\u0002J\b\u0010\u0015\u001a\u00020\u000fH\u0002J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\rH\u0016J\u0010\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0019H\u0016J\u0012\u0010\u001a\u001a\u00020\u000f2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0014J\u0010\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0018\u0010 \u001a\u00020\u00192\u0006\u0010!\u001a\u00020\u000b2\u0006\u0010\"\u001a\u00020#H\u0016J\u0010\u0010$\u001a\u00020\u00192\u0006\u0010%\u001a\u00020&H\u0016J\b\u0010\'\u001a\u00020\u000fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/atecresa/gestionCobros/tpv/ActivityCobrosTPV;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/atecresa/gestionCobros/tpv/ListenerCobrosTPV;", "()V", "binding", "Lcom/atecresa/application/databinding/CobrosTpvActivityBinding;", "mAdapterCobrosTPV", "Lcom/atecresa/gestionCobros/tpv/AdapterCobrosTPV;", "mDrawingView", "Lcom/atecresa/gestionCobros/tpv/DrawingView;", "requestPaymentCode", "", "getFirma", "", "initFormasPago", "", "mostrarDialogCobro", "formaPago", "importe", "entrega", "devolucion", "mostrarDialogFirma", "notifyPaymentError", "response", "notifyPaymentSuccess", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateOptionsMenu", "menu", "Landroid/view/Menu;", "onKeyUp", "keyCode", "event", "Landroid/view/KeyEvent;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "volverAComanda", "MyGest 2_debug"})
public final class ActivityCobrosTPV extends androidx.appcompat.app.AppCompatActivity implements com.atecresa.gestionCobros.tpv.ListenerCobrosTPV {
    private com.atecresa.gestionCobros.tpv.AdapterCobrosTPV mAdapterCobrosTPV;
    private final int requestPaymentCode = 1;
    private com.atecresa.gestionCobros.tpv.DrawingView mDrawingView;
    private com.atecresa.application.databinding.CobrosTpvActivityBinding binding;
    
    public ActivityCobrosTPV() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initFormasPago() {
    }
    
    @java.lang.Override
    public void notifyPaymentSuccess(boolean response) {
    }
    
    @java.lang.Override
    public void notifyPaymentError(@org.jetbrains.annotations.NotNull
    java.lang.String response) {
    }
    
    private final void mostrarDialogCobro(java.lang.String formaPago, java.lang.String importe, java.lang.String entrega, java.lang.String devolucion) {
    }
    
    @java.lang.Override
    public boolean onCreateOptionsMenu(@org.jetbrains.annotations.NotNull
    android.view.Menu menu) {
        return false;
    }
    
    @java.lang.Override
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull
    android.view.MenuItem item) {
        return false;
    }
    
    @java.lang.Override
    public boolean onKeyUp(int keyCode, @org.jetbrains.annotations.NotNull
    android.view.KeyEvent event) {
        return false;
    }
    
    private final void volverAComanda() {
    }
    
    private final void mostrarDialogFirma() {
    }
    
    private final java.lang.String getFirma() {
        return null;
    }
}