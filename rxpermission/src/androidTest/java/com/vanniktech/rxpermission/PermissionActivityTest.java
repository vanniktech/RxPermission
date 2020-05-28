package com.vanniktech.rxpermission;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.vanniktech.rxpermission.Permission.State.DENIED;
import static com.vanniktech.rxpermission.Permission.State.DENIED_NOT_SHOWN;
import static com.vanniktech.rxpermission.Permission.State.GRANTED;
import static com.vanniktech.rxpermission.PermissionActivity.VIEW_ID_BACKGROUND_LOCATION;
import static com.vanniktech.rxpermission.PermissionActivity.VIEW_ID_CALL_PHONE;
import static com.vanniktech.rxpermission.PermissionActivity.VIEW_ID_CAMERA;
import static com.vanniktech.rxpermission.PermissionActivity.VIEW_ID_TEXT;
import static com.vanniktech.rxpermission.PermissionActivity.VIEW_ID_WRITE;

@RunWith(AndroidJUnit4.class) public final class PermissionActivityTest {
  @Rule public final ActivityTestRule<PermissionActivity> activityTestRule = new ActivityTestRule<>(PermissionActivity.class);

  private Roboter roboter;

  @Before public void setUp() {
    roboter = new Roboter();
  }

  @Test public void cameraDenied() throws UiObjectNotFoundException {
    roboter.requestCameraPermission()
        .clickOnDeny()
        .assertPermissionState(DENIED);
  }

  @Test public void bluetoothGranted() throws UiObjectNotFoundException {
    roboter.requestCallPhonePermission()
        .clickOnAllow()
        .assertPermissionState(GRANTED);
  }

  @Test public void backgroundLocation() throws UiObjectNotFoundException {
    roboter.requestBackgroundLocationPermission()
        .clickOnAllowAllTheTime()
        .assertPermissionState(GRANTED);
  }

  @Test public void writeDeniedNotShown() throws UiObjectNotFoundException {
    roboter.requestWritePermission()
        .clickOnDeny()
        .requestWritePermission()
        .clickOnDoNotShowAgain()
        .requestWritePermission()
        .assertPermissionState(DENIED_NOT_SHOWN);
  }

  static class Roboter {
    Roboter requestCameraPermission() {
      onView(withId(VIEW_ID_CAMERA)).perform(click());
      return this;
    }

    Roboter requestCallPhonePermission() {
      onView(withId(VIEW_ID_CALL_PHONE)).perform(click());
      return this;
    }

    Roboter requestBackgroundLocationPermission() {
      onView(withId(VIEW_ID_BACKGROUND_LOCATION)).perform(click());
      return this;
    }

    Roboter requestWritePermission() {
      onView(withId(VIEW_ID_WRITE)).perform(click());
      return this;
    }

    Roboter clickOnAllow() throws UiObjectNotFoundException {
      getButton("Allow").click();
      return this;
    }

    Roboter clickOnAllowAllTheTime() throws UiObjectNotFoundException {
      getButton("Allow all the time").click();
      return this;
    }

    Roboter clickOnDeny() throws UiObjectNotFoundException {
      getButton("Deny").click();
      return this;
    }

    Roboter clickOnDoNotShowAgain() throws UiObjectNotFoundException {
      getButton("Deny & don’t ask again").click();
      return this;
    }

    void assertPermissionState(final Permission.State state) {
      onView(withId(VIEW_ID_TEXT)).check(matches(withText(state.toString())));
    }

    private static UiObject getButton(final String text) {
      final UiDevice device = UiDevice.getInstance(getInstrumentation());
      return device.findObject(new UiSelector().text(text));
    }
  }
}
