package com.atecresa.gestionCobros.tpv;

import java.lang.System;

/**
 * Created by carlos on 14/02/2018.
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0013B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\bH\u0016J\u0018\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u0014\u0010\u0011\u001a\u00020\n2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005R\u0016\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/atecresa/gestionCobros/tpv/AdapterCobrosTPV;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/atecresa/gestionCobros/tpv/AdapterCobrosTPV$ViewHolder;", "()V", "lista", "Ljava/util/ArrayList;", "Lcom/atecresa/gestionCobros/tpv/FormaPagoTPV;", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setLista", "_listaFormaPagoTPVS", "ViewHolder", "MyGest 2_debug"})
public final class AdapterCobrosTPV extends androidx.recyclerview.widget.RecyclerView.Adapter<com.atecresa.gestionCobros.tpv.AdapterCobrosTPV.ViewHolder> {
    private java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV> lista;
    
    public AdapterCobrosTPV() {
        super();
    }
    
    public final void setLista(@org.jetbrains.annotations.NotNull
    java.util.ArrayList<com.atecresa.gestionCobros.tpv.FormaPagoTPV> _listaFormaPagoTPVS) {
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public com.atecresa.gestionCobros.tpv.AdapterCobrosTPV.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.atecresa.gestionCobros.tpv.AdapterCobrosTPV.ViewHolder holder, int position) {
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u00020\bX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2 = {"Lcom/atecresa/gestionCobros/tpv/AdapterCobrosTPV$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "(Landroid/view/View;)V", "cv", "Landroidx/cardview/widget/CardView;", "tvDescripcion", "Landroid/widget/TextView;", "getTvDescripcion$MyGest_2_debug", "()Landroid/widget/TextView;", "setTvDescripcion$MyGest_2_debug", "(Landroid/widget/TextView;)V", "MyGest 2_debug"})
    public static final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private androidx.cardview.widget.CardView cv;
        @org.jetbrains.annotations.NotNull
        private android.widget.TextView tvDescripcion;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull
        android.view.View v) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.TextView getTvDescripcion$MyGest_2_debug() {
            return null;
        }
        
        public final void setTvDescripcion$MyGest_2_debug(@org.jetbrains.annotations.NotNull
        android.widget.TextView p0) {
        }
    }
}