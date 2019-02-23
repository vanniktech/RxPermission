package com.vanniktech.rxpermission.testing;

import android.annotation.SuppressLint;
import com.vanniktech.rxpermission.Permission;
import org.junit.Before;
import org.junit.Test;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

public final class MockRxPermissionTest {
  private Permission callPhoneGranted;

  private Permission cameraGranted;
  private Permission cameraDenied;
  private Permission cameraDeniedNotShown;
  private Permission cameraRevokedByPolicy;

  @Before public void setUp() {
    callPhoneGranted = Permission.granted(CALL_PHONE);

    cameraGranted = Permission.granted(CAMERA);
    cameraDenied = Permission.denied(CAMERA);
    cameraDeniedNotShown = Permission.deniedNotShown(CAMERA);
    cameraRevokedByPolicy = Permission.revokedByPolicy(CAMERA);
  }

  @Test @SuppressLint("CheckResult") /* https://issuetracker.google.com/issues/125753102 */ public void requestNull() {
    try {
      new MockRxPermission(cameraGranted).request(null);
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("permission == null");
    }
  }

  @Test public void requestNothingPreConfigured() {
    new MockRxPermission()
        .request(CAMERA)
        .test()
        .assertFailure(IllegalStateException.class)
        .assertErrorMessage("No permission was pre-configured for android.permission.CAMERA");
  }

  @Test public void requestNoMatch() {
    new MockRxPermission(callPhoneGranted)
        .request(CAMERA)
        .test()
        .assertFailure(IllegalStateException.class)
        .assertErrorMessage("No permission was pre-configured for android.permission.CAMERA");
  }

  @Test public void requestSingleGranted() {
    new MockRxPermission(cameraGranted)
        .request(CAMERA)
        .test()
        .assertResult(cameraGranted);
  }

  @Test public void requestSingleDenied() {
    new MockRxPermission(cameraDenied)
        .request(CAMERA)
        .test()
        .assertResult(cameraDenied);
  }

  @Test public void requestSingleDeniedNotShown() {
    new MockRxPermission(cameraDeniedNotShown)
        .request(CAMERA)
        .test()
        .assertResult(cameraDeniedNotShown);
  }

  @Test public void requestSingleRevokedByPolicy() {
    new MockRxPermission(cameraRevokedByPolicy)
        .request(CAMERA)
        .test()
        .assertResult(cameraRevokedByPolicy);
  }

  @Test @SuppressLint("CheckResult") /* https://issuetracker.google.com/issues/125753102 */ public void requestEachEmpty() {
    try {
      new MockRxPermission(cameraGranted).requestEach();
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("permissions are null or empty");
    }
  }

  @Test @SuppressLint("CheckResult") /* https://issuetracker.google.com/issues/125753102 */ public void requestEachNull() {
    try {
      new MockRxPermission(cameraGranted).requestEach((String[]) null);
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("permissions are null or empty");
    }
  }

  @Test public void requestEachNothingPreConfigured() {
    new MockRxPermission()
        .requestEach(CAMERA)
        .test()
        .assertFailure(IllegalStateException.class)
        .assertErrorMessage("No permission was pre-configured for android.permission.CAMERA");
  }

  @Test public void requestEachNoMatch() {
    new MockRxPermission(callPhoneGranted)
        .requestEach(CAMERA)
        .test()
        .assertFailure(IllegalStateException.class)
        .assertErrorMessage("No permission was pre-configured for android.permission.CAMERA");
  }

  @Test public void requestEachSingleGranted() {
    new MockRxPermission(cameraGranted)
        .requestEach(CAMERA)
        .test()
        .assertResult(cameraGranted);
  }

  @Test public void requestEachSingleDenied() {
    new MockRxPermission(cameraDenied)
        .requestEach(CAMERA)
        .test()
        .assertResult(cameraDenied);
  }

  @Test public void requestEachSingleDeniedNotShown() {
    new MockRxPermission(cameraDeniedNotShown)
        .requestEach(CAMERA)
        .test()
        .assertResult(cameraDeniedNotShown);
  }

  @Test public void requestEachSingleRevokedByPolicy() {
    new MockRxPermission(cameraRevokedByPolicy)
        .requestEach(CAMERA)
        .test()
        .assertResult(cameraRevokedByPolicy);
  }

  @Test public void requestEachMultiple() {
    new MockRxPermission(callPhoneGranted, cameraGranted)
        .requestEach(CALL_PHONE, CAMERA)
        .test()
        .assertResult(callPhoneGranted, cameraGranted);
  }

  @Test public void isGranted() {
    assertThat(new MockRxPermission().isGranted(CAMERA)).isFalse();
    assertThat(new MockRxPermission(cameraGranted).isGranted(CAMERA)).isTrue();
    assertThat(new MockRxPermission(cameraDenied).isGranted(CAMERA)).isFalse();
    assertThat(new MockRxPermission(cameraDeniedNotShown).isGranted(CAMERA)).isFalse();
    assertThat(new MockRxPermission(cameraRevokedByPolicy).isGranted(CAMERA)).isFalse();
  }

  @Test public void isRevokedByPolicy() {
    assertThat(new MockRxPermission().isRevokedByPolicy(CAMERA)).isFalse();
    assertThat(new MockRxPermission(cameraGranted).isRevokedByPolicy(CAMERA)).isFalse();
    assertThat(new MockRxPermission(cameraDenied).isRevokedByPolicy(CAMERA)).isFalse();
    assertThat(new MockRxPermission(cameraDeniedNotShown).isRevokedByPolicy(CAMERA)).isFalse();
    assertThat(new MockRxPermission(cameraRevokedByPolicy).isRevokedByPolicy(CAMERA)).isTrue();
  }
}
