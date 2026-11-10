// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/1.1.4/sabera-app-core-kmmbridge-1.1.4.zip"
let remoteKotlinChecksum = "c0f3f5b4c76aad1aed6b7e7bed9e55b44eeb7265533c4bf76281031392fe9884"
let packageName = "SaberaAppSDK"
// END KMMBRIDGE BLOCK

let remoteBridgeUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.4/sabera-app-bridge-1.1.4.zip"
let remoteBridgeChecksum = "c83e193d263ad07441b9392bf7aa747a9a2b967048c85d316a1d3abc43bd56fd"
let remoteOpusUrl = "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.4/sabera-app-opus-1.1.4.zip"
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
