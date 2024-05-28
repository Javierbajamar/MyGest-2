// Generated by view binder compiler. Do not edit!
package com.atecresa.application.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.atecresa.application.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class SelectorMesaItemBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView txtInferior;

  @NonNull
  public final TextView txtSuperior;

  private SelectorMesaItemBinding(@NonNull LinearLayout rootView, @NonNull TextView txtInferior,
      @NonNull TextView txtSuperior) {
    this.rootView = rootView;
    this.txtInferior = txtInferior;
    this.txtSuperior = txtSuperior;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static SelectorMesaItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static SelectorMesaItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.selector_mesa_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static SelectorMesaItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.txtInferior;
      TextView txtInferior = ViewBindings.findChildViewById(rootView, id);
      if (txtInferior == null) {
        break missingId;
      }

      id = R.id.txtSuperior;
      TextView txtSuperior = ViewBindings.findChildViewById(rootView, id);
      if (txtSuperior == null) {
        break missingId;
      }

      return new SelectorMesaItemBinding((LinearLayout) rootView, txtInferior, txtSuperior);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
