package com.atecresa.adaptadoresUIComanda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;

@SuppressWarnings("rawtypes")
public class Ad_combo_unidades extends ArrayAdapter {

	AppCompatActivity context;

	@SuppressWarnings("unchecked")
	public Ad_combo_unidades(AppCompatActivity context) {

		super(context, R.layout.comanda_item_combo_unidades, Inicio.getArrayUnidades());
		this.context = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.comanda_item_combo_unidades, null);
		try {
			TextView tvNombreUnidad = item
					.findViewById(R.id.txt_lista_unidad);
			
			String descripcionUnid;
			descripcionUnid = Inicio.getArrayUnidades().get(position);
			if (descripcionUnid.equals("0.25")) {
				tvNombreUnidad.setText("1/4");
			} else if (descripcionUnid.equals("0.5")) {
				tvNombreUnidad.setText("1/2");
			} else if (descripcionUnid.equals("0.75")) {
				tvNombreUnidad.setText("3/4");
			} else {
				tvNombreUnidad.setText(descripcionUnid);
			}
			item.setTag(descripcionUnid);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
		return (item);
	}
}