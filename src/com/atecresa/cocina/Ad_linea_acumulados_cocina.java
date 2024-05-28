package com.atecresa.cocina;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.R;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.Sistema;
import com.atecresa.util.Formateador;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class Ad_linea_acumulados_cocina extends ArrayAdapter {
	AppCompatActivity context;
	ArrayList<Linea> listaPlatos;

	@SuppressWarnings("unchecked")
	public Ad_linea_acumulados_cocina(AppCompatActivity context,
                                      ArrayList<Linea> lista) {

		super(context, R.layout.item_plato_acumulado, lista);
		this.listaPlatos = lista;
		this.context = context;

	}
	
	static class ViewHolder {

		TextView t;

	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {

			LayoutInflater inflater = context.getLayoutInflater();
			if (Sistema.usarElementosVisualesCompatibles()) {
				convertView = inflater.inflate(R.layout.item_plato_acumulado_compatible, parent,false);
			}else{
				convertView = inflater.inflate(R.layout.item_plato_acumulado, parent,false);
			}

			viewHolder = new ViewHolder();
			viewHolder.t = convertView
					.findViewById(R.id.tvAcumulado);
			convertView.setTag(viewHolder);
			viewHolder.t.setMinWidth(PreferenciasManager.getAnchoColumnasCocina()); //TODO PROBAR AQUÍ
			//viewHolder.t.setTypeface(Inicio.getFontRobotoLight());
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			Linea plato = listaPlatos.get(position);

			viewHolder.t.setText(Formateador.formatearUds(Double.parseDouble(plato.getUds()))
					+ " x " + plato.getArticulo());
			viewHolder.t.setTextSize(PreferenciasManager.getTamFuenteNew());

		} catch (Exception e) {
			Log.e("ADAP LINEA ACUMULADOS", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
		}
		return (convertView);
	}

}
