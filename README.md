# MKryptos

<p align="center">
  <img src="app/src/main/res/mipmap-xxhdpi/icon_main.png" alt="MKryptos Logo" width="100"/>
</p>

<p align="center">
  <strong>A comprehensive Android cryptography toolkit for secure message, file, and media encryption.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Min%20SDK-15-blue?style=flat-square"/>
  <img src="https://img.shields.io/badge/Target%20SDK-21-blue?style=flat-square"/>
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=flat-square&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?style=flat-square&logo=gradle&logoColor=white"/>
  <img src="https://img.shields.io/badge/Version-1.0-lightgrey?style=flat-square"/>
</p>

---

## Overview

**MKryptos** is an Android application developed by **Moun Defense and Security** that provides a full cryptographic toolkit directly on your mobile device. It supports symmetric and asymmetric encryption, digital signatures, key pair generation, and secure encryption of messages, photos, videos, and voice recordings — all with a clean, icon-driven interface.

---

## Features

| Category | Details |
|---|---|
| **Text Encryption** | Encrypt and decrypt messages using multiple algorithms |
| **Digital Signatures** | Sign and verify messages with RSA |
| **Media Encryption** | Encrypt/decrypt photos, videos, and audio recordings |
| **Key Management** | Generate, store, and manage cryptographic keys |
| **PRNG** | Generate cryptographically secure pseudo-random numbers |

### Supported Algorithms

**Symmetric Encryption**
- `DES` — Data Encryption Standard
- `3DES` — Triple DES for enhanced security
- `AES` — Advanced Encryption Standard (256-bit)
- `Blowfish` — Fast block cipher
- `RC4` — Stream cipher
- `IDEA` — International Data Encryption Algorithm

**Asymmetric Encryption & Key Agreement**
- `RSA` — Key sizes from 512 to 4096 bits
- `DSA` — Digital Signature Algorithm
- `ECDH` — Elliptic Curve Diffie-Hellman (secp224r1 to secp521r1)

**Authentication**
- `HMAC` — Hash-based Message Authentication Code

---

## Tech Stack

| Technology | Role |
|---|---|
| **Java** | Primary programming language |
| **Android SDK 15–21** | Target platform (Android 4.0.3 – 5.0 Lollipop) |
| **Gradle** | Build system and dependency management |
| **javax.crypto / java.security** | Core cryptographic operations |
| **SQLite (SQLiteOpenHelper)** | Persistent local key storage |
| **Android Support Library v7** | ActionBar compatibility across API levels |
| **android.util.Base64** | Binary-to-text encoding for encrypted output |

---

## Project Structure

```
MKryptos/
├── app/
│   ├── build.gradle                  # App module build config
│   ├── libs/                         # External JAR dependencies
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # App manifest & permissions
│           ├── java/com/example/quoson/mkryptos/
│           │   ├── Activities/
│           │   │   ├── MainActivity.java             # Main navigation hub
│           │   │   ├── YourMessageActivity.java      # Text encrypt/decrypt
│           │   │   ├── YourSignatureActivity.java    # RSA signatures
│           │   │   ├── PhotoActivity.java            # Photo encryption
│           │   │   ├── VideoActivity.java            # Video encryption
│           │   │   ├── VoiceActivity.java            # Audio encryption
│           │   │   ├── KeysActivity.java             # Key generation
│           │   │   ├── YourKeysActivity.java         # Key management
│           │   │   ├── NumbersActivity.java          # PRNG
│           │   │   ├── AboutActivity.java            # App info
│           │   │   └── ShowInformationActivity.java  # Feature details
│           │   ├── Ciphers/
│           │   │   ├── AESCipher.java
│           │   │   ├── DESCipher.java
│           │   │   ├── TripleDESCipher.java
│           │   │   ├── BlowfishCipher.java
│           │   │   ├── RC4Cipher.java
│           │   │   ├── IDEACipher.java
│           │   │   ├── RSACipher.java
│           │   │   ├── DSACipher.java
│           │   │   ├── ECDHCipher.java
│           │   │   ├── HMACCipher.java
│           │   │   └── ModernCipher.java
│           │   └── Utils/
│           │       ├── DataKeys.java                 # SQLite DB helper
│           │       ├── ProgressKeys.java             # Async key generation
│           │       ├── PseudoRandomGenerator.java    # PRNG utility
│           │       ├── generatorKey.java             # AES key generator
│           │       └── ListKey.java                  # Key list helper
│           └── res/
│               ├── drawable/                         # Icons and images
│               ├── layout/                           # 13 XML layout files
│               ├── menu/                             # Menu definitions
│               ├── mipmap-{hdpi,mdpi,xhdpi,xxhdpi}/  # App icons
│               └── values/                           # Strings, styles, dimens
├── build.gradle                      # Root build config
├── settings.gradle                   # Module settings
├── gradle.properties                 # Gradle JVM properties
├── gradlew / gradlew.bat             # Gradle wrapper scripts
└── local.properties                  # Local SDK path (not committed)
```

---

## Requirements

- **Android Studio** 1.2+ (or any Gradle-compatible IDE)
- **JDK** 7 or higher
- **Android SDK** with API Level 15–21 installed
- **Android Build Tools** 21.1.2

---

## Building the Project

**1. Clone or download the project:**

```bash
git clone <repository-url>
cd MKryptos
```

**2. Configure the local SDK path** in `local.properties`:

```properties
sdk.dir=/path/to/your/android/sdk
```

**3. Build using the Gradle wrapper:**

```bash
# macOS / Linux
./gradlew assembleDebug      # Debug APK
./gradlew assembleRelease    # Release APK (requires signing config)
./gradlew clean build        # Clean then full build

# Windows
gradlew.bat assembleDebug
```

**4. Or open in Android Studio:**

```
File → Open → Select the MKryptos folder → Run 'app'
```

The generated APK will be located at:

```
app/build/outputs/apk/app-debug.apk
```

---

## Permissions

The application requires the following Android permissions:

| Permission | Purpose |
|---|---|
| `WRITE_EXTERNAL_STORAGE` | Save and load encrypted files (photos, videos, audio) |
| `RECORD_AUDIO` | Capture voice recordings for encryption |

---

## Architecture Notes

- **Single-module Gradle project** with the `:app` module.
- **Activity-based navigation** — each feature maps to a dedicated Activity.
- **Cipher classes are stateless utilities** — they wrap `javax.crypto` and `java.security` APIs with Android-friendly Base64 encoding.
- **Key persistence** is handled through SQLite via `DataKeys.java`, which manages a `KeysCipher` table storing key metadata and values.
- **Key generation** runs on a background thread via `ProgressKeys.java` (AsyncTask) to avoid blocking the UI during expensive RSA/EC operations.

---

<div align="center">

_Built for education. Encryption is fascinating — explore it._

</div>
