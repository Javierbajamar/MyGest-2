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


public class Ad_grid_familias extends ArrayAdapter <JSONObject> {

	AppCompatActivity context;


	public Ad_grid_familias(AppCompatActivity context) {

		super(context, R.layout.comanda_item_grid_v2, Inicio.gb.getFamiliasVisibles());
		this.context = context;



	}

	static class ViewHolder {

		TextView tvFamilia;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		int colorBoton;
		int colorTexto;

		try {
			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.comanda_item_grid_v2, parent,
						false);
				viewHolder = new ViewHolder();
				viewHolder.tvFamilia = convertView
						.findViewById(R.id.txtSuperior);
				viewHolder.tvFamilia.setTextSize(PreferenciasManager.getTamFuenteNew());
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			JSONObject familia=Inicio.gb.getFamiliasVisibles().get(position);
			convertView.setId(familia.getInt("ID"));
			colorTexto = DesignM.genColor(Integer.parseInt(familia.getString("FOREC")));
			colorBoton = DesignM.genColor(Integer.parseInt(familia.getString("BACKC")));
			viewHolder.tvFamilia.setTextColor(colorTexto);
			viewHolder.tvFamilia.setBackgroundColor(colorBoton);
			viewHolder.tvFamilia.setText(familia.getString("DES").trim());
		} catch (Exception e) {
			Log.e("ADT FAMILIAS", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}
		return (convertView);
	}

}
