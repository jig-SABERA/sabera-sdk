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
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-bridge/1.1.4/sabera-app-bridge-1.1.4.zip",
            checksum: "c83e193d263ad07441b9392bf7aa747a9a2b967048c85d316a1d3abc43bd56fd"
        ),
        .binaryTarget(
            name: "OggOpus",
            url: "https://sdk.sabera.glass/maven/jp/jig/sabera/app/sdk/sabera-app-opus/1.1.4/sabera-app-opus-1.1.4.zip",
            checksum: "c8d349d31943f87d1a2212bc0a83327260ff46bea21b3cf15882566b07799d75"
        ),
    ]
)
