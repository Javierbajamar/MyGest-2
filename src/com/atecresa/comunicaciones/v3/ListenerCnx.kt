package com.atecresa.comunicaciones.v3

interface ListenerCnx {
    fun notifySuccess(response: CnxResponseK)
    fun notifyError(response: CnxResponseK)
}