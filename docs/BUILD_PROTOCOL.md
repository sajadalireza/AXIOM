# WARRIOR1 Build Protocol Documentation

## Overview

This document describes the comprehensive build protocol for the WARRIOR1 Android application. The protocol is implemented through GitHub Actions workflows that automate building, testing, and releasing the application.

## Build Architecture

### Technology Stack
- **Build System**: Gradle (Kotlin DSL)
- **Language**: Kotlin (77.6%) + Java (22.4%)
- **Target SDK**: Android 35
- **Min SDK**: Android 26
- **JDK**: Java 17
- **CI/CD**: GitHub Actions

### Key Dependencies
- Jetpack Compose (UI Framework)
- Hilt (Dependency Injection)
- Room (Local Database)
- Retrofit + Moshi (Networking)
- Supabase (Backend)
- Google Generative AI (Gemini)

## Workflows

### 1. Android Build Protocol (`android-build.yml`)

**Triggers**:
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`
- Manual workflow dispatch

**Build Types**:
- **Debug**: For development and testing
- **Release**: For production (requires signing credentials)

**Steps**:
1. Checkout code with full history
2. Setup JDK 17
3. Load environment variables from secrets
4. Create `.env` file with Supabase and Google credentials
5. Validate Gradle wrapper
6. Run Supabase diagnostics
7. Assemble APK (debug or release)
8. Upload artifacts
9. Generate lint reports
10. Comment on PR with build status

**Artifacts**:
- `app-debug/*.apk` (Debug builds)
- `app/build/outputs/apk/release/AXIOM.apk` (Signed release builds)
- `lint-report/lint-results*.html` (Lint reports)

### 2. Test Protocol (`test-protocol.yml`)

**Triggers**:
- Push to `main` or `develop`
- Pull requests
- Daily schedule (midnight UTC)

**Test Suites**:

#### Unit Tests
- Runs on Ubuntu
- Uses JUnit framework
- Tests Kotlin/Java code logic
- Generates XML reports

**Reports**:
- `test-results/` (Unit test reports)

### 3. Quality Checks Protocol (`quality-checks.yml`)

**Code Quality Tools**:

#### Android Lint
- Android-specific code analysis
- Detects API compatibility issues

**Reports**:
- `quality-reports/` (All lint reports)

### 4. Release Build Protocol (`release-protocol.yml`)

**Triggers**:
- Git tags matching `v*` pattern
- Manual workflow dispatch

**Steps**:
1. Checkout code
2. Setup JDK 17
3. Load signing keystore from secrets
4. Create signing configuration
5. Build AAB (Android App Bundle) for Play Store
6. Build signed APK
7. Create GitHub release
8. Upload artifacts to release
9. Clean up sensitive files

**Artifacts**:
- `*.aab` (Android App Bundle for Play Store)
- `*.apk` (Signed APK)

### 5. Nightly Build Protocol (`nightly-build.yml`)

**Triggers**:
- Daily schedule (2 AM UTC)
- Manual workflow dispatch

**Purpose**:
- Continuous integration testing
- Early detection of issues
- Provides latest development builds

**Artifacts**:
- Nightly APK with timestamp (retained 7 days)

### 6. PR Checks Workflow (`pr-checks.yml`)

**Triggers**:
- Pull request opened, synchronized, or reopened

**Purpose**:
- Automatic build and test verification
- Auto-comment with build status

## Required Secrets

Configure these secrets in GitHub repository settings:

```
SUPABASE_URL          - Supabase project URL
SUPABASE_KEY          - Supabase API key (anon or service_role)
GOOGLE_WEB_CLIENT_ID  - Google OAuth client ID

KEYSTORE_BASE64       - Release keystore encoded in base64
KEYSTORE_PASSWORD     - Keystore password
KEY_ALIAS             - Key alias in keystore
KEY_PASSWORD          - Key password
```

### Setting up Keystore Secret

```bash
# Encode keystore to base64
base64 -i release.keystore > keystore.b64

# Copy content to KEYSTORE_BASE64 secret
cat keystore.b64
```

## Environment Variables

The build system supports loading environment variables from multiple sources (in order of precedence):

1. **GitHub Secrets** (via `${{ secrets.* }}`)
2. **.env file** in project root
3. **local.properties** (for local development)
4. **Environment variables** (system-level)

### .env File Example

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
GOOGLE_WEB_CLIENT_ID=your-client-id.apps.googleusercontent.com
```

## Font Management

Required fonts are vendored in `app/src/main/res/font/`; builds never download or generate font files.

### Fonts Included
- **Outfit** variable font
- **Fraunces** upright and italic variable fonts
- **Fira Code** Regular and Medium

`Inter` and `JetBrainsMono` remain compatibility aliases for Outfit and Fira Code in the Compose theme. Official source pins, SHA-256 values, and SIL OFL 1.1 notices are packaged under `app/src/main/assets/licenses/fonts/`.

## Build Customization

### Custom Gradle Tasks

#### verifyVendoredFonts
Verifies every vendored font against its pinned SHA-256 without network access. Android `preBuild` runs this task automatically:
```bash
./gradlew verifyVendoredFonts
```

#### renamePackages
Automatic package renaming utility:
```bash
./gradlew renamePackages
```

#### rebrandStrings
Automatic string rebranding:
```bash
./gradlew rebrandStrings
```

## Local Development Build

### Prerequisites
- JDK 17 or later
- Android SDK (API 26-35)
- Gradle 8.5+

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Signed release build (requires keystore configuration)
./gradlew assembleRelease

# Run tests
./gradlew test

# Quality checks
./gradlew lint

# Build with custom JVM memory
./gradlew assembleDebug -Dorg.gradle.jvmargs="-Xmx4096m"

# Verify vendored font integrity
./gradlew verifyVendoredFonts
```

## Troubleshooting

### Build Failures

#### Vendored Font Integrity Error
```
Missing vendored font: src/main/res/font/fira_code_medium.ttf
```
**Solution**: Restore the required font from its immutable source recorded in `app/src/main/assets/licenses/fonts/README.md`. Do not create a fallback file. For an intentional asset update, update the binary, checksum, source pin, and license evidence together.

#### Supabase Connection Error
```
❌ ERROR: SUPABASE_URL is not set!
```
**Solution**: Set `SUPABASE_URL` in secrets or `.env` file.

#### Keystore Issues (Release Build)
```
Release build requested but no valid KEYSTORE_PATH found
```
**Solution**: Configure keystore secrets or provide keystore file.

#### Gradle Memory Issues
```
Gradle ran out of memory
```
**Solution**: Increase JVM memory: `-Dorg.gradle.jvmargs="-Xmx4096m"`

### Dependency Resolution

If you encounter dependency resolution issues:

```bash
# Clear Gradle cache
./gradlew clean

# Rebuild with dependency tree
./gradlew dependencies

# Force dependency update
./gradlew --refresh-dependencies
```

## Best Practices

1. **Always run tests before committing**
   ```bash
   ./gradlew test
   ```

2. **Run quality checks regularly**
   ```bash
   ./gradlew lint
   ```

3. **Keep dependencies updated**
   - Review dependency updates monthly
   - Update gradually and test thoroughly

4. **Use proper branching**
   - `develop` for feature development
   - `main` for production-ready code
   - Tag releases with semantic versioning (v1.0.0)

5. **Review workflow logs**
   - Check GitHub Actions for detailed build logs
   - Use logs to diagnose issues early

## Performance Optimization

### Gradle Build Optimization

```gradle
org.gradle.parallel=true
org.gradle.workers.max=4
org.gradle.jvmargs=-Xmx4096m
android.useAndroidX=true
android.enableJetifier=false
```

### CI/CD Optimization

- **Gradle wrapper caching**: Enabled in all workflows
- **Artifact retention**: Set to 30 days for builds, 7 days for nightly

## Version Management

### Semantic Versioning
- **MAJOR**: Breaking changes (increment from 1.0 to 2.0)
- **MINOR**: New features (increment from 1.0 to 1.1)
- **PATCH**: Bug fixes (increment from 1.0 to 1.0.1)

### Current Version
- **Code**: 3 (versionCode in build.gradle.kts)
- **Name**: 1.2.0 (versionName)

## GitHub Actions Configuration

### Enabling Workflows

1. Go to Settings → Actions → General
2. Ensure "Allow all actions and reusable workflows" is selected
3. Workflows automatically run on push/PR

### Viewing Build Status

- Check Actions tab on GitHub repository
- Pull request comments show build status
- Individual workflow runs show detailed logs

## Additional Resources

- [Gradle Documentation](https://docs.gradle.org/)
- [Android Build System](https://developer.android.com/studio/build)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
