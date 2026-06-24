enum GestureType {
  singleTap,
  doubleTap,
  hold;

  static GestureType? fromString(String? value) {
    return switch (value) {
      'SINGLE_TAP' => GestureType.singleTap,
      'DOUBLE_TAP' => GestureType.doubleTap,
      'HOLD' => GestureType.hold,
      _ => null,
    };
  }
}
