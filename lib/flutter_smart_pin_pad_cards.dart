import 'package:flutter/services.dart';
import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';

import 'card_reader_exception.dart';

/// Flutter Smart PIN Pad Cards Plugin
/// Enhanced version with dynamic PIN block support
class FlutterSmartPinPadCards {
  static const MethodChannel _channel =
  MethodChannel('flutter_smart_pin_pad_cards');

  // PIN Block Format Constants
  static const int PIN_BLOCK_FORMAT_0 = 0; // ISO 9564-1 Format 0 (Standard)
  static const int PIN_BLOCK_FORMAT_1 = 1; // ISO 9564-1 Format 1 (Random)
  static const int PIN_BLOCK_FORMAT_2 = 2; // ISO 9564-1 Format 2 (Filler)
  static const int PIN_BLOCK_FORMAT_3 = 3; // ISO 9564-1 Format 3 (Random+XOR)
  static const int PIN_BLOCK_FORMAT_4 = 4; // ISO 9564-1 Format 4 (Enhanced)

  // Encryption Type Constants
  static const int ENCRYPT_3DES = 0;
  static const int ENCRYPT_AES = 1;
  static const int ENCRYPT_DES = 2;

  // Key Type Constants
  static const int KEY_TYPE_PIK = 0; // PIN encryption key
  static const int KEY_TYPE_MAK = 1; // MAC key
  static const int KEY_TYPE_DEK = 2; // Data encryption key

  // Processing Codes
  static const String PROCESSING_CODE_CREATE_PIN = "920000";
  static const String PROCESSING_CODE_CHANGE_PIN = "930000";
  static const String PROCESSING_CODE_AUTHORIZE_PIN = "940000";

  /// Get platform version
  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// Card Reading Methods

  /// Start swipe card reading
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

  /// Stop swipe card reading
  static Future<void> stopSwipeCardReading() async {
    try {
      await _channel.invokeMethod('stopSwipeCardReading');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Decrypt working key using master key
  static Future<WorkingKeyResult> decryptWorkingKey({
    required String encryptedWorkingKey,
  }) async {
    try {
      final result = await _channel.invokeMethod('decryptWorkingKey', {
        'encryptedWorkingKey': encryptedWorkingKey,
      });
      return WorkingKeyResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Set working key directly (for already decrypted working key)
  static Future<WorkingKeyResult> setWorkingKey({
    required String workingKey,
  }) async {
    try {
      final result = await _channel.invokeMethod('setWorkingKey', {
        'workingKey': workingKey,
      });
      return WorkingKeyResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Clear working key cache
  static Future<Map<String, dynamic>> clearWorkingKeyCache() async {
    try {
      final result = await _channel.invokeMethod('clearWorkingKeyCache');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Get working key status
  static Future<Map<String, dynamic>> getWorkingKeyStatus() async {
    try {
      final result = await _channel.invokeMethod('getWorkingKeyStatus');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Get master key info
  static Future<Map<String, dynamic>> getMasterKeyInfo() async {
    try {
      final result = await _channel.invokeMethod('getMasterKeyInfo');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }


  /// Start insert card reading
  static Future<CardData> startInsertCardReading({
    bool enableMag = true,
    bool enableIcc = true,
    bool enableRf = true,
    int timeout = 60000,
  }) async {
    try {
      final result = await _channel.invokeMethod('startInsertCardReading', {
        'enableMag': enableMag,
        'enableIcc': enableIcc,
        'enableRf': enableRf,
        'timeout': timeout,
      });
      // Log hasil untuk debugging
      print('Card Reading Result: $result');

      // Konversi hasil ke format yang diharapkan
      Map<String, dynamic> processedResult = _processResult(result);

      // Buat objek CardData dari hasil yang sudah diproses
      return CardData.fromMap(processedResult);    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Stop insert card reading
  static Future<void> stopInsertCardReading() async {
    try {
      await _channel.invokeMethod('stopInsertCardReading');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// PIN Block Methods

  /// Create dynamic PIN block with full ISO 9564 support
  static Future<PinBlockResult> createDynamicPinBlock({
    required String pin,
    required String cardNumber,
    int format = PIN_BLOCK_FORMAT_0,
    String? encryptionKey,
    int encryptionType = ENCRYPT_3DES,
    String fillerChar = "F",
    bool useHardwareEncryption = true,
  }) async {
    try {
      final result = await _channel.invokeMethod('createDynamicPinBlock', {
        'pin': pin,
        'cardNumber': cardNumber,
        'format': format,
        'encryptionKey': encryptionKey ?? "404142434445464748494A4B4C4D4E4F",
        'encryptionType': encryptionType,
        'fillerChar': fillerChar,
        'useHardwareEncryption': useHardwareEncryption,
      });
      return PinBlockResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Create PIN operation (Processing Code: 920000)
  static Future<PinBlockResult> createPinDynamic({
    required String newPin,
    required String cardNumber,
    int format = PIN_BLOCK_FORMAT_0,
    String? encryptionKey,
    int encryptionType = ENCRYPT_3DES,
    bool useHardwareEncryption = true,
  }) async {
    try {
      final result = await _channel.invokeMethod('createPinDynamic', {
        'newPin': newPin,
        'cardNumber': cardNumber,
        'format': format,
        'encryptionKey': encryptionKey ?? "404142434445464748494A4B4C4D4E4F",
        'encryptionType': encryptionType,
        'useHardwareEncryption': useHardwareEncryption,
      });
      return PinBlockResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Change PIN operation (Processing Code: 930000)
  static Future<PinBlockResult> changePinDynamic({
    required String currentPin,
    required String newPin,
    required String cardNumber,
    int format = PIN_BLOCK_FORMAT_0,
    String? encryptionKey,
    int encryptionType = ENCRYPT_3DES,
    bool useHardwareEncryption = true,
  }) async {
    try {
      final result = await _channel.invokeMethod('changePinDynamic', {
        'currentPin': currentPin,
        'newPin': newPin,
        'cardNumber': cardNumber,
        'format': format,
        'encryptionKey': encryptionKey ?? "404142434445464748494A4B4C4D4E4F",
        'encryptionType': encryptionType,
        'useHardwareEncryption': useHardwareEncryption,
      });
      return PinBlockResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// PIN Authorization operation (Processing Code: 940000)
  static Future<PinBlockResult> authorizePinDynamic({
    required String pin,
    required String cardNumber,
    int? transactionAmount,
    int format = PIN_BLOCK_FORMAT_0,
    String? encryptionKey,
    int encryptionType = ENCRYPT_3DES,
    bool useHardwareEncryption = true,
  }) async {
    try {
      final result = await _channel.invokeMethod('authorizePinDynamic', {
        'pin': pin,
        'cardNumber': cardNumber,
        'transactionAmount': transactionAmount,
        'format': format,
        'encryptionKey': encryptionKey ?? "404142434445464748494A4B4C4D4E4F",
        'encryptionType': encryptionType,
        'useHardwareEncryption': useHardwareEncryption,
      });
      return PinBlockResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Test all PIN block formats (for debugging)
  /// Gak Dipakai
  // static Future<Map<String, PinBlockResult>> testAllPinBlockFormats({
  //   required String pin,
  //   required String cardNumber,
  //   String? encryptionKey,
  // }) async {
  //   try {
  //     final result = await _channel.invokeMethod('testAllPinBlockFormats', {
  //       'pin': pin,
  //       'cardNumber': cardNumber,
  //       'encryptionKey': encryptionKey ?? "404142434445464748494A4B4C4D4E4F",
  //     });
  //
  //     final Map<String, dynamic> resultMap = Map<String, dynamic>.from(result);
  //     final Map<String, PinBlockResult> formattedResults = {};
  //
  //     for (final entry in resultMap.entries) {
  //       formattedResults[entry.key] = PinBlockResult.fromMap(
  //           Map<String, dynamic>.from(entry.value)
  //       );
  //     }
  //
  //     return formattedResults;
  //   } on PlatformException catch (e) {
  //     throw CardReaderException(e.code, e.message ?? 'Unknown error');
  //   }
  // }

  /// Legacy PIN Block method for backward compatibility
  static Future<PinBlockResult> createPinBlock({
    required String pin,
    required String cardNumber,
    int format = PIN_BLOCK_FORMAT_0,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      final result = await _channel.invokeMethod('createPinBlock', {
        'pin': pin,
        'cardNumber': cardNumber,
        'format': format,
        'keyIndex': keyIndex,
        'encryptionType': encryptionType,
      });
      return PinBlockResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Pinpad Management Methods

  /// Initialize pinpad
  static Future<bool> initPinpad() async {
    try {
      final result = await _channel.invokeMethod('initPinpad');
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Close pinpad
  static Future<void> closePinpad() async {
    try {
      await _channel.invokeMethod('closePinpad');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Get pinpad status
  static Future<Map<String, dynamic>> getPinpadStatus() async {
    try {
      final result = await _channel.invokeMethod('getPinpadStatus');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Load main key
  static Future<bool> loadMainKey({
    required int keyIndex,
    required String keyData,
    String? checkValue,
  }) async {
    try {
      final result = await _channel.invokeMethod('loadMainKey', {
        'keyIndex': keyIndex,
        'keyData': keyData,
        'checkValue': checkValue,
      });
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Load work key
  static Future<bool> loadWorkKey({
    required int keyType,
    required int masterKeyId,
    required int workKeyId,
    required String keyData,
    String? checkValue,
  }) async {
    try {
      final result = await _channel.invokeMethod('loadWorkKey', {
        'keyType': keyType,
        'masterKeyId': masterKeyId,
        'workKeyId': workKeyId,
        'keyData': keyData,
        'checkValue': checkValue,
      });
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Get key state
  // static Future<bool> getKeyState({
  //   required int keyType,
  //   required int keyIndex,
  // }) async {
  //   try {
  //     final result = await _channel.invokeMethod('getKeyState', {
  //       'keyType': keyType,
  //       'keyIndex': keyIndex,
  //     });
  //     return result as bool;
  //   } on PlatformException catch (e) {
  //     throw CardReaderException(e.code, e.message ?? 'Unknown error');
  //   }
  // }

  /// Get MAC
  static Future<Map<String, dynamic>> getMac(Map<String, dynamic> params) async {
    try {
      final result = await _channel.invokeMethod('getMac', params);
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Generate random number
  // static Future<String> getRandom() async {
  //   try {
  //     final result = await _channel.invokeMethod('getRandom');
  //     return result as String;
  //   } on PlatformException catch (e) {
  //     throw CardReaderException(e.code, e.message ?? 'Unknown error');
  //   }
  // }

  /// Utility Methods

  /// Get format description
  static String getFormatDescription(int format) {
    switch (format) {
      case PIN_BLOCK_FORMAT_0:
        return "ISO 9564-1 Format 0 (Standard)";
      case PIN_BLOCK_FORMAT_1:
        return "ISO 9564-1 Format 1 (Random)";
      case PIN_BLOCK_FORMAT_2:
        return "ISO 9564-1 Format 2 (Filler)";
      case PIN_BLOCK_FORMAT_3:
        return "ISO 9564-1 Format 3 (Random+XOR)";
      case PIN_BLOCK_FORMAT_4:
        return "ISO 9564-1 Format 4 (Enhanced)";
      default:
        return "Unknown Format";
    }
  }

  /// Get encryption type description
  static String getEncryptionTypeDescription(int encryptionType) {
    switch (encryptionType) {
      case ENCRYPT_3DES:
        return "3DES";
      case ENCRYPT_AES:
        return "AES";
      case ENCRYPT_DES:
        return "DES";
      default:
        return "Unknown";
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