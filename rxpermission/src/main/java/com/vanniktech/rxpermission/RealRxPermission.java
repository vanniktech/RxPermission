package com.vanniktech.rxpermission;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.ChecksSdkIntAtLeast;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static com.vanniktech.rxpermission.Utils.checkPermissions;

/**
 * Real implementation of RxPermission that will show the usual Android dialog when requesting for permissions.
 */
public final class RealRxPermission implements RxPermission {
  static final Object TRIGGER = new Object();
  static RealRxPermission instance;

  static final String PREF_REQUESTED_PERMISSIONS = "requested-permissions";

  /**
   * @param context any context
   * @return a Singleton instance of this class
   */
  public static RealRxPermission getInstance(final Context context) {
    synchronized (RealRxPermission.class) {
      if (instance == null) {
        instance = new RealRxPermission((Application) context.getApplicationContext());
      }
    }

    return instance;
  }

  private final Application application;

  // Contains all the current permission requests. Once granted or denied, they are removed from it.
  private final Map<String, PublishSubject<Permission>> currentPermissionRequests = new HashMap<>();

  RealRxPermission(final Application application) {
    this.application = application;
  }

  /**
   * Requests permissions immediately, <b>must be invoked during initialization phase of your application</b>.
   */
  @Override @NonNull @CheckReturnValue public Observable<Permission> requestEach(@NonNull final String... permissions) {
    return Observable.just(TRIGGER)
        .compose(ensureEach(permissions));
  }

  /**
   * Requests the permission immediately, <b>must be invoked during initialization phase of your application</b>.
   */
  @Override @NonNull public Single<Permission> request(@NonNull final String permission) {
    return requestEach(permission)
        .firstOrError();
  }

  /**
   * Map emitted items from the source observable into {@link Permission} objects for each
   * permission in parameters.
   * <p>
   * If one or several permissions have never been requested, invoke the related framework method
   * to ask the user if he allows the permissions.
   */
  @NonNull @CheckReturnValue private <T> ObservableTransformer<T, Permission> ensureEach(@NonNull final String... permissions) {
    checkPermissions(permissions);

    return new ObservableTransformer<T, Permission>() {
      @Override @NonNull @CheckReturnValue public ObservableSource<Permission> apply(final Observable<T> o) {
        return request(o, permissions);
      }
    };
  }

  @NonNull @CheckReturnValue @SuppressWarnings("checkstyle:overloadmethodsdeclarationorder") Observable<Permission> request(final Observable<?> trigger, @NonNull final String... permissions) {
    return Observable.merge(trigger, pending(permissions))
        .flatMap(new Function<Object, Observable<Permission>>() {
          @Override @NonNull @CheckReturnValue public Observable<Permission> apply(final Object o) {
            return requestOnM(permissions);
          }
        });
  }

  @NonNull @CheckReturnValue private Observable<?> pending(@NonNull final String... permissions) {
    for (final String p : permissions) {
      if (!currentPermissionRequests.containsKey(p)) {
        return Observable.empty();
      }
    }

    return Observable.just(TRIGGER);
  }

  @NonNull @CheckReturnValue @TargetApi(M) Observable<Permission> requestOnM(@NonNull final String... permissions) {
    final List<Observable<Permission>> list = new ArrayList<>(permissions.length);
    final List<String> unrequestedPermissions = new ArrayList<>();

    // In case of multiple permissions, we create an observable for each of them.
    // At the end, the observables are combined to have a unique response.

    for (final String permission : permissions) {
      if (isGranted(permission)) {
        list.add(Observable.just(Permission.granted(permission)));
      } else if (isRevokedByPolicy(permission)) {
        list.add(Observable.just(Permission.revokedByPolicy(permission)));
      } else {
        PublishSubject<Permission> subject = currentPermissionRequests.get(permission);

        // Create a new subject if not exists
        if (subject == null) {
          unrequestedPermissions.add(permission);
          subject = PublishSubject.create();
          currentPermissionRequests.put(permission, subject);
        }

        list.add(subject);
      }
    }

    if (!unrequestedPermissions.isEmpty()) {
      final String[] permissionsToRequest = unrequestedPermissions.toArray(new String[0]);
      startShadowActivity(permissionsToRequest);
    }

    return Observable.concat(Observable.fromIterable(list));
  }

  /**
   * Returns true if the permission is already granted.
   * <p>
   * Always true if SDK &lt; 23.
   */
  @Override @CheckReturnValue public boolean isGranted(@NonNull final String permission) {
    return !isMarshmallow() || isGrantedOnM(permission);
  }

  /**
   * Returns true if the permission has been revoked by a policy.
   * <p>
   * Always false if SDK &lt; 23.
   */
  @Override @CheckReturnValue public boolean isRevokedByPolicy(@NonNull final String permission) {
    return isMarshmallow() && isRevokedOnM(permission);
  }

  @TargetApi(M) private boolean isGrantedOnM(final String permission) {
    return application.checkSelfPermission(permission) == PERMISSION_GRANTED;
  }

  @TargetApi(M) private boolean isRevokedOnM(final String permission) {
    return application.getPackageManager().isPermissionRevokedByPolicy(permission, application.getPackageName());
  }

  void startShadowActivity(final String[] permissions) {
    final SharedPreferences sharedPreferences = getSharedPreferences();
    final Set<String> requestedPermission = new HashSet<>(sharedPreferences.getStringSet(PREF_REQUESTED_PERMISSIONS, Collections.emptySet()));
    requestedPermission.addAll(Arrays.asList(permissions));

    sharedPreferences
        .edit()
        .putStringSet(PREF_REQUESTED_PERMISSIONS, requestedPermission)
        .apply();

    ShadowActivity.start(application, permissions);
  }

  void onRequestPermissionsResult(@NonNull final int[] grantResults, @NonNull final boolean[] rationale, @NonNull final boolean[] rationaleAfter, @NonNull final String... permissions) {
    final int size = permissions.length;

    for (int i = 0; i < size; i++) {
      final PublishSubject<Permission> subject = currentPermissionRequests.get(permissions[i]);

      if (subject == null) {
        throw new IllegalStateException("RealRxPermission.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
      }

      currentPermissionRequests.remove(permissions[i]);

      final boolean granted = grantResults[i] == PERMISSION_GRANTED;

      if (granted) {
        subject.onNext(Permission.granted(permissions[i]));
      } else if (!rationale[i] && !rationaleAfter[i]) {
        subject.onNext(Permission.deniedNotShown(permissions[i]));
      } else {
        subject.onNext(Permission.denied(permissions[i]));
      }

      subject.onComplete();
    }
  }

  @ChecksSdkIntAtLeast(api = M) boolean isMarshmallow() {
    return SDK_INT >= M;
  }

  void cancelPermissionsRequests() {
    currentPermissionRequests.clear();
  }

  @Override public boolean hasRequested(@NotNull String permission) {
    final SharedPreferences sharedPreferences = getSharedPreferences();
    final Set<String> requestedPermission = sharedPreferences.getStringSet(PREF_REQUESTED_PERMISSIONS, Collections.emptySet());
    return requestedPermission.contains(permission);
  }

  private SharedPreferences getSharedPreferences() {
    return application.getSharedPreferences("RxPermission", Context.MODE_PRIVATE);
  }
}
