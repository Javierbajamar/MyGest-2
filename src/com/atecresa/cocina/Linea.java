package com.atecresa.cocina;

public class Linea {
	
	private String _iddetalle;
	private String _idarticulo;
	private String _articulo;
	private String _uds;
	private String _obs;
	private String _elaborado_oper;
	private String elaborado_fecha;
	private String _tipo;  //TEXTO LIBRE, MIXTO...
	private int _estado;  
	
	public String getIdarticulo() {
		return _idarticulo;
	}

	public String getArticulo() {
		return _articulo;
	}

	public String getUds() {
		return _uds;
	}
	
	public void setUds(String _uds){
		this._uds=_uds;
	}
	
	public String getObs(){
		return this._obs;
	}

	public String getElaborado_oper() {
		return _elaborado_oper;
	}
	public void setElaborado_oper(String _elaborado_oper) {
		this._elaborado_oper = _elaborado_oper;
	}
	public String getElaborado_fecha() {
		return elaborado_fecha;
	}
	public void setElaborado_fecha(String elaborado_fecha) {
		this.elaborado_fecha = elaborado_fecha;
	}
	public String getIddetalle() {
		return _iddetalle;
	}
	
	public String getTipo() {
		return _tipo;
	}
	
	public int getEstado() {
		return _estado;
	}

	public void setEstado(int _estado) {
		this._estado = _estado;
	}

	
	public Linea(String _iddetalle, String _idarticulo, String _articulo,
                 String _uds, String _obs, String _elaborado_oper, String elaborado_fecha, String _tipo, int _estado) {
		super();
		this._iddetalle = _iddetalle;
		this._idarticulo = _idarticulo;
		this._articulo = _articulo;
		this._uds = _uds;
		this._obs=_obs;
		this._elaborado_oper = _elaborado_oper;
		this.elaborado_fecha = elaborado_fecha;
		this._tipo=_tipo;
		this._estado=_estado;
	}
	
	
}
