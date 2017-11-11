package com.vanniktech.rxpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import static android.os.Build.VERSION_CODES.M;

@TargetApi(M) public final class ShadowActivity extends Activity {
  private static final String ARG_PERMISSIONS = "permissions";
  private static final String SAVE_RATIONALE = "save-rationale";
  private static final int REQUEST_CODE = 42;

  static void start(final Context context, final String[] permissions) {
    final Intent intent = new Intent(context, ShadowActivity.class);
    intent.putExtra(ARG_PERMISSIONS, permissions);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  private boolean[] shouldShowRequestPermissionRationale;

  @Override protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {
      handleIntent(getIntent());
    } else {
      shouldShowRequestPermissionRationale = savedInstanceState.getBooleanArray(SAVE_RATIONALE);
    }
  }

  @Override protected void onNewIntent(final Intent intent) {
    handleIntent(intent);
  }

  private void handleIntent(final Intent intent) {
    final String[] permissions = intent.getStringArrayExtra(ARG_PERMISSIONS);
    shouldShowRequestPermissionRationale = rationales(permissions);

    requestPermissions(permissions, REQUEST_CODE);
  }

  @Override public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
    if (requestCode == REQUEST_CODE) {
      final boolean[] rationales = rationales(permissions);
      RealRxPermission.getInstance(getApplication()).onRequestPermissionsResult(grantResults, shouldShowRequestPermissionRationale, rationales, permissions);
      finish();
    }
  }

  @Override public void finish() {
    // Reset the animation to avoid flickering.
    overridePendingTransition(0, 0);
    super.finish();
  }

  @Override protected void onSaveInstanceState(final Bundle outState) {
    outState.putBooleanArray(SAVE_RATIONALE, shouldShowRequestPermissionRationale);
    super.onSaveInstanceState(outState);
  }

  private boolean[] rationales(@NonNull final String[] permissions) {
    final boolean[] rationales = new boolean[permissions.length];

    for (int i = 0; i < permissions.length; i++) {
      rationales[i] = shouldShowRequestPermissionRationale(permissions[i]);
    }

    return rationales;
  }
}
