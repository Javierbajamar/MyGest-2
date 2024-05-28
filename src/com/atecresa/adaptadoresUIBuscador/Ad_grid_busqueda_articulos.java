package com.atecresa.adaptadoresUIBuscador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.util.DesignM;

import org.json.JSONException;
import org.json.JSONObject;

public class Ad_grid_busqueda_articulos extends ArrayAdapter <JSONObject> {
	AppCompatActivity context;

	public Ad_grid_busqueda_articulos(AppCompatActivity context) {

		super(context, R.layout.item_articulo, Inicio.gb.getResultadosBusqueda());
		this.context = context;

	}


	static class ViewHolder {

		TextView tvArticulo;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;

		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_articulo, parent, false);
			vh = new ViewHolder();
			vh.tvArticulo = convertView.findViewById(R.id.tvNomArticulo);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.tvArticulo.setTextSize(PreferenciasManager.getTamFuenteNew());

		try {
			JSONObject busqueda=Inicio.gb.getResultadosBusqueda().get(position);

			vh.tvArticulo.setTextColor(DesignM.genColor(Integer.parseInt(Inicio.gb
					.getResultadosBusqueda().get(position).getString("FOREC"))));
			vh.tvArticulo.setBackgroundColor(DesignM.genColor(Integer.parseInt(Inicio.gb
					.getResultadosBusqueda().get(position).getString("BACKC"))));
			convertView.setId(busqueda.getInt("ID"));
			vh.tvArticulo.setText(busqueda
					.getString("DES").trim());
		} catch (JSONException e) {
			
			e.printStackTrace();
		}


		return (convertView);
	}

}
