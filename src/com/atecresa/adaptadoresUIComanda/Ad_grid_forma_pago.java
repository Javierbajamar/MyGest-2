package com.atecresa.adaptadoresUIComanda;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atecresa.application.R;
import com.atecresa.gestionCobros.cloud.FormaPago;

import java.util.ArrayList;

/**
 * Created by carlos on 14/02/2018.
 */

public class Ad_grid_forma_pago extends RecyclerView.Adapter<Ad_grid_forma_pago.ViewHolder> {

    private ArrayList<FormaPago> lista;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView tvDescripcion;


        public ViewHolder(View v) {
            super(v);
            cv = v.findViewById(R.id.item_cobro);
            tvDescripcion =  v.findViewById(R.id.tv_descripcion_cobro);
        }
    }

    public Ad_grid_forma_pago(ArrayList<FormaPago> myDataset) {
        lista = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Ad_grid_forma_pago.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cobro, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String tratamiento=lista.get(position).getTratamiento();
        if (tratamiento.equals("") || tratamiento.length() < 2)
            holder.tvDescripcion.setText(lista.get(position).getForma());
        else
            holder.tvDescripcion.setText(lista.get(position).getForma() + "\n" + "/" + tratamiento);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lista.size();
    }
}

