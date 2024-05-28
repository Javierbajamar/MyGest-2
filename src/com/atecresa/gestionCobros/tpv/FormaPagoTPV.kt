package com.atecresa.gestionCobros.tpv

import com.google.gson.annotations.SerializedName

data class FormaPagoTPV(@SerializedName("ID") val id: String,
                        @SerializedName("DESCRIPCION") val descripcion: String,
                        @SerializedName("TRATAMIENTO") val tratamiento: String,
                        @SerializedName("DISPOSITIVO") val dispositivo:String)