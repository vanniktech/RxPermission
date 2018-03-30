package com.vanniktech.rxpermission;

import com.google.auto.value.AutoValue;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;

import static com.vanniktech.rxpermission.Permission.State.DENIED;
import static com.vanniktech.rxpermission.Permission.State.DENIED_NOT_SHOWN;
import static com.vanniktech.rxpermission.Permission.State.GRANTED;
import static com.vanniktech.rxpermission.Permission.State.REVOKED_BY_POLICY;

@AutoValue public abstract class Permission {
  @CheckReturnValue public static Permission granted(final String name) {
    return new AutoValue_Permission(name, GRANTED);
  }

  @CheckReturnValue public static Permission denied(final String name) {
    return new AutoValue_Permission(name, DENIED);
  }

  @CheckReturnValue public static Permission deniedNotShown(final String name) {
    return new AutoValue_Permission(name, DENIED_NOT_SHOWN);
  }

  @CheckReturnValue public static Permission revokedByPolicy(final String name) {
    return new AutoValue_Permission(name, REVOKED_BY_POLICY);
  }

  /** The name of the permission. For instance android.permission.CAMERA */
  @NonNull public abstract String name();

  /** The state of the permission. */
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

    /** Permission has been revoked by a policy. */
    REVOKED_BY_POLICY
  }
}
