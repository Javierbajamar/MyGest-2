package com.atecresa.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.adaptadoresUIPedidos.Ad_grid_pedidos;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.TpvConfig;

public class SelectorPedidos extends AppCompatActivity {

	ActionBar actionBar;

	GridView gridPedidos;
	Ad_grid_pedidos adGridPedidos;

	private static final int REQUEST_TEXT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_mesa_activity);

		actionBar = getSupportActionBar();
		actionBar.setTitle(getString(R.string.actionbar_titulo_pedidos_myorder));
		actionBar.setSubtitle(getString(R.string.actionbar_subtitulo_pedidos_myorder));

		ColorDrawable colorDrawable = new ColorDrawable();
		colorDrawable.setColor(TpvConfig.getAppBackColor());
		actionBar.setBackgroundDrawable(colorDrawable);
		LinearLayout layoutBase = findViewById(R.id.layout_selector_mesa);
		layoutBase.setBackgroundColor(TpvConfig.getAppBackColor());

		gridPedidos = findViewById(R.id.gridMesas);
		adGridPedidos = new Ad_grid_pedidos(SelectorPedidos.this);
		gridPedidos.setAdapter(adGridPedidos);
		gridPedidos.setOnItemClickListener(listenerGridPedidos);

		gridPedidos = findViewById(R.id.gridMesas);
		gridPedidos.setNumColumns(Integer.parseInt(PreferenciasManager.getNumColumnas()));
		/*
		 * Elimina la notificacion de la barra superior
		 */
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(1);

	}

	@Override
	public void onResume() {
		super.onResume();
		Inicio.setContext(SelectorPedidos.this);
		Inicio.setPantallaActual(this.getClass().getName());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_selector_mesa, menu);
		menu.getItem(0).setVisible(false); // OCULTAMOS ITEM DE PEDIDOS
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TEXT) {
			if (resultCode == Activity.RESULT_OK) {
				if (Inicio.gb.hayPedidosSinConfirmar()) {
					adGridPedidos.notifyDataSetChanged();
				} else {
					finish();
				}
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

	AdapterView.OnItemClickListener listenerGridPedidos = (adapterView, v, position, id) -> abrirPedido(position);

	private void abrirPedido(int position) {
		Intent pedidoI = new Intent(SelectorPedidos.this, Pedido.class);
		pedidoI.putExtra("position", position);
		this.startActivityForResult(pedidoI, REQUEST_TEXT);
	}

}
