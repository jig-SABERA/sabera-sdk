import 'package:flutter/material.dart';

import 'src/glasses_sdk.dart';

class ScanPage extends StatefulWidget {
  const ScanPage({super.key});

  @override
  State<ScanPage> createState() => _ScanPageState();
}

class _ScanPageState extends State<ScanPage> {
  String? _error;
  bool _scanning = false;

  Future<void> _pickAndConnect() async {
    setState(() {
      _error = null;
      _scanning = true;
    });
    try {
      final device = await GlassesSdk.instance.showSelectionDialog();
      if (device == null) return;
      // showSelectionDialog already connects internally via GlassManager
    } catch (e) {
      setState(() => _error = 'Connection error: $e');
    } finally {
      if (mounted) setState(() => _scanning = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'Glasses SDK Flutter Sample',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              const Text('OS のデバイス選択ダイアログから glasses を選んでください'),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _scanning ? null : _pickAndConnect,
                child: Text(_scanning ? 'スキャン中...' : 'スキャン開始'),
              ),
              if (_error != null) ...[
                const SizedBox(height: 16),
                Text(_error!, style: const TextStyle(color: Colors.red)),
                TextButton(
                  onPressed: () => setState(() => _error = null),
                  child: const Text('エラーを消す'),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
