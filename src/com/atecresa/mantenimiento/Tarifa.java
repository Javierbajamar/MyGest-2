package com.atecresa.mantenimiento;

public class Tarifa {
	//IDTARIFA":"1","PVP":"1.00"
	
	String _idTarifa;
	String _pvp;
	
	public String getIdTarifa() {
		return _idTarifa;
	}

	public String getPvp() {
		return _pvp;
	}
	
	public Tarifa(String _idTarifa, String _pvp) {
		super();
		this._idTarifa = _idTarifa;
		this._pvp = _pvp;
	}
	
}
