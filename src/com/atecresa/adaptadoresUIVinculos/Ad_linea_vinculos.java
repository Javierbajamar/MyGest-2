package com.atecresa.adaptadoresUIVinculos;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.util.Formateador;

import org.json.JSONObject;


public class Ad_linea_vinculos extends ArrayAdapter <JSONObject>  {
	AppCompatActivity context;

	public Ad_linea_vinculos(AppCompatActivity context) {

		super(context, R.layout.comanda_item_linea, Inicio.gb.getVinculos().getListaPreComanda());
		this.context = context;

	}
     
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.comanda_item_linea, null);

		TextView txtUds = item.findViewById(R.id.txt_comanda_uds);
		TextView txtDescripcion = item
				.findViewById(R.id.txt_comanda_descripcion);
		TextView txtPvp = item.findViewById(R.id.txt_comanda_pvp);
		txtPvp.setTextColor(Color.BLACK);
		Double unidades;
		String unidadesMostrar = "";
		String descripcion = "";
		String total = "";
		
		txtUds.setTextSize(PreferenciasManager.getTamFuenteNew());
		txtDescripcion.setTextSize(PreferenciasManager.getTamFuenteNew());
		txtPvp.setTextSize(PreferenciasManager.getTamFuenteNew());

		String tipo_articulo;

		try {
			JSONObject linea = Inicio.gb.getVinculos().getListaPreComanda().get(position);
			item.setBackgroundColor(Color.WHITE);

			descripcion = linea.getString("DESCRIPCION");
			tipo_articulo = linea.getString("T");

			if (linea.getInt("TIPO") == Constantes.linea_nueva_menu_detalle){
				ImageView ie= item.findViewById(R.id.img_enlace);
				ie.setVisibility(View.VISIBLE);
				txtDescripcion.setTypeface(null, Typeface.BOLD);
			}

			if (!tipo_articulo.equals("H")) { // si no es un texto libre
				unidades = linea.getDouble("UNID");
				txtUds.setTextColor(Color.BLACK);
				if (unidades == 0.25) {
					unidadesMostrar = "1/4";
				} else if (unidades == 0.5) {
					unidadesMostrar = "1/2";
				} else if (unidades == 0.75) {
					unidadesMostrar = "3/4";
				} else if (unidades < 0) {
					unidadesMostrar = Formateador.formatearUds(unidades); // FALLA
					txtUds.setTextColor(Color.RED);
				} else {
					unidadesMostrar = Formateador.formatearUds(unidades);
				}
				unidadesMostrar += " x";

				total = Formateador.formatearImporte(linea.getDouble("TOTAL")) + "";
				if (linea.has("TARIFA")
						&& (linea.getString("TARIFA").equals("0"))) {
	                	total="0,00 €";
	                }else{
	                	if (!total.equals("")) {
	    					total = total + " €";
	    				}	
	                }
			} else {
				txtDescripcion.setTextColor(Color.BLUE);
				item.setBackgroundDrawable(context.getResources().getDrawable(
						android.R.drawable.alert_light_frame));
				txtDescripcion.setTypeface(Typeface.MONOSPACE,
						Typeface.BOLD);
			}
			
			if (position==0){
				txtDescripcion.setTextColor(Color.BLUE);
				item.setBackgroundDrawable(context.getResources().getDrawable(
						android.R.drawable.alert_light_frame));
				txtDescripcion.setTypeface(Typeface.MONOSPACE,
						Typeface.BOLD);
			}else{
				txtDescripcion.setTextColor(Color.BLACK);

			}

			// item.setId(Integer.parseInt(idLinea));

		} catch (Exception e) {
			e.printStackTrace();
		}

		txtUds.setText(unidadesMostrar);
		txtDescripcion.setText(descripcion);
		txtPvp.setText(total);
		return (item);
	}
	
	

}
