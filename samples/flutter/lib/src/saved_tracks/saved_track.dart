/// A song the user saved (e.g. after an on-glasses song identification),
/// optionally tied to where/when it was heard.
///
/// [mapX]/[mapY] are placeholder coordinates (0-100, percentage position on
/// the mock map canvas) until real geolocation + map rendering is wired up.
class SavedTrack {
  final String id;
  final String title;
  final String artist;

  /// Human-readable area label (e.g. "渋谷"). Will later be derived from a
  /// real lat/lng once location data is linked in.
  final String area;

  /// Display label for when the track was saved (e.g. "8/12 19:40").
  final String savedAtLabel;

  final double mapX;
  final double mapY;

  /// Jacket artwork / streaming link, filled in once the song-info API
  /// (see MusicSearchPage's ItunesSearchService) is linked to saved tracks.
  final String? artworkUrl;
  final String? listenUrl;

  const SavedTrack({
    required this.id,
    required this.title,
    required this.artist,
    required this.area,
    required this.savedAtLabel,
    required this.mapX,
    required this.mapY,
    this.artworkUrl,
    this.listenUrl,
  });
}
