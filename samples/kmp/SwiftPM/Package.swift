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
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.2/sabera-app-bridge-1.1.2.zip",
            checksum: "f1275c974d5b2579c76c99ee7b16a2556333cfa74960f3090ca11c577fb5bd2a"
        ),
        .binaryTarget(
            name: "OggOpus",
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.2/sabera-app-opus-1.1.2.zip",
            checksum: "c8d349d31943f87d1a2212bc0a83327260ff46bea21b3cf15882566b07799d75"
        ),
    ]
)
