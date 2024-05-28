package com.atecresa.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.clientes.Adapter_Clientes;
import com.atecresa.clientes.Clientes;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.TpvConfig;

public class BuscadorClientes extends AppCompatActivity implements SearchView.OnQueryTextListener {
	
	ActionBar actionBar;

	Adapter_Clientes abc;
	ListView lista;

	View fondoBusqueda;
	TextView tvTituloBusqueda;

	Intent i;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscar_cliente);

		i = new Intent(BuscadorClientes.this, BuscadorClientes.class);

		actionBar = getSupportActionBar();
		ColorDrawable colorDrawable = new ColorDrawable();
		colorDrawable.setColor(TpvConfig.getAppBackColor());
		actionBar.setBackgroundDrawable(colorDrawable);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(android.R.drawable.ic_menu_revert);
		actionBar.setTitle(getString(R.string.txt_volver));

		this.fondoBusqueda = findViewById(R.id.layout_busqueda);
		this.fondoBusqueda.setBackgroundColor(TpvConfig.getAppBackColor());

		this.tvTituloBusqueda = findViewById(R.id.tv_titulo_busqueda);
		this.tvTituloBusqueda.setTextColor(TpvConfig.getAppForecolor());

		lista = findViewById(R.id.lista_clientes);
		//ESQUINAS REDONDEADAS
		lista.setBackgroundResource(R.drawable.lista_clientes);
		lista.setDividerHeight(10);

		Clientes.buscar("");
		this.mostrarResultadosClientes(); 

		setResult(Activity.RESULT_CANCELED, i);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_busqueda_articulos, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.requestFocus();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public boolean onQueryTextSubmit(String _cliente) {
		Clientes.buscar(_cliente);
		this.mostrarResultadosClientes(); 
		return false;
	}

	@Override
	public boolean onQueryTextChange(String _cliente) {
		Clientes.buscar(_cliente);
		this.mostrarResultadosClientes(); 
		return false;
	}
	
	private void mostrarResultadosClientes() {
		try {
			abc = new Adapter_Clientes(BuscadorClientes.this);
			lista.setAdapter(abc);
			lista.setOnItemClickListener(listenerListaBuscadorClientes);
		} catch (Exception e) {
			Log.e("BUSCADOR CLIENTES", "Linea "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ ": " + e.getMessage());
		}

	}

	AdapterView.OnItemClickListener listenerListaBuscadorClientes = (adapterView, v, position, id) -> {
        try {
            Intent res = new Intent();
            res.putExtra("activity", Constantes.activity_busqueda_clientes);
            setResult(Activity.RESULT_OK, res);
            Inicio.gb.getMesaActual().setCliente(Clientes.ITEMS_BUSQUEDA.get(position));
            BuscadorClientes.this.finish();
        } catch (Exception e) {
            Log.e("BUSCADOR CLIENTES", "Linea "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber()
                    + ": " + e.getMessage());
        }
    };
	
}
