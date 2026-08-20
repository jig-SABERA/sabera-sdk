import '../music/itunes_search_service.dart';
import 'saved_track.dart';

/// Source of the user's saved tracks. [MockSavedTracksRepository] below is
/// the only implementation for now; once the song-identification API and
/// location data are ready, add e.g. an `ApiSavedTracksRepository` that
/// implements this same interface and swap it in in `main.dart` /
/// `SavedTracksMapPage` — the UI does not need to change.
abstract class SavedTracksRepository {
  Future<List<SavedTrack>> fetchSavedTracks();

  Future<void> removeTrack(String id);
}

class MockSavedTracksRepository implements SavedTracksRepository {
  final ItunesSearchService _musicLookup = ItunesSearchService();

  static final List<SavedTrack> _seeds = [
    const SavedTrack(
      id: '1',
      title: '丸ノ内サディスティック',
      artist: '椎名林檎',
      area: '渋谷',
      savedAtLabel: '8/12 19:40',
      mapX: 40,
      mapY: 42,
    ),
    const SavedTrack(
      id: '2',
      title: 'Pretender',
      artist: 'Official髭男dism',
      area: '新宿',
      savedAtLabel: '8/14 08:05',
      mapX: 68,
      mapY: 66,
    ),
    const SavedTrack(
      id: '3',
      title: '夜に駆ける',
      artist: 'YOASOBI',
      area: '池袋',
      savedAtLabel: '8/16 21:12',
      mapX: 22,
      mapY: 74,
    ),
    const SavedTrack(
      id: '4',
      title: 'Lemon',
      artist: '米津玄師',
      area: '渋谷',
      savedAtLabel: '8/18 12:30',
      mapX: 48,
      mapY: 30,
    ),
  ];

  List<SavedTrack>? _tracks;

  @override
  Future<List<SavedTrack>> fetchSavedTracks() async {
    _tracks ??= await Future.wait(_seeds.map(_withJacketAndListenUrl));
    return List.unmodifiable(_tracks!);
  }

  /// Looks up the jacket image and listen URL for a saved track via the
  /// iTunes Search API (title/area/time stay as saved; only song metadata
  /// comes from the lookup).
  Future<SavedTrack> _withJacketAndListenUrl(SavedTrack seed) async {
    try {
      final results = await _musicLookup.search('${seed.title} ${seed.artist}', limit: 1);
      final match = results.isEmpty ? null : results.first;
      return SavedTrack(
        id: seed.id,
        title: seed.title,
        artist: seed.artist,
        area: seed.area,
        savedAtLabel: seed.savedAtLabel,
        mapX: seed.mapX,
        mapY: seed.mapY,
        artworkUrl: match?.artworkUrl512 ?? match?.artworkUrl100,
        listenUrl: match?.trackViewUrl,
      );
    } catch (_) {
      return seed;
    }
  }

  @override
  Future<void> removeTrack(String id) async {
    _tracks?.removeWhere((t) => t.id == id);
  }
}
