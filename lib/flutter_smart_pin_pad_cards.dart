import 'dart:async';
import 'package:flutter/services.dart';

import 'card_reader_exception.dart';
import 'card_data.dart';

class FlutterSmartPinPadCards {
  static const MethodChannel _channel =
  MethodChannel('flutter_smart_pin_pad_cards');

  /// Starts the card reading process for magnetic swipe cards
  /// Returns a CardData object if successful
  /// Throws a CardReaderException if there's an error
  static Future<CardData> startSwipeCardReading() async {
    try {
      final Map<dynamic, dynamic> result =
      await _channel.invokeMethod('startSwipeCardReading');

      // Konversi hasil ke format yang diharapkan
      Map<String, dynamic> processedResult = _processResult(result);

      // Buat objek CardData dari hasil yang sudah diproses
      return CardData.fromMap(processedResult);
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
  /// Returns a CardData object if successful
  /// Throws a CardReaderException if there's an error
  static Future<CardData> startInsertCardReading({
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

      // Log hasil untuk debugging
      print('Card Reading Result: $result');

      // Konversi hasil ke format yang diharapkan
      Map<String, dynamic> processedResult = _processResult(result);

      // Buat objek CardData dari hasil yang sudah diproses
      return CardData.fromMap(processedResult);
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

  /// Helper method to process the result from method channel
  /// and convert it to a consistent format
  static Map<String, dynamic> _processResult(Map<dynamic, dynamic> result) {
    // Konversi keys dari dynamic ke String
    Map<String, dynamic> processedResult = {};

    result.forEach((key, value) {
      if (key is String) {
        processedResult[key] = value;
      }
    });

    // Standardisasi keys untuk PAN/cardNumber
    if (processedResult.containsKey('pan') && !processedResult.containsKey('cardNumber')) {
      processedResult['cardNumber'] = processedResult['pan'];
    } else if (processedResult.containsKey('cardNumber') && !processedResult.containsKey('pan')) {
      processedResult['pan'] = processedResult['cardNumber'];
    }

    // Handle EMV data untuk kartu IC jika diperlukan
    if (processedResult.containsKey('cardType') &&
        processedResult['cardType'] == 'IC' &&
        processedResult.containsKey('emvData')) {
      // Ekstrak data tambahan dari EMV jika diperlukan
      var emvData = processedResult['emvData'];
      if (emvData is Map && emvData.containsKey('pan') &&
          (processedResult['pan'] == null || processedResult['pan'].isEmpty)) {
        processedResult['pan'] = emvData['pan'];
        processedResult['cardNumber'] = emvData['pan'];
      }
    }

    return processedResult;
  }
}