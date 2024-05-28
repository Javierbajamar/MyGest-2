package com.atecresa.adaptadoresUIComanda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.TpvConfig;

import org.json.JSONException;

@SuppressWarnings("rawtypes")
public class Ad_combo_funciones extends ArrayAdapter {

	AppCompatActivity context;

	@SuppressWarnings("unchecked")
	public Ad_combo_funciones(AppCompatActivity context) {

		super(context, R.layout.comanda_item_grid, Inicio.gb.getListaFunciones());
		this.context = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.comanda_item_grid, null);
		TextView tvNF = item.findViewById(R.id.txtSuperior);
		tvNF.setBackgroundColor(TpvConfig.getAppBackColor());
		tvNF.setTextColor(TpvConfig.getAppForecolor());
		//tvNF.setTypeface(Inicio.getFontRobotoLight());
		String descripcion = "";

		try {
			descripcion = Inicio.gb.getListaFunciones().get(position)
					.getString("DES").trim();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}

		tvNF.setText(descripcion);
		return (item);
	}
}
