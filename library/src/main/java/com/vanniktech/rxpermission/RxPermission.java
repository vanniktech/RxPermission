package com.vanniktech.rxpermission;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;

public interface RxPermission {
  @NonNull @CheckReturnValue Single<Permission> request(@NonNull String permission);

  @NonNull @CheckReturnValue Observable<Permission> requestEach(@NonNull String... permissions);

  @CheckResult boolean isGranted(@NonNull String permission);

  @CheckResult boolean isRevokedByPolicy(@NonNull String permission);
}
