import 'package:flutter/services.dart';

import 'gesture_type.dart';
import 'glass_connection_state.dart';

export 'gesture_type.dart';
export 'glass_connection_state.dart';

class GlassesSdk {
  GlassesSdk._();
  static final instance = GlassesSdk._();

  static const _method = MethodChannel('jp.jig.glasses.sdk/glasses');
  static const _connectionEvent =
      EventChannel('jp.jig.glasses.sdk/connectionState');
  static const _gestureEvent =
      EventChannel('jp.jig.glasses.sdk/gestureEvents');

  Stream<GlassConnectionState>? _connectionStateStream;

  Stream<GlassConnectionState> get connectionState {
    _connectionStateStream ??= _connectionEvent
        .receiveBroadcastStream()
        .map(
          (event) => GlassConnectionState.fromMap(
            event as Map<dynamic, dynamic>,
          ),
        )
        .handleError((_) => GlassConnectionState.disconnected);
    return _connectionStateStream!;
  }

  Stream<GestureType> get gestureEvents {
    return _gestureEvent
        .receiveBroadcastStream()
        .map(
          (event) =>
              GestureType.fromString((event as Map<dynamic, dynamic>)['type'] as String?),
        )
        .where((e) => e != null)
        .cast<GestureType>();
  }

  Future<void> initialize({bool isProd = true}) async {
    await _method.invokeMethod('initialize', {'isProd': isProd});
  }

  Future<Map<String, String?>?> showSelectionDialog() async {
    final result = await _method.invokeMethod<Map<dynamic, dynamic>?>(
      'showSelectionDialog',
    );
    if (result == null) return null;
    return {
      'deviceId': result['deviceId'] as String?,
      'deviceName': result['deviceName'] as String?,
    };
  }

  Future<void> connect(String deviceId) async {
    await _method.invokeMethod('connect', {'deviceId': deviceId});
  }

  Future<void> disconnect() async {
    await _method.invokeMethod('disconnect');
  }

  // Page navigation
  Future<void> enterHomePage() => _method.invokeMethod('enterHomePage');
  Future<void> enterTeleprompterPage() =>
      _method.invokeMethod('enterTeleprompterPage');
  Future<void> enterAIPage({bool isAiPower = false}) =>
      _method.invokeMethod('enterAIPage', {'isAiPower': isAiPower});
  Future<void> enterTranslatePage() =>
      _method.invokeMethod('enterTranslatePage');

  // Content sending
  Future<void> sendTeleprompterContent(String content) =>
      _method.invokeMethod('sendTeleprompterContent', {'content': content});
  Future<void> sendAIContent(String content) =>
      _method.invokeMethod('sendAIContent', {'content': content});
  Future<void> sendTranslateContent(String content) =>
      _method.invokeMethod('sendTranslateContent', {'content': content});
  Future<void> sendTranslateLanguage({
    required String source,
    required String target,
  }) => _method.invokeMethod('sendTranslateLanguage', {
    'source': source,
    'target': target,
  });
}
