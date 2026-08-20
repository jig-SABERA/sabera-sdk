import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import 'src/saved_tracks/saved_track.dart';
import 'src/saved_tracks/saved_tracks_repository.dart';

enum _ViewMode { map, list }

/// "保存した曲" screen: map view with one pin per saved track, or a plain
/// list view. Ported from the saved_tracks_map_apple.html mock.
///
/// Data (tracks, and later real map/geo + song-info-API fields on
/// [SavedTrack]) comes from [repository] — swap in a real implementation
/// once that's ready; this widget only depends on [SavedTracksRepository].
class SavedTracksMapPage extends StatefulWidget {
  final SavedTracksRepository repository;

  SavedTracksMapPage({super.key, SavedTracksRepository? repository})
      : repository = repository ?? MockSavedTracksRepository();

  @override
  State<SavedTracksMapPage> createState() => _SavedTracksMapPageState();
}

class _SavedTracksMapPageState extends State<SavedTracksMapPage> {
  _ViewMode _view = _ViewMode.map;
  List<SavedTrack> _tracks = const [];
  bool _loading = true;
  String? _selectedId;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final tracks = await widget.repository.fetchSavedTracks();
    if (!mounted) return;
    setState(() {
      _tracks = tracks;
      _loading = false;
    });
  }

  Future<void> _removeSelected() async {
    final id = _selectedId;
    if (id == null) return;
    await widget.repository.removeTrack(id);
    if (!mounted) return;
    setState(() {
      _tracks = _tracks.where((t) => t.id != id).toList();
      _selectedId = null;
    });
  }

  int get _areaCount => _tracks.map((t) => t.area).toSet().length;

  Future<void> _openListenUrl(SavedTrack track) async {
    final url = track.listenUrl;
    if (url == null) return;
    final uri = Uri.tryParse(url);
    if (uri == null) return;
    final launched = await launchUrl(uri, mode: LaunchMode.externalApplication);
    if (!launched && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('URL を開けませんでした')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: _loading
            ? const Center(child: CircularProgressIndicator())
            : Padding(
                padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    _Header(view: _view, onChanged: (v) => setState(() => _view = v)),
                    const SizedBox(height: 14),
                    _StatsRow(count: _tracks.length, areaCount: _areaCount),
                    const SizedBox(height: 14),
                    Expanded(
                      child: _view == _ViewMode.map
                          ? _MapView(
                              tracks: _tracks,
                              selectedId: _selectedId,
                              onSelect: (id) => setState(
                                () => _selectedId = (_selectedId == id) ? null : id,
                              ),
                              onOpenListenUrl: _openListenUrl,
                              onRemoveSelected: _removeSelected,
                            )
                          : _ListView(tracks: _tracks),
                    ),
                  ],
                ),
              ),
      ),
    );
  }
}

class _Header extends StatelessWidget {
  final _ViewMode view;
  final ValueChanged<_ViewMode> onChanged;

  const _Header({required this.view, required this.onChanged});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Text('保存した曲',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
        SegmentedButton<_ViewMode>(
          segments: const [
            ButtonSegment(value: _ViewMode.map, label: Text('マップ')),
            ButtonSegment(value: _ViewMode.list, label: Text('一覧')),
          ],
          selected: {view},
          onSelectionChanged: (s) => onChanged(s.first),
          showSelectedIcon: false,
        ),
      ],
    );
  }
}

class _StatsRow extends StatelessWidget {
  final int count;
  final int areaCount;

  const _StatsRow({required this.count, required this.areaCount});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(child: _StatCard(label: '保存曲数', value: '$count')),
        const SizedBox(width: 10),
        Expanded(child: _StatCard(label: 'エリア数', value: '$areaCount')),
      ],
    );
  }
}

class _StatCard extends StatelessWidget {
  final String label;
  final String value;

  const _StatCard({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.zero,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label,
                style: TextStyle(fontSize: 12, color: Theme.of(context).hintColor)),
            const SizedBox(height: 3),
            Text(value, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          ],
        ),
      ),
    );
  }
}

class _MapView extends StatelessWidget {
  final List<SavedTrack> tracks;
  final String? selectedId;
  final ValueChanged<String> onSelect;
  final ValueChanged<SavedTrack> onOpenListenUrl;
  final VoidCallback onRemoveSelected;

  const _MapView({
    required this.tracks,
    required this.selectedId,
    required this.onSelect,
    required this.onOpenListenUrl,
    required this.onRemoveSelected,
  });

  SavedTrack? get _selected {
    for (final track in tracks) {
      if (track.id == selectedId) return track;
    }
    return null;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Expanded(
          flex: 3,
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: Container(
              color: const Color(0xFFE3E7EA),
              // TODO: replace with a real map widget (e.g. google_maps_flutter)
              // once [SavedTrack] carries real lat/lng instead of mock
              // percentage coordinates.
              child: LayoutBuilder(
                builder: (context, constraints) {
                  return Stack(
                    clipBehavior: Clip.none,
                    children: [
                      for (final track in tracks)
                        Positioned(
                          left: constraints.maxWidth * track.mapX / 100 -
                              _MapPin.size / 2,
                          top: constraints.maxHeight * track.mapY / 100 -
                              _MapPin.size / 2,
                          child: _MapPin(
                            track: track,
                            selected: track.id == selectedId,
                            onTap: () {
                              onSelect(track.id);
                              onOpenListenUrl(track);
                            },
                          ),
                        ),
                    ],
                  );
                },
              ),
            ),
          ),
        ),
        if (_selected != null) ...[
          const SizedBox(height: 12),
          _SelectedTrackSheet(track: _selected!, onRemove: onRemoveSelected),
        ],
      ],
    );
  }
}

/// A map pin rendered as the track's small jacket photo. Tapping it jumps
/// to the track's listen URL (see [_SavedTracksMapPageState._openListenUrl]).
class _MapPin extends StatelessWidget {
  static const double size = 40;

  final SavedTrack track;
  final bool selected;
  final VoidCallback onTap;

  const _MapPin({required this.track, required this.selected, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final borderColor = selected ? const Color(0xFF007AFF) : const Color(0xFFFF3B30);
    final artwork = track.artworkUrl;

    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          border: Border.all(color: borderColor, width: 2.5),
          boxShadow: const [
            BoxShadow(color: Color(0x40000000), blurRadius: 3, offset: Offset(0, 1)),
          ],
        ),
        child: ClipOval(
          child: artwork == null
              ? Container(
                  color: const Color(0xFFE3E7EA),
                  child: const Icon(Icons.music_note, size: 18),
                )
              : Image.network(
                  artwork,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) => Container(
                    color: const Color(0xFFE3E7EA),
                    child: const Icon(Icons.music_note, size: 18),
                  ),
                ),
        ),
      ),
    );
  }
}

class _SelectedTrackSheet extends StatelessWidget {
  final SavedTrack track;
  final VoidCallback onRemove;

  const _SelectedTrackSheet({required this.track, required this.onRemove});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.zero,
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(track.title,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 2),
                  Text(
                    '${track.artist}　${track.area}　${track.savedAtLabel}',
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: TextStyle(fontSize: 13, color: Theme.of(context).hintColor),
                  ),
                ],
              ),
            ),
            IconButton(
              onPressed: onRemove,
              icon: const Icon(Icons.delete_outline, color: Color(0xFFFF3B30)),
            ),
          ],
        ),
      ),
    );
  }
}

class _ListView extends StatelessWidget {
  final List<SavedTrack> tracks;

  const _ListView({required this.tracks});

  @override
  Widget build(BuildContext context) {
    if (tracks.isEmpty) {
      return Center(
        child: Text(
          'まだ何も保存されていません。\n曲を判定すると、ここに記録されます。',
          textAlign: TextAlign.center,
          style: TextStyle(color: Theme.of(context).hintColor),
        ),
      );
    }
    return Card(
      margin: EdgeInsets.zero,
      clipBehavior: Clip.antiAlias,
      child: ListView.separated(
        itemCount: tracks.length,
        separatorBuilder: (_, __) => const Divider(height: 1),
        itemBuilder: (context, index) {
          final track = tracks[index];
          return ListTile(
            leading: const CircleAvatar(
              backgroundColor: Color(0xFFFF3B30),
              foregroundColor: Colors.white,
              child: Icon(Icons.music_note, size: 16),
            ),
            title: Text(track.title, maxLines: 1, overflow: TextOverflow.ellipsis),
            subtitle: Text(
              '${track.artist}　${track.area}　${track.savedAtLabel}',
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
          );
        },
      ),
    );
  }
}
