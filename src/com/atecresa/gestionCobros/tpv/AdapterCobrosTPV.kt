package com.atecresa.gestionCobros.tpv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.atecresa.application.R
import com.atecresa.gestionCobros.tpv.AdapterCobrosTPV.ViewHolder
import java.util.*


/**
 * Created by carlos on 14/02/2018.
 */

class AdapterCobrosTPV : RecyclerView.Adapter<ViewHolder>() {

    private var lista: ArrayList<FormaPagoTPV>? = null

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var cv: CardView = v.findViewById(R.id.item_cobro)
        internal var tvDescripcion: TextView = v.findViewById(R.id.tv_descripcion_cobro)
    }

    fun setLista(_listaFormaPagoTPVS: ArrayList<FormaPagoTPV>) {
        lista = _listaFormaPagoTPVS
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cobro, parent, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tratamiento = lista!![position].tratamiento
        if (tratamiento == "" || tratamiento.length < 2 || tratamiento == " ")
            holder.tvDescripcion.text = lista!![position].descripcion
        else
            holder.tvDescripcion.text = lista!![position].descripcion + "\n" + "/" + tratamiento

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return lista!!.size
    }
}

