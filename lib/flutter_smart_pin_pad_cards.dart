import 'dart:async';
import 'package:flutter/services.dart';

import 'card_reader_exception.dart';

class FlutterSmartPinPadCards {
  static const MethodChannel _channel =
      MethodChannel('flutter_smart_pin_pad_cards');

  /// Starts the card reading process
  /// Returns a Map containing card data if successful
  /// Throws a PlatformException if there's an error
  Future<Map<dynamic, dynamic>> startSwipeCardReading() async {
    try {
      final Map<dynamic, dynamic> result =
          await _channel.invokeMethod('startSwipeCardReading');
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Stops the card reading process
  /// Returns void if successful
  /// Throws a PlatformException if there's an error
  Future<void> stopSwipeCardReading() async {
    try {
      await _channel.invokeMethod('stopSwipeCardReading');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }
}

/// Custom exception for card reader errors
