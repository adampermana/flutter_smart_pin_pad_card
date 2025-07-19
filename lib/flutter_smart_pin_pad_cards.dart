import 'dart:async';
import 'package:flutter/services.dart';
import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';

import 'card_reader_exception.dart';

class FlutterSmartPinPadCards {
  static const MethodChannel _channel =
  MethodChannel('flutter_smart_pin_pad_cards');

  // PIN Block format constants
  static const int PIN_BLOCK_FORMAT_0 = 0;
  static const int PIN_BLOCK_FORMAT_1 = 1;
  static const int PIN_BLOCK_FORMAT_2 = 2;
  static const int PIN_BLOCK_FORMAT_3 = 3;

  // Encryption type constants
  static const int ENCRYPT_3DES = 0;
  static const int ENCRYPT_AES = 1;

  // Key type constants
  static const int KEY_TYPE_PIK = 0; // PIN encryption key
  static const int KEY_TYPE_MAK = 1; // MAC key
  static const int KEY_TYPE_DEK = 2; // Data encryption key

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

  /// Initialize the PIN pad
  /// Returns true if successful, false otherwise
  /// Throws a CardReaderException if there's an error
  static Future<bool> initPinpad() async {
    try {
      final bool result = await _channel.invokeMethod('initPinpad');
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Close the PIN pad
  /// Returns void if successful
  /// Throws a CardReaderException if there's an error
  static Future<void> closePinpad() async {
    try {
      await _channel.invokeMethod('closePinpad');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Get PIN pad status
  /// Returns a Map containing status information
  /// Throws a CardReaderException if there's an error
  static Future<Map<String, dynamic>> getPinpadStatus() async {
    try {
      final Map<dynamic, dynamic> result =
      await _channel.invokeMethod('getPinpadStatus');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Create a PIN Block
  /// Parameters:
  /// - pin: User entered PIN (4-12 digits)
  /// - cardNumber: Card number (PAN)
  /// - format: PIN Block format (0, 1, 2, 3) - default is 0
  /// - keyIndex: Key index for encryption - default is 0
  /// - encryptionType: Encryption algorithm (0=3DES, 1=AES) - default is 3DES
  /// Returns a PinBlockResult object if successful
  /// Throws a CardReaderException if there's an error
  static Future<PinBlockResult> createPinBlock({
    required String pin,
    required String cardNumber,
    int format = PIN_BLOCK_FORMAT_0,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'createPinBlock',
        {
          'pin': pin,
          'cardNumber': cardNumber,
          'format': format,
          'keyIndex': keyIndex,
          'encryptionType': encryptionType,
        },
      );

      return PinBlockResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Verify PIN with PIN Block
  /// Parameters:
  /// - pinBlock: Encrypted PIN Block (hex string)
  /// - cardNumber: Card number (PAN)
  /// - format: PIN Block format (0, 1, 2, 3) - default is 0
  /// - keyIndex: Key index for decryption - default is 0
  /// - encryptionType: Encryption algorithm (0=3DES, 1=AES) - default is 3DES
  /// Returns a PinVerifyResult object if successful
  /// Throws a CardReaderException if there's an error
  static Future<PinVerifyResult> verifyPin({
    required String pinBlock,
    required String cardNumber,
    int format = PIN_BLOCK_FORMAT_0,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'verifyPin',
        {
          'pinBlock': pinBlock,
          'cardNumber': cardNumber,
          'format': format,
          'keyIndex': keyIndex,
          'encryptionType': encryptionType,
        },
      );

      return PinVerifyResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Load main key into the PIN pad
  /// Parameters:
  /// - keyIndex: Key index
  /// - keyData: Key data (hex string)
  /// - checkValue: Check value (hex string, optional)
  /// Returns true if successful, false otherwise
  /// Throws a CardReaderException if there's an error
  static Future<bool> loadMainKey({
    required int keyIndex,
    required String keyData,
    String? checkValue,
  }) async {
    try {
      final bool result = await _channel.invokeMethod(
        'loadMainKey',
        {
          'keyIndex': keyIndex,
          'keyData': keyData,
          'checkValue': checkValue,
        },
      );
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Load work key into the PIN pad
  /// Parameters:
  /// - keyType: Key type (0=PIK, 1=MAK, 2=DEK)
  /// - masterKeyId: Master key ID
  /// - workKeyId: Work key ID
  /// - keyData: Key data (hex string)
  /// - checkValue: Check value (hex string, optional)
  /// Returns true if successful, false otherwise
  /// Throws a CardReaderException if there's an error
  static Future<bool> loadWorkKey({
    required int keyType,
    required int masterKeyId,
    required int workKeyId,
    required String keyData,
    String? checkValue,
  }) async {
    try {
      final bool result = await _channel.invokeMethod(
        'loadWorkKey',
        {
          'keyType': keyType,
          'masterKeyId': masterKeyId,
          'workKeyId': workKeyId,
          'keyData': keyData,
          'checkValue': checkValue,
        },
      );
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Get key state from the PIN pad
  /// Parameters:
  /// - keyType: Key type (0=PIK, 1=MAK, 2=DEK)
  /// - keyIndex: Key index
  /// Returns true if key exists, false otherwise
  /// Throws a CardReaderException if there's an error
  static Future<bool> getKeyState({
    required int keyType,
    required int keyIndex,
  }) async {
    try {
      final bool result = await _channel.invokeMethod(
        'getKeyState',
        {
          'keyType': keyType,
          'keyIndex': keyIndex,
        },
      );
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Get MAC (Message Authentication Code)
  /// Parameters:
  /// - params: Parameters for MAC calculation
  /// Returns a MacResult object if successful
  /// Throws a CardReaderException if there's an error
  static Future<MacResult> getMac({
    required Map<String, dynamic> params,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'getMac',
        params,
      );

      return MacResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Get random number from PIN pad
  /// Returns a random number (hex string) if successful
  /// Throws a CardReaderException if there's an error
  static Future<String> getRandom() async {
    try {
      final String result = await _channel.invokeMethod('getRandom');
      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Change PIN (Ganti PIN)
  /// Parameters:
  /// - oldPinBlock: Encrypted old PIN block (hex string)
  /// - newPinBlock: Encrypted new PIN block (hex string)
  /// - cardNumber: Card number (PAN)
  /// - keyIndex: Key index for encryption/decryption - default is 0
  /// - encryptionType: Encryption algorithm (0=3DES, 1=AES) - default is 3DES
  /// Returns a ChangePinResult object if successful
  /// Throws a CardReaderException if there's an error
  static Future<ChangePinResult> changePin({
    required String oldPinBlock,
    required String newPinBlock,
    required String cardNumber,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'changePin',
        {
          'oldPinBlock': oldPinBlock,
          'newPinBlock': newPinBlock,
          'cardNumber': cardNumber,
          'keyIndex': keyIndex,
          'encryptionType': encryptionType,
        },
      );

      return ChangePinResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// PIN Authorization/Verification (Otorisasi PIN)
  /// Parameters:
  /// - pinBlock: Encrypted PIN block (hex string)
  /// - cardNumber: Card number (PAN)
  /// - amount: Transaction amount (optional)
  /// - keyIndex: Key index for decryption - default is 0
  /// - encryptionType: Encryption algorithm (0=3DES, 1=AES) - default is 3DES
  /// Returns an AuthorizePinResult object if successful
  /// Throws a CardReaderException if there's an error
  static Future<AuthorizePinResult> authorizePin({
    required String pinBlock,
    required String cardNumber,
    int? amount,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'authorizePin',
        {
          'pinBlock': pinBlock,
          'cardNumber': cardNumber,
          'amount': amount,
          'keyIndex': keyIndex,
          'encryptionType': encryptionType,
        },
      );

      return AuthorizePinResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Create PIN (Create new PIN)
  /// Parameters:
  /// - newPin: New PIN to be created (4-12 digits)
  /// - cardNumber: Card number (PAN)
  /// - keyIndex: Key index for encryption - default is 0
  /// - encryptionType: Encryption algorithm (0=3DES, 1=AES) - default is 3DES
  /// Returns a CreatePinResult object if successful
  /// Throws a CardReaderException if there's an error
  static Future<CreatePinResult> createPin({
    required String newPin,
    required String cardNumber,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'createPin',
        {
          'newPin': newPin,
          'cardNumber': cardNumber,
          'keyIndex': keyIndex,
          'encryptionType': encryptionType,
        },
      );

      return CreatePinResult.fromMap(Map<String, dynamic>.from(result));
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

/// Result class for PIN Block creation
class PinBlockResult {
  final bool success;
  final String? pinBlock;
  final int? format;
  final int? keyIndex;
  final int? encryptionType;
  final String? error;

  PinBlockResult({
    required this.success,
    this.pinBlock,
    this.format,
    this.keyIndex,
    this.encryptionType,
    this.error,
  });

  factory PinBlockResult.fromMap(Map<String, dynamic> map) {
    return PinBlockResult(
      success: map['success'] ?? false,
      pinBlock: map['pinBlock'],
      format: map['format'],
      keyIndex: map['keyIndex'],
      encryptionType: map['encryptionType'],
      error: map['error'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'success': success,
      'pinBlock': pinBlock,
      'format': format,
      'keyIndex': keyIndex,
      'encryptionType': encryptionType,
      'error': error,
    };
  }

  @override
  String toString() {
    return 'PinBlockResult{success: $success, pinBlock: $pinBlock, format: $format, keyIndex: $keyIndex, encryptionType: $encryptionType, error: $error}';
  }
}

/// Result class for PIN verification
class PinVerifyResult {
  final bool success;
  final String? pin;
  final int? pinLength;
  final String? error;

  PinVerifyResult({
    required this.success,
    this.pin,
    this.pinLength,
    this.error,
  });

  factory PinVerifyResult.fromMap(Map<String, dynamic> map) {
    return PinVerifyResult(
      success: map['success'] ?? false,
      pin: map['pin'],
      pinLength: map['pinLength'],
      error: map['error'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'success': success,
      'pin': pin,
      'pinLength': pinLength,
      'error': error,
    };
  }

  @override
  String toString() {
    return 'PinVerifyResult{success: $success, pin: $pin, pinLength: $pinLength, error: $error}';
  }
}

/// Result class for MAC operations
class MacResult {
  final bool success;
  final String? mac;
  final String? error;

  MacResult({
    required this.success,
    this.mac,
    this.error,
  });

  factory MacResult.fromMap(Map<String, dynamic> map) {
    return MacResult(
      success: map['success'] ?? false,
      mac: map['mac'],
      error: map['error'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'success': success,
      'mac': mac,
      'error': error,
    };
  }

  @override
  String toString() {
    return 'MacResult{success: $success, mac: $mac, error: $error}';
  }
}