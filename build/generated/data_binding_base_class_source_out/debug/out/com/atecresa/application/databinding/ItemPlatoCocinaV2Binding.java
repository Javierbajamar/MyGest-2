// Generated by view binder compiler. Do not edit!
package com.atecresa.application.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.atecresa.application.R;
import com.google.android.material.card.MaterialCardView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemPlatoCocinaV2Binding implements ViewBinding {
  @NonNull
  private final MaterialCardView rootView;

  @NonNull
  public final CheckBox checkPlatoTv;

  @NonNull
  public final TextView txtTextoLibre;

  private ItemPlatoCocinaV2Binding(@NonNull MaterialCardView rootView,
      @NonNull CheckBox checkPlatoTv, @NonNull TextView txtTextoLibre) {
    this.rootView = rootView;
    this.checkPlatoTv = checkPlatoTv;
    this.txtTextoLibre = txtTextoLibre;
  }

  @Override
  @NonNull
  public MaterialCardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemPlatoCocinaV2Binding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemPlatoCocinaV2Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_plato_cocina_v2, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemPlatoCocinaV2Binding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.checkPlatoTv;
      CheckBox checkPlatoTv = ViewBindings.findChildViewById(rootView, id);
      if (checkPlatoTv == null) {
        break missingId;
      }

      id = R.id.txt_texto_libre;
      TextView txtTextoLibre = ViewBindings.findChildViewById(rootView, id);
      if (txtTextoLibre == null) {
        break missingId;
      }

      return new ItemPlatoCocinaV2Binding((MaterialCardView) rootView, checkPlatoTv, txtTextoLibre);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
