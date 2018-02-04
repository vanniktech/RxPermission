package com.vanniktech.rxpermission;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import static com.vanniktech.rxpermission.Permission.State.GRANTED;
import static com.vanniktech.rxpermission.Permission.State.REVOKED_BY_POLICY;
import static com.vanniktech.rxpermission.Utils.checkPermissions;

public final class MockRxPermission implements RxPermission {
  private final Permission[] permissions;

  public MockRxPermission(final Permission... permissions) {
    this.permissions = permissions;
  }

  @NonNull @Override @SuppressWarnings("PMD.GuardLogStatement") /* False positive - https://github.com/pmd/pmd/issues/869 */ public Single<Permission> request(@NonNull final String permission) {
    if (permission == null) {
      throw new IllegalArgumentException("permission == null");
    }

    final Permission p = get(permission);

    if (p != null) {
      return Single.just(p);
    }

    return Single.error(new IllegalStateException("No permission was pre-configured for " + permission));
  }

  @Override @NonNull public Observable<Permission> requestEach(@NonNull final String... requestPermissions) {
    checkPermissions(requestPermissions);
    return Observable.fromArray(requestPermissions)
        .map(new Function<String, Permission>() {
          @Override public Permission apply(final String permission) throws Exception { // NOPMD
            final Permission p = get(permission);

            if (p != null) {
              return p;
            }

            throw new IllegalStateException("No permission was pre-configured for " + permission);
          }
        });
  }

  @Override @CheckResult public boolean isGranted(@NonNull final String permission) {
    final Permission p = get(permission);
    return p != null && p.state() == GRANTED;
  }

  @Override @CheckResult public boolean isRevokedByPolicy(@NonNull final String permission) {
    final Permission p = get(permission);
    return p != null && p.state() == REVOKED_BY_POLICY;
  }

  @Nullable Permission get(@NonNull final String name) {
    for (final Permission permission : permissions) {
      if (permission.name().equals(name)) {
        return permission;
      }
    }

    return null;
  }
}
