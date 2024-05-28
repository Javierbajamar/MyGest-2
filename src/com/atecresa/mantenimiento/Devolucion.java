package com.atecresa.mantenimiento;

/**
 * Created by carloskinn on 8/3/18.
 * AQUÍ GUARDAREMOS LA ÚLTIMA DEVOLUCIÓN. LA LEEREMOS DESDE GLOBAL
 * <p>
 * {"ID":"0","IDFP":"-1","IDTRAT":"1","CP":"1",
 * "CONCEPTO":"Efectivo\/Efectivo","IMPORTE":"50.0",
 * "DEVOLUCION":"0.0","TOTALIMPORTE":"262.36","TOTALCOBRADO":"100.0",
 * "TOTALPENDIENTE":"162.36"}}]
 */

public class Devolucion {

    String id;
    String idfp;
    String idtrat;
    String cp;
    String concepto;
    String importe;
    String devolucion;
    String totalImporte;
    String totalCobrado;
    String totalPendiente;

    public Devolucion(String id, String idfp,String idtrat, String cp, String concepto, String importe, String devolucion, String totalImporte, String totalCobrado, String totalPendiente) {
        this.id = id;
        this.idfp = idfp;
        this.idtrat=idtrat;
        this.cp = cp;
        this.concepto = concepto;
        this.importe = importe;
        this.devolucion = devolucion;
        this.totalImporte = totalImporte;
        this.totalCobrado = totalCobrado;
        this.totalPendiente = totalPendiente;
    }

    public String getId() {
        return id;
    }

    public String getIdfp() {
        return idfp;
    }

    public String getIdtrat() {
        return idtrat;
    }

    public String getCp() {
        return cp;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getImporte() {
        return importe;
    }

    public String getDevolucion() {
        return devolucion;
    }

    public String getTotalImporte() {
        return totalImporte;
    }

    public String getTotalCobrado() {
        return totalCobrado;
    }

    public String getTotalPendiente() {
        return totalPendiente;
    }
}
