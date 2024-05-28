package com.atecresa.cocina;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.Sistema;
import com.atecresa.util.Formateador;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class Ad_linea_comanda_cocina extends ArrayAdapter {

    AppCompatActivity context;
    ArrayList<Linea> listaPlatos;
    int positionComanda; // Para saber a que comanda pertenezco para modificar
    // booleano mesacompleta

    Intent intent; // mensaje para la activity
    public static final String BROADCAST_ACTION = "receptor";

    @SuppressWarnings("unchecked")
    public Ad_linea_comanda_cocina(AppCompatActivity context, ArrayList<Linea> lista,
                                   int _positionComanda) {
        super(context, R.layout.item_plato_cocina_compatible, lista);
        this.listaPlatos = lista;
        this.context = context;
        this.positionComanda = _positionComanda;
        intent = new Intent(BROADCAST_ACTION);
    }

    static class ViewHolder {

        CheckBox c;
        TextView t;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            if (Sistema.usarElementosVisualesCompatibles()) {
                convertView = inflater.inflate(R.layout.item_plato_cocina_compatible, parent,false);
            }else{
                convertView = inflater.inflate(R.layout.item_plato_cocina_v2, parent,false);
            }

            viewHolder = new ViewHolder();
            viewHolder.c = convertView
                    .findViewById(R.id.checkPlatoTv);
            viewHolder.t = convertView
                    .findViewById(R.id.txt_texto_libre);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.c.setVisibility(View.VISIBLE);
            viewHolder.t.setVisibility(View.GONE);
            viewHolder.c.setPaintFlags(0);
        }

        String textoPlato;
        try {
            Linea plato = listaPlatos.get(position);

            if (plato.getTipo().equals("H")) {
                textoPlato = plato.getArticulo();

                // OCULTAMOS CHECK, MOSTRRAMOS TEXTVIEW E IMAGEN
                viewHolder.c.setClickable(false);
                viewHolder.c.setVisibility(View.GONE);
                viewHolder.t.setVisibility(View.VISIBLE);
                viewHolder.t.setTextColor(Color.BLUE);
                // Demasiado peque�o esto
                viewHolder.t.setTextSize(PreferenciasManager.getTamFuenteNew());
                viewHolder.t.setText(textoPlato);

            } else {
                textoPlato = Formateador.formatearUds(Double.parseDouble(plato
                        .getUds())) + " x " + plato.getArticulo();
                if (!plato.getObs().equals(""))
                    textoPlato += "\n<<" + plato.getObs() + ">>";
                viewHolder.c.setTextColor(Color.BLACK);
                //TODO BUSCAR ICONO GENÉRICO QUE SE ENTIENDA
                viewHolder.c.setButtonDrawable(R.drawable.ic_crop_square);
                switch (plato.getEstado()) {
                    case Constantes.linea_cocina_recibida_hecha:
                        viewHolder.c.setChecked(true);
                        viewHolder.c.setPaintFlags(viewHolder.c.getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG);
                        viewHolder.c.setBackgroundColor(Color.GREEN);
                        viewHolder.c.setButtonDrawable(R.drawable.ic_check_circle);
                        break;
                    case Constantes.linea_cocina_recibida_sin_hacer:
                        viewHolder.c.setChecked(false);
                        if (Double.parseDouble(plato.getUds()) < 0)
                            //ANULACIONES
                            viewHolder.c.setBackgroundColor(Color.RED);
                        else {
                            viewHolder.c.setBackgroundColor(Color.WHITE);
                            viewHolder.c.setPaintFlags(0);
                        }
                        break;
                    case Constantes.linea_cocina_no_notificada_hecha:
                        viewHolder.c.setChecked(true);
                        viewHolder.c.setBackgroundColor(Color.YELLOW);
                        viewHolder.c.setButtonDrawable(R.drawable.ic_check_circle);
                        break;
                    case Constantes.linea_cocina_no_notificada_sin_hacer:
                        viewHolder.c.setChecked(false);
                        viewHolder.c.setBackgroundColor(Color.YELLOW);
                        viewHolder.c.setButtonDrawable(R.drawable.ic_check_circle);
                        break;
                    case Constantes.linea_cocina_en_preparacion:
                        viewHolder.c.setChecked(false);
                        viewHolder.c.setBackgroundColor(Color.BLUE);
                        viewHolder.c.setTextColor(Color.WHITE);
                        viewHolder.c.setButtonDrawable(R.drawable.ic_update_24dp);
                        break;
                }
                //TODO Experimento para no usar iconos

                //TODO. TENDREMOS ESTADO INTERMEDIO
                viewHolder.c.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        CheckBox cb = (CheckBox) v;
                        Linea l = (Linea) cb.getTag();


                        if (cb.isChecked()) {
                            // SI MARCAMOS
                            switch (l.getEstado()) {
                                //LINEAS RECIBIDAS SIN HACER
                                case Constantes.linea_cocina_recibida_sin_hacer:
                                    if (PreferenciasManager.confirmarPlatoDosPasos()) {
                                        //TODO ESTE PASO LO HAREMOS OPCIONAL IF PREFERENCIAS.USAR_PLATOS_PREPARADOS. SI ES FALSE LO PASAMOS DIRECTAMENTE A LINEA COCINA NO NOTIFICADA HECHA
                                        //PARÁMETRO POR DEFECTO EN FALSE
                                        l.setEstado(Constantes.linea_cocina_en_preparacion);
                                        cb.setChecked(false);
                                        cb.setBackgroundColor(Color.BLUE);
                                        cb.setTextColor(Color.WHITE);
                                        //TODO Meter lineas en nueva colección de platos en preparación
                                        //Inicio.gb.getCocina().addLineaSinNotificar(l,false);
                                        Inicio.gb.getCocina().addPlatoPreparacion(l);
                                        cb.setButtonDrawable(R.drawable.ic_update_24dp);
                                    } else {
                                        l.setEstado(Constantes.linea_cocina_no_notificada_hecha);
                                        Inicio.gb.getCocina().deletePlatoPreparado(l.getIddetalle());
                                        Inicio.gb.getCocina().addLineaSinNotificar(l, true);
                                        cb.setBackgroundColor(Color.YELLOW);
                                        cb.setTextColor(Color.BLACK);
                                        cb.setButtonDrawable(R.drawable.ic_check_circle);
                                    }
                                    break;
                                //LINEAS RECIBIDAS DESMARCADAS PREVIAMENTE
                                case Constantes.linea_cocina_no_notificada_sin_hacer:
                                    l.setEstado(Constantes.linea_cocina_recibida_hecha);
                                    cb.setPaintFlags(cb.getPaintFlags()
                                            | Paint.STRIKE_THRU_TEXT_FLAG);
                                    cb.setBackgroundColor(Color.GREEN);
                                    cb.setTextColor(Color.BLACK);
                                    cb.setButtonDrawable(R.drawable.ic_check_circle);
                                    break;
                                //LINEAS EN PREPARACIÓN
                                case Constantes.linea_cocina_en_preparacion:
                                    l.setEstado(Constantes.linea_cocina_no_notificada_hecha);
                                    Inicio.gb.getCocina().deletePlatoPreparado(l.getIddetalle());
                                    Inicio.gb.getCocina().addLineaSinNotificar(l, true);
                                    cb.setBackgroundColor(Color.YELLOW);
                                    cb.setTextColor(Color.BLACK);
                                    cb.setButtonDrawable(R.drawable.ic_check_circle);
                                    //PRUEBA BORRAMOS DE PLATOS EN PREPARACIÓN
                                    break;
                            }

                        } else {
                            // SI DESMARCAMOS
                            cb.setPaintFlags(0);
                            switch (l.getEstado()) {
                                case Constantes.linea_cocina_recibida_hecha:
                                    Inicio.gb.getCocina().deletePlatoPreparado(l.getIddetalle());
                                    cb.setBackgroundColor(Color.YELLOW);
                                    l.setEstado(Constantes.linea_cocina_no_notificada_sin_hacer);
                                    Inicio.gb.getCocina().addLineaSinNotificar(l,
                                            false); // NOTIFICAR
                                    cb.setButtonDrawable(R.drawable.ic_crop_square);

                                    break;
                                case Constantes.linea_cocina_no_notificada_hecha:
                                    cb.setBackgroundColor(Color.WHITE);
                                    l.setEstado(Constantes.linea_cocina_recibida_sin_hacer);
                                    Inicio.gb.getCocina().addLineaSinNotificar(l,
                                            false);
                                    cb.setButtonDrawable(R.drawable.ic_crop_square);
                                    break;
                                case Constantes.linea_cocina_en_preparacion:
                                    l.setEstado(Constantes.linea_cocina_no_notificada_hecha);
                                    Inicio.gb.getCocina().deletePlatoPreparado(l.getIddetalle());
                                    Inicio.gb.getCocina().addLineaSinNotificar(l, true);
                                    cb.setBackgroundColor(Color.YELLOW);
                                    cb.setTextColor(Color.BLACK);
                                    cb.setButtonDrawable(R.drawable.ic_check_circle);
                                    //PRUEBA BORRAMOS DE PLATOS EN PREPARACIÓN
                                    break;
                            }
                            Inicio.gb.getCocina().getListaComandaCocinas()
                                    .get(positionComanda).setCompleta(false);
                        }

                        /*
                         * MENSAJE A LA ACTIVITY PARA QUE SE ACTUALICE
                         */
                        //TODO Cuidado con esta lineas
                        intent.putExtra("mensaje",
                                Constantes.mensaje_actualizacion_acumulados);
                        context.sendBroadcast(intent);
                    }

                });

            }
            viewHolder.c.setTextSize(PreferenciasManager.getTamFuenteNew());
            viewHolder.c.setText(textoPlato);
            viewHolder.c.setTag(plato);
            //TODO PRUEBA PARA TRABAJAR SIN ICONOS Y APROVECHAR EL ESPACIO
        } catch (Exception e) {
            Log.e("ADAPTER LINEA COCINA", "Linea "+ Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + e.getMessage());
        }

        return (convertView);
    }

}
