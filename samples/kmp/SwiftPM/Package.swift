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
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.3/sabera-app-bridge-1.1.3.zip",
            checksum: "9a5cde166f2b0982a903bb9d6d767484eb336a0e4c0a27474f73f670d71d9d4e"
        ),
        .binaryTarget(
            name: "OggOpus",
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.3/sabera-app-opus-1.1.3.zip",
            checksum: "c8d349d31943f87d1a2212bc0a83327260ff46bea21b3cf15882566b07799d75"
        ),
    ]
)
