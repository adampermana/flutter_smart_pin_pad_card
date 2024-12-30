class CardReaderException implements Exception {
  final String code;
  final String message;

  CardReaderException(this.code, this.message);

  @override
  String toString() => 'CardReaderException: [$code] $message';
}