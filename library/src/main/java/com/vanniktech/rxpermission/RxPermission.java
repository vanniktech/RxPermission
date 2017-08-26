package com.vanniktech.rxpermission;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;

public interface RxPermission {
  /** Requests a single permission. */
  @NonNull @CheckReturnValue Single<Permission> request(@NonNull String permission);

  /** Requests multiple permissions. */
  @NonNull @CheckReturnValue Observable<Permission> requestEach(@NonNull String... permissions);

  /** Returns true when the given permission is granted. */
  @CheckResult boolean isGranted(@NonNull String permission);

  /** Returns true when the given permission is revoked by a policy. */
  @CheckResult boolean isRevokedByPolicy(@NonNull String permission);
}
