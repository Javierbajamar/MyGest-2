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
import com.atecresa.preferencias.TpvConfig;
import com.atecresa.util.DesignM;

import org.json.JSONObject;

public class Ad_grid_operadores extends ArrayAdapter<JSONObject> {

    AppCompatActivity context;

    public Ad_grid_operadores(AppCompatActivity context) {

        super(context, R.layout.login_item_operador_v2, Inicio.gb.getOperadores());
        this.context = context;

    }

    static class ViewHolder {
        TextView tvDescripcionOP;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        try {
            if (convertView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                convertView = inflater.inflate(R.layout.login_item_operador_v2, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvDescripcionOP = convertView
                        .findViewById(R.id.txtSuperior);
                viewHolder.tvDescripcionOP.setTextSize(PreferenciasManager.getTamFuenteNew());
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            int colorBoton;
            int colorTexto;
            JSONObject operador = Inicio.gb.getOperadores().get(position);

            if (Integer.parseInt(Inicio.gb
                    .getOperadores().get(position).getString("FOREC"))== TpvConfig.getAppBackColor()){
                colorTexto = DesignM.genColor(Integer.parseInt(Inicio.gb
                        .getOperadores().get(position).getString("FOREC")));
                colorBoton = DesignM.genColor(Integer.parseInt(Inicio.gb
                        .getOperadores().get(position).getString("BACKC"))); // FOREC
                viewHolder.tvDescripcionOP
                        .setText(operador.getString("DES").trim());
                viewHolder.tvDescripcionOP.setTextColor(colorTexto);
                viewHolder.tvDescripcionOP.setBackgroundColor(colorBoton);
            }else{
                colorTexto = DesignM.genColor(Integer.parseInt(Inicio.gb
                        .getOperadores().get(position).getString("FOREC")));
                colorBoton = DesignM.genColor(Integer.parseInt(Inicio.gb
                        .getOperadores().get(position).getString("BACKC"))); // FOREC
                viewHolder.tvDescripcionOP
                        .setText(operador.getString("DES").trim());
                viewHolder.tvDescripcionOP.setTextColor(colorTexto);
                viewHolder.tvDescripcionOP.setBackgroundColor(colorBoton);
            }

            convertView.setId(operador.getInt("ID"));
        } catch (Exception e) {
            Log.e("ADT OPERADORES", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
        return (convertView);
    }

}