package com.atecresa.clientes;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;
import com.atecresa.preferencias.Constantes;
import com.atecresa.preferencias.TpvConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class ClientesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterRecyclerClientes.OnListInteractionListener {

    private AdapterRecyclerClientes.OnListInteractionListener mListener;
    private RecyclerView recyclerClientes;
    Intent i;

    //region SCAN CODE

    //MENÚ INFERIOR
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.cam_scan) {
                scan(ClientesActivity.this, request_code_cliente);
                return true;
            }
            return false;
        }
    };

    public void scan(Context ctx, final int scanType) {
        //scanType es la constante con la que vamos a discriminar en el onresult lo que queremos obtener (ARTICULO, PRECIO, URL...)
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            startActivityForResult(intent, scanType);  //COMENTAMOS ESTO PORQUE REALMENTE ESTO VA DENTRO DE UNA ACTIVITY
        } catch (Exception e) {
            //SI NO TIENE INSTALADO EL BARCODE SCANNER LO LLEVAMOS AL MARKET
            final AlertDialog d = new AlertDialog.Builder(ctx.getApplicationContext())
                    .setPositiveButton("SI",
                            (dialog, id) -> {
                                Intent i = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://market.android.com/details?id=com.google.zxing.client.android&hl=es"));
                                //startActivityForResult(i, scanType);  //VOID PARA UNA ACTIVITY
                            }).setNegativeButton("CANCELAR", null)
                    .setMessage("MENSAJE DE ERROR")
                    .create();
            d.show();
        }
    }

    private final int request_code_cliente=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == request_code_cliente) {
                buscar(data.getStringExtra("SCAN_RESULT"));
            }
        }
    }

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        //INTENT DE RESULTADO
        i = new Intent(ClientesActivity.this, ClientesActivity.class);
        setResult(Activity.RESULT_CANCELED, i);
        //TOOLBAR SUPERIOR
        Toolbar toolbar = findViewById(R.id.toolbarClientes);
        toolbar.setBackgroundColor(TpvConfig.getAppBackColor());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //LISTENER PARA LOS CAMBIOS. IMPLEMENTAMOS LA INTERFAZ QUE TIENE EL PROPIO ADAPTADOR
        mListener = ClientesActivity.this;
        //RECYCLERVIEW
        recyclerClientes = findViewById(R.id.recyclerBuscador);
        recyclerClientes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //LLENAMOS CON TODOS LOS CLIENTES
        buscar("");
        //INICIALIZAMOS MENÚ INFERIOR
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    //ASIGNAMOS EL CONTROL DE BÚSQUEDA
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_busqueda_clientes, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.requestFocus();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    //FUNCIÓN PARA BUSCAR
    private void buscar(String campo){
        Clientes.buscar(campo);
        AdapterRecyclerClientes adClientes = new AdapterRecyclerClientes(Clientes.ITEMS_BUSQUEDA, mListener);
        recyclerClientes.setAdapter(adClientes);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        buscar(newText);
        return false;
    }
    //VIENE DEL IMPLEMENTS DE LA INTERFAZ DEL ADAPTADOR
    @Override
    public void onListInteraction(int position) {
        Intent res = new Intent();
        res.putExtra("activity", Constantes.activity_busqueda_clientes);
        setResult(RESULT_OK, res);
        Inicio.gb.getMesaActual().setCliente(Clientes.ITEMS_BUSQUEDA.get(position));
        finish();
    }


}
