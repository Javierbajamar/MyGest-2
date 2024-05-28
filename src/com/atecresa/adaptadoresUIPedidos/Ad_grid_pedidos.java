package com.atecresa.adaptadoresUIPedidos;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.PreferenciasManager;

import org.json.JSONObject;

@SuppressWarnings("rawtypes")
public class Ad_grid_pedidos extends ArrayAdapter {

	AppCompatActivity context;

	@SuppressWarnings("unchecked")
	public Ad_grid_pedidos(AppCompatActivity context) {

		super(context, R.layout.selector_mesa_item, Inicio.gb.getListaPedidosMyorder()); // USAR
																				// NUEVA
																				// COLECCI�N
																				// DE
																				// PEDIDOS
		this.context = context;

	}

	@Override
	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.selector_mesa_item, null);

		TextView tvSuperior = item.findViewById(R.id.txtSuperior);
		TextView tvInferior = item.findViewById(R.id.txtInferior);
		
		tvSuperior.setTextSize(PreferenciasManager.getTamFuenteNew());
		tvInferior.setTextSize(PreferenciasManager.getTamFuenteNew());

		String etiquetaSuperior = "";
		String etiquetaInferior = "";

		String numero;
		String mesa;
		String total;
		String fecha;

		// LEER NÚMERO DE PEDIDO, MESA, IMPORTE
		try {
			JSONObject pedido = Inicio.gb.getListaPedidosMyorder()
					.get(position);

			mesa = pedido.getJSONObject("CABECERA").getString("MESA");
			numero = pedido.getJSONObject("CABECERA").getString("NUMERO");
			fecha = pedido.getJSONObject("CABECERA").getString("FECHA");
			total = pedido.getJSONObject("CABECERA").getString("TOTAL");
			etiquetaSuperior = "Num " + numero + "\nMesa: " + mesa;
			etiquetaInferior = fecha + "\n" + total + " €";

		} catch (Exception e) {

		}
		tvSuperior.setText(etiquetaSuperior);
		tvInferior.setText(etiquetaInferior);

		return (item);
	}

}
