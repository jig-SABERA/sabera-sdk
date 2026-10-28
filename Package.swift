// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/1.1.2/sabera-app-core-kmmbridge-1.1.2.zip"
let remoteKotlinChecksum = "66285ce0602fdd165d500a3790b564a836620a6604c6b57a29d657d0d14cad7f"
let packageName = "SaberaAppSDK"
// END KMMBRIDGE BLOCK

let remoteBridgeUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.2/sabera-app-bridge-1.1.2.zip"
let remoteBridgeChecksum = "f1275c974d5b2579c76c99ee7b16a2556333cfa74960f3090ca11c577fb5bd2a"
let remoteOpusUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.2/sabera-app-opus-1.1.2.zip"
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
