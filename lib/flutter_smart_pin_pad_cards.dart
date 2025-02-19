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
      final Map<dynamic, dynamic> result =
          await _channel.invokeMethod('startSwipeCardReading');
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
  static Future<Map<dynamic, dynamic>> startInsertCardReading({
    bool enableMag = false,
    bool enableIcc = false,
    bool enableRf = false,
    int timeout = 60000,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'startInsertCardReading',
        {
          'enableIcc': enableIcc,
          'enableMag': enableMag,
          'enableRf': enableRf,
          'timeout': timeout,
        },
      );

      print('Card Number: ${result['pan']}');
      print('Expiry Date: ${result['expiryDate']}');
      // For IC cards, we need to extract PAN through the EMV transaction process
      // if (result.containsKey('cardType') && result['cardType'] == 'IC') {
      //   // This should be handled in the native code and returned properly
      //   if (result.containsKey('emvData') && result['emvData'] is Map) {
      //     Map<dynamic, dynamic> emvData = result['emvData'];
      //     if (emvData.containsKey('pan')) {
      //       result['pan'] = emvData['pan']; // Extract PAN from EMV data
      //     }
      //   }
      // }

      if (result.containsKey('pan')) {
        String cardNumber = result['pan'];
        print('Card Number: $cardNumber');
      } else {
        print('No card number found in the response');
      }
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
      await _channel.invokeMethod('stopInsertCardReading');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }
}

/// Custom exception for card reader errors
