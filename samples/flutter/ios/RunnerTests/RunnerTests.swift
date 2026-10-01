import Flutter
import SaberaAppSDK
import XCTest

@testable import Runner

final class RunnerTests: XCTestCase {
  func testScanContextCanBeConstructed() {
    XCTAssertNotNil(FlutterPlatformContext())
  }

  func testCommandsFailWhenDisconnected() {
    let plugin = GlassesSdkPlugin()
    for method in [
      "enterHomePage", "enterTeleprompterPage", "enterTranslatePage",
      "sendTeleprompterContent", "sendAIContent", "sendTranslateContent", "sendTranslateLanguage",
    ] {
      var replies = 0
      plugin.handle(FlutterMethodCall(methodName: method, arguments: nil)) { result in
        replies += 1
        XCTAssertEqual((result as? FlutterError)?.code, "NOT_CONNECTED", method)
      }
      XCTAssertEqual(replies, 1)
    }
  }

  func testConnectRequiresAStringDeviceIdentifier() {
    let plugin = GlassesSdkPlugin()
    for arguments: Any? in [nil, ["deviceId": 123]] {
      plugin.handle(FlutterMethodCall(methodName: "connect", arguments: arguments)) { result in
        XCTAssertEqual((result as? FlutterError)?.code, "INVALID_ARGS")
      }
    }
  }

  func testUnknownMethodIsNotImplemented() {
    let plugin = GlassesSdkPlugin()
    plugin.handle(FlutterMethodCall(methodName: "unknown", arguments: nil)) { result in
      XCTAssertTrue(result as AnyObject === FlutterMethodNotImplemented)
    }
  }

  func testConnectionEventsIncludeCurrentStateAndStopAfterCancel() {
    let plugin = GlassesSdkPlugin()
    var events: [[String: Any]] = []
    _ = plugin.connectionStream.onListen(withArguments: nil) { event in
      events.append(event as! [String: Any])
    }
    XCTAssertEqual(events.last?["connected"] as? Bool, false)
    XCTAssertTrue(events.last?["deviceId"] is NSNull)

    let client = GlassClientDummy(
      deviceName: "SABERA", deviceIdentifier: "test-device", shouldFail: false)
    plugin.updateConnection(client)
    XCTAssertEqual(events.last?["connected"] as? Bool, true)
    XCTAssertEqual(events.last?["deviceId"] as? String, "test-device")
    XCTAssertEqual(events.last?["deviceName"] as? String, "SABERA")

    _ = plugin.connectionStream.onCancel(withArguments: nil)
    plugin.updateConnection(nil)
    XCTAssertEqual(events.count, 2)

    _ = plugin.connectionStream.onListen(withArguments: nil) { event in
      events.append(event as! [String: Any])
    }
    XCTAssertEqual(events.count, 3)
    XCTAssertEqual(events.last?["connected"] as? Bool, false)
    XCTAssertTrue(events.last?["deviceName"] is NSNull)
  }

  func testConnectedCommandsAreSupported() {
    let plugin = GlassesSdkPlugin()
    plugin.updateConnection(
      GlassClientDummy(deviceName: nil, deviceIdentifier: "test-device", shouldFail: false))
    for method in [
      "enterHomePage", "enterTeleprompterPage", "enterTranslatePage",
      "sendTeleprompterContent", "sendAIContent", "sendTranslateContent", "sendTranslateLanguage",
    ] {
      let call = FlutterMethodCall(
        methodName: method, arguments: ["content": "こんにちは", "source": "ENG", "target": "JPN"])
      var replies = 0
      plugin.handle(call) { result in
        replies += 1
        XCTAssertNil(result, method)
      }
      XCTAssertEqual(replies, 1)
    }
  }

  func testFlowValuesAreDeliveredOnMainThread() {
    let delivered = expectation(description: "value delivered")
    let completed = expectation(description: "emit completed")
    let observer = SdkFlowObserver(
      onValue: { value in
        XCTAssertTrue(Thread.isMainThread)
        XCTAssertEqual((value as? GestureType)?.name, "SINGLE_TAP")
        delivered.fulfill()
      },
      onError: { error in XCTFail("\(error)") })
    DispatchQueue.global().async {
      observer.emit(value: GestureType.singleTap) { error in
        XCTAssertNil(error)
        completed.fulfill()
      }
    }
    wait(for: [delivered, completed], timeout: 2)
    observer.cancel()
  }

  func testCancelledObserverDropsAlreadyQueuedValues() {
    let completed = expectation(description: "emit cancelled")
    let observer = SdkFlowObserver(
      onValue: { _ in XCTFail("Cancelled observer delivered a value") },
      onError: { _ in XCTFail("Cancelled observer delivered an error") })
    observer.emit(value: GestureType.doubleTap) { error in
      XCTAssertNotNil(error)
      completed.fulfill()
    }
    observer.cancel()
    wait(for: [completed], timeout: 2)
  }
}
