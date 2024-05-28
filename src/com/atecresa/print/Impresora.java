package com.atecresa.print;

public class Impresora {
	
	private String nombre;
	private String dispositivo_windows;
	
	public Impresora(String nombre, String dispositivo_windows) {
		super();
		this.nombre = nombre;
		this.dispositivo_windows = dispositivo_windows;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDispositivo_windows() {
		return dispositivo_windows;
	}

	

}
