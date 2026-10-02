import Flutter
import SaberaAppSDK

final class FlutterPlatformContext: PlatformContext {}

final class GlassesSdkPlugin: NSObject, FlutterPlugin {
  private let manager = GlassManager.companion.shared
  private var currentClient: GlassClient?
  private var commandManager: CommandManager?
  private var connectionObserver: SdkFlowObserver?
  private var gestureObserver: SdkFlowObserver?

  let connectionStream = GlassesEventStream()
  let gestureStream = GlassesEventStream()

  static func register(with registrar: FlutterPluginRegistrar) {
    let plugin = GlassesSdkPlugin()
    let methods = FlutterMethodChannel(
      name: "jp.jig.glasses.sdk/glasses", binaryMessenger: registrar.messenger())
    registrar.addMethodCallDelegate(plugin, channel: methods)
    FlutterEventChannel(
      name: "jp.jig.glasses.sdk/connectionState", binaryMessenger: registrar.messenger()
    ).setStreamHandler(plugin.connectionStream)
    FlutterEventChannel(
      name: "jp.jig.glasses.sdk/gestureEvents", binaryMessenger: registrar.messenger()
    ).setStreamHandler(plugin.gestureStream)
    plugin.startMonitoring()
  }

  override init() {
    super.init()
    connectionStream.onListen = { [weak self] in
      guard let self else { return }
      self.connectionStream.send(self.connectionEvent)
    }
    gestureStream.onListen = { [weak self] in self?.startGestureMonitoring() }
    gestureStream.onCancel = { [weak self] in
      self?.gestureObserver?.cancel()
      self?.gestureObserver = nil
    }
  }

  func startMonitoring() {
    connectionObserver?.cancel()
    let observer = SdkFlowObserver(
      onValue: { [weak self] value in self?.updateConnection(value as? GlassClient) },
      onError: { [weak self] error in
        self?.connectionStream.send(
          FlutterError(code: "CONNECTION_ERROR", message: error.localizedDescription, details: nil))
      })
    connectionObserver = observer
    observer.collect(manager.connectedDevice)
  }

  func updateConnection(_ client: GlassClient?) {
    gestureObserver?.cancel()
    gestureObserver = nil
    currentClient = client
    commandManager = client?.createCommandManager()
    startGestureMonitoring()
    connectionStream.send(connectionEvent)
  }

  private var connectionEvent: [String: Any] {
    [
      "connected": currentClient != nil,
      "deviceId": currentClient?.deviceIdentifier as Any? ?? NSNull(),
      "deviceName": currentClient?.deviceName as Any? ?? NSNull(),
    ]
  }

  private func startGestureMonitoring() {
    gestureObserver?.cancel()
    gestureObserver = nil
    guard gestureStream.isListening, let commandManager else { return }
    let observer = SdkFlowObserver(
      onValue: { [weak self] value in
        guard let gesture = value as? GestureType else { return }
        self?.gestureStream.send(["type": gesture.name])
      },
      onError: { [weak self] error in
        self?.gestureStream.send(
          FlutterError(code: "GESTURE_ERROR", message: error.localizedDescription, details: nil))
      })
    gestureObserver = observer
    observer.collect(commandManager.gestureEvents)
  }

  func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    let arguments = call.arguments as? [String: Any] ?? [:]
    switch call.method {
    case "initialize":
      GlassesSDK.shared.setProd(isProd: arguments["isProd"] as? Bool ?? true)
      result(nil)
    case "showSelectionDialog":
      manager.showAutomaticSelectionDialog(context: FlutterPlatformContext()) { client, error in
        DispatchQueue.main.async {
          if let error {
            result(
              FlutterError(code: "SCAN_ERROR", message: error.localizedDescription, details: nil))
          } else if let client {
            result([
              "deviceId": client.deviceIdentifier,
              "deviceName": client.deviceName as Any? ?? NSNull(),
            ])
          } else {
            result(nil)
          }
        }
      }
    case "connect":
      guard let deviceId = arguments["deviceId"] as? String else {
        result(FlutterError(code: "INVALID_ARGS", message: "deviceId is required", details: nil))
        return
      }
      guard let client = manager.createClientFromDeviceID(deviceId: deviceId) else {
        result(
          FlutterError(
            code: "DEVICE_NOT_FOUND", message: "Device not found: \(deviceId)", details: nil))
        return
      }
      manager.connect(
        glassClient: client, completionHandler: completion(result, code: "CONNECT_ERROR"))
    case "disconnect":
      guard let currentClient else {
        result(nil)
        return
      }
      manager.disconnect(
        glassClient: currentClient, completionHandler: completion(result, code: "DISCONNECT_ERROR"))
    case "enterHomePage", "enterTeleprompterPage", "enterTranslatePage",
      "sendTeleprompterContent", "sendAIContent", "sendTranslateContent", "sendTranslateLanguage":
      guard let commandManager else {
        result(FlutterError(code: "NOT_CONNECTED", message: "No device connected", details: nil))
        return
      }
      switch call.method {
      case "enterHomePage": commandManager.enterHomePage()
      case "enterTeleprompterPage": commandManager.enterTeleprompterPage()
      case "enterTranslatePage": commandManager.enterTranslatePage()
      case "sendTeleprompterContent":
        commandManager.sendTeleprompterContent(content: arguments["content"] as? String ?? "")
      case "sendAIContent":
        commandManager.sendAiChatText(text: arguments["content"] as? String ?? "")
      case "sendTranslateContent":
        commandManager.sendTranslateContent(content: arguments["content"] as? String ?? "")
      case "sendTranslateLanguage":
        commandManager.sendTranslateLanguage(
          source: arguments["source"] as? String ?? "", target: arguments["target"] as? String ?? ""
        )
      default: break
      }
      result(nil)
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  private func completion(_ result: @escaping FlutterResult, code: String) -> (Error?) -> Void {
    { error in
      DispatchQueue.main.async {
        if let error {
          result(FlutterError(code: code, message: error.localizedDescription, details: nil))
        } else {
          result(nil)
        }
      }
    }
  }

  deinit {
    connectionObserver?.cancel()
    gestureObserver?.cancel()
  }
}

final class GlassesEventStream: NSObject, FlutterStreamHandler {
  var onListen: (() -> Void)?
  var onCancel: (() -> Void)?
  private var sink: FlutterEventSink?
  var isListening: Bool { sink != nil }

  func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink)
    -> FlutterError?
  {
    sink = events
    onListen?()
    return nil
  }

  func onCancel(withArguments arguments: Any?) -> FlutterError? {
    sink = nil
    onCancel?()
    return nil
  }

  func send(_ value: Any) {
    sink?(value)
  }
}
