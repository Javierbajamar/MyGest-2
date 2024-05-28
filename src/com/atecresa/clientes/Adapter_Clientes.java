package com.atecresa.clientes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.R;

public class Adapter_Clientes extends ArrayAdapter<Clientes.Cliente> {

	
	AppCompatActivity context;

	public Adapter_Clientes(AppCompatActivity context) {

		super(context, R.layout.item_cliente_v2, Clientes.ITEMS_BUSQUEDA);
		this.context = context;

	}

	static class ViewHolder {

		TextView tvCliente;
		TextView tvTfno;
		TextView tvEmail;
		TextView tvDir;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vh;

		try {
			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.item_cliente_v2, parent, false);
				vh = new ViewHolder();
				vh.tvCliente = convertView.findViewById(R.id.txt_cliente);
				vh.tvTfno= convertView.findViewById(R.id.tvTfno);
				vh.tvEmail= convertView.findViewById(R.id.tvEmail);
				vh.tvDir= convertView.findViewById(R.id.tvDir);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			Clientes.Cliente c = Clientes.ITEMS_BUSQUEDA.get(position);

			convertView.setId(Integer.parseInt(c.getId()));
			vh.tvCliente.setText(c.getNombre().trim().toUpperCase());
			vh.tvTfno.setText("TFNO: "+c.getTfno().trim());
			vh.tvEmail.setText(c.getEmail().trim());
			vh.tvDir.setText("DIR: "+c.getDir().trim());
		} catch (Exception e) {
			Log.e("ADT BUSCADOR CLIENTES", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}

		return (convertView);
	}



}
