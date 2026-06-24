import 'package:flutter/material.dart';

import 'command_page.dart';
import 'scan_page.dart';
import 'src/glasses_sdk.dart';

void main() {
  runApp(const GlassesSdkSampleApp());
}

class GlassesSdkSampleApp extends StatelessWidget {
  const GlassesSdkSampleApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Glasses SDK Flutter Sample',
      theme: ThemeData(
        colorSchemeSeed: Colors.blue,
        useMaterial3: true,
      ),
      home: const HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return StreamBuilder<GlassConnectionState>(
      stream: GlassesSdk.instance.connectionState,
      initialData: GlassConnectionState.disconnected,
      builder: (context, snapshot) {
        final state = snapshot.data ?? GlassConnectionState.disconnected;
        if (state.connected) {
          return CommandPage(connection: state);
        }
        return const ScanPage();
      },
    );
  }
}
