package com.vanniktech.rxpermission;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static org.assertj.core.api.Java6Assertions.assertThat;

public final class MockRxPermissionTest {
  @Rule public final ExpectedException expectedException = ExpectedException.none();

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

  @Test public void requestNull() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("permission == null");
    new MockRxPermission(cameraGranted).request(null);
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

  @Test public void requestEachEmpty() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("permissions are null or empty");
    new MockRxPermission(cameraGranted).requestEach();
  }

  @Test public void requestEachNull() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("permissions are null or empty");
    new MockRxPermission(cameraGranted).requestEach((String[]) null);
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
