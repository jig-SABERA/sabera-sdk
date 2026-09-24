// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/1.1.0/sabera-app-core-kmmbridge-1.1.0.zip"
let remoteKotlinChecksum = "5ef0cb6710f73bccfdca8961cd050f915a5d2c02b5f5afe1048ec2562dbab5b3"
let packageName = "SaberaAppSDK"
// END KMMBRIDGE BLOCK

let remoteBridgeUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.0/sabera-app-bridge-1.1.0.zip"
let remoteBridgeChecksum = "16ac565ffa92a6b8dd09fb60acc6f8d2de032b6d8c35f549f49705fe9f9f28c6"
let remoteOpusUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.0/sabera-app-opus-1.1.0.zip"
let remoteOpusChecksum = "399a5c44e39ee7cfb7c741b934b2f29f1f250aff15ba60bf3981045be1e60399"
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
