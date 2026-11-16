// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/1.1.5/sabera-app-core-kmmbridge-1.1.5.zip"
let remoteKotlinChecksum = "8451a4e12dd75d9bba59cc6488d4b4fc3ea0fcc1e3520355f78a53de769ab6dd"
let packageName = "SaberaAppSDK"
// END KMMBRIDGE BLOCK

let remoteBridgeUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.5/sabera-app-bridge-1.1.5.zip"
let remoteBridgeChecksum = "8d653b58c4207460c3809e348a6297810a7fe59869c263ca5a896c8192037815"
let remoteOpusUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.5/sabera-app-opus-1.1.5.zip"
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
