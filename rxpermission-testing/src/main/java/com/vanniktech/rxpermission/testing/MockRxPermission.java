package com.vanniktech.rxpermission.testing;

import com.vanniktech.rxpermission.Permission;
import com.vanniktech.rxpermission.RxPermission;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;
import java.util.HashSet;
import java.util.Set;

import static com.vanniktech.rxpermission.Permission.State.GRANTED;
import static com.vanniktech.rxpermission.Permission.State.REVOKED_BY_POLICY;
import static com.vanniktech.rxpermission.testing.Utils.checkPermissions;

/**
 * Mocking class that can be used for unit testing.
 * The passed in stubs from the constructor will be used in each of the methods.
 */
public final class MockRxPermission implements RxPermission {
  private final Permission[] permissions;

  private final Set<String> requestedPermissions = new HashSet<>();

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

  @Override @CheckReturnValue public boolean isGranted(@NonNull final String permission) {
    final Permission p = get(permission);
    return p != null && p.state() == GRANTED;
  }

  @Override @CheckReturnValue public boolean isRevokedByPolicy(@NonNull final String permission) {
    final Permission p = get(permission);
    return p != null && p.state() == REVOKED_BY_POLICY;
  }

  @Nullable Permission get(@NonNull final String name) {
    for (final Permission permission : permissions) {
      requestedPermissions.add(name);

      if (permission.name().equals(name)) {
        return permission;
      }
    }

    return null;
  }

  @Override public boolean hasRequested(@NonNull final String permission) {
    return requestedPermissions.contains(permission);
  }
}
