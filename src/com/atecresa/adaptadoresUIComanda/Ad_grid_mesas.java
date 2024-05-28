
package com.atecresa.adaptadoresUIComanda;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.gestionLineasComanda.GestorLineas;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.util.DesignM;
import com.atecresa.util.Formateador;

import org.json.JSONObject;

public class Ad_grid_mesas extends ArrayAdapter <JSONObject> {

	AppCompatActivity context;

	public Ad_grid_mesas(AppCompatActivity context) {

		super(context, R.layout.selector_mesa_item_v2, Inicio.gb.getMesas());
		this.context = context;
	}
	
	static class ViewHolder {

		TextView tvSuperior;
		TextView tvInferior;

	}

	@Override
	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.selector_mesa_item_v2, parent,
					false);
			viewHolder = new ViewHolder();			
			viewHolder.tvSuperior = convertView.findViewById(R.id.txtSuperior);
			viewHolder.tvInferior = convertView.findViewById(R.id.txtInferior);

			viewHolder.tvSuperior.setTextSize(PreferenciasManager.getTamFuenteNew());
			viewHolder.tvInferior.setTextSize(PreferenciasManager.getTamFuenteNew());
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		String numMesa;
		String comensales;
		String importe;
		String tiempo;
		String estado;
		String etiquetaSuperior="";
		String etiquetaInferior="";
		boolean mesaBloqueada;
		boolean retenida;
		int colorBoton;
		int colorTexto;
		
		try {
            JSONObject mesa=Inicio.gb.getMesas().get(position);
			estado = mesa.getString("ESTADO");
			numMesa = mesa.getString("MESA");
			mesaBloqueada = mesa.getInt("IDOPERADORBLOQUEO") > 0;
			//retenida = Inicio.gb.tieneLineasRetenidas(numMesa); //TODO Ver si esto hay que leerlo de memoria
			//TODO Prueba
			retenida=GestorLineas.Companion.hayLineasRetenidasEnMesa(numMesa);
			convertView.setId(Integer.parseInt(numMesa));
			if (estado.equals("LIBRE")) {
				etiquetaSuperior = Inicio.gb.getTextoRango() + " " + numMesa;
				etiquetaInferior = "\n";

				if (mesaBloqueada)
					etiquetaSuperior += "\nBloqueada";
				if (retenida)
					etiquetaInferior = "Retenida\n";
				if ((retenida) || (mesaBloqueada)) {
					colorBoton = DesignM.genColor(Integer.parseInt(mesa.getString("FOREC")));
					colorTexto = DesignM.genColor(Integer.parseInt(mesa.getString("BACKC"))); // FOREC
				} else {
					colorTexto = DesignM.genColor(Integer.parseInt(mesa.getString("FOREC")));
					colorBoton = DesignM.genColor(Integer.parseInt(mesa.getString("BACKC"))); // FOREC
				}
			} else {
				if (estado.equals("PREFACTURA")) {
					estado = "Prefactura\n";
				} else if (estado.equals("PAGANDO")) {
					estado = "Pagando\n";
				} else {
					estado = "\n";
				}

				colorBoton = DesignM.genColor(Integer.parseInt(mesa.getString("FOREC")));
				colorTexto = DesignM.genColor(Integer.parseInt(mesa.getString("BACKC"))); // FOREC
				comensales = Inicio.gb.getMesas().get(position)
						.getString("COMENSALES");
				if (!retenida) {
					importe = Formateador.formatearImporte(mesa.getDouble("IMPORTE"))
							+ " €";
				} else {
					importe = "Retenida";
				}

				tiempo = mesa.getString("TIEMPO");

				if (comensales.equals("0")) {
				} else {
				}

				if (mesaBloqueada)
					estado += "Bloqueada";

				etiquetaSuperior = Inicio.gb.getTextoRango() + " " + numMesa
						+ "\n" + estado;
				etiquetaInferior = importe + "\n" + tiempo;  //SI PONEMOS CRONÓMETRO COMENTAR DESDE \N
			}
			viewHolder.tvSuperior.setTextColor(colorTexto);
			viewHolder.tvSuperior.setBackgroundColor(colorBoton);
			viewHolder.tvInferior.setTextColor(colorTexto);
			viewHolder.tvInferior.setBackgroundColor(colorBoton);
		} catch (Exception e) {

		}
		viewHolder.tvSuperior.setText(etiquetaSuperior);
		viewHolder.tvInferior.setText(etiquetaInferior);

		return (convertView);
	}

}
