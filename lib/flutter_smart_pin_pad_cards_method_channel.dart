import 'package:flutter/services.dart';
import 'flutter_smart_pin_pad_cards_platform_interface.dart';

class MethodChannelFlutterSmartPinPadCards extends FlutterSmartPinPadCardsPlatform {
  final methodChannel = const MethodChannel('flutter_smart_pin_pad_cards');

  // ============================================================================
  // CARD READER METHODS
  // ============================================================================

  @override
  Future<Map<dynamic, dynamic>> startSwipeCardReading() async {
    final result = await methodChannel.invokeMethod('startSwipeCardReading');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<void> stopSwipeCardReading() async {
    await methodChannel.invokeMethod('stopSwipeCardReading');
  }

  @override
  Future<Map<dynamic, dynamic>> startInsertCardReading({
    bool enableMag = false,
    bool enableIcc = false,
    bool enableRf = false,
    int timeout = 60000,
  }) async {
    final result = await methodChannel.invokeMethod('startInsertCardReading', {
      'enableMag': enableMag,
      'enableIcc': enableIcc,
      'enableRf': enableRf,
      'timeout': timeout,
    });
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<void> stopInsertCardReading() async {
    await methodChannel.invokeMethod('stopInsertCardReading');
  }

  // ============================================================================
  // PINPAD BASIC METHODS
  // ============================================================================

  @override
  Future<bool> initPinpad() async {
    final result = await methodChannel.invokeMethod('initPinpad');
    return result as bool;
  }

  @override
  Future<void> closePinpad() async {
    await methodChannel.invokeMethod('closePinpad');
  }

  @override
  Future<Map<String, dynamic>> getPinpadStatus() async {
    final result = await methodChannel.invokeMethod('getPinpadStatus');
    return Map<String, dynamic>.from(result);
  }

  // ============================================================================
  // PIN BLOCK OPERATIONS
  // ============================================================================

  @override
  Future<Map<String, dynamic>> createPinBlock({
    required String pin,
    required String cardNumber,
    int format = 0,
    int keyIndex = 0,
    int encryptionType = 0,
  }) async {
    final result = await methodChannel.invokeMethod('createPinBlock', {
      'pin': pin,
      'cardNumber': cardNumber,
      'format': format,
      'keyIndex': keyIndex,
      'encryptionType': encryptionType,
    });
    return Map<String, dynamic>.from(result);
  }

  @override
  Future<Map<String, dynamic>> verifyPin({
    required String pinBlock,
    required String cardNumber,
    int format = 0,
    int keyIndex = 0,
    int encryptionType = 0,
  }) async {
    final result = await methodChannel.invokeMethod('verifyPin', {
      'pinBlock': pinBlock,
      'cardNumber': cardNumber,
      'format': format,
      'keyIndex': keyIndex,
      'encryptionType': encryptionType,
    });
    return Map<String, dynamic>.from(result);
  }

  @override
  Future<Map<String, dynamic>> changePin({
    required String oldPinBlock,
    required String newPinBlock,
    required String cardNumber,
    int keyIndex = 0,
    int encryptionType = 0,
  }) async {
    final result = await methodChannel.invokeMethod('changePin', {
      'oldPinBlock': oldPinBlock,
      'newPinBlock': newPinBlock,
      'cardNumber': cardNumber,
      'keyIndex': keyIndex,
      'encryptionType': encryptionType,
    });
    return Map<String, dynamic>.from(result);
  }

  @override
  Future<Map<String, dynamic>> authorizePin({
    required String pinBlock,
    required String cardNumber,
    int? amount,
    int keyIndex = 0,
    int encryptionType = 0,
  }) async {
    final Map<String, dynamic> parameters = {
      'pinBlock': pinBlock,
      'cardNumber': cardNumber,
      'keyIndex': keyIndex,
      'encryptionType': encryptionType,
    };

    if (amount != null) {
      parameters['amount'] = amount;
    }

    final result = await methodChannel.invokeMethod('authorizePin', parameters);
    return Map<String, dynamic>.from(result);
  }

  @override
  Future<Map<String, dynamic>> createPin({
    required String newPin,
    required String cardNumber,
    int keyIndex = 0,
    int encryptionType = 0,
  }) async {
    final result = await methodChannel.invokeMethod('createPin', {
      'newPin': newPin,
      'cardNumber': cardNumber,
      'keyIndex': keyIndex,
      'encryptionType': encryptionType,
    });
    return Map<String, dynamic>.from(result);
  }

  // ============================================================================
  // DYNAMIC PIN BLOCK METHODS
  // ============================================================================

  @override
  Future<Map<String, dynamic>> createDynamicPinBlock({
    required String pin,
    required String cardNumber,
    int format = 0,
    String encryptionKey = "404142434445464748494A4B4C4D4E4F",
    int encryptionType = 0,
    String fillerChar = "F",
    bool useHardwareEncryption = true,
  }) async {
    final result = await methodChannel.invokeMethod('createDynamicPinBlock', {
      'pin': pin,
      'cardNumber': cardNumber,
      'format': format,
      'encryptionKey': encryptionKey,
      'encryptionType': encryptionType,
      'fillerChar': fillerChar,
      'useHardwareEncryption': useHardwareEncryption,
    });
    return Map<String, dynamic>.from(result);
  }

  @override
  Future<Map<String, dynamic>> createPinDynamic({
    required String newPin,
    required String cardNumber,
    int format = 0,
    String encryptionKey = "404142434445464748494A4B4C4D4E4F",
    int encryptionType = 0,
    bool useHardwareEncryption = true,
  }) async {
    final result = await methodChannel.invokeMethod('createPinDynamic', {
      'newPin': newPin,
      'cardNumber': cardNumber,
      'format': format,
      'encryptionKey': encryptionKey,
      'encryptionType': encryptionType,
      'useHardwareEncryption': useHardwareEncryption,
    });
    return Map<String, dynamic>.from(result);
  }

  @override
  Future<Map<String, dynamic>> changePinDynamic({
    required String currentPin,
    required String newPin,
    required String cardNumber,
    int format = 0,
    String encryptionKey = "404142434445464748494A4B4C4D4E4F",
    int encryptionType = 0,
    bool useHardwareEncryption = true,
  }) async {
    final result = await methodChannel.invokeMethod('changePinDynamic', {
      'currentPin': currentPin,
      'newPin': newPin,
      'cardNumber': cardNumber,
      'format': format,
      'encryptionKey': encryptionKey,
      'encryptionType': encryptionType,
      'useHardwareEncryption': useHardwareEncryption,
    });
    return Map<String, dynamic>.from(result);
  }

  @override
  Future<Map<String, dynamic>> authorizePinDynamic({
    required String pin,
    required String cardNumber,
    int? transactionAmount,
    int format = 0,
    String encryptionKey = "404142434445464748494A4B4C4D4E4F",
    int encryptionType = 0,
    bool useHardwareEncryption = true,
  }) async {
    final Map<String, dynamic> parameters = {
      'pin': pin,
      'cardNumber': cardNumber,
      'format': format,
      'encryptionKey': encryptionKey,
      'encryptionType': encryptionType,
      'useHardwareEncryption': useHardwareEncryption,
    };

    if (transactionAmount != null) {
      parameters['transactionAmount'] = transactionAmount;
    }

    final result = await methodChannel.invokeMethod('authorizePinDynamic', parameters);
    return Map<String, dynamic>.from(result);
  }

  // ============================================================================
  // KEY MANAGEMENT METHODS
  // ============================================================================

  @override
  Future<bool> loadMainKey({
    required int keyIndex,
    required String keyData,
    String? checkValue,
  }) async {
    final Map<String, dynamic> parameters = {
      'keyIndex': keyIndex,
      'keyData': keyData,
    };

    if (checkValue != null) {
      parameters['checkValue'] = checkValue;
    }

    final result = await methodChannel.invokeMethod('loadMainKey', parameters);
    return result as bool;
  }

  @override
  Future<bool> loadWorkKey({
    required int keyType,
    required int masterKeyId,
    required int workKeyId,
    required String keyData,
    String? checkValue,
  }) async {
    final Map<String, dynamic> parameters = {
      'keyType': keyType,
      'masterKeyId': masterKeyId,
      'workKeyId': workKeyId,
      'keyData': keyData,
    };

    if (checkValue != null) {
      parameters['checkValue'] = checkValue;
    }

    final result = await methodChannel.invokeMethod('loadWorkKey', parameters);
    return result as bool;
  }

  @override
  Future<bool> getKeyState({
    required int keyType,
    required int keyIndex,
  }) async {
    final result = await methodChannel.invokeMethod('getKeyState', {
      'keyType': keyType,
      'keyIndex': keyIndex,
    });
    return result as bool;
  }

  // ============================================================================
  // UTILITY METHODS
  // ============================================================================

  // @override
  // Future<Map<String, dynamic>> getMac({
  //   required Map<String, dynamic> params,
  // }) async {
  //   final result = await methodChannel.invokeMethod('getMac', params);
  //   return Map<String, dynamic>.from(result);
  // }

  @override
  Future<String> getRandom() async {
    final result = await methodChannel.invokeMethod('getRandom');
    return result as String;
  }

  // ============================================================================
  // TESTING AND DEBUG METHODS
  // ============================================================================

  @override
  Future<Map<String, dynamic>> testAllPinBlockFormats({
    required String pin,
    required String cardNumber,
    String encryptionKey = "404142434445464748494A4B4C4D4E4F",
  }) async {
    final result = await methodChannel.invokeMethod('testAllPinBlockFormats', {
      'pin': pin,
      'cardNumber': cardNumber,
      'encryptionKey': encryptionKey,
    });
    return Map<String, dynamic>.from(result);
  }
}