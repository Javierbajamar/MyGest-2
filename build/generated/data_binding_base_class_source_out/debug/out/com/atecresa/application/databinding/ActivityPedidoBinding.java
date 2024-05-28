// Generated by view binder compiler. Do not edit!
package com.atecresa.application.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.atecresa.application.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityPedidoBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageButton btCancelarPedido;

  @NonNull
  public final ImageButton btConfirmarPedido;

  @NonNull
  public final LinearLayout layoutPedido;

  @NonNull
  public final ListView listaPedido;

  private ActivityPedidoBinding(@NonNull LinearLayout rootView,
      @NonNull ImageButton btCancelarPedido, @NonNull ImageButton btConfirmarPedido,
      @NonNull LinearLayout layoutPedido, @NonNull ListView listaPedido) {
    this.rootView = rootView;
    this.btCancelarPedido = btCancelarPedido;
    this.btConfirmarPedido = btConfirmarPedido;
    this.layoutPedido = layoutPedido;
    this.listaPedido = listaPedido;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityPedidoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityPedidoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_pedido, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityPedidoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btCancelarPedido;
      ImageButton btCancelarPedido = ViewBindings.findChildViewById(rootView, id);
      if (btCancelarPedido == null) {
        break missingId;
      }

      id = R.id.btConfirmarPedido;
      ImageButton btConfirmarPedido = ViewBindings.findChildViewById(rootView, id);
      if (btConfirmarPedido == null) {
        break missingId;
      }

      LinearLayout layoutPedido = (LinearLayout) rootView;

      id = R.id.listaPedido;
      ListView listaPedido = ViewBindings.findChildViewById(rootView, id);
      if (listaPedido == null) {
        break missingId;
      }

      return new ActivityPedidoBinding((LinearLayout) rootView, btCancelarPedido, btConfirmarPedido,
          layoutPedido, listaPedido);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
