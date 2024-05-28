package com.atecresa.print;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.util.Formateador;


public class ActionBarDialog extends DialogFragment {

    private Context ctx;

    public void setContext(Context _ctx) {
        ctx = _ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        View v = inflater.inflate(R.layout.dialog_cobros, container, false);

        //TEXTVIEWS
        TextView tvImporte = v.findViewById(R.id.tvImporte);
        tvImporte.setText(Formateador.formatearImporteString(args.getString("importe"))+" €");
        TextView tvEntrega = v.findViewById(R.id.tvEntrega);
        if (!args.getString("entrega").equals(""))
            tvEntrega.setText(Formateador.formatearImporteString(args.getString("entrega"))+" €");
        else
            tvEntrega.setText(Formateador.formatearImporteString(Inicio.gb.getMesaActual().getTotal())+" €");
        TextView tvDevolucion = v.findViewById(R.id.tvDevolucion);
        tvDevolucion.setText(Formateador.formatearImporteString(args.getString("devolucion")) +" €");

        Button btCerrar = v.findViewById(R.id.btCerrar);
        btCerrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        //TOOLBAR
        Toolbar toolbar = v.findViewById(R.id.toolbarCobros);
        /*
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        */
        //toolbar.inflateMenu(R.menu.menu_comanda_actionbar); //ESTO MENÚ NO VAMOS A SACARLO PERO ES DE PRUEBA. HAREMOS UNO NUEVO
        toolbar.setTitle(args.getString("forma_de_pago"));
        toolbar.setSubtitle("Forma de pago");
        //TODO Implementar aquí el click del botón para cerrar el dialog
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Intent i = new Intent();
        i.putExtra("finalizar", Inicio.gb.sePuedeFinalizarTicket());
        ((Activity) ctx).setResult(Activity.RESULT_OK, i);
        ((Activity) ctx).finish();
    }
}