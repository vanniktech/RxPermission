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

import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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

    findViewById(R.id.enableCameraAndWriteExternal).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View v) {
        compositeDisposable.add(
                rxPermission.requestEachToSingle(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                  @Override
                  public void accept(Boolean granted) throws Exception {
                    Toast.makeText(MainActivity.this, granted.toString(), Toast.LENGTH_SHORT).show();
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
