# Toolchain compatibility matrix

Use this when bumping AGP, Kotlin, Hilt or KSP. Versions below are the ones
the project has actually been built with.

## Current pinned versions (v0.6.1)

| Component                    | Version           | Notes                                                  |
|------------------------------|-------------------|--------------------------------------------------------|
| JDK                          | 21                | Required by AGP 9.                                     |
| Gradle                       | 9.1+              | Required by AGP 9 (see `gradle/wrapper`).              |
| Android Gradle Plugin (AGP)  | 9.1.1             | Has built-in Kotlin support.                            |
| Kotlin                       | 2.3.10            | Aligned with Compose plugin 2.3.10.                     |
| Compose Compiler plugin      | 2.3.10            | `org.jetbrains.kotlin.plugin.compose`.                  |
| KSP                          | 2.3.6             | Decoupled from Kotlin version since 2.3.0.              |
| Hilt                         | 2.59.2            | Requires AGP 9 since Dagger 2.59.                       |
| hilt-navigation-compose      | 1.2.0             | For `hiltViewModel()` in Compose.                       |
| compileSdk / targetSdk       | 36                |                                                         |
| minSdk                       | 27                |                                                         |

## Hard rules

- AGP 9 ships with built-in Kotlin support; **do not** add the
  `org.jetbrains.kotlin.android` plugin to `app/build.gradle.kts`.
- KAPT is not compatible with built-in Kotlin support — use **KSP only**
  for Hilt and any future annotation processors.
- Dagger/Hilt **2.59 or newer** is required for AGP 9. Anything ≤ 2.58
  fails with `Android BaseExtension not found`.
- KSP **2.3.x** uses the new versioning scheme (decoupled from Kotlin).
  Older `<kotlin>-<ksp>` style versions (e.g. `2.3.10-2.0.2`) do not
  exist — they will fail on the Plugin Portal.

## When bumping anything

1. Bump only one component at a time.
2. Run `./gradlew :app:dependencies --configuration releaseRuntimeClasspath`
   to confirm plugin/dependency resolution.
3. Run `./gradlew :app:assembleRelease` to confirm `kspReleaseKotlin` and
   the rest of the toolchain still work.
4. Run `./gradlew :app:assembleDebugAndroidTest` so the Hilt test
   processor stays in sync.
5. Update this file with the new versions.

## Known historical pitfalls

- **2026-04 — KSP version mistake.** Tried `com.google.devtools.ksp:2.3.10-2.0.2`
  expecting the legacy `<kotlin>-<ksp>` format. The KSP team dropped that
  format in 2.3.0; the correct id is just `2.3.x` (e.g. `2.3.6`).
- **2026-04 — KAPT incompatible with AGP 9 built-in Kotlin.** `kapt` plugin
  fails to apply. Switched everything to KSP.
- **2026-04 — Hilt 2.57 fails on AGP 9.** Bumped to 2.59.2 which added
  AGP 9 support (`HiltSyncTask` etc).
