package com.vanniktech.rxpermission;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.annotations.CheckReturnValue;
import io.reactivex.rxjava3.annotations.NonNull;

public interface RxPermission {
  /** Requests a single permission. */
  @NonNull @CheckReturnValue Single<Permission> request(@NonNull String permission);

  /** Requests multiple permissions. */
  @NonNull @CheckReturnValue Observable<Permission> requestEach(@NonNull String... permissions);

  /** Returns true when the given permission is granted. */
  @CheckReturnValue boolean isGranted(@NonNull String permission);

  /** Returns true when the given permission is revoked by a policy. */
  @CheckReturnValue boolean isRevokedByPolicy(@NonNull String permission);
}
