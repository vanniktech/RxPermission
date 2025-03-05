package com.vanniktech.rxpermission;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageManager;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.WARN;

@SuppressWarnings("CheckResult") @SuppressLint("NewApi") public class RealRxPermissionTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(WARN);

  private static final boolean[] FALSE_ARRAY = { false };
  private static final boolean[] FALSE_ARRAY_2 = { false, false };
  private static final boolean[] TRUE_ARRAY = { true };

  private static int[] permissionGranted() {
    return new int[] { PERMISSION_GRANTED };
  }

  private static int[] permissionDenied() {
    return new int[] { PERMISSION_DENIED };
  }

  @Mock private Application context;
  @Mock private PackageManager pm;

  @Captor private ArgumentCaptor<String[]> requestedPermissions;

  private RealRxPermission rxPermission;

  @Before public void setUp() {
    doReturn(context).when(context).getApplicationContext();
    rxPermission = spy(new RealRxPermission(context));

    doNothing().when(rxPermission).startShadowActivity(any(String[].class));
  }

  @Test public void requestPreM() {
    doReturn(true).when(rxPermission).isGranted(READ_PHONE_STATE);

    rxPermission.request(READ_PHONE_STATE)
        .test()
        .assertResult(Permission.granted(READ_PHONE_STATE));
  }

  @Test public void requestGranted() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.request(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionGranted(), FALSE_ARRAY, FALSE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.granted(READ_PHONE_STATE));
  }

  @Test public void requestDeniedFirstTime() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.request(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionDenied(), FALSE_ARRAY, TRUE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.denied(READ_PHONE_STATE));
  }

  @Test public void requestDeniedAnyOtherTime() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.request(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionDenied(), TRUE_ARRAY, TRUE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.denied(READ_PHONE_STATE));
  }

  @Test public void requestDeniedNotShown() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.request(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionDenied(), FALSE_ARRAY, FALSE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.deniedNotShown(READ_PHONE_STATE));
  }

  @Test public void requestRevoked() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);
    doReturn(true).when(rxPermission).isRevokedByPolicy(READ_PHONE_STATE);

    rxPermission.request(READ_PHONE_STATE)
        .test()
        .assertResult(Permission.revokedByPolicy(READ_PHONE_STATE));
  }

  @Test public void requestEachPreM() {
    doReturn(true).when(rxPermission).isGranted(READ_PHONE_STATE);

    rxPermission.requestEach(READ_PHONE_STATE)
        .test()
        .assertResult(Permission.granted(READ_PHONE_STATE));
  }

  @Test public void requestEachGranted() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.requestEach(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionGranted(), FALSE_ARRAY, FALSE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.granted(READ_PHONE_STATE));
  }

  @Test public void requestEachDeniedFirstTime() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.requestEach(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionDenied(), FALSE_ARRAY, TRUE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.denied(READ_PHONE_STATE));
  }

  @Test public void requestEachDeniedAnyOtherTime() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.requestEach(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionDenied(), TRUE_ARRAY, TRUE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.denied(READ_PHONE_STATE));
  }

  @Test public void requestEachDeniedNotShown() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);

    final TestObserver<Permission> o = rxPermission.requestEach(READ_PHONE_STATE)
        .test();

    rxPermission.onRequestPermissionsResult(permissionDenied(), FALSE_ARRAY, FALSE_ARRAY, READ_PHONE_STATE);
    o.assertResult(Permission.deniedNotShown(READ_PHONE_STATE));
  }

  @Test public void requestEachRevoked() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);
    doReturn(true).when(rxPermission).isRevokedByPolicy(READ_PHONE_STATE);

    rxPermission.requestEach(READ_PHONE_STATE)
        .test()
        .assertResult(Permission.revokedByPolicy(READ_PHONE_STATE));
  }

  @Test public void requestEachSeveralPermissionsGranted() {
    final String[] permissions = { READ_PHONE_STATE, CAMERA };
    doReturn(false).when(rxPermission).isGranted(anyString());
    final int[] result = { PERMISSION_GRANTED, PERMISSION_GRANTED };

    final TestObserver<Permission> o = rxPermission.requestEach(permissions)
        .test();

    rxPermission.onRequestPermissionsResult(result, FALSE_ARRAY_2, FALSE_ARRAY_2, permissions);

    o.assertResult(Permission.granted(READ_PHONE_STATE), Permission.granted(CAMERA));
  }

  @Test public void requestEachSeveralPermissionsOneAlreadyGranted() {
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);
    doReturn(true).when(rxPermission).isGranted(CAMERA);

    final TestObserver<Permission> o = rxPermission.requestEach(READ_PHONE_STATE, CAMERA)
        .test();

    rxPermission.onRequestPermissionsResult(permissionGranted(), FALSE_ARRAY_2, FALSE_ARRAY_2, READ_PHONE_STATE);

    o.assertResult(Permission.granted(READ_PHONE_STATE), Permission.granted(CAMERA));
    verify(rxPermission).startShadowActivity(requestedPermissions.capture());
    assertThat(requestedPermissions.getValue()).containsExactly(READ_PHONE_STATE);
  }

  @Test public void requestEachSeveralPermissionsOneDenied() {
    final String[] permissions = { READ_PHONE_STATE, CAMERA };
    doReturn(false).when(rxPermission).isGranted(anyString());
    final int[] result = { PERMISSION_GRANTED, PERMISSION_DENIED };

    final TestObserver<Permission> o = rxPermission.requestEach(permissions)
        .test();

    rxPermission.onRequestPermissionsResult(result, FALSE_ARRAY_2, FALSE_ARRAY_2, permissions);

    o.assertResult(Permission.granted(READ_PHONE_STATE), Permission.deniedNotShown(CAMERA));
  }

  @Test public void requestEachSeveralPermissionsOneRevoked() {
    final String[] permissions = { READ_PHONE_STATE, CAMERA };
    doReturn(false).when(rxPermission).isGranted(READ_PHONE_STATE);
    doReturn(false).when(rxPermission).isGranted(CAMERA);
    doReturn(true).when(rxPermission).isRevokedByPolicy(CAMERA);

    final TestObserver<Permission> o = rxPermission.requestEach(permissions)
        .test();

    rxPermission.onRequestPermissionsResult(permissionGranted(), FALSE_ARRAY, FALSE_ARRAY, READ_PHONE_STATE);

    o.assertResult(Permission.granted(READ_PHONE_STATE), Permission.revokedByPolicy(CAMERA));
  }

  @Test public void isGrantedPreMarshmallow() {
    doReturn(false).when(rxPermission).isMarshmallow();

    assertThat(rxPermission.isGranted("p")).isTrue();
  }

  @Test public void isGrantedGranted() {
    doReturn(true).when(rxPermission).isMarshmallow();
    doReturn(PERMISSION_GRANTED).when(context).checkSelfPermission("p");

    assertThat(rxPermission.isGranted("p")).isTrue();
  }

  @Test public void isGrantedDenied() {
    doReturn(true).when(rxPermission).isMarshmallow();
    doReturn(PERMISSION_DENIED).when(context).checkSelfPermission("p");

    assertThat(rxPermission.isGranted("p")).isFalse();
  }

  @Test public void isRevokedPreMarshmallow() {
    doReturn(false).when(rxPermission).isMarshmallow();

    assertThat(rxPermission.isRevokedByPolicy("p")).isFalse();
  }

  @Test public void isRevokedTrue() {
    doReturn(true).when(rxPermission).isMarshmallow();
    doReturn(pm).when(context).getPackageManager();
    doReturn("Test1234").when(context).getPackageName();
    doReturn(true).when(pm).isPermissionRevokedByPolicy(eq("p"), anyString());

    assertThat(rxPermission.isRevokedByPolicy("p")).isTrue();
  }

  @Test public void isGrantedFalse() {
    doReturn(true).when(rxPermission).isMarshmallow();
    doReturn(pm).when(context).getPackageManager();
    doReturn("Test1234").when(context).getPackageName();
    doReturn(false).when(pm).isPermissionRevokedByPolicy(eq("p"), anyString());

    assertThat(rxPermission.isRevokedByPolicy("p")).isFalse();
  }
}
