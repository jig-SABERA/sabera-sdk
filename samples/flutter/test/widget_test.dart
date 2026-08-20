import 'package:flutter_test/flutter_test.dart';

import 'package:sabera_app_sdk_flutter_sample/main.dart';

void main() {
  testWidgets('app builds and shows the bottom navigation tabs',
      (WidgetTester tester) async {
    await tester.pumpWidget(const GlassesSdkSampleApp());
    await tester.pump();

    expect(find.text('楽曲検索'), findsOneWidget);
    expect(find.text('保存した曲'), findsOneWidget);
  });
}
