// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/1.1.1/sabera-app-core-kmmbridge-1.1.1.zip"
let remoteKotlinChecksum = "ddc4d2baadbd8afa8c40a8d8d5c45acf1b34cfd08a039120add246dd517b9e78"
let packageName = "SaberaAppSDK"
// END KMMBRIDGE BLOCK

let remoteBridgeUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.1/sabera-app-bridge-1.1.1.zip"
let remoteBridgeChecksum = "c3dafeffacd117ef100b3e45a6be89413400443eebea2df823feda3403f55ca4"
let remoteOpusUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.1/sabera-app-opus-1.1.1.zip"
let remoteOpusChecksum = "c8d349d31943f87d1a2212bc0a83327260ff46bea21b3cf15882566b07799d75"
let bridgeTargetName = "SaberaIOSBridge"
let opusTargetName = "OggOpus"

let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName]
        ),
        .library(
            name: "SaberaIOS",
            targets: [bridgeTargetName, packageName, opusTargetName]
        ),
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
            checksum: remoteKotlinChecksum
        )
        ,
        .binaryTarget(
            name: bridgeTargetName,
            url: remoteBridgeUrl,
            checksum: remoteBridgeChecksum
        ),
        .binaryTarget(
            name: opusTargetName,
            url: remoteOpusUrl,
            checksum: remoteOpusChecksum
        ),
    ]
)
