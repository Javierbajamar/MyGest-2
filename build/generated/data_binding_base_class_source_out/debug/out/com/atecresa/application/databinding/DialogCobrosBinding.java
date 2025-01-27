// Generated by view binder compiler. Do not edit!
package com.atecresa.application.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.atecresa.application.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DialogCobrosBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btCerrar;

  @NonNull
  public final TextView textView9;

  @NonNull
  public final Toolbar toolbarCobros;

  @NonNull
  public final TextView tvDevolucion;

  @NonNull
  public final TextView tvEntrega;

  @NonNull
  public final TextView tvImporte;

  @NonNull
  public final TextView tvLabelImporte;

  private DialogCobrosBinding(@NonNull ConstraintLayout rootView, @NonNull Button btCerrar,
      @NonNull TextView textView9, @NonNull Toolbar toolbarCobros, @NonNull TextView tvDevolucion,
      @NonNull TextView tvEntrega, @NonNull TextView tvImporte, @NonNull TextView tvLabelImporte) {
    this.rootView = rootView;
    this.btCerrar = btCerrar;
    this.textView9 = textView9;
    this.toolbarCobros = toolbarCobros;
    this.tvDevolucion = tvDevolucion;
    this.tvEntrega = tvEntrega;
    this.tvImporte = tvImporte;
    this.tvLabelImporte = tvLabelImporte;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogCobrosBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogCobrosBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_cobros, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogCobrosBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btCerrar;
      Button btCerrar = ViewBindings.findChildViewById(rootView, id);
      if (btCerrar == null) {
        break missingId;
      }

      id = R.id.textView9;
      TextView textView9 = ViewBindings.findChildViewById(rootView, id);
      if (textView9 == null) {
        break missingId;
      }

      id = R.id.toolbarCobros;
      Toolbar toolbarCobros = ViewBindings.findChildViewById(rootView, id);
      if (toolbarCobros == null) {
        break missingId;
      }

      id = R.id.tvDevolucion;
      TextView tvDevolucion = ViewBindings.findChildViewById(rootView, id);
      if (tvDevolucion == null) {
        break missingId;
      }

      id = R.id.tvEntrega;
      TextView tvEntrega = ViewBindings.findChildViewById(rootView, id);
      if (tvEntrega == null) {
        break missingId;
      }

      id = R.id.tvImporte;
      TextView tvImporte = ViewBindings.findChildViewById(rootView, id);
      if (tvImporte == null) {
        break missingId;
      }

      id = R.id.tvLabelImporte;
      TextView tvLabelImporte = ViewBindings.findChildViewById(rootView, id);
      if (tvLabelImporte == null) {
        break missingId;
      }

      return new DialogCobrosBinding((ConstraintLayout) rootView, btCerrar, textView9,
          toolbarCobros, tvDevolucion, tvEntrega, tvImporte, tvLabelImporte);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
