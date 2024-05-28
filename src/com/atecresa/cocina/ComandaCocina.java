package com.atecresa.cocina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ComandaCocina {

	private String _mesa;
	
	private ArrayList<Linea> _lineasComanda;
	private String _fecha; //POR AHORA STRING
	private String _tiempo;
	private String _comensales;
	private String _operador;
	private boolean _impreso;
	private boolean _completa;
	
	private int _tiempoEspera; //ENTERO PARA GUARDAR LA CONSTANTE Y DIBUJAR LA UI CON LOS COLORES 
	
	public ArrayList<Linea> getLineasComanda() {
		return _lineasComanda;
	}

	public String getMesa() {
		return _mesa;
	}
	public String getFecha() {
		return _fecha;
	}
	public String getTiempo() {
		return _tiempo;
	}
	
	public void setTiempo(String tiempo) {
		this._tiempo=tiempo;
	}

	public boolean isImpreso() {
		return _impreso;
	}

	public String getComensales() {
		return _comensales;
	}
	
	public String getOperador() {
		return _operador;
	}
	
	public boolean isCompleta(){
		return this._completa;
	}
	
	public void setCompleta(boolean completa){
		this._completa=completa;
	}
	
	public int getTiempoEspera(){
		return this._tiempoEspera;
	}
	
	public void setTiempoEspera(int t){
		this._tiempoEspera=t;
	}

	public ComandaCocina(String _mesa, String _fecha, String _tiempo, String _comensales, String _operador,
						 boolean _impreso, boolean _completa, int tiempoEspera) {
		super();
		this._mesa = _mesa;
		this._fecha = _fecha;
		this._tiempo = _tiempo;
		this._impreso = _impreso;
		this._comensales=_comensales;
		this._operador=_operador;
		this._completa=_completa;
		this._tiempoEspera=tiempoEspera;
		_lineasComanda=new ArrayList<>();
	}
	
	private class ComLineasHechas implements Comparator<Linea> {
	    
	    public int compare(Linea l1, Linea l2) {
            return l2.getEstado() - l1.getEstado();
        }
	}
	
	public void ordenarLineasComanda(){
		Collections.sort(_lineasComanda,
				new ComLineasHechas());
	}

}
