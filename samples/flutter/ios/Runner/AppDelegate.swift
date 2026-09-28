import Flutter
import SaberaAppSDK
import SaberaIOSBridge
import UIKit

@main
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GlassesSDK.shared.setProd(isProd: true)
    GlassesSDK.shared.setDevicePersistence(persistence: NSUserDefaultsDevicePersistence())
    SaberaIOS.initialize(productImage: UIImage(systemName: "eyeglasses")!)
    GeneratedPluginRegistrant.register(with: self)
    GlassesSdkPlugin.register(with: registrar(forPlugin: "GlassesSdkPlugin")!)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  override func applicationDidBecomeActive(_ application: UIApplication) {
    super.applicationDidBecomeActive(application)
    SaberaIOS.onAppActivated()
  }
}
