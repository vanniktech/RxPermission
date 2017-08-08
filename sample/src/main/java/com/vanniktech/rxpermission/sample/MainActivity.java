package com.vanniktech.rxpermission.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.vanniktech.rxpermission.Permission;
import com.vanniktech.rxpermission.RealRxPermission;
import com.vanniktech.rxpermission.RxPermission;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
  RxPermission rxPermission;

  @NonNull final CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Override protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    rxPermission = RealRxPermission.getInstance(getApplication());

    setContentView(R.layout.activity_main);

    findViewById(R.id.enableCamera).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View v) {
        compositeDisposable.add(rxPermission.requestEach(Manifest.permission.CAMERA)
            .subscribe(new Consumer<Permission>() {
              @Override public void accept(final Permission granted) throws Exception {
                Toast.makeText(MainActivity.this, granted.toString(), Toast.LENGTH_LONG).show();
              }
            }));
      }
    });
  }

  @Override protected void onDestroy() {
    compositeDisposable.clear();
    super.onDestroy();
  }
}
