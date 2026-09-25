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
            url: "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.0/sabera-app-bridge-1.1.0.zip",
            checksum: "16ac565ffa92a6b8dd09fb60acc6f8d2de032b6d8c35f549f49705fe9f9f28c6"
        ),
        .binaryTarget(
            name: "OggOpus",
            url: "https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.0/sabera-app-opus-1.1.0.zip",
            checksum: "399a5c44e39ee7cfb7c741b934b2f29f1f250aff15ba60bf3981045be1e60399"
        ),
    ]
)
