package com.atecresa.cocina;

/*
CAMBIOS 2019
 */

import android.annotation.SuppressLint;
import android.util.Log;

import com.atecresa.application.Inicio;
import com.atecresa.preferencias.Constantes;
import com.atecresa.util.Formateador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Cocina {

    private ArrayList<ComandaCocina> listaComandaCocinas;
    private ArrayList<Linea> listaLineasSinNotificar;

    public ArrayList<ComandaCocina> getListaComandaCocinas() {
        return listaComandaCocinas;
    }

    public ArrayList<Linea> getListaLineasSinNotificar() {
        return listaLineasSinNotificar;
    }

    public boolean hayLineasSinNotificar(){
        return this.listaLineasSinNotificar.size()>0;
    }

    public Cocina() {
        this.listaComandaCocinas = new ArrayList<>();
        this.listaLineasSinNotificar = new ArrayList<>();
    }

    public boolean cargarComandas(String js) {
        JSONObject jsonPlatos;
        JSONArray filas; //TODO ESTO TIENE QUE INICIALIZARSE AQUÍ PARA NO REPETIR EN EL IF LA CONDICIÓN DOS VECES
        try {
            int numComandas = this.listaComandaCocinas.size();
            jsonPlatos = new JSONObject(js);
            if (js.contains("getcocina"))
                filas = jsonPlatos.getJSONObject("getcocina")
                        .getJSONObject("resultado").getJSONArray("MESAS");
            else
                filas = jsonPlatos.getJSONObject("getCocina")
                        .getJSONObject("resultado").getJSONArray("MESAS");
            /*
             * COPIAR COLECCI DE COMANDAS
             * HACER UN COMPARADOR PARA SABER SI HAY UN CAMBIO EN ALGUNA LINEA
             * SÓLO SI LAS COLECCIONES SON DEL MISMO TAMAÑO
             */
            //TODO BUSCAR LA FORMA AQUÍ DE DETECTAR LAS LINEAS EN PREPARACIÓN, EN EL NUEVO ESTADO INTERMEDIO
            ArrayList<ComandaCocina> listaComandasOriginal = (ArrayList<ComandaCocina>) this.listaComandaCocinas.clone();
            this.listaComandaCocinas.clear();
            for (int i = 0; i < filas.length(); i++) {
                JSONObject comanda = filas.getJSONObject(i);
                boolean comandaCompleta = true;
                ComandaCocina c = new ComandaCocina(comanda.getString("MESA"),
                        comanda.getString("FECHA"),
                        comanda.getString("TIEMPO"),
                        comanda.getString("COMENSALES"),
                        comanda.getString("OPERADOR"), !comanda.getString(
                        "IMPRESO").equals("0"), comandaCompleta,
                        Formateador.getTiempoDeEsperaMesa(comanda.getString("TIEMPO")));

                JSONArray _lineas = comanda.getJSONArray("LINEAS");
                for (int x = 0; x < _lineas.length(); x++) {
                    JSONObject _linea = _lineas.getJSONObject(x);
                    int estado;
                    if (isPlatoEnPreparacion(_linea.getString("IDDETALLE"))) { //TODO SI ESTO NO VA BORRAR ESTE PRIMER IF
                        estado = Constantes.linea_cocina_en_preparacion;
                    } else if (_linea.getString("ELABORADO_OPER").equals("")) {
                        estado = Constantes.linea_cocina_recibida_sin_hacer;
                    } else {
                        estado = Constantes.linea_cocina_recibida_hecha;
                    }
                    if (_linea.getString("TIPO").equals("H"))
                        estado = Constantes.linea_cocina_recibida_hecha;
                    Linea lc = new Linea(_linea.getString("IDDETALLE"),
                            _linea.getString("IDARTICULO"),
                            _linea.getString("ARTICULO"),
                            _linea.getString("UDS"), _linea.getString("OBS"),
                            _linea.getString("ELABORADO_OPER"),
                            _linea.getString("ELABORADO_FECHA"),
                            _linea.getString("TIPO"), estado);

                    lc.setEstado(this.setNuevoEstado(lc));
                    c.getLineasComanda().add(lc);

                    if (lc.getEstado() == Constantes.linea_cocina_no_notificada_sin_hacer
                            || lc.getEstado() == Constantes.linea_cocina_recibida_sin_hacer
                            || lc.getEstado() == Constantes.linea_cocina_no_notificada_hecha
                            || lc.getEstado() == Constantes.linea_cocina_en_preparacion) {
                        comandaCompleta = false;
                    }
                }
                c.setCompleta(comandaCompleta);
                this.listaComandaCocinas.add(c);

            }

            if (this.listaComandaCocinas.size() != numComandas) {
                return this.listaComandaCocinas.size() > numComandas;
            } else {
                for (int x = 0; x < this.listaComandaCocinas.size(); x++) {
                    if (compararPlatos(this.listaComandaCocinas.get(x), listaComandasOriginal.get(x)))
                        return true;
                }
                return false;
            }
        } catch (Exception ex) {
            Log.e("COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + ex.getMessage());
            return false;
        } finally {
        }

    }

    private boolean compararPlatos(ComandaCocina c1, ComandaCocina c2) {
        return c1.getLineasComanda().size() != c2.getLineasComanda().size();
    }

    // FUNCI�N PARA ASIGNARLE EL TIPO DE LINEA DEPENDE DE SI ESTA PENDIENTE DE
    // NOTIFICAR O NO
    // LA USAREMOS EN CARGARCOMANDAS
    private int setNuevoEstado(Linea _linea) {
        if (this.listaLineasSinNotificar.size() == 0)
            return _linea.getEstado();
        else {
            for (Linea lsn : this.listaLineasSinNotificar) {
                if (lsn.getIddetalle().equals(_linea.getIddetalle())) {
                    return lsn.getEstado();
                }

            }
        }
        return _linea.getEstado();
    }

    public void addLineaSinNotificar(Linea lc, boolean preparado) {
        try {
            Linea nuevoPlato = this.getPlatoSinNotificar(lc.getIddetalle());
            if (nuevoPlato == null) { // No existe en lista de platos pendientes
                nuevoPlato = new Linea(lc.getIddetalle(), lc.getIdarticulo(),
                        lc.getArticulo(), lc.getUds(), lc.getObs(),
                        lc.getElaborado_oper(), lc.getElaborado_fecha(),
                        lc.getTipo(), lc.getEstado());

                if (preparado) {
                    lc.setElaborado_oper(Inicio.gb.getOperadorActual().getId());
                    this.listaLineasSinNotificar.add(lc);
                } else {
                    lc.setElaborado_oper("0");
                    this.listaLineasSinNotificar.add(lc);
                }
            } else {
                if (preparado) {
                    nuevoPlato.setElaborado_oper(Inicio.gb.getOperadorActual().getId());
                } else {
                    this.listaLineasSinNotificar.remove(nuevoPlato);
                }
            }
        } catch (Exception e) {

        }
    }

    public Linea getPlatoSinNotificar(String iddetalle) {
        for (Linea pl : this.listaLineasSinNotificar) {
            try {
                if (pl.getIddetalle().equals(iddetalle)) {
                    return pl;
                }
            } catch (Exception e) {
                Log.e("COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }
        }
        return null;
    }

    public String getVozAcumulados() {
        String voz = "";
        if (this.lineasAcumuladas.size() > 0) {
            voz = "Platos pendientes de preparar: ";
            for (Linea linea : this.getLineasAcumuladas()) {
                String uds = Formateador.formatearUds(Double.parseDouble(linea
                        .getUds()));
                if (uds.equals("1") || (uds.equals("1.0")))
                    uds = "";
                else {
                    uds = Formateador.formatearUds(Double.parseDouble(uds));
                }
                voz += uds + " " + linea.getArticulo() + "."
                        + linea.getObs() + ".";

            }
        }

        return voz;
    }

    public String getVozContenidoMesa(int position) {
        String voz = "";
        boolean platosSinPreparar = false;
        ComandaCocina comandaCocina = this.listaComandaCocinas.get(position);
        try {
            voz = Inicio.gb.getRangoMesa(Integer.parseInt(comandaCocina.getMesa()))
                    .getString("TX") + " " + comandaCocina.getMesa() + ".";
            for (Linea linea : comandaCocina.getLineasComanda()) {
                Linea lineaModificada = this.getPlatoSinNotificar(linea
                        .getIddetalle());

                if (lineaModificada == null) {
                    if (linea.getElaborado_oper().equals("")
                            || linea.getElaborado_oper().equals("0")) {
                        String uds = Formateador.formatearUds(Double.parseDouble(linea
                                .getUds()));
                        if (uds.equals("1") || (uds.equals("1.0")))
                            uds = "";
                        else {
                            uds = Formateador.formatearUds(Double.parseDouble(uds));
                        }
                        platosSinPreparar = true;
                        voz += uds + " " + linea.getArticulo() + "."
                                + linea.getObs() + ".";
                    }
                } else {
                    if (lineaModificada.getElaborado_oper().equals("")
                            || lineaModificada.getElaborado_oper().equals("0")) {
                        String uds = Formateador.formatearUds(Double.parseDouble(linea
                                .getUds()));
                        if (uds.equals("1") || (uds.equals("1.0")))
                            uds = "";
                        else if (!uds.startsWith("-")) {
                            uds = uds.replace("-", "");
                            uds = "Anulación "
                                    + Formateador.formatearUds(Double.parseDouble(uds));
                        } else {
                            uds = Formateador.formatearUds(Double.parseDouble(uds));
                        }
                        platosSinPreparar = true;
                        voz += uds + " " + lineaModificada.getArticulo() + "."
                                + lineaModificada.getObs() + ".";
                    }
                }

            }
            if (!platosSinPreparar)
                voz = "";
        } catch (JSONException e) {
            Log.e("COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }

        return voz;
    }

    public void notificarPlatosPreparados() {
        for (Linea lc : this.listaLineasSinNotificar) {
            try {
                Inicio.gb.agregarFuncion("setCocina", lc.getIddetalle() + ";" + lc.getElaborado_oper() + ";" + Inicio.getIdd());
            } catch (Exception e) {
                Log.e("COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }
        }
    }

    public void vaciarListaLineasSinNotificar() {
        if (this.listaLineasSinNotificar != null)
            this.listaLineasSinNotificar.clear();
    }

    public void vaciarLineasPlatosEnPreparacion(){
        if (this.listaPlatosEnPreparacion!=null)
            this.listaPlatosEnPreparacion.clear();
    }

    /*
     * ACUMULADOS
     */

    ArrayList<Linea> lineasAcumuladas;

    public ArrayList<Linea> getLineasAcumuladas() {
        return lineasAcumuladas;
    }

    public void generarListaAcumulados() {
        /*
         * Aqu� haremos el string para el acumulado, y tambi�n modificaremos el
         * booleano mesaCompleta, para ponerlo a true
         */
        lineasAcumuladas = new ArrayList<Linea>();

        for (ComandaCocina com : this.listaComandaCocinas) {
            if (!com.isCompleta()) {
                for (Linea linea : com.getLineasComanda()) {
                    if (linea.getEstado() == Constantes.linea_cocina_no_notificada_sin_hacer
                            || linea.getEstado() == Constantes.linea_cocina_recibida_sin_hacer
                            && !linea.getTipo().equals("H")) {
                        insertarEnListaAcumulados(linea);
                    }
                }

            }
        }
        Collections.sort(this.lineasAcumuladas,
                new ComparadorPlatosAcumulados());


    }

    private class ComparadorPlatosAcumulados implements Comparator<Linea> {

        public int compare(Linea o1, Linea o2) {

            try {
                return (Double.parseDouble(o1.getUds()) > Double.parseDouble(o2
                        .getUds()) ? -1
                        : (Double.parseDouble(o1.getUds())) == (Double
                        .parseDouble(o2.getUds())) ? 0 : 1);
            } catch (Exception e) {
                Log.e("COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }
            return 0;
        }
    }

    //TODO Implementar esta función en la pantalla de cocina y no usar la anterior
    private class ComparadorPlatosAcumuladosNuevo implements Comparator<Linea> {

        public int compare(Linea o1, Linea o2) {

            try {
                return Double.compare(Double.parseDouble(o2
                        .getUds()), Double.parseDouble(o1.getUds()));
            } catch (Exception e) {
                Log.e("COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }
            return 0;
        }
    }

    private void insertarEnListaAcumulados(Linea l) {
        boolean encontrado = false;
        if (this.lineasAcumuladas.size() > 0) {
            for (Linea ln : this.lineasAcumuladas) {
                if (ln.getIdarticulo().equals(l.getIdarticulo())) {
                    encontrado = true;
                    String u = String
                            .valueOf((Double.parseDouble(ln.getUds()) + (Double
                                    .parseDouble(l.getUds()))));
                    ln.setUds(u);
                }
            }
        }
        if (!encontrado) {
            this.lineasAcumuladas.add(new Linea(l.getIddetalle(), l
                    .getIdarticulo(), l.getArticulo(), l.getUds(), l.getObs(),
                    l.getElaborado_oper(), l.getElaborado_fecha(), l.getTipo(),
                    l.getEstado()));
        }

    }

    @SuppressLint("SimpleDateFormat")
    public void actualizarFechas() {
        Calendar ahora = Calendar.getInstance();
        Date fechaActual = ahora.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        long horas;
        long minutos;
        for (ComandaCocina com : this.listaComandaCocinas) {
            // Cambiamos la hora s�lo de las mesas en
            // curso, para ahorrar tiempo
            try {
                if (!com.isCompleta()) {
                    Date fechaMesa = formatter.parse(com.getFecha());
                    long diff = fechaActual.getTime() - fechaMesa.getTime();

                    horas = diff / (60 * 60 * 1000);
                    minutos = diff / (60 * 1000) - horas * 60;
                    String tiempo;
                    if (horas > 0)
                        tiempo = horas + "h " + minutos + "m";
                    else
                        tiempo = minutos + " m";
                    com.setTiempo(tiempo);
                    if (horas > 0 || minutos > 15)
                        com.setTiempoEspera(Constantes.tiempo_con_retraso_grave);
                    else if (minutos > 5)
                        com.setTiempoEspera(Constantes.tiempo_con_retraso);
                    else
                        com.setTiempoEspera(Constantes.tiempo_ok);
                }

            } catch (ParseException e) {
                Log.e("COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }

        }

    }

    public boolean hayPlatosSinNotificar() {
        for (ComandaCocina com : this.listaComandaCocinas) {
            for (Linea linea : com.getLineasComanda()) {
                if (linea.getEstado() == Constantes.linea_cocina_no_notificada_hecha
                        || linea.getEstado() == Constantes.linea_cocina_no_notificada_sin_hacer) {
                    return true;
                }
            }
        }
        return false;
    }

    //region PLATOS EN PREPARACIÓN

    private ArrayList<Linea> listaPlatosEnPreparacion = new ArrayList<>();

    public void addPlatoPreparacion(Linea l) {
        listaPlatosEnPreparacion.add(l);
    }

    public boolean hayPlatosEnPreparacion(){
        return this.listaPlatosEnPreparacion.size()>0;
    }

    public void deletePlatoPreparado(String iddetalle) {
        Linea l2 = null;
        for (Linea l : listaPlatosEnPreparacion) {
            if (l.getIddetalle().equals(iddetalle)) {
                l2 = l;
                break;
            }

        }
        if (l2 != null) {
            listaPlatosEnPreparacion.remove(l2);
        }
    }

    public boolean isPlatoEnPreparacion(String iddetalle) {
        for (Linea l : listaPlatosEnPreparacion) {
            if (l.getIddetalle().equals(iddetalle))
                return true;
        }
        return false;
    }
    //endregion

}
