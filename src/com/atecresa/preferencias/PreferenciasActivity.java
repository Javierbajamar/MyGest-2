package com.atecresa.preferencias;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.atecresa.application.Inicio;
import com.atecresa.application.R;

import java.util.Set;

@SuppressWarnings("ALL")
public class PreferenciasActivity extends AppCompatPreferenceActivity implements
        OnSharedPreferenceChangeListener {

    private Preference bluetooth;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            addPreferencesFromResource(R.xml.preferencias);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Inicio.setContext(PreferenciasActivity.this);

        Inicio.setPantallaActual(this.getClass().getName());
        listenerCursor lc = new listenerCursor();
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);

        //region MODO MYGEST
        Preference modo = findPreference("modo_mygest");
        ListPreference lp = (ListPreference) modo;

        try {
            if (lp.getEntry() != null) {
                modo.setSummary(lp.getEntry());
            } else {
                String[] modoArray;
                modoArray = getResources().getStringArray(
                        R.array.listaModosMygest);
                modo.setSummary(modoArray[0]);
            }
        } catch (Exception e1) {

            e1.printStackTrace();
        }

        //endregion

        //region SERVIDOR

        Preference prefServidor = findPreference("servidor");
        ListPreference lServidor = (ListPreference) prefServidor;
        try {
            if (lServidor.getEntry() != null) {
                prefServidor.setSummary(lServidor.getEntry());
            } else {
                String[] modoArray;
                modoArray = getResources().getStringArray(
                        R.array.lista_servidores);
                prefServidor.setSummary(modoArray[0]);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Preference pre_uuid = findPreference("uuid");
        EditTextPreference etUUID = (EditTextPreference) pre_uuid;
        etUUID.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        pre_uuid.setSummary(etUUID.getText());

        //endregion

        //region CANARYPAY
        Preference pre_evo_email = findPreference("pago_email");
        EditTextPreference etEmail = (EditTextPreference) pre_evo_email;
        etEmail.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        pre_evo_email.setSummary(etEmail.getText());

        Preference pre_evo_phone = findPreference("pago_phone");
        EditTextPreference etPhone = (EditTextPreference) pre_evo_phone;
        etPhone.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        pre_evo_phone.setSummary(etPhone.getText());

        Preference pre_evo_password = findPreference("pago_password");
        EditTextPreference etPassword = (EditTextPreference) pre_evo_password;
        etPassword.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (etPassword.getText() == null) {
            pre_evo_password.setSummary("");
        } else {
            pre_evo_password.setSummary(etPassword.getText().replaceAll(".", "*"));
        }

        Preference pre_evo_servidor = findPreference("pago_servidor");
        EditTextPreference etServidor = (EditTextPreference) pre_evo_servidor;
        etServidor.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        pre_evo_servidor.setSummary(etServidor.getText());

        //endregion

        //region CONEXIÓN CON TPV
        Preference prefIp = findPreference("ip");
        EditTextPreference etIP = (EditTextPreference) prefIp;
        etIP.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        prefIp.setSummary(etIP.getText());

        Preference prefPuerto;
        EditTextPreference etPuerto;
        prefPuerto = findPreference("puerto");
        etPuerto = (EditTextPreference) prefPuerto;
        etPuerto.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        prefPuerto.setSummary(etPuerto.getText());

        Preference prefTimeout = findPreference("timeout");
        EditTextPreference etTimeout = (EditTextPreference) prefTimeout;
        etTimeout.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        prefTimeout.setSummary(etTimeout.getText());
        //endregion

        //region ACTION BAR
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.pref_txt_version) + " "
                + Sistema.getVersionName() + "." + Sistema.getVersionCode());
        actionBar.setSubtitle(getString(R.string.txt_numero_de_serie) + Inicio.getNserie());
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //endregion

        //region LISTENER DE LOS EDIT TEXT PARA EL CURSOR
        etIP.setOnPreferenceClickListener(lc);
        etPuerto.setOnPreferenceClickListener(lc);
        etTimeout.setOnPreferenceClickListener(lc);
        //endregion


        Preference pAnchoColumnas = findPreference("tam_ancho_columnas_cocina");
        EditTextPreference etAnchoColumnas = (EditTextPreference) pAnchoColumnas;
        pAnchoColumnas.setSummary(etAnchoColumnas.getText());

        Preference pAltoColumnas = findPreference("tam_alto_celda_cocina");
        EditTextPreference etAltoColumnas = (EditTextPreference) pAltoColumnas;
        pAltoColumnas.setSummary(etAltoColumnas.getText());

        Preference tamFuente = findPreference("tam_fuente");
        EditTextPreference etTamFuente = (EditTextPreference) tamFuente;
        tamFuente.setSummary(etTamFuente.getText());

        Preference tamFuenteTitulo = findPreference("tam_fuente_titulo");
        EditTextPreference etTamFuenteTitulo = (EditTextPreference) tamFuenteTitulo;
        tamFuenteTitulo.setSummary(etTamFuenteTitulo.getText());

        etAnchoColumnas.setOnPreferenceClickListener(lc);
        etAltoColumnas.setOnPreferenceClickListener(lc);
        etTamFuente.setOnPreferenceClickListener(lc);
        etTamFuenteTitulo.setOnPreferenceClickListener(lc);


        Preference team = findPreference("opt_teamviewer");
        team.setOnPreferenceClickListener(preference -> {
            descargarTeamviewer();
            return false;
        });

        Preference wifiP = findPreference("opt_conf_wifi");
        wifiP.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            return false;
        });

        Preference horaP = findPreference("opt_conf_hora");
        horaP.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
            return false;
        });

        Preference info = findPreference("opt_info");
        info.setOnPreferenceClickListener(preference -> {
            try {
                startActivity(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS));
            } catch (Exception e) {
                Toast.makeText(PreferenciasActivity.this,
                        "Opción no disponible en esta versión de Android. Acceda desde los ajustes del dispositivo",
                        Toast.LENGTH_LONG).show();
            }
            return false;
        });



        bluetooth = findPreference("opt_bluetooth");

        if (PreferenciasManager.getModoUsoApp().equals(Constantes.MODO_CLOUD)) {
            if (PreferenciasManager.getImpresoraBluetooth().equals("")) {
                bluetooth.setSummary(getString(R.string.sumario_bluetooth));
            } else {
                bluetooth.setSummary(PreferenciasManager.getImpresoraBluetooth());
            }
            bluetooth.setOnPreferenceClickListener(preference -> {
                //MOSTRAR CUADRO DIÁLOGO DISPOSITIVOS BLUETOOTH IF LISTA>0
                seleccionarImpresoraBluetooth();
                return false;
            });
        } else {
            bluetooth.setEnabled(false);
            bluetooth.setSummary(getString(R.string.txt_tpv_no_permite_bluetooth));
        }

        Preference beta=findPreference("opt_beta");
        beta.setOnPreferenceClickListener(preference -> {
            descargarBeta();
            return false;
        });



    }

    // LISTENER CON EL CUAL EL CURSOR LO PONE AL FINAL DEL TEXTO. PARA IP Y
    // PUERTO
    private class listenerCursor implements OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference pref) {
            EditTextPreference et = (EditTextPreference) pref;
            try {
                if (et != null && et.getText() != null)
                    et.getEditText().setSelection(et.getText().length());
            } catch (Exception e) {
                Log.e("Act Preferencias", e.getMessage());
            }
            return true;
        }
    }

    // ESCRIBIMOS EN EL SUMARIO DE LAS PREFERENCIAS PARA REFLEJAR LOS CAMBIOS
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;

            if (key.equals("evolupay_password")) {
                pref.setSummary(etp.getText().replaceAll(".", "*"));
            } else {
                pref.setSummary(etp.getText());
            }

        } else if (pref instanceof ListPreference) {
            ListPreference lp = (ListPreference) pref;
            if (lp.getKey().equals("modo_mygest")||lp.getKey().equals("2_pasos_cocina")||lp.getKey().equals("servidor")) {
                lp.setSummary(lp.getEntry());
            }
        }

    }

    private void descargarBeta() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/apps/testing/com.atecresa.Mygest2"));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"No hay versión BETA disponible",Toast.LENGTH_LONG).show();
        }
    }

    private void descargarTeamviewer() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.teamviewer.quicksupport.market"));
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  //return to caller
            return true;
        }
        return false;
    }

    private void seleccionarImpresoraBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PreferenciasActivity.this, android.R.layout.select_dialog_item);
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    arrayAdapter.add(device.getName());
                }
                arrayAdapter.add(getString(R.string.txt_no_usar_impresora_bluetooth));
            }

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(PreferenciasActivity.this, R.style.MyDialogTheme);
            builderSingle.setIcon(R.drawable.ic_bluetooth_white_24dp);
            builderSingle.setTitle("Seleccione impresora bluetooth");

            builderSingle.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            builderSingle.setAdapter(arrayAdapter, (dialog, pos) -> {
                try {
                    if (arrayAdapter.getItem(pos) != null) {
                        if (arrayAdapter.getItem(pos).equals(getString(R.string.txt_no_usar_impresora_bluetooth))) {
                            PreferenciasManager.setImpresoraBluetooth("");
                            bluetooth.setSummary(getString(R.string.sumario_bluetooth));
                        } else {
                            PreferenciasManager.setImpresoraBluetooth(arrayAdapter.getItem(pos));
                            bluetooth.setSummary(arrayAdapter.getItem(pos));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            });
            builderSingle.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region PERMISOS

    //endregion


}
