import 'dart:async';
import 'package:flutter/services.dart';

import 'card_reader_exception.dart';

class FlutterSmartPinPadCards {
  static const MethodChannel _channel =
      MethodChannel('flutter_smart_pin_pad_cards');

  /// Starts the card reading process
  /// Returns a Map containing card data if successful
  /// Throws a PlatformException if there's an error
  static Future<Map<dynamic, dynamic>> startSwipeCardReading() async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod('startSwipeCardReading');
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Stops the swipe card reading process
  /// Returns void if successful
  /// Throws a CardReaderException if there's an error
  static Future<void> stopSwipeCardReading() async {
    try {
      await _channel.invokeMethod('stopSwipeCardReading');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Starts the card reading process with options for different card types
  /// Parameters:
  /// - enableMag: Enable magnetic card reading
  /// - enableIcc: Enable IC card reading
  /// - enableRf: Enable RF card reading
  /// - timeout: Timeout in milliseconds (default 60000)
  /// Returns a Map containing card data if successful
  /// Throws a CardReaderException if there's an error
  static Future<Map<dynamic, dynamic>> startCardReading({
    bool enableMag = true,
    bool enableIcc = true,
    bool enableRf = true,
    int timeout = 60000,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'startCardReading',
        {
          'enableMag': enableMag,
          'enableIcc': enableIcc,
          'enableRf': enableRf,
          'timeout': timeout,
        },
      );
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Stops the card reading process
  /// Returns void if successful
  /// Throws a CardReaderException if there's an error
  static Future<void> stopCardReading() async {
    try {
      await _channel.invokeMethod('stopCardReading');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }
}

/// Custom exception for card reader errors
