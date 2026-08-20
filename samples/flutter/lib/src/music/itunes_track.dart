/// A single song result from the iTunes Search API.
class ItunesTrack {
  final int trackId;
  final String trackName;
  final String artistName;

  /// Small artwork thumbnail (100x100) returned by the API.
  final String? artworkUrl100;

  /// 30 second AAC preview of the track, when available.
  final String? previewUrl;

  /// Apple Music / iTunes page for the track — used to open the full song.
  final String trackViewUrl;

  const ItunesTrack({
    required this.trackId,
    required this.trackName,
    required this.artistName,
    required this.artworkUrl100,
    required this.previewUrl,
    required this.trackViewUrl,
  });

  /// Higher resolution artwork, derived from the 100x100 thumbnail URL.
  String? get artworkUrl512 =>
      artworkUrl100?.replaceFirst('100x100bb', '512x512bb');

  factory ItunesTrack.fromJson(Map<String, dynamic> json) {
    return ItunesTrack(
      trackId: json['trackId'] as int,
      trackName: json['trackName'] as String? ?? 'Unknown Track',
      artistName: json['artistName'] as String? ?? 'Unknown Artist',
      artworkUrl100: json['artworkUrl100'] as String?,
      previewUrl: json['previewUrl'] as String?,
      trackViewUrl: json['trackViewUrl'] as String? ?? '',
    );
  }
}
