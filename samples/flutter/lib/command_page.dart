import 'dart:async';

import 'package:flutter/material.dart';

import 'src/glasses_sdk.dart';

class CommandPage extends StatefulWidget {
  final GlassConnectionState connection;
  const CommandPage({super.key, required this.connection});

  @override
  State<CommandPage> createState() => _CommandPageState();
}

class _CommandPageState extends State<CommandPage> {
  final _sdk = GlassesSdk.instance;
  String? _error;

  final _teleprompterCtrl = TextEditingController(text: 'Hello from Flutter');
  final _aiCtrl = TextEditingController(text: '質問内容をどうぞ');
  final _translateCtrl = TextEditingController(text: 'Translate this');
  final _sourceLangCtrl = TextEditingController(text: 'en');
  final _targetLangCtrl = TextEditingController(text: 'ja');

  final _gestures = <String>[];
  StreamSubscription<GestureType>? _gestureSub;

  @override
  void initState() {
    super.initState();
    _gestureSub = _sdk.gestureEvents.listen((g) {
      setState(() => _gestures.add(g.name));
    });
  }

  @override
  void dispose() {
    _gestureSub?.cancel();
    _teleprompterCtrl.dispose();
    _aiCtrl.dispose();
    _translateCtrl.dispose();
    _sourceLangCtrl.dispose();
    _targetLangCtrl.dispose();
    super.dispose();
  }

  void _safeRun(Future<void> Function() action) async {
    try {
      await action();
    } catch (e) {
      setState(() => _error = e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    final name = widget.connection.deviceName ??
        widget.connection.deviceId ??
        'Unknown';

    return Scaffold(
      appBar: AppBar(title: Text('接続中: $name')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Page navigation
            const Text('ページ遷移',
                style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            _CommandButton(
              label: 'Home に戻す',
              onPressed: () => _safeRun(_sdk.enterHomePage),
            ),
            _CommandButton(
              label: 'Teleprompter を開く',
              onPressed: () => _safeRun(_sdk.enterTeleprompterPage),
            ),
            _CommandButton(
              label: '翻訳ページを開く',
              onPressed: () => _safeRun(_sdk.enterTranslatePage),
            ),

            const Divider(height: 32),

            // Text sending
            const Text('テキスト送信',
                style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            _SendableTextField(
              label: 'Teleprompter テキスト',
              controller: _teleprompterCtrl,
              onSend: () => _safeRun(
                () => _sdk.sendTeleprompterContent(_teleprompterCtrl.text),
              ),
            ),
            _SendableTextField(
              label: 'AI テキスト',
              controller: _aiCtrl,
              onSend: () => _safeRun(
                () => _sdk.sendAIContent(_aiCtrl.text),
              ),
            ),
            _SendableTextField(
              label: '翻訳テキスト',
              controller: _translateCtrl,
              onSend: () => _safeRun(
                () => _sdk.sendTranslateContent(_translateCtrl.text),
              ),
            ),

            // Language pair
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _sourceLangCtrl,
                    decoration: const InputDecoration(
                      labelText: '翻訳元',
                      border: OutlineInputBorder(),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _targetLangCtrl,
                    decoration: const InputDecoration(
                      labelText: '翻訳先',
                      border: OutlineInputBorder(),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: () => _safeRun(
                () => _sdk.sendTranslateLanguage(
                  source: _sourceLangCtrl.text,
                  target: _targetLangCtrl.text,
                ),
              ),
              child: const Text('翻訳言語を送信'),
            ),

            const Divider(height: 32),

            // Gesture events
            if (_gestures.isNotEmpty) ...[
              const Text('ジェスチャーイベント',
                  style: TextStyle(fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              Text(_gestures.join(', ')),
              const SizedBox(height: 16),
            ],

            // Disconnect
            OutlinedButton(
              onPressed: () => _safeRun(_sdk.disconnect),
              child: const Text('切断'),
            ),

            // Error display
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
    );
  }
}

class _CommandButton extends StatelessWidget {
  final String label;
  final VoidCallback onPressed;

  const _CommandButton({required this.label, required this.onPressed});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: ElevatedButton(
        onPressed: onPressed,
        child: Text(label),
      ),
    );
  }
}

class _SendableTextField extends StatelessWidget {
  final String label;
  final TextEditingController controller;
  final VoidCallback onSend;

  const _SendableTextField({
    required this.label,
    required this.controller,
    required this.onSend,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          TextField(
            controller: controller,
            decoration: InputDecoration(
              labelText: label,
              border: const OutlineInputBorder(),
            ),
            maxLines: null,
          ),
          const SizedBox(height: 4),
          ElevatedButton(
            onPressed: onSend,
            child: Text('$label を送信'),
          ),
        ],
      ),
    );
  }
}
