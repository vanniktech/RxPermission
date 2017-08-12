package com.vanniktech.rxpermission;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

import static com.vanniktech.rxpermission.Permission.State.DENIED;
import static com.vanniktech.rxpermission.Permission.State.DENIED_NOT_SHOWN;
import static com.vanniktech.rxpermission.Permission.State.GRANTED;
import static com.vanniktech.rxpermission.Permission.State.REVOKED_BY_POLICY;

@AutoValue public abstract class Permission {
  @CheckResult public static Permission granted(final String name) {
    return new AutoValue_Permission(name, GRANTED);
  }

  @CheckResult public static Permission denied(final String name) {
    return new AutoValue_Permission(name, DENIED);
  }

  @CheckResult public static Permission deniedNotShown(final String name) {
    return new AutoValue_Permission(name, DENIED_NOT_SHOWN);
  }

  @CheckResult public static Permission revokedByPolicy(final String name) {
    return new AutoValue_Permission(name, REVOKED_BY_POLICY);
  }

  @NonNull public abstract String name();

  @NonNull public abstract State state();

  public enum State {
    /** Permission has been granted. */
    GRANTED,

    /** Permission has been denied. */
    DENIED,

    /**
     * Permission is denied.
     * Previously the requested permission was denied and never ask again was selected.
     * This means that the user hasn't seen the permission dialog.
     * The only way to let the user grant the permission is via the settings now.
     */
    DENIED_NOT_SHOWN,

    /** Permission has been denied by a policy. */
    REVOKED_BY_POLICY
  }
}
