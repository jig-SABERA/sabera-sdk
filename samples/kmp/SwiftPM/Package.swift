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
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.5/sabera-app-bridge-1.1.5.zip",
            checksum: "8d653b58c4207460c3809e348a6297810a7fe59869c263ca5a896c8192037815"
        ),
        .binaryTarget(
            name: "OggOpus",
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.5/sabera-app-opus-1.1.5.zip",
            checksum: "c8d349d31943f87d1a2212bc0a83327260ff46bea21b3cf15882566b07799d75"
        ),
    ]
)
