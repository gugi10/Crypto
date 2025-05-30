# Generating a Signed APK/AAB for CryptoTracker

## 1. Overview

All Android applications must be digitally signed with a certificate before they can be installed on a device or published to app stores like Google Play. The digital signature is used to verify the app's authenticity and integrity, ensuring that the app code has not been tampered with since it was signed.

This document provides instructions on how to:
- Generate a private signing key and keystore.
- Configure your Gradle build to use this key for release builds.
- Generate a signed APK or Android App Bundle (AAB).

## 2. Generating a Keystore

You need a private key to sign your application. This key is stored in a **keystore** file (`.jks` or `.keystore` extension).

**Using `keytool` (Java JDK Command-Line Utility)**

The Java Development Kit (JDK) includes a utility called `keytool` that can be used to generate a keystore and a private key.

**Example `keytool` Command:**

Open your terminal or command prompt and run the following command:

```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

Let's break down the command:
-   `-genkey`: Tells `keytool` to generate a key.
-   `-v`: Verbose output.
-   `-keystore my-release-key.jks`: Specifies the name and location of the keystore file to be created. Replace `my-release-key.jks` with your desired filename.
-   `-keyalg RSA`: Specifies the algorithm to be used for the key (RSA is common).
-   `-keysize 2048`: Specifies the size of the key (2048 bits is recommended).
-   `-validity 10000`: Specifies the validity period of the key in days (10000 days is about 27 years).
-   `-alias my-key-alias`: Specifies the alias for the key entry within the keystore. Replace `my-key-alias` with your desired alias name.

You will be prompted to:
-   Create a **keystore password**.
-   Provide details for the certificate (Name, Organization, City, etc.).
-   Create a **key password** for the specific alias. It's recommended to use the same password as the keystore password for simplicity, but you can choose a different one.

**Important:**
-   **Store your keystore file (`.jks`) in a secure location.** You will need this file for every release and to publish updates. Losing it means you won't be able to publish updates to your app under the same identity.
-   **Keep your keystore password and key alias password extremely secure.** Store them in a password manager or another secure place.
-   **Do NOT add the keystore file to your version control system (e.g., Git).**

## 3. Configuring Gradle for Signing

You need to tell Gradle how to use your keystore to sign release builds.

### Option A: Using `signingConfigs` in `app/build.gradle.kts` (Recommended)

This method is good for consistency and automated builds (like CI/CD). However, **never hardcode passwords directly in `build.gradle.kts` if the file is committed to version control.**

1.  **Store Keystore Properties Securely**:
    Place your keystore file in a secure location outside your project's main directory if possible, or within the project but ensure it's added to `.gitignore`.
    Store your keystore path, alias, and passwords in your user-level `gradle.properties` file (usually located at `~/.gradle/gradle.properties` on Linux/macOS or `C:\Users\<YourUser>\.gradle\gradle.properties` on Windows), or use environment variables.

    Example for `gradle.properties` (ensure this file is in your global `.gitignore` or project's `.gitignore` if stored locally):
    ```properties
    # In ~/.gradle/gradle.properties or a project-local gradle.properties (add to .gitignore)
    MYAPP_RELEASE_STORE_FILE=/path/to/your/my-release-key.jks
    MYAPP_RELEASE_STORE_PASSWORD=your_keystore_password
    MYAPP_RELEASE_KEY_ALIAS=my-key-alias
    MYAPP_RELEASE_KEY_PASSWORD=your_key_alias_password
    ```

2.  **Configure `signingConfigs` in `app/build.gradle.kts`**:
    Modify your `app/build.gradle.kts` file to include a `signingConfigs` block and reference it in your `release` build type.

    ```kotlin
    // In app/build.gradle.kts

    android {
        // ... other configurations ...

        signingConfigs {
            create("release") {
                // Try to read from environment variables first, then from gradle.properties
                val storeFileProp = System.getenv("KEYSTORE_FILE") 
                    ?: project.findProperty("MYAPP_RELEASE_STORE_FILE")?.toString()
                val storePasswordProp = System.getenv("KEYSTORE_PASSWORD") 
                    ?: project.findProperty("MYAPP_RELEASE_STORE_PASSWORD")?.toString()
                val keyAliasProp = System.getenv("KEYSTORE_ALIAS") 
                    ?: project.findProperty("MYAPP_RELEASE_KEY_ALIAS")?.toString()
                val keyPasswordProp = System.getenv("KEYSTORE_ALIAS_PASSWORD") 
                    ?: project.findProperty("MYAPP_RELEASE_KEY_PASSWORD")?.toString()

                if (storeFileProp != null && storePasswordProp != null && keyAliasProp != null && keyPasswordProp != null) {
                    storeFile = file(storeFileProp)
                    storePassword = storePasswordProp
                    keyAlias = keyAliasProp
                    keyPassword = keyPasswordProp
                } else {
                    // Fallback or error if properties are not found
                    // For CI, you might throw an error if ENVs are not set.
                    // For local development, you might fall back to debug signing or skip.
                    println("Release signing config not found. Using debug signing for release build.")
                    // This often defaults to using the debug keystore if not explicitly set,
                    // which is fine for local testing of a release build but not for actual publishing.
                }
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                // Apply the signing configuration to the release build type
                signingConfig = signingConfigs.getByName("release")
            }
            // ... debug build type ...
        }
    }
    ```

### Option B: Providing Details at Build Time (Manual / Android Studio)

If you don't configure `signingConfigs` in Gradle, Android Studio will prompt you for the keystore path, alias, and passwords when you use its "Generate Signed Bundle / APK" wizard. This is suitable for manual builds but not for automated CI/CD pipelines.

## 4. Generating the Signed APK/AAB

Once signing is configured (or if you plan to provide details manually):

**Using Android Studio:**
1.  Go to "Build" > "Generate Signed Bundle / APK...".
2.  Choose "Android App Bundle" (AAB - recommended for Google Play) or "APK".
3.  Follow the wizard. If you configured `signingConfigs` in Gradle and it finds the properties, it might pre-fill some details or use them automatically. Otherwise, you'll be prompted to select your keystore, enter passwords, and choose the key alias.
4.  Select the "release" build variant.
5.  Choose the destination folder for the signed AAB/APK.

**Using Gradle Commands:**
These commands work if signing is correctly configured in your `app/build.gradle.kts` file. Open a terminal in your project's root directory.

-   **For Android App Bundle (AAB - recommended for Google Play):**
    ```bash
    ./gradlew bundleRelease
    ```
    The signed AAB will be located in `app/build/outputs/bundle/release/`.

-   **For APK:**
    ```bash
    ./gradlew assembleRelease
    ```
    The signed APK will be located in `app/build/outputs/apk/release/`.

## 5. Important Security Notes

-   **NEVER commit your keystore file (`.jks`) to version control (e.g., Git).** Add its filename to your `.gitignore` file.
-   **NEVER hardcode passwords directly in `build.gradle.kts` if the file is version controlled.**
    -   Use `gradle.properties` (added to `.gitignore` if it contains sensitive info and is project-local) or environment variables for storing sensitive credentials.
    -   For shared projects, each developer can have their own `gradle.properties` or set up environment variables. CI systems should use secure environment variables.
-   **Back up your keystore file and securely store its passwords.** If you lose your keystore or its passwords, you will NOT be able to publish updates to your app under the same app listing on Google Play.
-   Consider using Google Play App Signing, where Google manages your app signing key for you after you upload your initial signed AAB. This can help mitigate the risk of losing your upload key.

---
This document provides a basic guide. Always refer to the official Android Developer documentation for the most up-to-date and detailed information on app signing.
