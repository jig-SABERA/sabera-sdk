import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sabera_app_sdk_flutter_sample/main.dart';

void main() {
  const methods = MethodChannel('jp.jig.glasses.sdk/glasses');
  const connectionChannel = 'jp.jig.glasses.sdk/connectionState';
  const gestureChannel = 'jp.jig.glasses.sdk/gestureEvents';
  const codec = StandardMethodCodec();
  final binding = TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    for (final channel in [connectionChannel, gestureChannel]) {
      binding.defaultBinaryMessenger.setMockMethodCallHandler(
        MethodChannel(channel),
        (_) async => null,
      );
    }
  });

  tearDown(() {
    for (final channel in [methods.name, connectionChannel, gestureChannel]) {
      binding.defaultBinaryMessenger.setMockMethodCallHandler(
        MethodChannel(channel),
        null,
      );
    }
  });

  void emit(String channel, Object value) {
    binding.channelBuffers.push(
      channel,
      codec.encodeSuccessEnvelope(value),
      (_) {},
    );
  }

  testWidgets('接続状態の通知で操作画面とスキャン画面を切り替える', (tester) async {
    await tester.pumpWidget(const GlassesSdkSampleApp());
    await tester.pumpAndSettle();
    expect(find.text('スキャン開始'), findsOneWidget);

    emit(connectionChannel, {
      'connected': true,
      'deviceId': 'test-device',
      'deviceName': 'SABERA',
    });
    await tester.pumpAndSettle();
    expect(find.text('接続中: SABERA'), findsOneWidget);
    expect(find.text('Home に戻す'), findsOneWidget);

    emit(connectionChannel, {
      'connected': false,
      'deviceId': null,
      'deviceName': null,
    });
    await tester.pumpAndSettle();
    expect(find.text('スキャン開始'), findsOneWidget);
  });

  testWidgets('デバイスを選択しても接続通知まではスキャン画面を保つ', (tester) async {
    binding.defaultBinaryMessenger.setMockMethodCallHandler(methods, (
      call,
    ) async {
      expect(call.method, 'showSelectionDialog');
      return {'deviceId': 'test-device', 'deviceName': 'SABERA'};
    });
    await tester.pumpWidget(const GlassesSdkSampleApp());
    await tester.tap(find.text('スキャン開始'));
    await tester.pumpAndSettle();
    expect(find.text('スキャン開始'), findsOneWidget);
    expect(find.text('接続中: SABERA'), findsNothing);
  });

  testWidgets('選択をキャンセルしたら再びスキャンできる', (tester) async {
    var selections = 0;
    binding.defaultBinaryMessenger.setMockMethodCallHandler(methods, (
      call,
    ) async {
      expect(call.method, 'showSelectionDialog');
      selections++;
      return null;
    });
    await tester.pumpWidget(const GlassesSdkSampleApp());
    for (var i = 0; i < 2; i++) {
      await tester.tap(find.text('スキャン開始'));
      await tester.pumpAndSettle();
    }
    expect(selections, 2);
    expect(find.textContaining('Connection error:'), findsNothing);
  });

  testWidgets('ネイティブ側のスキャンエラーを表示する', (tester) async {
    binding.defaultBinaryMessenger.setMockMethodCallHandler(methods, (_) async {
      throw PlatformException(code: 'SCAN_ERROR', message: 'Bluetooth is off');
    });
    await tester.pumpWidget(const GlassesSdkSampleApp());
    await tester.tap(find.text('スキャン開始'));
    await tester.pumpAndSettle();
    expect(find.textContaining('Bluetooth is off'), findsOneWidget);
    expect(find.text('スキャン開始'), findsOneWidget);
  });
}
