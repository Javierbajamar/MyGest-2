package com.atecresa.comunicaciones.v3;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0007"}, d2 = {"Lcom/atecresa/comunicaciones/v3/ListenerCnx;", "", "notifyError", "", "response", "Lcom/atecresa/comunicaciones/v3/CnxResponseK;", "notifySuccess", "MyGest 2_debug"})
public abstract interface ListenerCnx {
    
    public abstract void notifySuccess(@org.jetbrains.annotations.NotNull
    com.atecresa.comunicaciones.v3.CnxResponseK response);
    
    public abstract void notifyError(@org.jetbrains.annotations.NotNull
    com.atecresa.comunicaciones.v3.CnxResponseK response);
}