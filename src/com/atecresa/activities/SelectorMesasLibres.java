package com.atecresa.activities;

/*
 * Selector de mesas libres
 * Con tabs pero sin menu arriba
 */

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBar.Tab;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.atecresa.adaptadoresUIComanda.Ad_grid_mesas;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.notificaciones.Notifica;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.PreferenciasManager;
import com.atecresa.preferencias.Sistema;
import com.atecresa.preferencias.TpvConfig;

public class SelectorMesasLibres extends AppCompatActivity {

	@Override
	protected void onResume() {
		Inicio.setContext(SelectorMesasLibres.this);
		super.onResume();
	}

	public GridView gridMesas;
	public Ad_grid_mesas adGridMesas;
	private int positionRango = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_mesa_activity);

		Inicio.setPantallaActual(this.getClass().getName());

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(TpvConfig.getAppBackColor());
        actionBar.setBackgroundDrawable(colorDrawable);
		actionBar.setTitle(getString(R.string.subtitulo_action_mesas_libres).toUpperCase());
		actionBar.setDisplayHomeAsUpEnabled(true);

		LinearLayout layoutBase = findViewById(R.id.layout_selector_mesa);
		//layoutBase.setBackgroundColor(TpvConfig.getAppBackColor());

		for (int i = 0; i < Inicio.gb.getlistaRangos().size(); i++) {
			try {
				String nombreRango = Inicio.gb.getlistaRangos().get(i)
						.getString("TX");

				Tab tab = actionBar.newTab().setText(nombreRango)
						.setTabListener(new TabListener(this, nombreRango));
				actionBar.addTab(tab);
			} catch (Exception e) {
				Log.e("SELECTOR MESAS LIBRES",
						"Linea "
								+ Thread.currentThread().getStackTrace()[2]
										.getLineNumber() + ": "
								+ e.getMessage());
			}

		}

		gridMesas = findViewById(R.id.gridMesas);
		gridMesas.setNumColumns(Integer.parseInt(PreferenciasManager.getNumColumnas()));

		adGridMesas = new Ad_grid_mesas(this);
		gridMesas.setAdapter(adGridMesas);
		gridMesas.setOnItemClickListener(ListenerGridMesas);
		if (!Inicio.gb.isMesasCargadas()) {
			Notifica.mostrarToastSimple(SelectorMesasLibres.this,
					getString(R.string.mensaje_no_mesas_libres) + " "
							+ Inicio.gb.getTextoRango());
		}

	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemClickListener ListenerGridMesas = (adapterView, v, position, id) -> {

        CambiarMesas cm = new CambiarMesas();
        cm.execute(v.getId() + "");

    };

	private class TabListener implements ActionBar.TabListener {

        public TabListener(AppCompatActivity activity, String tag) {

        }

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (Inicio.hayRed()|| Sistema.isEmulator()) {
				ActualizarGridMesas updateGrid = new ActualizarGridMesas();
				updateGrid.execute(tab.getPosition());
				SelectorMesasLibres.this.positionRango = tab.getPosition();
			} else {
				Notifica.mostrarToast(SelectorMesasLibres.this,
						getString(R.string.mensaje_error_conexion), 2,
						Toast.LENGTH_LONG);
				finish();
			}

		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}
	}

	class ActualizarGridMesas extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			Inicio.gb.cargarJSONFunciones();
			Inicio.gb.cargarMesas(params[0], true);
			Inicio.gb.ejecutarfuncion();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			actualizarGridMesas();
			if (!Inicio.gb.isMesasCargadas()) {
				Notifica.mostrarToastSimple(SelectorMesasLibres.this,
						getString(R.string.mensaje_no_mesas_libres) + " "
								+ Inicio.gb.getTextoRango());
			}

		}
	}

	public void actualizarGridMesas() {

		adGridMesas = new Ad_grid_mesas(SelectorMesasLibres.this);
		adGridMesas.setNotifyOnChange(true);
		gridMesas.setAdapter(adGridMesas);
		gridMesas.setOnItemClickListener(ListenerGridMesas);

	}

	class CambiarMesas extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			Inicio.gb.cargarJSONFunciones();
			Inicio.gb.cambiarMesa(params[0]);
			Inicio.gb.desbloquearMesa(Inicio.gb.getMesa());
			Inicio.gb.bloquearMesa(params[0]);
			Inicio.gb.ejecutarfuncion();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (Inicio.gb.getError() == Constantes.app_OK) {
				if (Inicio.gb.getResultado().equals("Ok")) {
					Notifica.mostrarToast(SelectorMesasLibres.this,
							getString(R.string.mensaje_mesa_editada), 1,
							Toast.LENGTH_SHORT);
					finish();
				} else {
					Notifica.mostrarToast(SelectorMesasLibres.this,
							getString(R.string.mensaje_mesa_no_editada), 2,
							Toast.LENGTH_SHORT);
					finish();
				}

			} else {
				Notifica.mostrarMensajeEstado(SelectorMesasLibres.this);
				finish();
			}

		}
	}

}
