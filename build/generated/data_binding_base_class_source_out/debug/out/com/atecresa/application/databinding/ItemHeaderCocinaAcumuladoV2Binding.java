// Generated by view binder compiler. Do not edit!
package com.atecresa.application.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.atecresa.application.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemHeaderCocinaAcumuladoV2Binding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final TextView txtTituloCocinaAcumulado;

  private ItemHeaderCocinaAcumuladoV2Binding(@NonNull CardView rootView,
      @NonNull TextView txtTituloCocinaAcumulado) {
    this.rootView = rootView;
    this.txtTituloCocinaAcumulado = txtTituloCocinaAcumulado;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemHeaderCocinaAcumuladoV2Binding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemHeaderCocinaAcumuladoV2Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_header_cocina_acumulado_v2, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemHeaderCocinaAcumuladoV2Binding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.txt_titulo_cocina_acumulado;
      TextView txtTituloCocinaAcumulado = ViewBindings.findChildViewById(rootView, id);
      if (txtTituloCocinaAcumulado == null) {
        break missingId;
      }

      return new ItemHeaderCocinaAcumuladoV2Binding((CardView) rootView, txtTituloCocinaAcumulado);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
