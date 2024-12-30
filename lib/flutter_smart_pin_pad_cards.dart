import 'dart:async';
import 'package:flutter/services.dart';

class FlutterSmartPinPadCards {
  static const MethodChannel _channel = MethodChannel('flutter_smart_pin_pad_cards');

  /// Starts the card reading process
  /// Returns a Map containing card data if successful
  /// Throws a PlatformException if there's an error
  Future<Map<dynamic, dynamic>> startCardReading() async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod('startCardReading');
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Stops the card reading process
  /// Returns void if successful
  /// Throws a PlatformException if there's an error
  Future<void> stopCardReading() async {
    try {
      await _channel.invokeMethod('stopCardReading');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }
}

/// Custom exception for card reader errors
class CardReaderException implements Exception {
  final String code;
  final String message;

  CardReaderException(this.code, this.message);

  @override
  String toString() => 'CardReaderException: [$code] $message';
}