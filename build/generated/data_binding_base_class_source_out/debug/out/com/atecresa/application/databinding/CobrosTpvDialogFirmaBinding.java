// Generated by view binder compiler. Do not edit!
package com.atecresa.application.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.atecresa.application.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CobrosTpvDialogFirmaBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btDialogFirmaOk;

  @NonNull
  public final Button btFirmaCancelar;

  @NonNull
  public final LinearLayout layoutFirma;

  @NonNull
  public final TextView tvTituloFirma;

  private CobrosTpvDialogFirmaBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button btDialogFirmaOk, @NonNull Button btFirmaCancelar,
      @NonNull LinearLayout layoutFirma, @NonNull TextView tvTituloFirma) {
    this.rootView = rootView;
    this.btDialogFirmaOk = btDialogFirmaOk;
    this.btFirmaCancelar = btFirmaCancelar;
    this.layoutFirma = layoutFirma;
    this.tvTituloFirma = tvTituloFirma;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CobrosTpvDialogFirmaBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CobrosTpvDialogFirmaBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.cobros_tpv_dialog_firma, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CobrosTpvDialogFirmaBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bt_dialog_firma_ok;
      Button btDialogFirmaOk = ViewBindings.findChildViewById(rootView, id);
      if (btDialogFirmaOk == null) {
        break missingId;
      }

      id = R.id.bt_firma_cancelar;
      Button btFirmaCancelar = ViewBindings.findChildViewById(rootView, id);
      if (btFirmaCancelar == null) {
        break missingId;
      }

      id = R.id.layoutFirma;
      LinearLayout layoutFirma = ViewBindings.findChildViewById(rootView, id);
      if (layoutFirma == null) {
        break missingId;
      }

      id = R.id.tvTituloFirma;
      TextView tvTituloFirma = ViewBindings.findChildViewById(rootView, id);
      if (tvTituloFirma == null) {
        break missingId;
      }

      return new CobrosTpvDialogFirmaBinding((ConstraintLayout) rootView, btDialogFirmaOk,
          btFirmaCancelar, layoutFirma, tvTituloFirma);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}