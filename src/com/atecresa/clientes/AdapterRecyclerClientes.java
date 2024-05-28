package com.atecresa.clientes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.atecresa.application.R;

import java.util.List;

public class AdapterRecyclerClientes extends RecyclerView.Adapter<AdapterRecyclerClientes.ViewHolder>  {

    private final List<Clientes.Cliente> mValues;
    private final OnListInteractionListener mListener;

    public AdapterRecyclerClientes(List<Clientes.Cliente> items, OnListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_cliente_v2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //todo al pulsar sobre el item, tendremos al objeto completo por lo que podemos acceder desde la activity a todas sus propiedades
        holder.cliente = mValues.get(position);
        holder.tvCliente.setText(holder.cliente.getNombre().trim().toUpperCase());
        holder.tvTfno.setText("TFNO: "+holder.cliente.getTfno().trim());
        holder.tvEmail.setText(holder.cliente.getEmail().trim());
        holder.tvDir.setText("DIR: "+holder.cliente.getDir().trim());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onListFragmentInteraction(holder.mesa); //todo AQUÍ PODEMOS DEVOLVER EL OBJETO
                    mListener.onListInteraction(position);  //VAMOS A DEVOLVER EL POSITION PARA HACERLO MÁS GENÉRICO
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        TextView tvCliente;
        TextView tvTfno;
        TextView tvEmail;
        TextView tvDir;
        public Clientes.Cliente cliente;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvCliente = view.findViewById(R.id.txt_cliente);
            tvTfno= view.findViewById(R.id.tvTfno);
            tvEmail= view.findViewById(R.id.tvEmail);
            tvDir= view.findViewById(R.id.tvDir);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cliente.getNombre() + "'";
        }
    }

    //Interface para que el adaptador nos devuelva en el onclick la posición del objto o el objeto en si
    public interface OnListInteractionListener {
        void onListInteraction(int position);
    }
}
