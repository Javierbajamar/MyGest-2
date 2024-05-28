package com.atecresa.mantenimiento;

import java.util.ArrayList;

public class Articulo {
      //{"TARIFAS":[{"IDTARIFA":"1","PVP":"1.00"},{"IDTARIFA":"5000001","PVP":"1.50"},{}],
	//"T":"X","BACKC":"5580817","DES":"CAFE-INFUSION","FOREC":"65535","PVP":"1.00","COD":"400","ID":"5000155"}
	private String _tipo;
	private String _backC;
	private String _foreC;
	private String _id;
	private String _codigo;
	private String _descripcion;
	private String _pvp;
	private ArrayList<Tarifa> _listaTarifas;
	
	public String getPvp() {
		return _pvp;
	}
	public void setPvp(String _pvp) {
		this._pvp = _pvp;
	}
	public String getTipo() {
		return _tipo;
	}
	public String getBackC() {
		return _backC;
	}
	public String getForeC() {
		return _foreC;
	}
	public String getId() {
		return _id;
	}
	public String getCodigo() {
		return _codigo;
	}
	public String getDescripcion() {
		return _descripcion;
	}
	
	public ArrayList<Tarifa> getListaTarifas() {
		return _listaTarifas;
	}
	
	public Articulo(String _tipo, String _backC, String _foreC, String _id,
                    String _codigo, String _descripcion, String _pvp) {
		super();
		this._tipo = _tipo;
		this._backC = _backC;
		this._foreC = _foreC;
		this._id = _id;
		this._codigo = _codigo;
		this._descripcion = _descripcion;
		this._pvp = _pvp;
		this._listaTarifas=new ArrayList<>();
	}
	
}
