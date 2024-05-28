package com.atecresa.gestionCobros.cloud;

/**
 * Created by carlos on 14/02/2018.
 * "IDTRAT": "1",
 * "FORMA": "Efectivo",
 * "TRATAMIENTO": "Efectivo",
 * "CP": "1",
 * "PAYLINK": "0"
 * todo NO SABEMOS SI ESTA CLASE LA USAREMOS PARA LOS PAGOS EN EL MYGEST NORMAL
 */

public class FormaPago {

    private String id;
    private String forma;
    private String tratamiento;

    public FormaPago(String id, String forma, String tratamiento) {
        this.id = id;
        this.forma = forma;
        this.tratamiento = tratamiento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String descripcion) {
        this.forma = descripcion;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }
}
