// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/0.0.10/sabera-app-core-kmmbridge-0.0.10.zip"
let remoteKotlinChecksum = "cbc08c64ca54f362882e28e4105ba610ccce993563724d83454f6ecb4509bf02"
let packageName = "SaberaAppSDK"
// END KMMBRIDGE BLOCK

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
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
            checksum: remoteKotlinChecksum
        )
        ,
    ]
)