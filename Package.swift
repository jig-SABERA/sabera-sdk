// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/1.1.3/sabera-app-core-kmmbridge-1.1.3.zip"
let remoteKotlinChecksum = "f49ca27557ee4872f0fda6fcaf23049ceafdffcc8209bbd887c7668762d72350"
let packageName = "SaberaAppSDK"
// END KMMBRIDGE BLOCK

let remoteBridgeUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.3/sabera-app-bridge-1.1.3.zip"
let remoteBridgeChecksum = "9a5cde166f2b0982a903bb9d6d767484eb336a0e4c0a27474f73f670d71d9d4e"
let remoteOpusUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.3/sabera-app-opus-1.1.3.zip"
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
