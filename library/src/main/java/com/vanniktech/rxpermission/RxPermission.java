package com.vanniktech.rxpermission;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;

public interface RxPermission {
  /** Requests a single permission. */
  @NonNull @CheckReturnValue Single<Permission> request(@NonNull String permission);

  /** Requests multiple permissions. */
  @NonNull @CheckReturnValue Observable<Permission> requestEach(@NonNull String... permissions);

  /** Requests multiple permissions and return one result boolean granted for all permissions */
  @NonNull @CheckReturnValue Single<Boolean> requestEachToSingle(@NonNull String... permissions);

  /** Returns true when the given permission is granted. */
  @CheckReturnValue boolean isGranted(@NonNull String permission);

  /** Returns true when the given permission is revoked by a policy. */
  @CheckReturnValue boolean isRevokedByPolicy(@NonNull String permission);
}
