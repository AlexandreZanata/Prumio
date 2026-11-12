# Android release signing

Version 0.1.0 is prepared. The owner has no release keystore yet and requested an APK ready for
signing. No key was generated, no release installation or GitHub release publication is implied.

## Prepare the artifact

Run `./scripts/verify.sh`, then `./gradlew :app:android:assembleRelease`.
Without repository-root signing.properties the output is
`app/android/build/outputs/apk/release/android-release-unsigned.apk`. It is not installable until
signed. R8/resource shrinking are enabled for release. Never install release builds during QA;
use app.prumio.debug on the owner's physical phone.

## Configure the owner's key later

Copy signing.properties.example to signing.properties. Set storeFile to the owner's local
keystore path and fill storePassword, keyAlias and keyPassword locally. Do not send these values
in chat or commit them. The keystore, real properties and APKs are ignored by git. Back up the
key and credentials securely: future updates require the same identity.

With a complete configuration, `assembleRelease` signs with that key. A partial configuration
fails without printing secrets. Before publishing, verify using Android SDK apksigner:
`apksigner verify --verbose --print-certs <signed-apk>` and review the certificate fingerprint
with the owner. The debug key must never sign a public release.

## Existing unsigned APK

An owner-managed signing process can use SDK zipalign followed by apksigner sign with the private
key, then apksigner verify. Let apksigner prompt for passwords; never put them in shell history.
Do not attach an unsigned APK to a public release as though it were signed.

G4 still needs owner dogfooding and remaining device-matrix QA before signed release publication.
