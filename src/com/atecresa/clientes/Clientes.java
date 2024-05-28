package com.atecresa.clientes;

/*
NUEVA CLASE PARA GESTIONAR CLIENTES
EN UN FUTURO MIGRAREMOS TODAS LAS CLASES DE DATOS A ESTE PATRÓN
 */

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Clientes {

    //LISTAS ESTÁTICAS
    public static final ArrayList<Cliente> ITEMS = new ArrayList<>();

    public static final Map<String, Cliente> ITEM_MAP = new HashMap<>();

    public static final ArrayList<Cliente> ITEMS_BUSQUEDA = new ArrayList<>();

    //PROC PARA CONVERTIR EL JSON RECIBIDO EN NUESTRAS COLECCIONES
    public static void guardar(JSONObject c) {

        ITEMS.clear();
        ITEM_MAP.clear();

        try {
            for (int i = 0; i < c.getJSONObject("getClientes")
                    .getJSONArray("rows").length(); i++) {
                JSONObject cliente = c.getJSONObject("getClientes")
                        .getJSONArray("rows").getJSONObject(i);
                if (cliente.has("ID")) {
                    Cliente nc = new Cliente(cliente.getString("ID"), cliente.getString("NOMBRE"), cliente.getString("CODIGO"));
                    //Nuevos campos
                    if (cliente.has("TF1"))
                        nc.setTfno(cliente.getString("TF1"));
                    if (cliente.has("EMAIL"))
                        nc.setEmail(cliente.getString("EMAIL"));
                    if (cliente.has("DIR"))
                        nc.setDir(cliente.getString("DIR"));
                    //AÑADIMOS A LAS COLECCIONES
                    ITEMS.add(nc);
                    ITEM_MAP.put(nc.getId(), nc);
                } else {
                    break;
                }
            }
        } catch (JSONException e) {
            ITEMS.clear();
            ITEM_MAP.clear();
            Log.e("CLIENTES ", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }
    }

    public static void buscar(String busqueda) {
        ITEMS_BUSQUEDA.clear();
        try {
            for (Cliente cliente : ITEMS) {
                if (cliente.getNombre().toUpperCase(Locale.getDefault())
                        .contains(busqueda.toUpperCase(Locale.getDefault())) ||
                        cliente.getCodigo().toUpperCase(Locale.getDefault())
                                .contains(busqueda.toUpperCase(Locale.getDefault())) ||
                        cliente.getTfno().toUpperCase(Locale.getDefault())
                                .contains(busqueda.toUpperCase(Locale.getDefault())) ||
                        cliente.getEmail().toUpperCase(Locale.getDefault())
                                .contains(busqueda.toUpperCase(Locale.getDefault())) ||
                        cliente.getDir().toUpperCase(Locale.getDefault())
                                .contains(busqueda.toUpperCase(Locale.getDefault()))) {
                    ITEMS_BUSQUEDA.add(cliente);
                }
            }

        } catch (Exception ex) {
            Log.e("CLIENTES ", "Linea " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + ex.getMessage());
            ITEMS_BUSQUEDA.clear();
        }
    }

    //CLASE DEL ITEM CLIENTE
    public static class Cliente {

        private String _id;
        private String _nombre;
        private String _codigo;
        private String _tfno;
        private String _email;
        private String _dir;

        public Cliente(String _id, String _nombre, String _codigo) {
            super();
            this._id = _id;
            this._nombre = _nombre;
            this._codigo = _codigo;
            this._tfno = "";
            this._email = "";
            this._dir = "";
        }

        public String getId() {
            return _id;
        }

        public void setId(String _id) {
            this._id = _id;
        }

        public String getNombre() {
            return _nombre;
        }

        public String getCodigo() {
            return _codigo;
        }

        public String getDir() {
            return _dir;
        }

        public void setDir(String _dir) {
            this._dir = _dir;
        }

        public String getEmail() {
            return _email;
        }

        public void setEmail(String _email) {
            if (_email.equals("@"))
                _email = "";
            this._email = _email;
        }

        public String getTfno() {
            return _tfno;
        }

        public void setTfno(String _tfno) {
            this._tfno = _tfno;
        }




    }
}
