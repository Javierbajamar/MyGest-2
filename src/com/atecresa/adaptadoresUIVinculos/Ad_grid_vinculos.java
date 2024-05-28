package com.atecresa.adaptadoresUIVinculos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.util.DesignM;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("rawtypes")
public class Ad_grid_vinculos extends ArrayAdapter {
	
	AppCompatActivity context;

	@SuppressWarnings("unchecked")
	public Ad_grid_vinculos(AppCompatActivity context) {

		super(context, R.layout.comanda_item_grid_v2, Inicio.gb.getVinculos().getListaArticulosGrupo());
		this.context = context;

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.comanda_item_grid_v2, null);
		
		TextView tvArt = item.findViewById(R.id.txtSuperior);
		tvArt.setTextSize(PreferenciasManager.getTamFuenteNew());
		JSONObject articulo;
		
		int colorBoton;
		int colorTexto;
		
		articulo=Inicio.gb.getVinculos().getListaArticulosGrupo().get(position);
		try {
			tvArt.setText(articulo.getString("DES"));
			if (articulo.getInt("ID")==-1){
				colorTexto = TpvConfig.getAppBackColor();
				colorBoton = TpvConfig.getAppForecolor();
			}else{
				colorTexto = DesignM.genColor(Integer.parseInt(articulo.getString("FOREC")));
				colorBoton = DesignM.genColor(Integer.parseInt(articulo.getString("BACKC")));
			}
			
			tvArt.setTextColor(colorTexto);
			tvArt.setBackgroundColor(colorBoton);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return item;
		
	}

}
