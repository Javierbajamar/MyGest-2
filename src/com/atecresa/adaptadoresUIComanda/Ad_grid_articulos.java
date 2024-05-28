package com.atecresa.adaptadoresUIComanda;

import android.util.Log;
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

import org.json.JSONObject;


public class Ad_grid_articulos extends ArrayAdapter <JSONObject> {
	AppCompatActivity context;

	
	public Ad_grid_articulos(AppCompatActivity context) {

		super(context, R.layout.item_articulo, Inicio.gb.getArticulos());
		this.context = context;

	}
	
	static class ViewHolder {

		TextView tvArticulo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		try {
		if (convertView == null) {

			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.comanda_item_grid_v2, parent,
					false);
			viewHolder = new ViewHolder();			
			viewHolder.tvArticulo = convertView.findViewById(R.id.txtSuperior);
			viewHolder.tvArticulo.setTextSize(PreferenciasManager.getTamFuenteNew());

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
				
		String descripcion;
		int id;

		int colorBoton;
		int colorTexto;

		
			JSONObject articulo = Inicio.gb.getArticulos().get(position);
			id = articulo.getInt("ID");
			descripcion = articulo.getString("DES").trim();


			colorTexto = DesignM.genColor(Integer.parseInt(Inicio.gb
					.getArticulos().get(position).getString("FOREC")));
			colorBoton = DesignM.genColor(Integer.parseInt(Inicio.gb
					.getArticulos().get(position).getString("BACKC")));
			viewHolder.tvArticulo.setTextColor(colorTexto);
			viewHolder.tvArticulo.setBackgroundColor(colorBoton);
			//viewHolder.tvArticulo.setTypeface(Inicio.getFontRobotoLight());
			convertView.setId(id);
			viewHolder.tvArticulo.setText(descripcion);
		} catch (Exception e) {
			Log.e("ADT ARTICULOS", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}

		return (convertView);
	}

}
