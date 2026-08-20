import 'dart:convert';

import 'package:http/http.dart' as http;

import 'itunes_track.dart';

/// Searches songs via the public iTunes Search API (no auth required).
/// https://performance-partners.apple.com/search-api
class ItunesSearchService {
  static const _endpoint = 'https://itunes.apple.com/search';

  Future<List<ItunesTrack>> search(String term, {int limit = 25}) async {
    final trimmed = term.trim();
    if (trimmed.isEmpty) return const [];

    final uri = Uri.parse(_endpoint).replace(queryParameters: {
      'term': trimmed,
      'media': 'music',
      'entity': 'song',
      'limit': '$limit',
    });

    final response = await http.get(uri);
    if (response.statusCode != 200) {
      throw Exception('iTunes Search API error: HTTP ${response.statusCode}');
    }

    final body = jsonDecode(utf8.decode(response.bodyBytes)) as Map<String, dynamic>;
    final results = (body['results'] as List<dynamic>? ?? const [])
        .cast<Map<String, dynamic>>();

    return results.map(ItunesTrack.fromJson).toList();
  }
}
