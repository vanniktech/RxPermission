RxPermission
============

This library wraps the Android Runtime Permissions with RxJava 2. It's based on the [RxPermissions](https://github.com/tbruyelle/RxPermissions) library and was adjusted with simplicity in mind. Here are a few things that are different:

- API is really small and focused
- Uses a shadowing Activity to request the permission which allows you to use the library within Services, Broadcastreceiver etc.
- Supports the 'Never ask again' case

# Download

```groovy
implementation 'com.vanniktech:rxpermission:0.10.0'
implementation 'com.vanniktech:rxpermission:0.11.0-SNAPSHOT'
```

# Usage

The core functionality is provided via an interface:

```kotlin
interface RxPermission {
  /** Requests a single permission.  */
  @CheckReturnValue fun request(permission: String): Single<Permission>

  /** Requests multiple permissions.  */
  @CheckReturnValue fun requestEach(vararg permissions: String): Observable<Permission>

  /** Returns true when the given permission is granted.  */
  @CheckReturnValue fun isGranted(permission: String): Boolean

  /** Returns true when the given permission is revoked by a policy.  */
  @CheckReturnValue fun isRevokedByPolicy(permission: String): Boolean

  /** Returns tue when the given permission has been request at least once using either [request] or [requestEach].  */
  @CheckReturnValue fun hasRequested(permission: String): Boolean
}
```

And the Permission class:

```java
public class Permission {
  /** The name of the permission. For instance android.permission.CAMERA */
  @NonNull public String name();

  /** The state of the permission. */
  @NonNull public State state();

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
```

## Production

For your Android application you can get an instance of the interface via `RealRxPermission.getInstance(application)` and then simply use the above mentioned methods to your needs.

```java
RealRxPermission.getInstance(application)
    .request(Manifest.permission.CAMERA)
    .subscribe();
```

## Testing

In addition the library offers you a `MockRxPermission` that can be used for testing.

```gradle
implementation 'com.vanniktech:rxpermission-testing:0.10.0'
```

The constructor takes a vararg of Permissions.

```java
new MockRxPermission(Permission.denied(Manifest.permission.CAMERA))
    .request(Manifest.permission.CAMERA)
    .test()
    .assertResult(Permission.denied(Manifest.permission.CAMERA));
```

The Permission class provides you a few static factory methods:

```java
/** This will create a granted Camera Permission instance. */
Permission.granted(Manifest.permission.CAMERA)

/** This will create a denied Camera Permission instance. */
Permission.denied(Manifest.permission.CAMERA)

/** This will create a denied not shown Camera Permission instance. */
Permission.deniedNotShown(Manifest.permission.CAMERA)

/** This will create a revoked by policy Camera Permission instance. */
Permission.revokedByPolicy(Manifest.permission.CAMERA)
```

## Sample

Also checkout the sample app that shows you how to use the library.

# License

Copyright (C) 2017 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0
