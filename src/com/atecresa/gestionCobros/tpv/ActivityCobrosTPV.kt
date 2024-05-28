package com.atecresa.gestionCobros.tpv
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.atecresa.adaptadoresUIComanda.RecyclerTouchListener
import com.atecresa.application.Inicio
import com.atecresa.application.R
import com.atecresa.application.databinding.CobrosTpvActivityBinding
import com.atecresa.gestionCobros.canarypay.CanarypayActivity
import com.atecresa.preferencias.TpvConfig
import com.atecresa.print.ActionBarDialog
import com.atecresa.util.Formateador
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*
class ActivityCobrosTPV : AppCompatActivity(), ListenerCobrosTPV {
    private var mAdapterCobrosTPV: AdapterCobrosTPV? = null
    private val requestPaymentCode: Int = 1
    private lateinit var mDrawingView: DrawingView
    private lateinit var binding: CobrosTpvActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CobrosTpvActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Pendiente: " + Formateador.formatearImporteString(Inicio.gb.mesaActual.totalPendiente)
        supportActionBar!!.subtitle = "Mesa " + Inicio.gb.mesa
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        val colorDrawable = ColorDrawable()
        colorDrawable.color = TpvConfig.getAppBackColor()
        supportActionBar!!.setBackgroundDrawable(colorDrawable)
        initFormasPago()
        mDrawingView = DrawingView(baseContext)
    }
    private fun initFormasPago() {
        binding.rvPagos.setHasFixedSize(true)
        val mLayoutManager = GridLayoutManager(this, 3)
        mAdapterCobrosTPV = AdapterCobrosTPV()
        binding.rvPagos.adapter = mAdapterCobrosTPV
        binding.rvPagos.layoutManager = mLayoutManager
        mAdapterCobrosTPV!!.setLista(RepositoryCobrosTPV.items.value!!)
        binding.rvPagos.addOnItemTouchListener(RecyclerTouchListener(this@ActivityCobrosTPV, binding.rvPagos, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                when (RepositoryCobrosTPV.items.value!![position].descripcion) {
                    "CANARYPAY", "CLEARONE" -> {
                        startActivityForResult(Intent(this@ActivityCobrosTPV, CanarypayActivity::class.java), requestPaymentCode)
                    }
                    "CRÉDITO" -> {
                        val dialog = Dialog(this@ActivityCobrosTPV)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.cobros_tpv_dialog_firma)
                        val window = dialog.window
                        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        window.setGravity(Gravity.CENTER)
                        val viewFirma = dialog.findViewById(R.id.layoutFirma) as LinearLayout
                        viewFirma.addView(mDrawingView)
                        val yesBtn = dialog.findViewById(R.id.bt_dialog_firma_ok) as Button
                        val noBtn = dialog.findViewById(R.id.bt_firma_cancelar) as Button
                        yesBtn.setOnClickListener {
                            dialog.dismiss()
                            RepositoryCobrosTPV.sendPayment(
                                binding.etEntrega.text.toString(),
                                RepositoryCobrosTPV.items.value!![position].id,
                                "",
                                getFirma(),
                                this@ActivityCobrosTPV)
                        }
                        noBtn.setOnClickListener { dialog.dismiss() }
                        dialog.show()
                    }
                    else -> RepositoryCobrosTPV.sendPayment(
                        binding.etEntrega.text.toString(),
                        RepositoryCobrosTPV.items.value!![position].id,
                        "",
                        "",
                        this@ActivityCobrosTPV)
                }
            }
            override fun onLongClick(view: View?, position: Int) {
                TODO("not implemented")
            }
        }))
        RepositoryCobrosTPV.items.observe(this, Observer<ArrayList<FormaPagoTPV>> { formapago ->
            mAdapterCobrosTPV!!.setLista(formapago)
        })
    }
    override fun notifyPaymentSuccess(response: Boolean) {
        binding.etEntrega.setText("")
        if (response) {
            mostrarDialogCobro(Inicio.gb.devolucion.concepto,
                Inicio.gb.devolucion.totalImporte,
                binding.etEntrega.text.toString(),
                Inicio.gb.devolucion.devolucion)
        } else {
            Objects.requireNonNull<ActionBar>(supportActionBar).title = ("Pendiente: "
                    + Formateador.formatearImporteString(Inicio.gb.devolucion.totalPendiente)
                    + " €")
        }
    }
    override fun notifyPaymentError(response: String) {
        Toast.makeText(this@ActivityCobrosTPV, "Error envíando el cobro", Toast.LENGTH_LONG).show()
    }
    private fun mostrarDialogCobro(formaPago: String, importe: String, entrega: String, devolucion: String) {
        val args = Bundle()
        args.putString("forma_de_pago", formaPago)
        args.putString("importe", importe)
        args.putString("entrega", entrega)
        args.putString("devolucion", devolucion)
        val actionbarDialog = ActionBarDialog()
        actionbarDialog.context = this@ActivityCobrosTPV
        actionbarDialog.arguments = args
        actionbarDialog.show(supportFragmentManager,
            "action_bar_frag")
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_action_cobro, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cobro_cancelar) {
            volverAComanda()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            volverAComanda()
            return false
        }
        return false
    }
    private fun volverAComanda() {
        val c = Intent()
        if (Inicio.gb.devolucion != null) {
            Inicio.gb.mesaActual.totalPendiente = Inicio.gb.devolucion.totalPendiente
        }
        setResult(RESULT_CANCELED, c)
        finish()
    }
    private fun mostrarDialogFirma() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cobros_tpv_dialog_firma)
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        val viewFirma = dialog.findViewById(R.id.layoutFirma) as LinearLayout
        viewFirma.addView(mDrawingView)
        val yesBtn = dialog.findViewById(R.id.bt_dialog_firma_ok) as Button
        val noBtn = dialog.findViewById(R.id.bt_firma_cancelar) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            toast(getFirma())
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    private fun getFirma(): String {
        var firma = ""
        if (mDrawingView.isControlFirma) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            mDrawingView.bitmap.compress(
                Bitmap.CompressFormat.PNG,
                20,
                byteArrayOutputStream
            )
            val byteArray = byteArrayOutputStream.toByteArray()
            firma = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        return firma
    }
}
 