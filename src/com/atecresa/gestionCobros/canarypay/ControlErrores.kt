package com.atecresa.gestionCobros.canarypay

object ControlErrores {
    //ANOTACIÓN PARA HACER LA FUNCIÓN ESTÁTICA
    @JvmStatic fun codeToString(code: String): String {
        return when (code) {
            "U001" -> "USUARIO NO ENCONTRADO"
            "U018" -> "USUARIO NO IDENTIFICADO"
            "U019" -> "NO AUTORIZADO"
            "U021","T007" -> "SALDO INSUFICIENTE"
            else -> "HA HABIDO UN ERROR DE COMUNICACIÓN CON LA PLATAFORMA DE PAGO"
        }
    }
}