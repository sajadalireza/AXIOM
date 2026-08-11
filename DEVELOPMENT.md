# AXIOM Development

## Prerequisites

- JDK 17 or newer. JDK 17 is the Android Gradle Plugin 9.1.1 baseline; JDK 21 is also supported by the pinned Gradle runtime and is used for local verification.
- Android SDK with platform 36 and Build Tools 36.0.0.
- Network access for the first Gradle distribution and dependency download.

Set `ANDROID_HOME` to the installed SDK when it is not discovered by Android Studio or `local.properties`:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
```

Do not install a system Gradle version for this project. Use the checked-in wrapper for every build.

## Canonical Commands

```bash
./gradlew tasks
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew lintDebug
```

The debug build does not require release signing credentials. Optional Supabase and Google values can be supplied through environment variables, `.env`, or `local.properties`; never commit those local files.

## Pinned Build Runtime

- Android Gradle Plugin: `9.1.1`
- Gradle distribution: `9.3.1-bin`
- Distribution SHA-256: `b266d5ff6b90eada6dc3b20cb090e3731302e553a27c5d3e4df1f0d76beaff06`
- Minimum JDK: `17`

`gradle/wrapper/gradle-wrapper.properties` pins both the distribution URL and SHA-256. Changes to the Gradle version, URL, checksum, wrapper JAR, or launch scripts must be reviewed as a build-system change and regenerated with the official Gradle `wrapper` task.

## Wrapper Verification

From a fresh clone:

```bash
./gradlew --version
./gradlew tasks
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

The first command downloads Gradle only when it is absent from the local wrapper cache and rejects a distribution whose SHA-256 does not match the pinned value.
