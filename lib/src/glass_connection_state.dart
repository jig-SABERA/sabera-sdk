class GlassConnectionState {
  final bool connected;
  final String? deviceId;
  final String? deviceName;

  const GlassConnectionState({
    required this.connected,
    this.deviceId,
    this.deviceName,
  });

  factory GlassConnectionState.fromMap(Map<dynamic, dynamic> map) {
    return GlassConnectionState(
      connected: map['connected'] as bool? ?? false,
      deviceId: map['deviceId'] as String?,
      deviceName: map['deviceName'] as String?,
    );
  }

  static const disconnected = GlassConnectionState(connected: false);
}
