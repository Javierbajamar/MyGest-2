package com.atecresa.application;

import android.annotation.SuppressLint;
import android.util.Log;

import com.atecresa.clientes.Clientes;
import com.atecresa.cocina.Cocina;
import com.atecresa.comunicaciones.ClienteHTTP;
import com.atecresa.comunicaciones.ClienteSocket;
import com.atecresa.gestionCobros.cloud.FormaPago;
import com.atecresa.gestionCobros.tpv.RepositoryCobrosTPV;
import com.atecresa.gestionLineasComanda.GestorLineas;
import com.atecresa.login.Operador;
import com.atecresa.mantenimiento.Devolucion;
import com.atecresa.mantenimiento.Mesa;
import com.atecresa.mantenimiento.Vinculo;
import com.atecresa.preferencias.Backup;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.util.DesignM;
import com.atecresa.util.Formateador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@SuppressLint("DefaultLocale")
public class Global {

    private String resultadoF = ""; // para las funciones de pedir cuenta, etc

    // ERROR CON CONSTANTES

    private int error = 0;

    public int getError() {
        return this.error;
    }

    public String getResultado() {
        return this.resultadoF;
    }

    // PARÁMETROS DEL COMANDERO
    // CONEXIÓN CON EL TPV

    private final String nSerie;

    public String getNumeroSerie() {
        return this.nSerie;
    }

    private int estado_App = Constantes.app_serie_no_registrado;

    public int getEstadoApp() {
        return this.estado_App;
    }

    /*
     * GESTIÓN DE MESAS
     */

    private String mesa = "1";

    public String getMesa() {
        return this.mesa;
    }

    public void setMesa(String _mesa) {
        this.mesa = _mesa;
    }

    private JSONObject funcionesJSON;

    private ArrayList<JSONObject> listaOperadores;

    public ArrayList<JSONObject> getListaOperadores() {
        return listaOperadores;
    }

    private ArrayList<JSONObject> listaFamilias;
    //NUEVO
    private ArrayList<JSONObject> listaFamiliasVisibles;

    private ArrayList<JSONObject> listaArticulos;

    private String nombreRango = "";

    private int idRango = 0;

    public int getIdRango() {
        return this.idRango;
    }

    public String getTextoRango() { // GUARDAMOS EL NOMBRE DEL RANGO PARA
        // CONSULTARLO EN EL ADAPTADOR
        return this.nombreRango;
    }

    public JSONObject getRangoMesa(int numMesa) {
        try {
            for (JSONObject rango : this.listaRangos) {
                if (numMesa >= rango.getInt("INI")
                        && numMesa <= rango.getInt("FIN"))
                    return rango;
            }
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
        //{"INI":"1","FIN":"10","TX":"SALON","BACKC":"64","FOREC":"16777215"}

        JSONObject rangoNulo = new JSONObject();
        try {
            rangoNulo.put("INI", "0");
            rangoNulo.put("FIN", "0");
            rangoNulo.put("TX", "");
            rangoNulo.put("BACKC", "64");
            rangoNulo.put("FOREC", "16777215");
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }

        return rangoNulo;
    }

    private ArrayList<JSONObject> listaRangos = null; // Aquí tendremos los
    // objetos de tipo rango
    private ArrayList<JSONObject> listaMesas;

    public ArrayList<JSONObject> getlistaRangos() {
        return this.listaRangos;
    }

    public boolean isOperadoresCargados() {
        return (this.listaOperadores != null && this.listaOperadores.size() > 0);
    }

    public boolean isMesasCargadas() {
        return (this.listaMesas != null && this.listaMesas.size() > 0);
    }

    public ArrayList<JSONObject> getFamiliasVisibles() {
        return this.listaFamiliasVisibles;
    }

    public ArrayList<JSONObject> getArticulos() {
        return this.listaArticulos;
    }

    /*
     * Para los vinculos
     */

    //LEER DE LA LISTA DE FAMILIAS VISIBLES CUANDO ESTEMOS EN LA COMANDA
    public JSONObject getArticulo(int id) {

        try {

            for (JSONObject familia : this.listaFamilias) {
                for (int i = 0; i < familia.getJSONArray("ARTICULOS").length(); i++) {
                    if (familia.getJSONArray("ARTICULOS").getJSONObject(i)
                            .getInt("ID") == id) {
                        return familia.getJSONArray("ARTICULOS").getJSONObject(
                                i);
                    }
                }

            }

        } catch (Exception e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
        return null;
    }

    public ArrayList<JSONObject> getMesas() {
        return this.listaMesas;
    }

    public ArrayList<JSONObject> getOperadores() {
        return this.listaOperadores;
    }

    /*
     * INFORMACIÓN DEL CAMARERO
     */

    private final Operador op;

    public Operador getOperadorActual() {
        return op;
    }

    //PERMISOS

    private boolean _isMyorder = false;

    public boolean isMyorder() {
        return this._isMyorder;
    }

    private boolean verificarPrefactura = false;

    public boolean isVerificarPrefactura() {
        return verificarPrefactura;
    }

    //region CONSTRUCTOR

    public Global(String _nserie) {
        this.nSerie = _nserie;
        //TODO Poner a true el último parámetro para probar Canarypay
        this.op = new Operador("0", "", "", false, false);
        this.comensales = "0";
        //Todas estas listas tienen que estar en la clase Inicio, pero se quedan aquí
        this.listaOperadores = new ArrayList<>();
        this.listaMesas = new ArrayList<>();
        this.listaFamilias = new ArrayList<>();
        this.listaFamiliasVisibles = new ArrayList<>();
        this.listaArticulos = new ArrayList<>();
        cocina = new Cocina();
    }

    //endregion

    //region FUNCIONES JSON


    public void cargarJSONFunciones() {
        this.funcionesJSON = null;
        try {
            this.funcionesJSON = new JSONObject();
            if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD))
                funcionesJSON.put("UUID", PreferenciasManager.getUuid());
            JSONArray ja = new JSONArray();
            ja.put(new JSONObject().put("setserie", this.nSerie));
            ja.put(new JSONObject().put("setoperador", this.op.getId() + ";" + this.op.getClave()));
            funcionesJSON.put("FUNCIONES", ja);

        } catch (JSONException e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    private void cargarJSONFunciones(String _operador, String _clave) {
        this.funcionesJSON = null;
        try {
            this.funcionesJSON = new JSONObject();
            if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD))
                funcionesJSON.put("UUID", PreferenciasManager.getUuid());
            JSONArray ja = new JSONArray();
            ja.put(new JSONObject().put("setserie", this.nSerie));
            ja.put(new JSONObject().put("setoperador", _operador + ";" + _clave));
            funcionesJSON.put("FUNCIONES", ja);
        } catch (JSONException e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public void agregarFuncion(String nombre, String valor) {
        JSONObject funcion;
        try {
            //funcion = new JSONObject("{\"" + nombre + "\":\"" + valor + "\"}");
            funcion = new JSONObject();
            funcion.put(nombre, valor);
            this.funcionesJSON.getJSONArray("FUNCIONES").put(funcion);
        } catch (JSONException e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public void ejecutarfuncion() {
        this.error = Constantes.app_OK;
        try {
            //NO BORRAR LA FUNCION LEERRESPUESTA ORIGINAL
            if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)) {
                this.leerRespuestaNew(new JSONObject(ClienteHTTP.ejecutar(this.funcionesJSON.toString())) // + "|END|"
                        .getJSONArray("ROWS"));
            } else {
                this.leerRespuestaNew(new JSONObject(ClienteSocket.ejecutar(
                        PreferenciasManager.getIp(), PreferenciasManager.getPuerto(), PreferenciasManager.getTimeOut(),
                        this.funcionesJSON.toString() + "|END|"))
                        .getJSONArray("ROWS"));
            }


        } catch (JSONException e) {
            this.error = Constantes.error_conexion;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public void cargarImpresoras() {
        this.agregarFuncion("getImpresoras", "");
    }

    //endregion


    //region USUARIO

    public void validarUsuario(String _id, String _clave) {
        this.cargarJSONFunciones(_id, _clave);
        this.agregarFuncion("setoperador", _id + ";" + _clave);
        op.setId(_id);
        op.setClave(_clave);
        op.setValidado(false);
    }

    //endregion

    //region OPCIONES GENERALES

    public void cargarConfig() {
        this.agregarFuncion("getCfg", "");
    }

    public void cargarOperadores() {
        this.agregarFuncion("getoperadores", "");
    }

    public void cargarConfigMesas() {
        this.agregarFuncion("getcfgmesas", "");
    }

    private boolean mesasOK = false;

    public boolean isMesasOK() {
        return mesasOK;
    }

    public void setMesasOK(boolean mesasOK) {
        this.mesasOK = mesasOK;
    }

    /*
     * NUEVOS PERMISOS PARA ANULAR
     */

    private boolean _anularComanda = false; // ANULAR LINEAS RECIBIDAS

    private boolean _anularPrefactura = false; // ANULAR LINEAS PREFACTURA

    public boolean anularComanda() {
        return this._anularComanda;
    }

    public boolean anularPrefactura() {
        return this._anularPrefactura;
    }

    public void cargarPermisos() {
        this.agregarFuncion("getPermisos", "");
    }

    private void guardarPermisos(JSONObject j) {
        try {
            this._anularComanda = j.getJSONObject("resultado")
                    .getString("ANULARCOMANDA").equals("1");
            this._anularPrefactura = j.getJSONObject("resultado")
                    .getString("ANULARPREFACTURA").equals("1");
        } catch (JSONException e) {
            this._anularComanda = false;
            this._anularPrefactura = false;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    //endregion

    /*
     * carga de mesas
     */
    private boolean verSoloMesaslibres = false;

    public void cargarMesas(int _idRango, boolean soloMesasLibres) {
        this.verSoloMesaslibres = soloMesasLibres;
        if ((this.listaRangos != null) && (this.listaRangos.size() > 0)) {
            try {
                this.idRango = _idRango;
                this.nombreRango = this.listaRangos.get(_idRango).getString(
                        "TX");
                this.agregarFuncion("getmesas", this.listaRangos.get(_idRango)
                        .getString("INI")
                        + ";"
                        + this.listaRangos.get(_idRango).getString("FIN"));
            } catch (Exception e) {
                this.error = Constantes.error_tratando_JSON;
                Log.e("GLOBAL",
                        "Linea "
                                + Thread.currentThread().getStackTrace()[2]
                                .getLineNumber() + ": "
                                + e.getMessage());
            }
        }

    }

    /*
     * Función para cargar las mesas con el valor que hemos recibido en el
     * socketserver
     */
    public void funCargarMesasOffline(String objMesas) {


        JSONObject jo;
        JSONArray filas;
        if ((this.listaRangos != null) && (this.listaRangos.size() > 0)) {

            try {

                filas = new JSONObject(objMesas).getJSONObject("getmesas")
                        .getJSONArray("rows");
                if (filas.length() > 0) {
                    if (this.listaMesas == null) {
                        this.listaMesas = new ArrayList<>();
                    } else {
                        this.listaMesas.clear();
                    }

                    this.listaMesas = new ArrayList<>();
                    for (int i = 0; i < filas.length(); i++) {
                        try {
                            jo = (JSONObject) filas.get(i);

                            jo.put("BACKC", this.listaRangos.get(this.idRango)
                                    .getString("BACKC"));
                            jo.put("FOREC", this.listaRangos.get(this.idRango)
                                    .getString("FOREC"));
                            this.listaMesas.add(jo);

                        } catch (JSONException e) {
                            this.error = Constantes.error_tratando_JSON;
                            Log.e("GLOBAL",
                                    "Linea "
                                            + Thread.currentThread()
                                            .getStackTrace()[2]
                                            .getLineNumber() + ": "
                                            + e.getMessage());
                        } catch (Exception e1) {
                            Log.e("GLOBAL",
                                    "Linea "
                                            + Thread.currentThread()
                                            .getStackTrace()[2]
                                            .getLineNumber() + ": "
                                            + e1.getMessage());
                        }
                    }
                }

            } catch (Exception e) {
                this.error = Constantes.error_tratando_JSON;
                Log.e("GLOBAL",
                        "Linea "
                                + Thread.currentThread().getStackTrace()[2]
                                .getLineNumber() + ": "
                                + e.getMessage());
            }
        }

    }

    public void descargarCarta() {
        this.agregarFuncion("getcarta", "");
    }

    private ArrayList<JSONObject> listaFunciones;

    public ArrayList<JSONObject> getListaFunciones() {
        return this.listaFunciones;
    }

    private void guardarTextosLibres(JSONObject familia) {

        try {
            this.listaFunciones = null;
            this.listaFunciones = new ArrayList<>();
            String nombreFamiliaTextosLibres = familia.getString("DES");

            for (int i = 0; i < familia.getJSONArray("ARTICULOS").length(); i++) {
                this.listaFunciones.add(familia.getJSONArray("ARTICULOS")
                        .getJSONObject(i));
            }
        } catch (Exception e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public void cargarArticulos(int position) {
        JSONObject familia = this.listaFamiliasVisibles.get(position);
        // CUANDO CAMBIEN LA TARIFA EN EL TPV, QUE NOS ENVIE EL ID DE LA
        // NUEVA TARIFA AL SOCKET Y MODIFICAMOS NUESTRA VARIABLE LOCAL
        // {"TARIFAS":[{"IDTARIFA":"1","PVP":"1.00"},{"IDTARIFA":"5000001","PVP":"1.50"},{}]
        // DEBO GUARDAR EL IDTARIFA EN UNA VARIABLE LOCAL
        // IF IDTARIFA<>0 COGEMOS LA TARIFA QUE SEA, SINO, NO TOCAMOS NADA
        try {
            this.listaArticulos = null;
            this.listaArticulos = new ArrayList<>();

            for (int i = 0; i < familia.getJSONArray("ARTICULOS").length(); i++) {
                JSONObject a = familia.getJSONArray("ARTICULOS").getJSONObject(
                        i);
                if (this._idTarifa != 0) {
                    for (int x = 0; x < a.getJSONArray("TARIFAS").length(); x++) {
                        JSONObject tarifa = a.getJSONArray("TARIFAS")
                                .getJSONObject(x);
                        if (tarifa.has("IDTARIFA")) {
                            if (tarifa.getInt("IDTARIFA") == this._idTarifa) {
                                a.put("PVP", tarifa.getString("PVP"));
                                break;
                            }
                        }
                    }
                }

                this.listaArticulos.add(a);
            }
        } catch (Exception e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    /*
     * BÚSQUEDA DE ARTÍCULOS
     */

    private ArrayList<JSONObject> listaResultadosBusqueda;

    public ArrayList<JSONObject> getResultadosBusqueda() {
        return this.listaResultadosBusqueda;
    }

    public void buscarArticulos(String busqueda) {
        if (this.listaResultadosBusqueda == null)
            this.listaResultadosBusqueda = new ArrayList<>();
        this.listaResultadosBusqueda.clear();
        try {
            for (JSONObject familia : this.listaFamiliasVisibles) {
                for (int i = 0; i < familia.getJSONArray("ARTICULOS").length(); i++) {
                    JSONObject art = familia.getJSONArray("ARTICULOS")
                            .getJSONObject(i);

                    if (art.getString("DES")
                            .toUpperCase(Locale.getDefault())
                            .contains(busqueda.toUpperCase(Locale.getDefault()))
                            || (art.getString("COD").contains(busqueda))
                            && (!art.getString("T").equals("H"))) {
                        // NUEVO PARA TARIFAS
                        if (this._idTarifa != 0) {
                            for (int x = 0; x < art.getJSONArray("TARIFAS")
                                    .length(); x++) {
                                JSONObject tarifa = art.getJSONArray("TARIFAS")
                                        .getJSONObject(x);
                                if (tarifa.has("IDTARIFA")) {
                                    if (tarifa.getInt("IDTARIFA") == this._idTarifa) {
                                        art.put("PVP", tarifa.getString("PVP"));
                                        break;
                                    }
                                }
                            }
                        }
                        this.listaResultadosBusqueda.add(art);
                    }
                }

            }
        } catch (Exception ex) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + ex.getMessage());
        }
    }

    //region LINEAS

    //TODO Estos getters hay que dejar de usarlos y usar le referencia directa
    public ArrayList<JSONObject> getLineasVisiblesComanda() {
        return GestorLineas.Companion.getVISIBLES();
    }

    // PARA QUITAR LA CELDA AMARILLA
    public void quitarSeleccion() {
        for (JSONObject linea : GestorLineas.Companion.getVISIBLES()) {
            if (linea.has("SELECCIONADA"))
                linea.remove("SELECCIONADA");
        }
    }

    public boolean isComandaOK() {
        return (this._mesaActual != null && !this.mesaPagando && !this.mesaBloqueada);
    }

    public boolean hayLineasNuevas() {
        try {
            if (GestorLineas.Companion.getVISIBLES().size() > 0) {
                for (JSONObject jo : GestorLineas.Companion.getVISIBLES()) {
                    try {
                        if (jo.getInt("TIPO") == Constantes.tipo_linea_nueva
                                || jo.getInt("TIPO") == Constantes.linea_nueva_menu_maestro)
                            return true;
                    } catch (Exception e) {
                        this.error = Constantes.error_tratando_JSON;
                        Log.e("GLOBAL",
                                "Linea "
                                        + Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber() + ": "
                                        + e.getMessage());
                        return false;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean hayLineasRecibidas() {
        if (GestorLineas.Companion.getVISIBLES().size() > 0) {
            for (JSONObject jo : GestorLineas.Companion.getVISIBLES()) {
                try {
                    if (jo.getInt("TIPO") == Constantes.tipo_linea_recibida
                            || jo.getInt("TIPO") == Constantes.linea_recibida_menu_maestro)
                        return true;
                } catch (JSONException e) {
                    this.error = Constantes.error_tratando_JSON;
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                    return false;
                }
            }
        }
        return false;
    }

    public boolean hayLineasPendientes() {
        return GestorLineas.Companion.getLineasPendienteEnvio().size() > 0;
    }


    //endregion

    private String comensales;
    private String total;

    private boolean comandaEnviada = false;

    public boolean isComandaEnviada() {
        return this.comandaEnviada;
    }

    private boolean pedirComensales = false;

    public boolean isPedirComensales() {
        return this.pedirComensales;
    }

    public String getComensales() {
        return comensales;
    }

    public void setComensales(String _comensales) {
        this.comensales = _comensales;
    }

    public String getTotal() {
        return total;
    }

    private void setCamareroFactura(String _idcamarero) {

        for (JSONObject camarero : this.listaOperadores) {
            try {
                if (camarero.getString("ID").equals(_idcamarero)) {
                    String camareroFactura = camarero.getString("DES");
                }
            } catch (JSONException e) {
                this.error = Constantes.error_tratando_JSON;
                Log.e("GLOBAL",
                        "Linea "
                                + Thread.currentThread().getStackTrace()[2]
                                .getLineNumber() + ": "
                                + e.getMessage());
            }
        }
    }

    // MESA CON LAS CUENTAS SEPARADAS (pagando)
    private boolean mesaPagando = false;

    public boolean isMesaPagando() {
        return this.mesaPagando;
    }

    private boolean mesaBloqueada = false;

    public boolean isMesaBloqueada() {
        return this.mesaBloqueada;
    }

    public String getEstadoMesa() {
        return "";
    }

    public void bloquearMesa(String _mesa) {
        this.agregarFuncion("bloquearmesa", _mesa);
        Backup.guardarMesaBloqueada(_mesa);
    }

    public void desbloquearMesa(String _mesa) {
        this.agregarFuncion("desbloquearmesa", _mesa);
    }

    public void cargarComanda(String _mesa) {
        this.mesaPagando = false;
        this.mesa = _mesa;
        _mesaActual = null; //Objeto con el que controlaremos que se puede entrar en la mesa
        this.agregarFuncion("getdoc", this.mesa);

    }

    public void agregarTextoLibre(int position, int lista) {

        JSONObject articulo = null;
        try {
            switch (lista) {
                case Constantes.lista_funciones:
                    articulo = this.listaFunciones.get(position);
                    break;
                case Constantes.lista_normal:
                    articulo = this.listaArticulos.get(position);
                    break;
                case Constantes.lista_busqueda:
                    articulo = listaResultadosBusqueda.get(position);
            }

            assert articulo != null;
            String idArticulo = articulo.getString("ID");
            String desArticulo = articulo.getString("DES");
            String total = articulo.getString("PVP");
            JSONObject _nuevaLinea = new JSONObject();
            _nuevaLinea.put("NLINEA", Formateador.getNumLinea());
            _nuevaLinea.put("MESA", this.mesa);
            _nuevaLinea.put("IDARTICULO", idArticulo);
            _nuevaLinea.put("DESCRIPCION", desArticulo);
            _nuevaLinea.put("UNID", "1");
            _nuevaLinea.put("TIPO", Constantes.tipo_linea_nueva);
            _nuevaLinea.put("T", "H");
            _nuevaLinea.put("PRECIO", total);
            _nuevaLinea.put("TOTAL", total);
            _nuevaLinea.put("ID", "");
            _nuevaLinea.put("OBSERVACION", "");
            GestorLineas.Companion.getVISIBLES().add(_nuevaLinea);
        } catch (Exception e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }

    }

    public void agregarNuevaLinea(int position, double _unid, int lista) {

        JSONObject articulo = null;
        try {
            switch (lista) {
                case Constantes.lista_normal:
                    articulo = this.listaArticulos.get(position);
                    break;
                case Constantes.lista_busqueda:
                    articulo = this.listaResultadosBusqueda.get(position);
                    break;
            }

            Double nuevoPvpTotal;
            String idArticulo;
            String desArticulo;
            String tipo_articulo;
            assert articulo != null;
            idArticulo = articulo.getString("ID");
            desArticulo = articulo.getString("DES");
            String precioUnitario = articulo.getString("PVP");
            nuevoPvpTotal = articulo.getDouble("PVP") * _unid;
            tipo_articulo = articulo.getString("T");
            this.total = total + nuevoPvpTotal;

            JSONObject _nuevaLinea = new JSONObject();
            _nuevaLinea.put("NLINEA", Formateador.getNumLinea());
            _nuevaLinea.put("MESA", this.mesa);
            _nuevaLinea.put("IDARTICULO", idArticulo);
            _nuevaLinea.put("DESCRIPCION", desArticulo);
            _nuevaLinea.put("UNID", _unid);
            _nuevaLinea.put("TIPO", Constantes.tipo_linea_nueva);
            _nuevaLinea.put("PRECIO", precioUnitario);
            _nuevaLinea.put("TOTAL", nuevoPvpTotal);
            _nuevaLinea.put("ID", "");
            _nuevaLinea.put("OBSERVACION", "");
            _nuevaLinea.put("T", tipo_articulo);
            GestorLineas.Companion.getVISIBLES().add(_nuevaLinea);

        } catch (Exception e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public boolean NmoverLinea(int position_, int posiciones_) {
        boolean ret = false;
        try {
            if (isLineaMovible(position_)) {
                JSONObject miLinea = GestorLineas.Companion.getVISIBLES().get(position_);
                try {
                    miLinea.put("SELECCIONADA", "");
                } catch (JSONException e) {
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                }
                int npos = this.getIntCampo(miLinea, "NLINEA");
                while ((npos % 100) != 0) {
                    position_--;
                    miLinea = GestorLineas.Companion.getVISIBLES().get(position_);
                    npos = this.getIntCampo(miLinea, "NLINEA");

                }

                int newnpos = npos + 100 * posiciones_;
                int newposition = this.getNLinea(newnpos);
                if (newposition >= 0)
                    if (isLineaMovible(newposition)) {
                        intercambiar(position_, newposition);
                        ret = true;
                    }
            }
            return ret;
        } catch (Exception e) {
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            return false;
        }

    }

    private int getIntCampo(JSONObject l, String cmp) {
        try {
            return l.getInt(cmp);
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            return -1;
        }
    }

    private void intercambiar(int a, int b) {
        JSONObject lin;
        int naInit = this
                .getIntCampo(GestorLineas.Companion.getVISIBLES().get(a), "NLINEA");
        int nbInit = this
                .getIntCampo(GestorLineas.Companion.getVISIBLES().get(b), "NLINEA");

        int na = naInit;
        int nb = nbInit;
        while (na < (naInit + 99) && na >= 0) {
            lin = GestorLineas.Companion.getVISIBLES().get(a);
            this.modificarValor(lin, "XNLINEA", nb);
            a++;
            if (a > GestorLineas.Companion.getVISIBLES().size() - 1)
                na = -1;
            else
                na = this
                        .getIntCampo(GestorLineas.Companion.getVISIBLES().get(a), "NLINEA");
            nb++;
        }

        na = naInit;
        nb = nbInit;
        while (nb < (nbInit + 99) && nb >= 0) {
            lin = GestorLineas.Companion.getVISIBLES().get(b);
            this.modificarValor(lin, "XNLINEA", na);
            b++;
            if (b > GestorLineas.Companion.getVISIBLES().size() - 1)
                nb = -1;
            else
                nb = this
                        .getIntCampo(GestorLineas.Companion.getVISIBLES().get(b), "NLINEA");
            na++;
        }

        for (JSONObject l : GestorLineas.Companion.getVISIBLES()) {
            try {
                if (l.has("XNLINEA"))
                    this.modificarValor(l, "NLINEA", l.getInt("XNLINEA"));

            } catch (Exception e) {
                Log.e("GLOBAL",
                        "Linea "
                                + Thread.currentThread().getStackTrace()[2]
                                .getLineNumber() + ": "
                                + e.getMessage());
            }
        }

        Collections.sort(GestorLineas.Companion.getVISIBLES(), new Formateador.ComparadorJSON());
    }

    private boolean isLineaMovible(int position_) {
        JSONObject miLinea = GestorLineas.Companion.getVISIBLES().get(position_);
        return ((getIntCampo(miLinea, "TIPO") == Constantes.tipo_linea_nueva) || (getIntCampo(
                miLinea, "TIPO") == Constantes.linea_nueva_menu_maestro));
    }

    private int getNLinea(int pos_) {
        for (JSONObject l : GestorLineas.Companion.getVISIBLES()) {
            try {
                if (l.getInt("NLINEA") == pos_)
                    return GestorLineas.Companion.getVISIBLES().indexOf(l);
            } catch (JSONException e) {
                Log.e("GLOBAL",
                        "Linea "
                                + Thread.currentThread().getStackTrace()[2]
                                .getLineNumber() + ": "
                                + e.getMessage());
            }
        }
        return -1;
    }

    //region MODIFICACIÓN DE LINEAS

    private void modificarValor(JSONObject miLinea, String cmp, int valor) {
        try {
            miLinea.put(cmp, valor);
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    public void modLinea(int position, double unidNuevas, String observacion,
                         String _pvp) {

        JSONObject linea = GestorLineas.Companion.getVISIBLES().get(position);
        boolean precioModificado = false;

        try {
            //TODO TEEMOS QUE DECIDIR AQUÍ SI PONER EL NUEVO PRECIO EN PANTALLA
            precioModificado = !_pvp.equals("")
                    && !_pvp.equals(linea.getString("PRECIO"));

            if (precioModificado && !this._permitirPrecioLibreInferior) {
                Double p1 = Double.parseDouble(_pvp.replace(",", "."));
                Double p2 = Double.parseDouble(linea.getString("PRECIO")
                        .replace(",", "."));
                precioModificado = p1 > p2;
            }
        } catch (JSONException e1) {
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e1.getMessage());
        }
        _pvp = _pvp.replace(",", ".");
        try {
            linea.put("OBSERVACION", observacion);
            if ((linea.getInt("UNID") != unidNuevas) || (precioModificado)) {

                int tipo = linea.getInt("TIPO");
                switch (tipo) {
                    case Constantes.tipo_linea_recibida:
                        JSONObject nuevaLinea = new JSONObject();
                        unidNuevas = unidNuevas - linea.getDouble("UNID");
                        nuevaLinea.put("UNID", unidNuevas);
                        nuevaLinea.put("MESA", this.mesa);
                        nuevaLinea.put("IDARTICULO", linea.getString("IDARTICULO"));
                        nuevaLinea.put("DESCRIPCION",
                                linea.getString("DESCRIPCION"));
                        nuevaLinea.put("TIPO", Constantes.tipo_linea_nueva);

                        nuevaLinea.put("ID", "");
                        nuevaLinea.put("T", linea.getString("T"));
                        nuevaLinea.put("OBSERVACION", observacion);
                        String precio;
                        //CAMBIO EXCELL
                        precio = _pvp;
                        nuevaLinea.put("PRECIO", _pvp);
                        nuevaLinea.put("PVPMODIFICADO", 1);
                        /*
                        if (precioModificado) {
                            precio = _pvp;
                            nuevaLinea.put("PRECIO", _pvp);
                            nuevaLinea.put("PVPMODIFICADO", 1);
                        } else {
                            precio = linea.getString("PRECIO");
                            nuevaLinea.put("PRECIO", linea.getString("PRECIO"));
                        }
                        */
                        double nuevoTotal = unidNuevas * Double.parseDouble(precio);
                        nuevaLinea.put("TOTAL", nuevoTotal);
                        GestorLineas.Companion.getVISIBLES().add(nuevaLinea);
                        break;
                    case Constantes.tipo_linea_nueva:
                        /*
                        PROBLEMA AQUÍ. TENEMOS UNA LINEA NUEVA DE LA CUAL, A MENOS QUE MODIFIQUEMOS EL PRECIO, NO TENEMOS QUE ENVIARLO
                         */
                        linea.put("UNID", unidNuevas);
                        linea.put("TOTAL", Double.parseDouble(_pvp) * unidNuevas);
                        if (precioModificado) {
                            //CAMBIO BAMBI - EXCELL
                            linea.put("PRECIO", _pvp);
                            linea.put("PVPMODIFICADO", 1);
                        }
                        /*else { //ESTE SEGUNDO PARÁMETRO TAL VEZ COMENTARLO
                            // TAL VEZ PODEMOS ARREGLARLO QUE CUANDO SE AÑADAN MÁS FILAS DESDE EL BOTÓN + PONGAMOS EL CAMPO PVPMODIFICADO A TRUE
                            linea.put("TOTAL", Double.parseDouble(_pvp)
                                    * unidNuevas);
                            linea.put("PRECIO", _pvp);
                            linea.put("PVPMODIFICADO", 1);
                        }
                        */

                        break;
                    case Constantes.linea_nueva_menu_maestro:
                        if (precioModificado) {
                            linea.put("TOTAL", Double.parseDouble(_pvp)
                                    * unidNuevas);
                            linea.put("PRECIO", _pvp);
                            linea.put("PVPMODIFICADO", 1);
                        } else {
                            linea.put("TOTAL", linea.getDouble("PRECIO")
                                    * unidNuevas);
                        }
                        break;
                }
            }
        } catch (JSONException e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }

    }

    public boolean isLineaAnulable(int position) {
        try {
            int id = GestorLineas.Companion.getVISIBLES().get(position)
                    .getInt("IDARTICULO");
            double total = 0.0;
            for (JSONObject linea : GestorLineas.Companion.getVISIBLES()) {
                if (linea.getInt("IDARTICULO") == id) {
                    total += linea.getDouble("UNID");
                }
            }
            if (total > 0)
                return true;
        } catch (Exception e) {
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());

        }
        return false;

    }

    public void eliminarLinea(int position) {

        try {
            JSONObject linea = GestorLineas.Companion.getVISIBLES().get(position);
            int tipo = linea.getInt("TIPO");
            switch (tipo) {
                case Constantes.tipo_linea_recibida:
                    //if (!linea.getString("T").equals("H")) {
                    JSONObject nuevaLinea = new JSONObject();
                    nuevaLinea.put("UNID", linea.getDouble("UNID") * -1);
                    nuevaLinea.put("MESA", this.mesa);
                    nuevaLinea.put("IDARTICULO", linea.getString("IDARTICULO"));
                    nuevaLinea.put("DESCRIPCION",
                            linea.getString("DESCRIPCION"));
                    nuevaLinea.put("TIPO", Constantes.tipo_linea_nueva);
                    nuevaLinea.put("OBSERVACION", "");
                    nuevaLinea.put("PRECIO", linea.getString("PRECIO"));
                    nuevaLinea.put("ID", "");
                    nuevaLinea.put("TOTAL", linea.getDouble("PRECIO") * nuevaLinea.getDouble("UNID")); //TODO unidades en negativo X pvp
                    nuevaLinea.put("T", linea.getString("T"));
                    nuevaLinea.put("NLINEA", Formateador.getNumLinea());
                    nuevaLinea.put("PVPMODIFICADO", 1); //TODO CONTROLAR ESTO, PARA MODIFICACIONES DE LINEAS
                    GestorLineas.Companion.getVISIBLES().add(nuevaLinea);
                    //}

                    break;
                case Constantes.tipo_linea_nueva:
                    GestorLineas.Companion.getVISIBLES().remove(position);
                    break;
            }
            //NUEVO CAMBIO PARA RENUMERAR LINEAS
            renumerarLineas();
        } catch (JSONException e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    //endregion

    public void enviarComanda() {
        //TENGO QUE RESPETAR LOS DE LA TARIFA. LO MISMO QUE PARA MOSTRARLOS
        //TODO PARA GUARDAR UNA COPIA DE SEGURIDAD, ES AQUÍ DÓNDE TENGO QUE HACER USO DEL DAO, LINEA POR LINEA
        this.comandaEnviada = false;
        if (GestorLineas.Companion.getVISIBLES().size() > 0) {
            for (JSONObject linea : GestorLineas.Companion.getVISIBLES()) {
                try {
                    if (linea.getInt("TIPO") == Constantes.tipo_linea_nueva
                            || linea.getInt("TIPO") == Constantes.linea_nueva_menu_maestro
                            || linea.getInt("TIPO") == Constantes.linea_nueva_menu_detalle) {
                        String valores = linea.getString("MESA") + ";"
                                + linea.getString("IDARTICULO") + ";"
                                + linea.getString("UNID") + ";"
                                + linea.getString("OBSERVACION");
                        if (linea.has("NUMLINEA") && linea.has("NUMLINEAREF")
                                && linea.has("TARIFA")) {
                            valores += ";" + linea.getString("NUMLINEA") + ";"
                                    + linea.getString("NUMLINEAREF");
                        } else {
                            valores += ";;";
                        }
                        String tarifa = "";
                        if (linea.has("TARIFA")) {
                            tarifa = linea.getString("TARIFA");
                        }
                        // nuevaLinea.put("PVPMODIFICADO",1);
                        //TODO CONTROLAR BUG AQUÍ PARA CUANDO SE ELIMINA UNA LINEA, LA CUAL TIENE UN PRECIO YA MODIFICADO PREVIAMENTE
                        // QUE HACER?
                        //TODO PODEMOS CREAR UN BOOLEANO DE LINEA ELIMINADA O MODIFICADA, PARA ENVIAR EL PRECIO SIEMPRE, CUANDO VIENE ELIMINADA
                        if (linea.has("PVPMODIFICADO")) {
                            if (linea.has("PRECIO")
                                    && !linea.getString("PRECIO").equals("")) {
                                if (linea.getDouble("PRECIO") > 0) {
                                    valores += ";" + tarifa + ";"
                                            + linea.getString("PRECIO");
                                } else {
                                    valores += ";0;"
                                            + linea.getString("PRECIO");
                                }
                            } else {
                                valores += ";" + tarifa;
                            }
                        } else {

                            //ESTA ES LA LINEA ORIGINAL. VOLVER A PONERLA SI NO FUNCIONA
                            valores += ";" + tarifa + ";" + "";

                            //HAY QUE TENER EN CUENTA EL CLIENTE. HAY QUE ENVIARLO ANTES DE LAS LINEAS. HACER UN SETCLIENTE ANTES!!!
                            //ESTA BETA TIENE EL PROBLEMA DE QUE PUEDE HABER EN UNA MISMA MESA, PLATOS CON DIFERENTE TARIFA. ES UN PARCHE HASTA QUE LUIS REVISE EL TPV
                            //Yo no debería de enviar precios
                            //valores += ";" + tarifa + ";"
                            // + linea.getString("PRECIO");
                        }
                        // Campo nuevo al final con el identificador del
                        // movimiento
                        String idd = Inicio.getIdd();
                        linea.put("IDD", idd);
                        valores += ";" + idd;
                        if (linea.has("IDTIPO_ENLACE")) {
                            valores += ";" + linea.getString("IDTIPO_ENLACE");
                        }
                        this.agregarFuncion("setcomanda", valores);
                    }
                } catch (JSONException e) {
                    this.error = Constantes.error_tratando_JSON;
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                }

            }
        }

    }

    //region ENVÍO DE LINEAS PENDIENTES
    private boolean pendienteEnviado = false;

    public void enviarPendiente() {


        if (this.hayLineasPendientes() || this.hayLineasParaEliminar()) {

            this.enviarLineasEliminadas();

            this.comandaEnviada = false;
            for (JSONObject linea : GestorLineas.Companion.getLineasPendienteEnvio()) {
                try {
                    if (linea.getInt("TIPO") == Constantes.tipo_linea_nueva
                            || linea.getInt("TIPO") == Constantes.linea_nueva_menu_maestro
                            || linea.getInt("TIPO") == Constantes.linea_nueva_menu_detalle) {
                        String valores = linea.getString("MESA") + ";"
                                + linea.getString("IDARTICULO") + ";"
                                + linea.getString("UNID") + ";"
                                + linea.getString("OBSERVACION");
                        if (linea.has("NUMLINEA") && linea.has("NUMLINEAREF")
                                && linea.has("TARIFA")) {
                            valores += ";" + linea.getString("NUMLINEA") + ";"
                                    + linea.getString("NUMLINEAREF");
                        } else {
                            valores += ";;";
                        }
                        String tarifa = "";
                        if (linea.has("TARIFA")) {
                            tarifa = linea.getString("TARIFA");
                        }

                        // nuevaLinea.put("PVPMODIFICADO",1);
                        if (linea.has("PVPMODIFICADO")) {
                            if (linea.has("PRECIO")
                                    && !linea.getString("PRECIO").equals("")) {
                                if (linea.getDouble("PRECIO") > 0) {
                                    valores += ";" + tarifa + ";"
                                            + linea.getString("PRECIO");
                                } else {
                                    valores += ";0;"
                                            + linea.getString("PRECIO");
                                }
                            } else {
                                valores += ";" + tarifa; // nunca entra aqu�
                            }
                        } else {
                            valores += ";" + tarifa + ";" + "";
                        }


                        if (linea.has("IDD")) {
                            valores += ";" + linea.getString("IDD");
                        }
                        // ESTO FALTABA AQUÍ, PUEDE SER EL ORIGEN DE LOS
                        // PROBLEMAS
                        if (linea.has("IDTIPO_ENLACE")) {
                            valores += ";" + linea.getString("IDTIPO_ENLACE");
                        }

                        this.agregarFuncion("setcomanda", valores);
                    }
                } catch (JSONException e) {
                    this.error = Constantes.error_tratando_JSON;
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                }

            }
            //POR AHORA ESTO COMENTADO, PARA NO BORRAR LAS LINEAS PENDIENTES HASTA QUE SE HAYAN ENVÍADO
            //LineasComanda.PENDIENTES_ENVIO.clear();

            pendienteEnviado = true;
        }

    }
    //endregion

    //region PRECIOS

    private boolean cambioPVP = false;

    public boolean cambiarPVP() {
        return this.cambioPVP;
    }

    //endregion

    //region IMPRESORA DE COCINA

    private boolean impresoraCocinaConfiguradaEnTPV = true;

    public boolean isImpresoraConfiguradaEnTPV() {
        return !this.impresoraCocinaConfiguradaEnTPV;
    }

    //endregion
    private String idTerminal = "";

    public String getIdTerminal() {
        switch (PreferenciasManager.getModoUsoApp()) {
            case Constantes.MODO_COMANDERO:
                return "COMANDERO " + this.idTerminal;
            case Constantes.MODO_COCINA:
                return "TABLET COCINA " + this.idTerminal;
            case Constantes.MODO_CLOUD:
                return "COMANDERO CLOUD " + this.idTerminal;
            default:
                return "COMANDERO" + this.idTerminal;
        }
    }

    // Variable para tarifa;

    private int _idTarifa = 0;

    private boolean _permitirPrecioLibreInferior = true;

    public boolean permitirPrecioLibreInferior() {
        return this._permitirPrecioLibreInferior;
    }

    public void guardarConfigNEW(JSONObject res) {
        try {
            Iterator<String> iterMain = res.keys();
            while (iterMain.hasNext()) {
                switch (iterMain.next()) {
                    case "TEXTOSLIBRESCONTROL":
                        TpvConfig.setFamiliaTextosLibres(res.getString("TEXTOSLIBRESCONTROL"));
                        break;
                    case "FORECOLOR":
                        TpvConfig.setAppForecolor(DesignM.genColor(res.getInt(("FORECOLOR"))));
                        break;
                    case "BACKCOLOR":
                        TpvConfig.setAppBackColor(DesignM.genColor(res.getInt(("BACKCOLOR"))));
                        break;
                    case "COMENSALES":
                        pedirComensales = res.getInt("COMENSALES") == 1;
                        break;
                    case "PRECIOLIBRE":
                        cambioPVP = res.getInt("PRECIOLIBRE") == 1;
                        break;
                    case "MYORDER":
                        _isMyorder = res.getInt("MYORDER") == 1;
                        break;
                    case "IMPRESORA_COCINA":
                        if (!res.getString("").equals("")) {
                            impresoraCocinaConfiguradaEnTPV = res.getInt("IMPRESORA_COCINA") > 0;
                        } else
                            impresoraCocinaConfiguradaEnTPV = false;
                        break;
                    case "ID":
                        idTerminal = res.getString("ID");
                        break;
                    case "IDTARIFA":
                        _idTarifa = res.getInt("IDTARIFA");
                        break;
                    case "PRECIOLIBREINFERIOR":
                        _permitirPrecioLibreInferior = res.getInt("PRECIOLIBREINFERIOR") == 1;
                        break;
                    case "IDCLIENTE":
                        //String _idClientePorDefecto = res.getString("IDCLIENTE"); //POR AHORA NO SE USA
                        break;
                    case "VERIFICARPREFACTURA":
                        verificarPrefactura = res.getBoolean("VERIFICARPREFACTURA");
                        break;
                    case "FUNCIONES":
                        for (int i = 0; i < res.getJSONArray("FUNCIONES").length(); i++) {
                            Iterator<String> iter = res.getJSONArray("FUNCIONES").getJSONObject(i).keys();
                            while (iter.hasNext()) {
                                if ("getTicket".equals(iter.next())) {
                                    PreferenciasManager.setPrefacturaBluetooth(true);
                                }
                            }
                        }
                        break;
                }
            }

        } catch (Exception e) {
            Log.e("TPVCONFIG", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }


    private void guardarOperadores(JSONArray filas) {


        if (filas.length() > 0) {
            if (this.listaOperadores == null) {
                this.listaOperadores = new ArrayList<>();
            } else {
                this.listaOperadores.clear();
            }

            for (int x = 0; x < filas.length(); x++) {
                try {
                    this.listaOperadores.add((JSONObject) filas.get(x));
                } catch (JSONException e) {
                    this.error = Constantes.error_tratando_JSON;
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                } catch (Exception e1) {
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e1.getMessage());
                }
            }
        }
    }

    //NUEVA FUNCIÓN CON SWITCH Y UN ITERADOR
    private void leerRespuestaNew(JSONArray rows) {

        JSONArray filas;

        this.error = Constantes.app_OK;
        this.comandaEnviada = true;

        try {
            for (int i = 0; i < rows.length(); i++) {
                JSONObject jo = rows.getJSONObject(i);
                Iterator<String> iter = jo.keys();
                while (iter.hasNext()) {
                    switch (iter.next()) {
                        case "setserie":
                            switch (jo.getJSONObject("setserie").getString("resultado")
                                    .toUpperCase(Locale.getDefault())) {
                                case "REGISTRO":
                                    this.estado_App = Constantes.app_serie_no_registrado;
                                    break;
                                case "OK":
                                    this.estado_App = Constantes.app_serie_registrado;
                                    break;
                                case "CADUCADO":
                                    this.estado_App = Constantes.app_demo_caducada;
                                    break;
                            }
                            break;
                        case "getCfg":

                            this.guardarConfigNEW(jo.getJSONObject("getCfg")
                                    .getJSONObject("resultado"));
                            break;
                        case "getoperadores":
                            this.guardarOperadores(jo.getJSONObject("getoperadores")
                                    .getJSONArray("rows"));
                            break;
                        case "setoperador":
                            if (jo.getJSONObject("setoperador").getString("resultado")
                                    .equals("Ok")) {
                                op.setValidado(true);
                                //TODO Nuevo booleano para saber si puedo cobrar. No sé de donde lo sacaré
                                op.setPuedeCobrar(true); //TODO VER COMO GESTIONAR ESTE CAMPO. POR AHORA A TRUE PARA PROBAR CANARYPAY. Ponerlo a false en la beta
                            } else {
                                op.setValidado(false);
                            }
                            break;
                        case "getcfgmesas":
                            leerGetcfgmesas(jo.getJSONArray("getcfgmesas"));
                            break;
                        case "getmesas":
                            leerGetMesas(jo.getJSONObject("getmesas").getJSONArray("rows"));
                            break;
                        case "getcarta":
                            leerGetCarta(jo.getJSONObject("getcarta").getJSONObject("resultado").getJSONArray("FAMILIAS"));
                            break;
                        case "setcomanda":
                            leerSetComanda(jo);
                            break;
                        case "setmesa":
                            leerSetMesa(jo);
                            break;
                        case "bloquearmesa":
                            this.mesaBloqueada = !jo.getJSONObject("bloquearmesa")
                                    .getString("resultado").equals("Ok");
                            break;
                        case "desbloquearmesa":
                            //Esto por ahora no lo hacemos porque no estamos guardando las mesas que no hemos podido bloquear
                            //Backup.eliminarMesasBloqueadas();
                            break;
                        case "pedircuenta":
                            this.resultadoF = jo.getJSONObject("pedircuenta").getString("resultado");
                            break;
                        case "pedircuentadirecto":
                            this.resultadoF = jo.getJSONObject("pedircuentadirecto").getString("resultado");
                            break;
                        case "cobrarcuenta":
                            this.resultadoF = jo.getJSONObject("cobrarcuenta").getString("resultado");
                            break;
                        case "setcomensales":
                            this.resultadoF = jo.getJSONObject("setcomensales").getString("resultado");
                            break;
                        case "getdoc":
                            cargarComandaActual(jo);
                            break;
                        case "getmyorder":
                            guardarPedidosMyorder(jo.toString());
                            break;
                        case "setconfirmarmyorder":
                            this.resultadoF = jo.getJSONObject("setconfirmarmyorder").getString("resultado");
                            break;
                        case "setcancelarmyorder":
                            this.resultadoF = jo.getJSONObject("setcancelarmyorder").getString("resultado");
                            break;
                        case "getgruposeleccion":
                            filas = jo.getJSONObject("getgruposeleccion")
                                    .getJSONObject("resultado")
                                    .getJSONArray("GRUPOSELECCION");
                            for (int x = 0; x < filas.length(); x++) {
                                this.vinculo.getListaGrupos().add(
                                        (JSONObject) filas.get(x));
                            }
                            break;
                        case "getvinculoseleccion":
                            filas = jo.getJSONObject("getvinculoseleccion")
                                    .getJSONArray("rows");
                            for (int x = 0; x < filas.length(); x++) {
                                this.vinculo.getListaVinculos().add(
                                        (JSONObject) filas.get(x));
                            }
                            break;
                        case "getCocina":
                            this.cocina.cargarComandas(jo.toString());
                            break;
                        case "setCocina":
                            this.resultadoF = jo.getJSONObject("setCocina").getString(
                                    "resultado");
                            this.cocina.getListaLineasSinNotificar().clear();
                            break;
                        case "getImpresoras":
                            try {
                                TpvConfig.guardarImpresoras(jo);
                            } catch (Exception e) {
                                Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                            }
                            break;
                        case "getClientes":
                            try {
                                Clientes.guardar(jo);
                            } catch (Exception e) {
                                Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
                            }
                            break;
                        case "getPermisos":
                            try {
                                guardarPermisos(jo.getJSONObject("getPermisos"));
                            } catch (Exception e) {
                                this._anularComanda = false;
                                this._anularPrefactura = false;
                            }
                            break;
                        case "eliminarlinea":
                            GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS().clear();
                            break;
                        case "getTratamientosCobro":
                            guardarFormasDePago(jo.getJSONObject("getTratamientosCobro").getJSONArray("rows"));
                            break;
                        case "setCobro":
                            guardarDevolucion(jo.getJSONObject("setCobro"));
                            break;
                        case "setFinalizar":
                            if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_COMANDERO)){
                                _cerrarMesa = jo.getJSONObject("setFinalizar").getJSONObject("resultado").has("ID");
                            }else{
                                _cerrarMesa = jo.getJSONObject("setFinalizar").has("CABECERA");
                            }
                            break;
                        case "formaPagosTPV": //NO SÉ COMO SE LLAMARÁ LA FUNCIÓN
                            //TODO AQUÍ LLAMAREMOS AL REPOSITORIO DE LINEAS. HAY QUE PARSEAR DESDE AQUÍ SEGURAMENTE
                            //HAY QUE TRAZAR Y VER LA ESTRUCTURA PARA QUE EL GSON FUNCIONE CORRECTAMENTE
                            RepositoryCobrosTPV.Companion.guardarFormasPagoTPV(
                                    jo.getJSONObject("formaPagosTPV").getJSONArray("rows").toString());
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + ex.getMessage());
        }
        if (!this.comandaEnviada)
            //this.guardarBackupComandaBD(); //TODO PRUEBA GUARDAR EN BD
            GestorLineas.Companion.guardarBackupComandaBD(); //PREPARATE!
    }

    public void cobrarCuenta(String dispositivo){
        this.agregarFuncion("cobrarcuenta", this.mesa + ";" + dispositivo + ";" + Inicio.getIdd());
    }


    public void pedirCuenta(String dispositivo) {
        // this.agregarFuncion("pedircuenta", this.mesa);
        this.agregarFuncion("pedircuenta", this.mesa + ";" + dispositivo + ";"
                + Inicio.getIdd());
    }

    public void pedirCuenta(String mesa, String dispositivo) {
        // this.agregarFuncion("pedircuenta", this.mesa);
        this.agregarFuncion("pedircuenta", mesa + ";" + dispositivo + ";" + Inicio.getIdd());
    }

    public void pedirCuentaVerificado(String dispositivo, boolean verificar) {

        this.agregarFuncion("pedircuentadirecto", this.mesa + ";" + dispositivo + ";"
                + Inicio.getIdd());
    }


    /*
     * Envio de comensales
     */
    public void enviarComensales() {
        this.agregarFuncion("setcomensales", this.mesa + ";" + this.comensales);
    }

    /*
     * FUNCIONES PARA RETENER LINEAS Y ENVIARLAS POSTERIORMENTE
     */

    public boolean tieneLineasRetenidas(String mesa) {
        return GestorLineas.Companion.getLineasRetenidas(mesa).size() > 0;
    }

    private String mesaNueva = "";

    public void cambiarMesa(String mesaNueva) {
        this.mesaNueva = mesaNueva;
        this.agregarFuncion("setmesa", this.mesa + ";" + mesaNueva);
    }

    /*
     * PEDIDOS MYORDER
     */

    private ArrayList<JSONObject> listaPedidosMyorder;

    public ArrayList<JSONObject> getListaPedidosMyorder() {
        return this.listaPedidosMyorder;
    }

    public boolean hayPedidosSinConfirmar() {
        return (this.listaPedidosMyorder != null && this.listaPedidosMyorder
                .size() > 0);
    }

    public void consultarPedidosMyorder() {
        this.agregarFuncion("getmyorder", "");
    }

    // EL JSONARRAY CON LOS PEDIDOS LO PASAMOS A ARRAYLIST<JSON>
    public void guardarPedidosMyorder(String jsonP) {

        if (this.listaPedidosMyorder != null)
            this.listaPedidosMyorder.clear();
        else
            this.listaPedidosMyorder = new ArrayList<>();
        try {
            JSONObject jsonPedidos = new JSONObject(jsonP);
            JSONArray pedidos = jsonPedidos.getJSONArray("getmyorder");

            for (int i = 0; i < pedidos.length(); i++) {
                this.listaPedidosMyorder.add(pedidos.getJSONObject(i));
            }

        } catch (JSONException e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }

    }

    private ArrayList<JSONObject> listaLineasPedidoMyorder;

    public ArrayList<JSONObject> getListaLineasPedidoMyorder() {
        return this.listaLineasPedidoMyorder;
    }

    private String numPedido = "";
    private String mesaPedido = "";
    private String totalPedido = "";
    private String idPedido = "";

    public String getNumPedido() {
        return numPedido;
    }

    public String getMesaPedido() {
        return mesaPedido;
    }

    public String getTotalPedido() {
        return totalPedido;
    }

    public boolean cargarPedidoMyorder(int position) {
        boolean res = false;
        if (this.listaLineasPedidoMyorder != null)
            this.listaLineasPedidoMyorder.clear();
        else
            this.listaLineasPedidoMyorder = new ArrayList<>();
        try {
            JSONObject pedido = this.listaPedidosMyorder.get(position);
            JSONArray lineas = pedido.getJSONObject("CABECERA").getJSONArray(
                    "LINEAS");
            this.numPedido = pedido.getJSONObject("CABECERA").getString(
                    "NUMERO");
            this.mesaPedido = pedido.getJSONObject("CABECERA")
                    .getString("MESA");
            this.totalPedido = pedido.getJSONObject("CABECERA").getString(
                    "TOTAL");
            this.idPedido = pedido.getJSONObject("CABECERA").getString("ID");
            for (int i = 0; i < lineas.length(); i++) {
                this.listaLineasPedidoMyorder.add(lineas.getJSONObject(i));
                res = true;
            }
        } catch (JSONException e) {
            this.error = Constantes.error_tratando_JSON;
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
            res = false;
        }
        return res;
    }

    public void confirmarPedido() {
        this.agregarFuncion("setconfirmarmyorder", this.idPedido);
    }

    public void cancelarPedido() {
        this.agregarFuncion("setcancelarmyorder", this.idPedido);
    }

    /*
     * ENLACES
     */

    private Vinculo vinculo;

    public Vinculo getVinculos() {
        return this.vinculo;
    }

    public void cargarGruposSeleccion() {
        this.agregarFuncion("getgruposeleccion", "");
        vinculo = new Vinculo();
    }

    public void cargarVinculoSeleccion() {
        this.agregarFuncion("getvinculoseleccion", "");
    }

    /*
     * booleano para controlar que hay pendiente de enviar una anulación con la
     * nueva función
     */

    public boolean hayLineasParaEliminar() {
        return GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS() != null
                && GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS().size() > 0;
    }

    public void enviarLineasEliminadas() {
        if (GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS() != null
                && GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS().size() > 0) {
            for (String id : GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS()) {
                this.agregarFuncion("eliminarlinea", id);
            }
        }
    }

    public void eliminarListaLineasEliminadas() {
        GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS().clear();
    }

    public void eliminarMenu(int position, boolean recibido) {
        try {
            ArrayList<JSONObject> listaLineasEliminar = new ArrayList<>();
            listaLineasEliminar.add(GestorLineas.Companion.getVISIBLES().get(position));

            for (int i = position + 1; i < GestorLineas.Companion.getVISIBLES().size(); i++) {
                try {
                    if (GestorLineas.Companion.getVISIBLES().get(i).getInt("TIPO") == Constantes.linea_nueva_menu_detalle
                            || GestorLineas.Companion.getVISIBLES().get(i).getInt("TIPO") == Constantes.linea_recibida_menu_detalle)
                        listaLineasEliminar.add(GestorLineas.Companion.getVISIBLES().get(i));
                    else
                        break;
                } catch (JSONException e) {
                    this.error = Constantes.error_tratando_JSON;
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber() + ": "
                                    + e.getMessage());
                }
            }

            for (JSONObject linea : listaLineasEliminar) {
                if (recibido) {
                    try {
                        /*
                         * AQUÍ LLAMAREMOS DIRECTAMENTE A AGREGAR FUNCIÓN LEEMOS LA
                         * LINEA Constantes.linea_recibida_menu_maestro Y LLAMAMOS A
                         * LA FUNCIÓN ANULAR LINEA PASÁNDOLE POR PARÁMETRO EL ID DE
                         * LA MISMA SÓLO DIBUJAREMOS LA LINEA DE CABECERA EN
                         * NEGATIVO LA DEJAMOS COMO LINEA RECIBIDA PARA NO VOLVERLA
                         * A ENVIAR EN EL SETCOMANDA
                         */

                        if (linea.getInt("TIPO") == Constantes.linea_recibida_menu_maestro) {
                            JSONObject nuevaLinea = new JSONObject(linea.toString());
                            nuevaLinea.put("UNID", linea.getDouble("UNID") * -1);
                            GestorLineas.Companion.getVISIBLES().add(nuevaLinea);
                            GestorLineas.Companion.getIDS_LINEAS_ELIMINADAS().add(linea.getString("ID"));
                        }

                    } catch (JSONException e) {
                        this.error = Constantes.error_tratando_JSON;
                        Log.e("GLOBAL",
                                "Linea "
                                        + Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber() + ": "
                                        + e.getMessage());
                    }
                } else {
                    GestorLineas.Companion.getVISIBLES().remove(linea);
                }

            }
        } catch (Exception e) {
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }
    }

    /*
     * COCINA
     */

    private final Cocina cocina;

    public Cocina getCocina() {
        // return this.cocina; //2.0
        return this.cocina;
    }


    public void cargarMesasCocina() {
        this.agregarFuncion("getCocina", "");
    }


    private String mensajeVozNotific = "";

    private String mesasPreparadas = "";

    public String getMensajeVozNotificar() {
        return this.mensajeVozNotific;
    }

    public String getMesasPreparadas() {
        return this.mesasPreparadas;
    }

    public void guardarMensajeNotificar(JSONObject m) {
        try {
            String[] mesas = m.getString("getmarchando").split(",");
            this.mensajeVozNotific = "Nuevos platos preparados para ";
            this.mesasPreparadas = "Mesa :";
            if (mesas.length > 1) {
                this.mesasPreparadas = this.mesasPreparadas.replace("Mesa",
                        "Mesas");

            }

            String numMesa = "";
            StringBuilder stVoz = new StringBuilder();
            StringBuilder stNotif = new StringBuilder();
            for (String mesa : mesas) {
                String nombreRango = this.getRangoMesa(Integer.parseInt(mesa))
                        .getString("TX");
                numMesa = mesa;
                stVoz.append(",");
                stVoz.append(nombreRango);
                stVoz.append(" ");
                stVoz.append(numMesa);
                stNotif.append(", ");
                stNotif.append(numMesa);

            }
            this.mensajeVozNotific = stVoz.toString();
            this.mesasPreparadas += stNotif.toString();
            if (mesas.length > 1) {
                this.mensajeVozNotific = this.mensajeVozNotific.replace(","
                        + numMesa, " y " + numMesa);
                this.mesasPreparadas = this.mesasPreparadas.replace(","
                        + numMesa, " y " + numMesa);
            } else {
                this.mensajeVozNotific = this.mensajeVozNotific
                        .replace(",", "");
                this.mesasPreparadas = this.mesasPreparadas.replace(",", "");
            }
            this.mesasPreparadas = this.mesasPreparadas.replaceFirst(",", "");
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }

    }

    /*
     * NUEVA FUNCIÓN PARA GUARDAR LA COMANDA
     */

    private Mesa _mesaActual;

    public Mesa getMesaActual() {
        if (_mesaActual == null)
            _mesaActual = new Mesa();
        return _mesaActual;
    }

    //region CLIENTES

    public void cargarClientes() {
        this.agregarFuncion("getClientes", "");
    }

    public void enviarCliente() {
        this.agregarFuncion("setCliente", this.mesa + ";" + this._mesaActual.getCliente().getId());
    }


    //endregion

    //AL ELIMINAR UNA LINEA VOLVEMOS A ENUMERAR LA LINEA

    private void renumerarLineas() {
        Formateador.reiniciarContadorLineas();
        for (JSONObject linea : this.getLineasVisiblesComanda()) {
            try {
                if (linea.getInt("TIPO") == Constantes.linea_nueva_menu_detalle || linea.getInt("TIPO") == Constantes.linea_recibida_menu_detalle)
                    linea.put("NLINEA", Formateador.getNumLineaDetalle());
                else
                    linea.put("NLINEA", Formateador.getNumLinea());
            } catch (JSONException e) {
                Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
            }
        }
    }

    //region LECTURA DE RESPUESTA JSON

    private void cargarComandaActual(JSONObject jo) {

        JSONObject res;
        this._mesaActual = new Mesa();

        Formateador.reiniciarContadorLineas();

        GestorLineas.Companion.getVISIBLES().clear();

        try {
            res = jo.getJSONObject("getdoc").getJSONObject("CABECERA");

            //NULO EN LISTA DE LINEAS VISIBLES. CUIDADO
            if (res.getString("ID").equals("Error")) {
                //NO SE PUEDE ABRIR LA MESA
                this._mesaActual.setPagando(true);
            } else if (res.getString("ID").equals(
                    "Error: no se puede abrir la mesa")) {
                this._mesaActual.setBloqueada(this.mesaBloqueada);
            } else {
                if (res.getString("ID").equals("-1")) { //EN LA MESA VACIA EL TPV ME ENVÍA UN 0 EN EL NÚMERO
                    this._mesaActual.setNumero(this.mesa);
                } else {
                    _mesaActual.setNumero(res.getString("MESA"));
                }
                Clientes.Cliente c = null;
                if (res.getJSONObject("CLIENTEPROVEEDOR").has("ID")) {
                    c = new Clientes.Cliente(res.getJSONObject("CLIENTEPROVEEDOR").getString("ID"),
                            res.getJSONObject("CLIENTEPROVEEDOR").getString("NOMBRE"),
                            res.getJSONObject("CLIENTEPROVEEDOR").getString("CODIGO"));
                }

                _mesaActual.setId(res.getString("ID"));

                _mesaActual.setIdvendedor(res.getString("IDVENDEDOR"));
                _mesaActual.setCliente(c);
                _mesaActual.setEstado(res.getString("ESTADO"));
                _mesaActual.setFecha(res.getString("FECHA"));
                _mesaActual.setComensales(res.getString("COMENSALES"));
                _mesaActual.setTotal(res.getString("TOTAL"));
                //NUEVO CAMBIO
                _mesaActual.setBloqueada(this.mesaBloqueada);
                //NUEVO PARA COBROS TODO CONTROLAR CAMBIO
                if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)) {
                    _mesaActual.setTotalCobrado(res.getString("TOTALCOBRADO"));
                    _mesaActual.setTotalPendiente(res.getString("TOTALPENDIENTE"));
                }else{
                    //OPCIÓN PARA PREVENIR ERRORES FUTUROS CON NULOS TODO CONTROLAR
                    _mesaActual.setTotalCobrado("0");
                    _mesaActual.setTotalPendiente("0");
                }

                if (res.has("PREFACTURA")) {
                    _mesaActual.setPrefactura(res.getString("PREFACTURA").equals(
                            "1"));
                }

                JSONArray misfilas = res.getJSONArray("LINEAS");


                if (misfilas.length() > 0) {

                    for (int x = 0; x < misfilas.length(); x++) {
                        try {
                            res = (JSONObject) misfilas.get(x);
                            if (res != null) {
                                // VINCULADO =1 IDVINCULO=-1 --> MAESTRO
                                // VINCULADO =0 IDVINCULO>-1 --> DETALLE
                                if (res.getInt("VINCULADO") == 1
                                        && res.getInt("IDVINCULO") == -1) {
                                    res.put("TIPO",
                                            Constantes.linea_recibida_menu_maestro);
                                } else if (res.getInt("IDVINCULO") != -1) {
                                    res.put("TIPO",
                                            Constantes.linea_recibida_menu_detalle);
                                } else {
                                    res.put("TIPO", Constantes.tipo_linea_recibida);
                                }
                                res.put("NLINEA", Formateador.getNumLinea());
                                GestorLineas.Companion.getVISIBLES().add(res);
                            }
                        } catch (Exception e) {
                            Log.e("GLOBAL",
                                    "Linea "
                                            + Thread.currentThread()
                                            .getStackTrace()[2]
                                            .getLineNumber() + ": "
                                            + e.getMessage());
                        }
                    }
                } else {
                    this._mesaActual.setTotal("0");
                    this.setCamareroFactura(this.op.getId());
                }
                /*
                 * Metemos las lineas retenidas
                 */
                List<JSONObject> linRet = GestorLineas.Companion.getLineasRetenidas(this._mesaActual.getNumero());
                for (JSONObject jl : linRet) {

                    try {
                        jl.put("TIPO", Constantes.tipo_linea_nueva);
                    } catch (JSONException e) {
                        this.error = Constantes.error_tratando_JSON;
                        Log.e("GLOBAL",
                                "Linea "
                                        + Thread.currentThread()
                                        .getStackTrace()[2]
                                        .getLineNumber() + ": "
                                        + e.getMessage());
                    }
                    GestorLineas.Companion.getVISIBLES().add(jl);
                }
                if (!GestorLineas.Companion.getLineasRetenidas(this._mesaActual.getNumero()).isEmpty())
                    GestorLineas.Companion.eliminarLineasRetenidas(this._mesaActual.getNumero());
            }
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    }

    private void leerGetcfgmesas(JSONArray filas) {
        if (filas.length() > 0) {
            if (this.listaRangos == null) {
                this.listaRangos = new ArrayList<>();
            } else {
                this.listaRangos.clear();
            }

            this.listaRangos = new ArrayList<>();
            for (int x = 0; x < filas.length(); x++) {
                try {
                    this.listaRangos.add((JSONObject) filas
                            .get(x));
                } catch (JSONException e) {
                    this.error = Constantes.error_tratando_JSON;
                } catch (Exception e1) {
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread()
                                    .getStackTrace()[2]
                                    .getLineNumber()
                                    + ": " + e1.getMessage());
                }
            }
        }
    }

    private void leerGetMesas(JSONArray filas) {
        JSONObject res;
        if (filas.length() > 0) {
            if (this.listaMesas == null) {
                this.listaMesas = new ArrayList<>();
            } else {
                this.listaMesas.clear();
            }

            this.listaMesas = new ArrayList<>();
            for (int x = 0; x < filas.length(); x++) {

                try {
                    res = (JSONObject) filas.get(x);

                    if (this.verSoloMesaslibres) {
                        if (!res.getString("MESA").equals(
                                this.mesa)
                                && res.getString("ESTADO")
                                .equals("LIBRE")) {
                            res.put("BACKC", this.listaRangos
                                    .get(this.idRango)
                                    .getString("BACKC"));
                            res.put("FOREC", this.listaRangos
                                    .get(this.idRango)
                                    .getString("FOREC"));
                            this.listaMesas.add(res);
                        }
                    } else {
                        res.put("BACKC",
                                this.listaRangos.get(
                                        this.idRango)
                                        .getString("BACKC"));
                        res.put("FOREC",
                                this.listaRangos.get(
                                        this.idRango)
                                        .getString("FOREC"));
                        this.listaMesas.add(res);
                    }

                } catch (Exception e1) {
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread()
                                    .getStackTrace()[2]
                                    .getLineNumber()
                                    + ": " + e1.getMessage());
                }
            }
        }
    }

    private void leerGetCarta(JSONArray filas) {
        JSONObject res;
        if (filas.length() > 0) {
            this.listaFamilias = new ArrayList<>();
            this.listaFamiliasVisibles = new ArrayList<>();
            for (int x = 0; x < filas.length(); x++) {
                try {
                    res = (JSONObject) filas.get(x);
                    this.listaFamilias.add(res);
                    //Cambio para ocultar familias
                    if (!res.has("VISIBLE") || res.has("VISIBLE") && res.getInt("VISIBLE") == 1) {
                        this.listaFamiliasVisibles.add(res);
                    }
                    String familiaTextosLibres = "";
                    if (res.getString("COD").equals(
                            familiaTextosLibres)) {
                        guardarTextosLibres(res);

                    }
                } catch (JSONException e) {
                    this.error = Constantes.error_tratando_JSON;
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread()
                                    .getStackTrace()[2]
                                    .getLineNumber()
                                    + ": " + e.getMessage());
                } catch (Exception e1) {
                    Log.e("GLOBAL",
                            "Linea "
                                    + Thread.currentThread()
                                    .getStackTrace()[2]
                                    .getLineNumber()
                                    + ": " + e1.getMessage());
                }
            }
        }
    }

    private void leerSetComanda(JSONObject jo) {
        try {
            if (jo.getJSONObject("setcomanda").getString("resultado")
                    .equals("Ok")) {
                this.comandaEnviada = true;
                if ((this.pendienteEnviado)
                        && (GestorLineas.Companion.getLineasRetenidas(this._mesaActual.getNumero()).size() > 0)) {
                    this.pendienteEnviado = false;

                }
                GestorLineas.Companion.eliminarLineasPendientesBD();

            } else if (jo.getJSONObject("setcomanda")
                    .getString("resultado")
                    .equals("Error: no se pudo insertar la línea")) {
                this.comandaEnviada = false;
                this.error = Constantes.error_insertando_lineas;
            } else {
                this.comandaEnviada = false;

            }
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }
    }

    private void leerSetMesa(JSONObject jo) {
        try {
            this.resultadoF = jo.getJSONObject("setmesa").getString(
                    "resultado");
            if (resultadoF.equals("Ok")) {

                if (GestorLineas.Companion.getVISIBLES().size() > 0) {
                    for (JSONObject lr : GestorLineas.Companion.getVISIBLES()) {
                        try {
                            if ((lr.getInt("TIPO") == 0)
                                    && (lr.getString("MESA")
                                    .equals(this.mesa))) {
                                lr.put("MESA", this.mesaNueva);
                            }

                        } catch (JSONException e) {
                            this.error = Constantes.error_tratando_JSON;
                            Log.e("GLOBAL",
                                    "Linea "
                                            + Thread.currentThread()
                                            .getStackTrace()[2]
                                            .getLineNumber()
                                            + ": " + e.getMessage());
                        }
                    }
                }
                this.mesa = this.mesaNueva;
            }
        } catch (JSONException e) {
            Log.e("GLOBAL", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }
    }

    //endregion

    //region COBROS E IMPRESIÓN DE TICKETS


    /*
     * Función para cobrar la mesa.
     * setcobro (mesa; tratamiento; importe)
     * El parámetro de tratamiento, por ahora, creo que irá vacio
     * TODO HAY QUE PREGUNTARLE A LUIS DE DONDE RECIBO LOS TRATAMIENTOS
     */

    private ArrayList<FormaPago> listaFormasPago;

    public ArrayList<FormaPago> getListaFormasPago() {
        if (listaFormasPago == null)
            listaFormasPago = new ArrayList<>();
        return listaFormasPago;
    }

    public void setListaFormasPago(ArrayList<FormaPago> listaFormasPago) {
        this.listaFormasPago = listaFormasPago;
    }

    /*
     * Función para cobrar la mesa.
     * setcobro (mesa; tratamiento; importe)
     * El parámetro de tratamiento, por ahora, creo que irá vacio
     * TODO HAY QUE PREGUNTARLE A LUIS DE DONDE RECIBO LOS TRATAMIENTOS
     */
    public void cargarFormasDePago() {
        this.agregarFuncion("getTratamientosCobro", "");
    }

    private void guardarFormasDePago(JSONArray ja) {
        listaFormasPago = new ArrayList<>();
        //SI EL OBJETO TIENE CP=0 o PAYLINK<>0 NO GUARDARLO
        for (int i = 0; i < ja.length(); i++) {
            try {
                JSONObject j = ja.getJSONObject(i);
                if (j.getInt("CP") != 0) { //POR AHORA IGNORAMOS EL PAYLINK
                    listaFormasPago.add(new FormaPago(j.getString("IDTRAT"), j.getString("FORMA"), j.getString("TRATAMIENTO")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public void enviarCobro(String importe, String idTratamiento, String codeTransaction) { //CANARYPAY
        this.agregarFuncion("setCobro", this.mesa + ";" + idTratamiento + ";" + importe + ";" + codeTransaction); //NUEVO CAMBIO
    }

    public void enviarCobro(String importe, String idTratamiento) { //COBRO NORMAL
        this.agregarFuncion("setCobro", this.mesa + ";" + idTratamiento + ";" + importe);
    }

    public void finalizarMesa() {
        this.agregarFuncion("setFinalizar", getMesa());
    }

    private Devolucion devolucion;

    public Devolucion getDevolucion() {
        return devolucion;
    }

    /*
     * {"ID":"0","IDFP":"-1","IDTRAT":"1","CP":"1",
     * "CONCEPTO":"Efectivo\/Efectivo","IMPORTE":"50.0",
     * "DEVOLUCION":"0.0","TOTALIMPORTE":"262.36","TOTALCOBRADO":"100.0",
     * "TOTALPENDIENTE":"162.36"}}]
     */
    private void guardarDevolucionTPV(JSONObject res) {

        try {
            JSONObject ja = res.getJSONObject("resultado");
            devolucion = new Devolucion(
                    ja.getString("ID"),
                    ja.getString("IDFP"),
                    ja.getString("IDTRAT"),
                    ja.getString("CP"),
                    ja.getString("CONCEPTO"),
                    ja.getString("IMPORTE"),
                    ja.getString("DEVOLUCION"),
                    ja.getString("TOTALIMPORTE"),
                    ja.getString("TOTALCOBRADO"),
                    ja.getString("TOTALPENDIENTE")
            );
        } catch (JSONException e) {
            devolucion = null;
        }

    }
    //PARA LA NUBE - COMANDERO CLOUD
    private void guardarDevolucion(JSONObject ja) {
        try {
            devolucion = new Devolucion(
                    ja.getString("ID"),
                    ja.getString("IDFP"),
                    ja.getString("IDTRAT"),
                    ja.getString("CP"),
                    ja.getString("CONCEPTO"),
                    ja.getString("IMPORTE"),
                    ja.getString("DEVOLUCION"),
                    ja.getString("TOTALIMPORTE"),
                    ja.getString("TOTALCOBRADO"),
                    ja.getString("TOTALPENDIENTE")
            );
        } catch (JSONException e) {
            devolucion = null;
        }

    }

    public boolean sePuedeFinalizarTicket() {
        return (devolucion != null && Double.parseDouble(devolucion.getTotalPendiente()) == 0);
    }

    private boolean _cerrarMesa = false;


    public boolean cerrarMesa() {
        return _cerrarMesa;
    }

    //endregion


}
