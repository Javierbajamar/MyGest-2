package com.atecresa.adaptadoresUIPedidos;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.util.Formateador;

import org.json.JSONObject;

@SuppressWarnings("rawtypes")
public class Ad_lineas_pedido extends ArrayAdapter {

	AppCompatActivity context;

	@SuppressWarnings("unchecked")
	public Ad_lineas_pedido(AppCompatActivity context) {

		super(context, R.layout.selector_mesa_item, Inicio.gb
				.getListaLineasPedidoMyorder());
		this.context = context;

	}

	@Override
	@SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.comanda_item_linea, null);

		TextView txtUds = item.findViewById(R.id.txt_comanda_uds);
		TextView txtDescripcion = item
				.findViewById(R.id.txt_comanda_descripcion);
		TextView txtPvp = item.findViewById(R.id.txt_comanda_pvp);
		String unidades = "";
		String descripcion = "";
		String total = "";
		String obs;

		item.setBackgroundColor(android.R.drawable.list_selector_background);

		txtUds.setTextSize(PreferenciasManager.getTamFuenteNew());
		txtDescripcion.setTextSize(PreferenciasManager.getTamFuenteNew());
		txtPvp.setTextSize(PreferenciasManager.getTamFuenteNew());

		// LEER N�MERO DE PEDIDO, MESA, IMPORTE
		try {
			JSONObject linea = Inicio.gb.getListaLineasPedidoMyorder().get(
					position);

			descripcion = linea.getString("DESCRIPCION");
			obs = linea.getString("OBSERVACION");
			if ((!obs.equals("-")) && (!obs.equals(""))) {
				descripcion += "\n<<" + obs + ">>";
			}
			String tipo_articulo = linea.getString("T");
			if (!tipo_articulo.equals("H")) { // si no es un texto libre
				unidades = Formateador.formatearUds(linea.getDouble("UNID")) + "";

				if (unidades.equals("0,25")) {
					unidades = "1/4";
				} else if (unidades.equals("0,5")) {
					unidades = "1/2";
				} else if (unidades.equals("0,75")) {
					unidades = "3/4";
				} else if (Double.parseDouble(unidades) < 0) {

					txtUds.setTextColor(Color.RED);
				}
				unidades += " x";

				total = Formateador.formatearImporte(linea.getDouble("TOTAL")) + "";

				if (!total.equals("")) {
					total = total + " �";
				}
			} else {
				txtDescripcion.setTextColor(Color.BLUE);
				item.setBackgroundDrawable(context.getResources().getDrawable(
						android.R.drawable.alert_light_frame));
				txtDescripcion.setTypeface(Typeface.MONOSPACE,
						Typeface.BOLD_ITALIC);
				if (!linea.getString("TOTAL").equals("0.0")
						&& !linea.getString("TOTAL").equals("")) {
					total = Formateador.formatearImporte(linea.getDouble("TOTAL"))
							+ "";
					if (linea.has("TARIFA")
							&& (linea.getString("TARIFA").equals("0"))) {
						total = "";
					} else {
						total = total + " €";
					}
				} else {
					total = "";
				}
			}
		} catch (Exception e) {

		}
		txtUds.setText(unidades);
		txtDescripcion.setText(descripcion);
		txtPvp.setText(total);
		return (item);
	}

}
