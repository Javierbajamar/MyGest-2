package com.atecresa.mantenimiento;

import java.util.ArrayList;

public class Familia {
//"ID":"5000004","BACKC":"5580817","DES":"VINOS\/SANGRIA.","FOREC":"65535","COD":"5"
	
	private String _backC;
	private String _foreC;
	private String _id;
	private String _codigo;
	private String _descripcion;
	private ArrayList<Articulo> _listaArticulos;
	
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
	public ArrayList<Articulo> getListaArticulos() {
		return _listaArticulos;
	}
	
	public Familia(String _backC, String _foreC, String _id,
				   String _codigo, String _descripcion) {
		super();
		this._backC = _backC;
		this._foreC = _foreC;
		this._id = _id;
		this._codigo = _codigo;
		this._descripcion = _descripcion;
		this._listaArticulos=new ArrayList<>();
	}
}
