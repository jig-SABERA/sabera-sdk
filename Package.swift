// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/jig-jp/sabera-sdk/jp/jig/sabera/app/sdk/sabera-app-core-kmmbridge/0.0.10/sabera-app-core-kmmbridge-0.0.10.zip"
let remoteKotlinChecksum = "44ce24aeff87374711d117dbb68055ff86920ef10a43c504f5da9e7a14b70026"
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