package com.atecresa.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.adaptadoresUIPedidos.Ad_lineas_pedido;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.notificaciones.Notifica;
import com.atecresa.preferencias.TpvConfig;

public class Pedido extends AppCompatActivity {

	@Override
	protected void onResume() {
		Inicio.setContext(Pedido.this);
		super.onResume();
	}

	ActionBar actionBar;
	ListView listaLineas;
	View layoutPedido;
	Ad_lineas_pedido alp;
	ImageButton btConfirmar;
	ImageButton btCancelar;

	EjecutarPedido ep;

	Intent i;

	String numPedido = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedido);

		layoutPedido = findViewById(R.id.layout_pedido);

		i = new Intent(Pedido.this, Pedido.class);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		if (Inicio.gb.cargarPedidoMyorder(bundle.getInt("position"))) {
			this.numPedido = Inicio.gb.getNumPedido();
			actionBar = getSupportActionBar();
			assert actionBar != null;
			actionBar.setTitle(getString(R.string.titulo_pedido) + " "
					+ this.numPedido);
			actionBar.setSubtitle(getString(R.string.txt_mesa) + " "
					+ Inicio.gb.getMesaPedido() + " "
					+ getString(R.string.txt_total) + " "
					+ Inicio.gb.getTotalPedido() + " €");

			ColorDrawable colorDrawable = new ColorDrawable();
			colorDrawable.setColor(TpvConfig.getAppBackColor());
			actionBar.setBackgroundDrawable(colorDrawable);
			int titleId = Resources.getSystem().getIdentifier(
					"action_bar_title", "id", "android");
			TextView yourTextView = findViewById(titleId);
			yourTextView.setTextColor(TpvConfig.getAppForecolor());

			// layoutPedido.setBackgroundDrawable(colorDrawable);
			// LISTVIEW Y ADAPTADOR
			this.listaLineas = findViewById(R.id.listaPedido);
			this.alp = new Ad_lineas_pedido(this);
			listaLineas.setAdapter(alp);

			this.btConfirmar = findViewById(R.id.btConfirmarPedido);
			this.btConfirmar.setOnClickListener(arg0 -> {
                ep = new EjecutarPedido();
                ep.execute(1);
            });

			this.btCancelar = findViewById(R.id.btCancelarPedido);
			this.btCancelar.setOnClickListener(arg0 -> {
                ep = new EjecutarPedido();
                ep.execute(2);
            });

		} else {
			Notifica.mostrarToast(Pedido.this,
					getString(R.string.mensaje_error_cargando_pedido), 2,
					Toast.LENGTH_SHORT);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_selector_mesa, menu);
		menu.getItem(0).setVisible(false); // OCULTAMOS ITEM DE PEDIDOS
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	private class EjecutarPedido extends AsyncTask<Integer, Void, Void> {

		int funcion = 0;

		@Override
		protected void onPreExecute() {
			Notifica.mostrarProgreso(Pedido.this,"",
					getString(R.string.dialog_enviando) + "...", true);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			funcion = params[0];
			Inicio.gb.cargarJSONFunciones();
			switch (funcion) {
			case 1:
				Inicio.gb.confirmarPedido();
			case 2:
				Inicio.gb.cancelarPedido();
			}
			Inicio.gb.consultarPedidosMyorder();
			Inicio.gb.ejecutarfuncion();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Notifica.mostrarProgreso(Pedido.this,"", "", false);
			if (Inicio.gb.getResultado().equals("Ok")) {
				switch (funcion) {
				case 1:
					Notifica.mostrarToast(Pedido.this,
							getString(R.string.mensaje_pedido_confirmado), 1,
							Toast.LENGTH_SHORT);
					break;
				case 2:
					Notifica.mostrarToast(Pedido.this,
							getString(R.string.mensaje_pedido_cancelado), 1,
							Toast.LENGTH_SHORT);
					break;
				}

			} else {
				Notifica.mostrarMensajeEstado(Pedido.this);
			}
			setResult(Activity.RESULT_OK, i);
			finish();
		}
	}

}
