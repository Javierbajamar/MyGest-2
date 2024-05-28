package com.atecresa.adaptadoresUIComanda;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.util.Formateador;

import org.json.JSONObject;





public class Ad_linea_comanda extends ArrayAdapter <JSONObject> {
	AppCompatActivity context;

	public Ad_linea_comanda(AppCompatActivity context) {

		super(context, R.layout.comanda_item_linea, Inicio.gb
				.getLineasVisiblesComanda());
		this.context = context;

	}

	static class ViewHolder {

		TextView txtUds;
		TextView txtDescripcion;
		TextView txtPvp;
		ImageView ie;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {

			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater
					.inflate(R.layout.comanda_item_linea, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.txtUds = convertView
					.findViewById(R.id.txt_comanda_uds);
			viewHolder.txtDescripcion = convertView
					.findViewById(R.id.txt_comanda_descripcion);
			viewHolder.txtPvp = convertView
					.findViewById(R.id.txt_comanda_pvp);
			convertView.setTag(viewHolder);
			viewHolder.txtUds.setTextSize(PreferenciasManager.getTamFuenteNew());
			viewHolder.txtDescripcion.setTextSize(PreferenciasManager.getTamFuenteNew());
			viewHolder.txtPvp.setTextSize(PreferenciasManager.getTamFuenteNew());

			viewHolder.ie = convertView
					.findViewById(R.id.img_enlace);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		//COLOR POR DEFECTO
		viewHolder.txtUds.setTextColor(Color.BLACK);
		viewHolder.txtDescripcion.setTextColor(Color.BLACK);
		viewHolder.txtPvp.setTextColor(Color.BLACK);
		//QUITAMOS LINEAS TACHADAS
		viewHolder.txtUds.setPaintFlags(viewHolder.txtUds.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
		viewHolder.txtDescripcion.setPaintFlags(viewHolder.txtUds.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
		viewHolder.txtPvp.setPaintFlags(viewHolder.txtUds.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
		//QUITAMOS NEGRITA
		viewHolder.txtDescripcion.setTypeface(null, Typeface.NORMAL);
		//OCULTAMOS ICONO
		viewHolder.ie.setVisibility(View.GONE);

		Double unidades;
		String unidadesMostrar = "";
		String descripcion = "";
		String total = "";

		String tipo_articulo;
		String obs;

		try {



			JSONObject linea = Inicio.gb.getLineasVisiblesComanda().get(
					position);
			if (linea.has("SELECCIONADA")) {
				convertView.setBackgroundColor(Color.YELLOW);
			} else {
				convertView.setBackgroundColor(Color.WHITE);
			}

			descripcion = linea.getString("DESCRIPCION").trim();
			// AÑADIDA LAS OBSERVACIONES POR LINEA
			obs = linea.getString("OBSERVACION").trim();
			if ((!obs.equals("-")) && (!obs.equals(""))) {
				descripcion += "\n<<" + obs + ">>";
			}

			tipo_articulo = linea.getString("T");

			switch (linea.getInt("TIPO")){
                case Constantes.tipo_linea_nueva:
                    viewHolder.txtUds.setTypeface(null, Typeface.BOLD);
                    viewHolder.txtDescripcion.setTypeface(null, Typeface.BOLD);
                    viewHolder.txtPvp.setTypeface(null, Typeface.BOLD);
			        break;
                case Constantes.linea_nueva_menu_maestro:
                    viewHolder.txtUds.setTypeface(null, Typeface.BOLD);
                    viewHolder.txtDescripcion.setTypeface(null, Typeface.BOLD);
                    viewHolder.txtPvp.setTypeface(null, Typeface.BOLD);
                    break;
                case Constantes.linea_nueva_menu_detalle:
                    viewHolder.ie.setVisibility(View.VISIBLE); //ICONO DE SUBMENÚ
                    viewHolder.txtUds.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtDescripcion.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtPvp.setTypeface(null, Typeface.NORMAL);
                    break;
                case Constantes.linea_recibida_menu_detalle:
                    viewHolder.ie.setVisibility(View.VISIBLE);
                    viewHolder.txtUds.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtDescripcion.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtPvp.setTypeface(null, Typeface.NORMAL);
                    break;
                case Constantes.tipo_linea_recibida:
                    viewHolder.txtUds.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtDescripcion.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtPvp.setTypeface(null, Typeface.NORMAL);
                    break;
                case Constantes.linea_recibida_menu_maestro:
                    viewHolder.txtUds.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtDescripcion.setTypeface(null, Typeface.NORMAL);
                    viewHolder.txtPvp.setTypeface(null, Typeface.NORMAL);
                    break;
            }

			if (!tipo_articulo.equals("H")) { // si no es un texto libre
				viewHolder.txtDescripcion.setGravity(Gravity.CENTER_VERTICAL);
				viewHolder.txtDescripcion.setGravity(Gravity.START);

				unidades = linea.getDouble("UNID");

				if (unidades == 0.25) {
					unidadesMostrar = "1/4";
				} else if (unidades == 0.5) {
					unidadesMostrar = "1/2";
				} else if (unidades == 0.75) {
					unidadesMostrar = "3/4";
				} else if (unidades < 0) {
					unidadesMostrar = Formateador.formatearUds(unidades); //  Linea tachada
					viewHolder.txtUds.setTextColor(Color.RED);
					viewHolder.txtDescripcion.setTextColor(Color.RED);
					viewHolder.txtPvp.setTextColor(Color.RED);
					viewHolder.txtUds.setPaintFlags(viewHolder.txtUds.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					viewHolder.txtDescripcion.setPaintFlags(viewHolder.txtUds.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					viewHolder.txtPvp.setPaintFlags(viewHolder.txtUds.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				} else {
					unidadesMostrar = Formateador.formatearUds(unidades);

				}
				unidadesMostrar += " x";

				total = Formateador.formatearImporte(linea.getDouble("TOTAL")) + "";
				if (linea.has("TARIFA")
						&& (linea.getString("TARIFA").equals("0"))) {
					total = "0,00 €";
				} else {
					if (!total.equals("")) {
						total = total + " €";
					}
				}

			} else {
				//viewHolder.txtDescripcion.setGravity(Gravity.CENTER);
				viewHolder.txtDescripcion.setTextColor(Color.BLUE);
				/*
				 * CAMBIO PARA TEXTOS LIBRES CON PRECIO
				 */
				unidades = linea.getDouble("UNID");

				if (unidades<0){
					viewHolder.txtUds.setTextColor(Color.RED);
					viewHolder.txtDescripcion.setTextColor(Color.RED);
					viewHolder.txtPvp.setTextColor(Color.RED);
					viewHolder.txtUds.setPaintFlags(viewHolder.txtUds.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					viewHolder.txtDescripcion.setPaintFlags(viewHolder.txtUds.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					viewHolder.txtPvp.setPaintFlags(viewHolder.txtUds.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				}else if (unidades>1){ //POR AHORA PASAMOS DE LOS DECIMALES
					unidadesMostrar = Formateador.formatearUds(unidades);
					unidadesMostrar += " x";
				}



				if (!linea.getString("TOTAL").equals("0.0")
						&& !linea.getString("TOTAL").equals("")
						&& !linea.getString("TOTAL").equals("0")
						&& !linea.getString("TOTAL").equals("0.00")) {
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
			Log.e("ADT COMANDA", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}

		viewHolder.txtUds.setText(unidadesMostrar);
		viewHolder.txtDescripcion.setText(descripcion);
		viewHolder.txtPvp.setText(total);
		return (convertView);
	}
}
