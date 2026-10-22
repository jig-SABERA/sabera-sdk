// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "SaberaIOSAddons",
    platforms: [.iOS("18.2")],
    products: [
        .library(name: "SaberaIOSBridge", targets: ["SaberaIOSBridge"]),
        .library(name: "OggOpus", targets: ["OggOpus"]),
    ],
    targets: [
        .binaryTarget(
            name: "SaberaIOSBridge",
            url: "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.1/sabera-app-bridge-1.1.1.zip",
            checksum: "c3dafeffacd117ef100b3e45a6be89413400443eebea2df823feda3403f55ca4"
        ),
        .binaryTarget(
            name: "OggOpus",
            url: "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.1/sabera-app-opus-1.1.1.zip",
            checksum: "c8d349d31943f87d1a2212bc0a83327260ff46bea21b3cf15882566b07799d75"
        ),
    ]
)
