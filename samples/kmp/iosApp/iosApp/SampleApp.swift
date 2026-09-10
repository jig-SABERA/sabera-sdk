import SwiftUI
import SampleShared

@main
struct SampleApp: App {
    @Environment(\.scenePhase) private var scenePhase

    init() {
        #if DEBUG
        GlassesSDK.shared.setLogger { tag, msg in
            print("[\(tag)] \(msg)")
        }
        GlassesSDK.shared.setProd(isProd: false)
        #else
        GlassesSDK.shared.setProd(isProd: true)
        #endif

        GlassesSDK.shared.setDevicePersistence(persistence: NSUserDefaultsDevicePersistence())
        SaberaIOS.initialize(productImage: UIImage(systemName: "eyeglasses")!)
        SampleMediaPickerHolder.shared.picker = SampleMediaPickerImpl()
    }

    var body: some Scene {
        WindowGroup {
            SampleRootView()
        }
        .onChange(of: scenePhase) { _, phase in
            if phase == .active { SaberaIOS.onAppActivated() }
        }
    }
}
