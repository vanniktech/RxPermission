package com.vanniktech.rxpermission.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.vanniktech.rxpermission.RealRxPermission;
import com.vanniktech.rxpermission.RxPermission;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
  private static final String PERMISSION = Manifest.permission.CAMERA;
  RxPermission rxPermission;

  @NonNull final CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Override protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    rxPermission = RealRxPermission.getInstance(getApplication());

    setContentView(R.layout.activity_main);
    updateTextView();

    findViewById(R.id.enableCamera).setOnClickListener(v -> compositeDisposable.add(rxPermission.requestEach(
            PERMISSION)
        .subscribe(granted -> {
          updateTextView();
          Toast.makeText(this, granted.toString(), Toast.LENGTH_LONG).show();
        })));
  }

  @SuppressLint("SetTextI18n") private void updateTextView() {
    this.<TextView>findViewById(R.id.textView).setText("Has requested: " + rxPermission.hasRequested(PERMISSION));
  }

  @Override protected void onDestroy() {
    compositeDisposable.clear();
    super.onDestroy();
  }
}
