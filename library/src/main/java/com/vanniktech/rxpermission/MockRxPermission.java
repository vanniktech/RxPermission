package com.vanniktech.rxpermission;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;

import static com.vanniktech.rxpermission.Permission.State.GRANTED;
import static com.vanniktech.rxpermission.Permission.State.REVOKED_BY_POLICY;
import static com.vanniktech.rxpermission.Utils.checkPermissions;

/**
 * Mocking class that can be used for unit testing.
 * The passed in stubs from the constructor will be used in each of the methods.
 */
public final class MockRxPermission implements RxPermission {
  private final Permission[] permissions;
  private final String NO_PERMISSION_CONFIGURED = "No permission was pre-configured for ";

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

    return Single.error(new IllegalStateException(NO_PERMISSION_CONFIGURED + permission));
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

            throw new IllegalStateException(NO_PERMISSION_CONFIGURED + permission);
          }
        });
  }

  @Override @NonNull @CheckReturnValue public Single<Boolean> requestEachToSingle(@NonNull final String... requestPermissions) {
    checkPermissions(requestPermissions);
    return Observable.fromArray(requestPermissions)
            .map(new Function<String, Permission>() {
              @Override public Permission apply(final String permission) throws Exception { // NOPMD
                final Permission p = get(permission);

                if (p != null) {
                  return p;
                }

                throw new IllegalStateException(NO_PERMISSION_CONFIGURED + permission);
              }
            })
            .toList()
            .flatMap(new Function<List<Permission>, Single<Boolean>>() {
              @Override public Single<Boolean> apply(final List<Permission> permissions) throws Exception {
                boolean granted = true;
                for (Permission perm : permissions) {
                  if (perm.state() != Permission.State.GRANTED) {
                    granted = false;
                  }
                }
                return Single.just(granted);
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
      if (permission.name().equals(name)) {
        return permission;
      }
    }

    return null;
  }
}
