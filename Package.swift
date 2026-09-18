// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/1.0.1/sabera-app-core-kmmbridge-1.0.1.zip"
let remoteKotlinChecksum = "d513a52f7b92ea8a15c43f41f0cd84d21e2e648d8b568d4f7d6cc935c0eeda41"
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