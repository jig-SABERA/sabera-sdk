import SwiftUI
import UIKit
import SaberaAppSDK
import SaberaIOSBridge

@main
struct SaberaSwiftPMSampleApp: App {
    @Environment(\.scenePhase) private var scenePhase

    init() {
        #if DEBUG
        GlassesSDK.shared.setLogger { tag, message in
            print("[\(tag)] \(message)")
        }
        GlassesSDK.shared.setProd(isProd: false)
        #else
        GlassesSDK.shared.setProd(isProd: true)
        #endif

        SaberaIOS.initialize(productImage: UIImage(systemName: "eyeglasses")!)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .onChange(of: scenePhase) { _, phase in
            if phase == .active {
                SaberaIOS.onAppActivated()
            }
        }
    }
}

private struct ContentView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "eyeglasses")
                .font(.system(size: 56))
                .foregroundStyle(.blue)
            Text("Sabera iOS SDK")
                .font(.title)
            Text("Swift Package Manager sample")
                .foregroundStyle(.secondary)
        }
        .padding()
    }
}
