import Foundation
import SaberaAppSDK

final class NSUserDefaultsDevicePersistence: SdkDevicePersistence {
  private let key = "glasses_sdk_sample.last_device_id"

  var lastDeviceId: String? {
    get { UserDefaults.standard.string(forKey: key) }
    set {
      if let newValue {
        UserDefaults.standard.set(newValue, forKey: key)
      } else {
        UserDefaults.standard.removeObject(forKey: key)
      }
    }
  }
}
