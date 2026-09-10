#!/usr/bin/env bash
set -euo pipefail

SAMPLE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
: "${SABERA_SDK_PATH:?Set SABERA_SDK_PATH to the local jig-glass repository}"
export SABERA_SDK_PATH
SDK_APP="$SABERA_SDK_PATH/app"
test -d "$SDK_APP/glasses-sdk/ios/Sources/SaberaIOS"
mkdir -p "$SAMPLE_DIR/build/ios-sdk"
ditto "$SDK_APP/glasses-sdk/ios/Sources/SaberaIOS" "$SAMPLE_DIR/build/ios-sdk/SaberaIOS"
ditto "$SDK_APP/iosApp/iosApp/OggOpus.xcframework" "$SAMPLE_DIR/build/ios-sdk/OggOpus.xcframework"

xcodebuild \
    -project "$SAMPLE_DIR/iosApp/GlassesSample.xcodeproj" \
    -scheme GlassesSample -configuration Debug -destination 'generic/platform=iOS' \
    -derivedDataPath "$SAMPLE_DIR/build/ios" \
    -clonedSourcePackagesDirPath "$SAMPLE_DIR/build/packages" \
    CODE_SIGNING_ALLOWED=NO "$@" build
