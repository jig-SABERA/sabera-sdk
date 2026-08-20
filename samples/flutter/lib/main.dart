import 'package:flutter/material.dart';

import 'command_page.dart';
import 'music_search_page.dart';
import 'saved_tracks_map_page.dart';
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
  int _tabIndex = 0;

  @override
  Widget build(BuildContext context) {
    final glassesTab = StreamBuilder<GlassConnectionState>(
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

    final pages = [glassesTab, const MusicSearchPage(), SavedTracksMapPage()];

    return Scaffold(
      body: pages[_tabIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _tabIndex,
        onDestinationSelected: (index) => setState(() => _tabIndex = index),
        destinations: const [
          NavigationDestination(icon: Icon(Icons.bluetooth), label: 'Glasses'),
          NavigationDestination(icon: Icon(Icons.music_note), label: '楽曲検索'),
          NavigationDestination(icon: Icon(Icons.map), label: '保存した曲'),
        ],
      ),
    );
  }
}
