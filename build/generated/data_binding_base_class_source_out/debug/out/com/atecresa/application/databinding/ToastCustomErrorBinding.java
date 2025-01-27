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

public final class ToastCustomErrorBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout toastLayoutRoot;

  @NonNull
  public final TextView txtToast;

  private ToastCustomErrorBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout toastLayoutRoot, @NonNull TextView txtToast) {
    this.rootView = rootView;
    this.toastLayoutRoot = toastLayoutRoot;
    this.txtToast = txtToast;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ToastCustomErrorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ToastCustomErrorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.toast_custom_error, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ToastCustomErrorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      LinearLayout toastLayoutRoot = (LinearLayout) rootView;

      id = R.id.txtToast;
      TextView txtToast = ViewBindings.findChildViewById(rootView, id);
      if (txtToast == null) {
        break missingId;
      }

      return new ToastCustomErrorBinding((LinearLayout) rootView, toastLayoutRoot, txtToast);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
