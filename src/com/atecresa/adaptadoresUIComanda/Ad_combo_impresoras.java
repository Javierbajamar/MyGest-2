package com.atecresa.adaptadoresUIComanda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.R;
import com.atecresa.preferencias.TpvConfig;

@SuppressWarnings("rawtypes")
public class Ad_combo_impresoras extends ArrayAdapter {
	AppCompatActivity context;

	@SuppressWarnings("unchecked")
	public Ad_combo_impresoras(AppCompatActivity context) {

		super(context, R.layout.comanda_item_grid, TpvConfig.getImpresoras());
		this.context = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.comanda_item_grid, null);
		TextView tvNF = item.findViewById(R.id.txtSuperior);
		tvNF.setBackgroundColor(TpvConfig.getAppBackColor());
		tvNF.setTextColor(TpvConfig.getAppForecolor());
		String descripcion;
        descripcion = TpvConfig.getImpresoras().get(position).getNombre().trim() + " | " + TpvConfig.getImpresoras().get(position).getDispositivo_windows();
		tvNF.setText(descripcion);
		return (item);
	}
}
