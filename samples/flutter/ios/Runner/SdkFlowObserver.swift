import Foundation
import SaberaAppSDK

final class SdkFlowObserver: NSObject, Kotlinx_coroutines_coreFlowCollector {
  private var onValue: ((Any?) -> Void)?
  private var onError: ((Error) -> Void)?

  init(onValue: @escaping (Any?) -> Void, onError: @escaping (Error) -> Void) {
    self.onValue = onValue
    self.onError = onError
  }

  func collect(_ flow: Kotlinx_coroutines_coreFlow) {
    flow.collect(collector: self) { [weak self] error in
      guard let error else { return }
      DispatchQueue.main.async { self?.onError?(error) }
    }
  }

  func cancel() {
    // 公開SDKには購読を即時キャンセルするAPIがないため、次のemitで終了させる。
    onValue = nil
    onError = nil
  }

  func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
    DispatchQueue.main.async { [self] in
      guard let onValue else {
        completionHandler(KotlinCancellationException().asError())
        return
      }
      onValue(value)
      completionHandler(nil)
    }
  }
}
