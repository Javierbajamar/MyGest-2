
package com.atecresa.mantenimiento;

import com.atecresa.application.Inicio;
import com.atecresa.preferencias.Constantes;
import com.atecresa.util.Formateador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

/*
 * CLASE PARA LA GESTION DE LOS ENLACES COMBINADOS
 * MENU Y TEXTOS LIBRES POR GRUPO
 * 
 *
 {"getvinculoseleccion":{"rows":[
 {"ID":"60000001","TARIFA":"0","IDGRUPO":"60000002","TOTAL":"2","ORDEN":"1","IDART":"60000027","SALTAR":"1"},
 {"ID":"60000002","TARIFA":"0","IDGRUPO":"60000001","TOTAL":"1","ORDEN":"1","IDART":"60000027","SALTAR":"1"},
 {"ID":"60000003","TARIFA":"0","IDGRUPO":"60000003","TOTAL":"1","ORDEN":"1","IDART":"60000027","SALTAR":"1"}]}}


 {"getgruposeleccion":{"resultado":{"GRUPOSELECCION":[
 {"ARTICULOS":[{"ID":"16"},{"ID":"135"}],"ID":"60000001","DES":"SEGUNDOS"},
 {"ARTICULOS":[{"ID":"41"},{"ID":"7"}],"ID":"60000002","DES":"PRIMEROS"},
 {"ARTICULOS":[{"ID":"453"},{"ID":"187"}],"ID":"60000003","DES":"POSTRES"}]}}}

 llenas dos pilas al reves, la principal y la de lineas (con la referencia del elemento anterior)
 */

public class Vinculo {

	private Stack<JSONObject> pilaVinculos;
	private Stack<Integer> pilaLineas;
	private Stack<Integer> pilaUnidades;

	private ArrayList<JSONObject> listaGrupos;
	private ArrayList<JSONObject> listaVinculos;
	private ArrayList<JSONObject> listaArticulosGrupo;

	private ArrayList<JSONObject> listaPreComanda; // LISTA PARA GUARDAR LOS
													// ARTICULOS,
	// ANTES DE METERLOS EN LA COMANDA

	private int lineaRef = 0;

	private double unidades = 1;

	public void setUnidadesMaestro(double _unid) {
		this.unidades = _unid;
	}

	public Vinculo() {
		this.listaGrupos = new ArrayList<>();
		this.listaVinculos = new ArrayList<>();

		this.pilaVinculos = new Stack<>();
		this.pilaLineas = new Stack<>();
		this.pilaUnidades = new Stack<>();

		this.listaArticulosGrupo = new ArrayList<>();
		this.listaPreComanda = new ArrayList<>();
	}

	public boolean hayVinculos() {
		return this.listaVinculos != null && this.listaVinculos.size() > 0
				&& this.listaGrupos != null && this.listaGrupos.size() > 0;
	}

	private int tarifa = 0;

	public int getTarifa() {
		return this.tarifa;
	}

	public boolean apilarVinculos(JSONObject art) {
		boolean encontrado = false;
		for (JSONObject vinculo : this.listaVinculos) {
			try {
				if (vinculo.getInt("IDART") == art.getInt("ID")) {
					tarifa = vinculo.getInt("TARIFA");
					encontrado = true;
					int total = Integer.parseInt(Formateador.formatearUds(vinculo
							.getDouble("TOTAL") * this.unidades));
					for (int i = 0; i < total; i++) {
						this.pilaVinculos.push(vinculo);
						this.pilaUnidades.push(i + 1);
						if (art.has("NUMLINEA"))
							this.pilaLineas.push(art.getInt("NUMLINEA"));
						else
							this.pilaLineas.add(0, this.lineaRef);
					}
				}
			} catch (JSONException e) {
				return false;
			}
		}
		return encontrado;
	}

	public int getUnidadesGrupo() {
		if (!this.pilaUnidades.empty())
			return this.pilaUnidades.pop();
		else
			return 0;
	}

	//private int numLinea = 0;

	//public int getNumlinea() {
		//return this.numLinea;
	//}

	private String nombreGrupo = "";

	public String getNombreGrupo() {
		return this.nombreGrupo;
	}

    private int idGrupo = 0;

	public boolean cargarGrupo() {

		int tarifa;
		boolean saltar;
		int idRef;
		if (!this.pilaVinculos.isEmpty()) {
			JSONObject vinculo = this.pilaVinculos.pop();
			try {
				idGrupo = vinculo.getInt("IDGRUPO");
				tarifa = vinculo.getInt("TARIFA");
				saltar = vinculo.getInt("SALTAR") == 1;
			} catch (JSONException e1) {
				return false;
			}
			idRef = this.pilaLineas.pop();
			// BORRAMOS LISTA DE ARTICULOS
			if (this.listaArticulosGrupo != null)
				this.listaArticulosGrupo.clear();
			// BUSCAMOS EL GRUPO QUE NECESITAMOS
			//Aqui insertamos el nuevo campo dentro de art
			//nuevo campo IDTIPO_ENLACE introducirlo en la linea 
			//si este campo tiene valor 0 sustuir por 350
			for (JSONObject grupo : this.listaGrupos) {
				try {
					if (grupo.getInt("ID") == (idGrupo)) {
						this.nombreGrupo = grupo.getString("DES");
                        int numPlatosGrupo = vinculo.getInt("TOTAL");
						for (int i = 0; i < grupo.getJSONArray("ARTICULOS")
								.length(); i++) {

							int idArt = grupo.getJSONArray("ARTICULOS")
									.getJSONObject(i).getInt("ID");
							JSONObject art = Inicio.gb.getArticulo(idArt);
							if (art != null) {
								art.put("NUMLINEAREF", idRef);
								art.put("TARIFA", tarifa);
								//NUEVA LINEA DE VINCULOS COMBINADOS
								if (vinculo.has("IDTIPO_ENLACE")){
									art.put("IDTIPO_ENLACE", vinculo.getString("IDTIPO_ENLACE").replace("0", "350"));
								}
								this.listaArticulosGrupo.add(art);
							}
						}
					}
				} catch (Exception e) {
					return false;
				}
			}
			if (saltar) {
				JSONObject salto = new JSONObject();
				try {
					salto.put("DES", "SALTAR");
					salto.put("ID", -1);
					this.listaArticulosGrupo.add(0, salto);
				} catch (Exception e) {
					return false;
				}
			}
			return true;
		} else {
			this.listaArticulosGrupo.clear();
			return false;
		}

	}

	public void saltarVinculo() {
		// IDGRUPO
		if (!this.pilaVinculos.isEmpty()) {
			while (true) {
				if (!this.pilaVinculos.isEmpty()) {
					JSONObject vinculo = this.pilaVinculos.peek();
					try {
						if (vinculo.getInt("IDGRUPO") == this.idGrupo) {
							this.pilaVinculos.pop();
							this.pilaLineas.pop();
							this.pilaUnidades.pop();
						} else {
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else
					break;
			}
		}

	}

	public ArrayList<JSONObject> getListaVinculos() {
		return this.listaVinculos;
	}

	public ArrayList<JSONObject> getListaGrupos() {
		return this.listaGrupos;
	}

	public ArrayList<JSONObject> getListaArticulosGrupo() {
		return this.listaArticulosGrupo;
	}

	public ArrayList<JSONObject> getListaPreComanda() {
		return this.listaPreComanda;
	}

	
	public void insertarEnPrecomanda(JSONObject art, boolean principal) {
		JSONObject linea = new JSONObject();
        
		
		try {
			linea.put("MESA", Inicio.gb.getMesa());
			linea.put("IDARTICULO", art.getString("ID"));
			linea.put("DESCRIPCION", art.getString("DES"));

			linea.put("ID", art.getString("ID"));
			linea.put("T", art.getString("T"));
			linea.put("OBSERVACION", "");
			linea.put("NUMLINEA", art.getInt("NUMLINEA"));
			if (principal) {
				linea.put("NLINEA", Formateador.getNumLinea());  //PARA REORDENAR LINEAS RAPIDAMENTE
				linea.put("PRECIO", art.getDouble("PVP"));
				linea.put("UNID", this.unidades);
				linea.put("TOTAL", art.getDouble("PVP") * this.unidades);
				linea.put("NUMLINEAREF", "");
				linea.put("TIPO", Constantes.linea_nueva_menu_maestro);
				linea.put("TARIFA", "");
			} else {
				String _pvp=getPvp(art);
				if (_pvp.equals("")){
					_pvp=art.getString("PVP");
				}
				linea.put("NLINEA", Formateador.getNumLineaDetalle());
				linea.put("PRECIO", _pvp);
				linea.put("UNID", 1);
				linea.put("TOTAL", _pvp);
				linea.put("NUMLINEAREF", art.getInt("NUMLINEAREF"));
				linea.put("TIPO", Constantes.linea_nueva_menu_detalle);
				linea.put("TARIFA", art.getInt("TARIFA"));
			}
			if (art.has("IDTIPO_ENLACE")){
				linea.put("IDTIPO_ENLACE", art.getString("IDTIPO_ENLACE"));
			}
			
			this.listaPreComanda.add(linea);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void pasarAComanda() {
		for (JSONObject linea : this.listaPreComanda) {
			Inicio.gb.getLineasVisiblesComanda().add(linea);
		}

	}

	public void vaciarDatos() {
		this.listaPreComanda.clear();
		this.pilaLineas.clear();
		this.pilaVinculos.clear();
		this.lineaRef = 0;
	}
	
	private String getPvp(JSONObject art){
		String _pvpArt="";
		try {
			JSONArray l=art.getJSONArray("TARIFAS");
			for (int i=0;i<l.length();i++){
				if (l.getJSONObject(i).getInt("IDTARIFA")==art.getInt("TARIFA")){ 
					_pvpArt= l.getJSONObject(i).getString("PVP");
					break;
				}	
			}
		} catch (JSONException e) {
			_pvpArt="";
		}
		return _pvpArt;
	}

}
