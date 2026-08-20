import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import 'src/music/itunes_search_service.dart';
import 'src/music/itunes_track.dart';

class MusicSearchPage extends StatefulWidget {
  const MusicSearchPage({super.key});

  @override
  State<MusicSearchPage> createState() => _MusicSearchPageState();
}

class _MusicSearchPageState extends State<MusicSearchPage> {
  final _service = ItunesSearchService();
  final _queryCtrl = TextEditingController();

  List<ItunesTrack> _tracks = const [];
  bool _loading = false;
  String? _error;

  @override
  void dispose() {
    _queryCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    final term = _queryCtrl.text;
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final tracks = await _service.search(term);
      if (!mounted) return;
      setState(() => _tracks = tracks);
    } catch (e) {
      if (!mounted) return;
      setState(() => _error = e.toString());
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _openTrack(ItunesTrack track) async {
    final uri = Uri.tryParse(track.trackViewUrl);
    if (uri == null) return;
    final launched = await launchUrl(uri, mode: LaunchMode.externalApplication);
    if (!launched && mounted) {
      setState(() => _error = 'URL を開けませんでした: ${track.trackViewUrl}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('楽曲検索')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              controller: _queryCtrl,
              decoration: const InputDecoration(
                labelText: '曲名・アーティスト名で検索',
                border: OutlineInputBorder(),
                suffixIcon: Icon(Icons.search),
              ),
              textInputAction: TextInputAction.search,
              onSubmitted: (_) => _search(),
            ),
          ),
          if (_loading) const LinearProgressIndicator(),
          if (_error != null)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Text(_error!, style: const TextStyle(color: Colors.red)),
            ),
          Expanded(
            child: _tracks.isEmpty
                ? Center(
                    child: Text(
                      _loading ? '検索中...' : '曲名を入力して検索してください',
                      style: TextStyle(color: Theme.of(context).hintColor),
                    ),
                  )
                : ListView.separated(
                    itemCount: _tracks.length,
                    separatorBuilder: (_, __) => const Divider(height: 1),
                    itemBuilder: (context, index) {
                      final track = _tracks[index];
                      return _TrackRow(
                        track: track,
                        onTap: () => _openTrack(track),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }
}

class _TrackRow extends StatelessWidget {
  final ItunesTrack track;
  final VoidCallback onTap;

  const _TrackRow({required this.track, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: onTap,
      leading: _JacketWithPin(track: track, onTapPin: onTap),
      title: Text(track.trackName, maxLines: 1, overflow: TextOverflow.ellipsis),
      subtitle: Text(track.artistName, maxLines: 1, overflow: TextOverflow.ellipsis),
      trailing: const Icon(Icons.chevron_right),
    );
  }
}

/// Album jacket thumbnail with a pin-shaped play button pinned to its
/// bottom-right corner. Tapping the pin (or the jacket itself) jumps to the
/// track's streaming/listen URL.
class _JacketWithPin extends StatelessWidget {
  final ItunesTrack track;
  final VoidCallback onTapPin;

  const _JacketWithPin({required this.track, required this.onTapPin});

  @override
  Widget build(BuildContext context) {
    final artwork = track.artworkUrl512 ?? track.artworkUrl100;

    return SizedBox(
      width: 56,
      height: 64,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(6),
            child: artwork == null
                ? Container(
                    width: 56,
                    height: 56,
                    color: Theme.of(context).colorScheme.surfaceContainerHighest,
                    child: const Icon(Icons.music_note),
                  )
                : Image.network(
                    artwork,
                    width: 56,
                    height: 56,
                    fit: BoxFit.cover,
                    errorBuilder: (_, __, ___) => Container(
                      width: 56,
                      height: 56,
                      color: Theme.of(context).colorScheme.surfaceContainerHighest,
                      child: const Icon(Icons.music_note),
                    ),
                  ),
          ),
          Positioned(
            right: -8,
            bottom: -10,
            child: _PlayPin(onTap: onTapPin),
          ),
        ],
      ),
    );
  }
}

/// A map-pin shaped play button, e.g. `location_on` with a play glyph
/// inside the pin's head.
class _PlayPin extends StatelessWidget {
  final VoidCallback onTap;

  const _PlayPin({required this.onTap});

  @override
  Widget build(BuildContext context) {
    final color = Theme.of(context).colorScheme.primary;
    return GestureDetector(
      onTap: onTap,
      child: SizedBox(
        width: 24,
        height: 28,
        child: Stack(
          alignment: Alignment.center,
          children: [
            Icon(Icons.location_on, size: 28, color: color),
            const Positioned(
              top: 3,
              child: Icon(Icons.play_arrow, size: 12, color: Colors.white),
            ),
          ],
        ),
      ),
    );
  }
}
