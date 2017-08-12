package com.vanniktech.rxpermission;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vanniktech.rxpermission.Permission.State.DENIED;
import static com.vanniktech.rxpermission.Permission.State.DENIED_NOT_SHOWN;
import static com.vanniktech.rxpermission.Permission.State.GRANTED;
import static com.vanniktech.rxpermission.PermissionActivity.VIEW_ID_CAMERA;
import static com.vanniktech.rxpermission.PermissionActivity.VIEW_ID_LOCATION;
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

  @Test public void locationGranted() throws UiObjectNotFoundException {
    roboter.requestLocationPermission()
        .clickOnAllow()
        .assertPermissionState(GRANTED);
  }

  @Test public void writeDeniedNotShown() throws UiObjectNotFoundException {
    roboter.requestWritePermission()
        .clickOnDeny()
        .requestWritePermission()
        .clickOnDoNotShowAgain()
        .clickOnDeny()
        .requestWritePermission()
        .assertPermissionState(DENIED_NOT_SHOWN);
  }

  static class Roboter {
    Roboter requestCameraPermission() {
      onView(withId(VIEW_ID_CAMERA)).perform(click());
      return this;
    }

    Roboter requestLocationPermission() {
      onView(withId(VIEW_ID_LOCATION)).perform(click());
      return this;
    }

    Roboter requestWritePermission() {
      onView(withId(VIEW_ID_WRITE)).perform(click());
      return this;
    }

    Roboter clickOnAllow() throws UiObjectNotFoundException {
      final UiObject allowPermissions = getButton("ALLOW");

      if (allowPermissions.exists()) {
        allowPermissions.click();
      }

      return this;
    }

    Roboter clickOnDeny() throws UiObjectNotFoundException {
      getButton("DENY").click();
      return this;
    }

    Roboter clickOnDoNotShowAgain() throws UiObjectNotFoundException {
      final UiObject doNotAskAgain = getButton("Don't ask again");
      doNotAskAgain.click();
      return this;
    }

    Roboter assertPermissionState(final Permission.State state) throws UiObjectNotFoundException {
      onView(withId(VIEW_ID_TEXT)).check(matches(withText(state.toString())));
      return this;
    }

    private static UiObject getButton(final String text) {
      final UiDevice device = UiDevice.getInstance(getInstrumentation());
      return device.findObject(new UiSelector().text(text));
    }
  }
}
