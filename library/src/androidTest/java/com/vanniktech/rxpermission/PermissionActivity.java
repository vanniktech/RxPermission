package com.vanniktech.rxpermission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public final class PermissionActivity extends Activity implements BiConsumer<Permission, Throwable> {
  static final int VIEW_ID_TEXT = 10;
  static final int VIEW_ID_CAMERA = 11;
  static final int VIEW_ID_LOCATION = 12;
  static final int VIEW_ID_WRITE = 13;

  @NonNull final CompositeDisposable compositeDisposable = new CompositeDisposable();

  RxPermission rxPermission;
  private TextView textView;

  @Override @SuppressLint({ "SetTextI18n", "ResourceType" }) protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    rxPermission = RealRxPermission.getInstance(getApplication());

    textView = new TextView(this);
    textView.setId(VIEW_ID_TEXT);
    textView.setText("State");

    // Unfortunately since we have to test 3 scenarios, we'll have to request 3 different permissions.

    final LinearLayout linearLayout = new LinearLayout(this);

    final Button camera = new Button(this);
    camera.setId(VIEW_ID_CAMERA);
    camera.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View v) {
        compositeDisposable.add(rxPermission
            .request(CAMERA)
            .subscribe(PermissionActivity.this));
      }
    });

    final Button location = new Button(this);
    location.setId(VIEW_ID_LOCATION);
    location.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View v) {
        compositeDisposable.add(rxPermission
            .request(ACCESS_COARSE_LOCATION)
            .subscribe(PermissionActivity.this));
      }
    });

    final Button write = new Button(this);
    write.setId(VIEW_ID_WRITE);
    write.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View v) {
        compositeDisposable.add(rxPermission
            .request(WRITE_EXTERNAL_STORAGE)
            .subscribe(PermissionActivity.this));
      }
    });

    linearLayout.addView(textView);
    linearLayout.addView(camera);
    linearLayout.addView(location);
    linearLayout.addView(write);
    setContentView(linearLayout);
  }

  @Override protected void onDestroy() {
    compositeDisposable.clear();
    super.onDestroy();
  }

  @Override public void accept(final Permission permission, final Throwable throwable) {
    if (throwable != null) {
      throw new RuntimeException();
    }

    textView.setText(permission.state().toString());
  }
}
