package com.atecresa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.atecresa.application.R;

import java.util.Objects;


public class Visor_Docs extends BaseActivity {

    private String txtDoc="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_docs);
        TextView t= findViewById(R.id.tvVisor);
        txtDoc= Objects.requireNonNull(getIntent().getExtras()).getString("contenido","");
        try {
            t.setText(txtDoc);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getExtras().getString("tipodoc",""));
            getSupportActionBar().setSubtitle("Terminal comandero");
        } catch (Exception e) {
            t.setText("");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_visor, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_visor_cancelar:
                finish();
                break;
            case R.id.action_visor_enviar:
                compartirDoc();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void compartirDoc(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, txtDoc);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void ejecutarComandoVoz(String com, String mesa) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
