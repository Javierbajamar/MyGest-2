package com.atecresa.login;

/*
CLASE PARA GUARDAR LOS OPERADORES
GESTIONAREMOS PERMISOS DESDE AQUÍ
 */

public class Operador {
    private String id;
    private String nombre;
    private String clave;
    private boolean puedeCobrar;
    private boolean validado;

    public Operador(String _id, String _nombre, String _clave, boolean _validado,boolean _puedeCobrar) {
        this.id = _id;
        this.nombre = _nombre;
        this.clave = _clave;
        this.puedeCobrar = _puedeCobrar;
        this.validado=_validado;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getClave() {
        return clave;
    }

    public boolean isValidado() {
        return validado;
    }

    public boolean isPuedeCobrar() {
        return puedeCobrar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    public void setPuedeCobrar(boolean puedeCobrar) {
        this.puedeCobrar = puedeCobrar;
    }
}
