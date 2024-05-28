package com.atecresa.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.atecresa.adaptadoresUIVinculos.Ad_grid_vinculos;
import com.atecresa.adaptadoresUIVinculos.Ad_linea_vinculos;
import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.TpvConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Vinculos extends AppCompatActivity {

	LinearLayout layoutBase;
	LinearLayout layoutComanda;
	LinearLayout layoutGrids;

	Ad_grid_vinculos adGridVinculos;
	GridView gridVinculos;

	ListView listaVinculos;
	Ad_linea_vinculos adlineaVinculos;

	ActionBar actionBar;

	TextView txtInfo;

	int numLinea = 0;

	MenuItem menuItem;

	Intent i;

	JSONObject art;

	String descripcionPlato = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.comanda_activity_content_v2);
		Inicio.setPantallaActual(this.getClass().getName());

		i = new Intent(Vinculos.this, Vinculos.class);

		actionBar = getSupportActionBar();
		ColorDrawable colorDrawable = new ColorDrawable();
		colorDrawable.setColor(TpvConfig.getAppBackColor());
		actionBar.setBackgroundDrawable(colorDrawable);

		adaptarUI();

		Inicio.gb.getVinculos().cargarGrupo();
		actionBar.setTitle(Inicio.gb.getVinculos().getNombreGrupo());

		this.adGridVinculos = new Ad_grid_vinculos(Vinculos.this);
		this.gridVinculos = findViewById(R.id.gridFamilias);
		this.gridVinculos.setAdapter(adGridVinculos);
		this.gridVinculos.setOnItemClickListener(listenerGridVinculos);


		if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("isBusqueda")) {
			art = Inicio.gb.getResultadosBusqueda().get(getIntent().getExtras().getInt("position"));
		} else {
			art = Inicio.gb.getArticulos().get(getIntent().getExtras().getInt("position"));
		}
		this.txtInfo = findViewById(R.id.txtInfoComanda);

		try {
			art.put("NUMLINEA", 0);
			art.put("NUMLINEAREF", "");
			art.put("TARIFA", Inicio.gb.getVinculos().getTarifa());
			Inicio.gb.getVinculos().insertarEnPrecomanda(art, true);
			this.descripcionPlato = art.getString("DES");
			this.txtInfo.setText(getTextoUnidades());
			actionBar.setSubtitle(this.descripcionPlato);
			numLinea++;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.listaVinculos = findViewById(R.id.listComanda);
		this.adlineaVinculos = new Ad_linea_vinculos(this);
		this.listaVinculos.setAdapter(adlineaVinculos);
	}

	@Override
	protected void onResume() {
		Inicio.setContext(Vinculos.this);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// Inicio.gb.borrarVinculos();
		Inicio.gb.getVinculos().getListaPreComanda().clear();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_action_vinculos, menu);
		menuItem = menu.getItem(1);
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			cancelarVinculos();
			return false;
		case KeyEvent.KEYCODE_HOME:
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_vinculos_cancelar:
			cancelarVinculos();
			return true;
		case R.id.action_vinculos_confirmar:

            confirmarVinculosAComanda();
			return true;
		default:
			i.putExtra("activity", Constantes.activity_vinculos);
			setResult(Activity.RESULT_CANCELED, i);
			finish();
			return super.onOptionsItemSelected(item);
		}
	}

	private void confirmarVinculosAComanda(){

        try {
            Inicio.gb.getVinculos().pasarAComanda();
            Inicio.gb.getVinculos().vaciarDatos();
            i.putExtra("descripcionPlato", descripcionPlato);
            i.putExtra("activity", Constantes.activity_vinculos);
            setResult(AppCompatActivity.RESULT_OK, i);
        } catch (Exception e) {
            Log.e("ACTIVITY VÍNCULOS",
                    "Linea "
                            + Thread.currentThread().getStackTrace()[2]
                            .getLineNumber() + ": "
                            + e.getMessage());
        } finally {
            finish();
        }
	}

	AdapterView.OnItemClickListener listenerGridVinculos = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View v,
				int position, long id) {

			JSONObject art = Inicio.gb.getVinculos().getListaArticulosGrupo()
					.get(position);

			try {
				if (!art.getString("ID").equals("-1")) {
					art.put("NUMLINEA", numLinea);
					numLinea++;
					Inicio.gb.getVinculos().insertarEnPrecomanda(art, false);
					adlineaVinculos.notifyDataSetChanged();

				} else {
					Inicio.gb.getVinculos().saltarVinculo();
				}
				if (Inicio.gb.getVinculos().apilarVinculos(art)) {
					Inicio.gb.getVinculos().cargarGrupo();
				} else {
					if (Inicio.gb.getVinculos().cargarGrupo()) {
						actionBar.setTitle(Inicio.gb.getVinculos()
								.getNombreGrupo());
						txtInfo.setText(getTextoUnidades());
					} else {
                        //OPCIÓN 1 CONFIRMAR PEDIDO ANTES

                        /*
						layoutGrids.setVisibility(View.GONE);
						menuItem.setVisible(true);
						actionBar.setTitle(getString(R.string.txt_finalizar));
						actionBar.setSubtitle(null);
						*/

                        //OPCIÓN 2 PASAR DIRECTAMENTE A COMANDA SIN CONFIRMAR
						confirmarVinculosAComanda();
					}

				}
				adGridVinculos.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	};

	private void cancelarVinculos() {

		AlertDialog.Builder alert = new AlertDialog.Builder(Vinculos.this);

		alert.setTitle(getString(R.string.dialog_pedido_en_curso));
		alert.setMessage(getString(R.string.dialog_cancelar_vinculo));

		alert.setPositiveButton(getString(R.string.dialog_ok),
                (dialog, whichButton) -> {
                    Inicio.gb.getVinculos().vaciarDatos();
                    i.putExtra("activity", Constantes.activity_vinculos);
                    setResult(Activity.RESULT_CANCELED, i);
                    finish();
                });

		alert.setNegativeButton(getString(R.string.dialog_cancelar),
                (dialog, whichButton) -> {
                    // Canceled.
                });

		alert.show();
	}

	private void adaptarUI() {

		int pantalla = getResources().getConfiguration().orientation;
		layoutBase = findViewById(R.id.layout_principal);
		layoutComanda = findViewById(R.id.layout_principal_comanda);
		layoutGrids = findViewById(R.id.layout_grids);
		//layoutGrids.setBackgroundColor(TpvConfig.getAppBackColor());

		txtInfo = findViewById(R.id.txtInfoComanda);
		txtInfo.setBackgroundColor(TpvConfig.getAppBackColor());

		if (pantalla == 1) {
			layoutBase.setOrientation(LinearLayout.VERTICAL);
			layoutComanda.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 4));
			layoutGrids.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 6));
		} else {
			layoutBase.setOrientation(LinearLayout.HORIZONTAL);
			layoutComanda.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
			layoutGrids.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		}

	}

	private String getTextoUnidades() {
		return getString(R.string.txt_menu_escoja) + " "
				+ Inicio.gb.getVinculos().getUnidadesGrupo() + " "
				+ getString(R.string.txt_menu_unidades_abreviadas);
	}

}
