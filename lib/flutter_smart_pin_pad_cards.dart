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

  /// Create a PIN Block - Used for Create PIN operation (Processing Code 920000)
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
      // Validate input parameters
      if (!PinBlockUtil.isValidPin(pin)) {
        throw CardReaderException('INVALID_PIN', 'PIN must be 4-12 digits');
      }

      if (cardNumber.isEmpty) {
        throw CardReaderException('INVALID_CARD_NUMBER', 'Card number cannot be empty');
      }

      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'createPinBlock',
        {
          'pin': pin,
          'cardNumber': PinBlockUtil.formatCardNumber(cardNumber),
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
      // Validate input parameters
      if (!PinBlockUtil.isValidHex(pinBlock)) {
        throw CardReaderException('INVALID_PIN_BLOCK', 'PIN block must be valid hex string');
      }

      if (cardNumber.isEmpty) {
        throw CardReaderException('INVALID_CARD_NUMBER', 'Card number cannot be empty');
      }

      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'verifyPin',
        {
          'pinBlock': PinBlockUtil.formatPinBlockHex(pinBlock),
          'cardNumber': PinBlockUtil.formatCardNumber(cardNumber),
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

  /// Change PIN (Ganti PIN) - Processing Code: 930000
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
      // Validate input parameters
      if (!PinBlockUtil.isValidHex(oldPinBlock)) {
        throw CardReaderException('INVALID_OLD_PIN_BLOCK', 'Old PIN block must be valid hex string');
      }

      if (!PinBlockUtil.isValidHex(newPinBlock)) {
        throw CardReaderException('INVALID_NEW_PIN_BLOCK', 'New PIN block must be valid hex string');
      }

      if (cardNumber.isEmpty) {
        throw CardReaderException('INVALID_CARD_NUMBER', 'Card number cannot be empty');
      }

      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'changePin',
        {
          'oldPinBlock': PinBlockUtil.formatPinBlockHex(oldPinBlock),
          'newPinBlock': PinBlockUtil.formatPinBlockHex(newPinBlock),
          'cardNumber': PinBlockUtil.formatCardNumber(cardNumber),
          'keyIndex': keyIndex,
          'encryptionType': encryptionType,
        },
      );

      return ChangePinResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// PIN Authorization/Verification (Otorisasi PIN) - Processing Code: 940000
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
      // Validate input parameters
      if (!PinBlockUtil.isValidHex(pinBlock)) {
        throw CardReaderException('INVALID_PIN_BLOCK', 'PIN block must be valid hex string');
      }

      if (cardNumber.isEmpty) {
        throw CardReaderException('INVALID_CARD_NUMBER', 'Card number cannot be empty');
      }

      final Map<String, dynamic> parameters = {
        'pinBlock': PinBlockUtil.formatPinBlockHex(pinBlock),
        'cardNumber': PinBlockUtil.formatCardNumber(cardNumber),
        'keyIndex': keyIndex,
        'encryptionType': encryptionType,
      };

      if (amount != null) {
        parameters['amount'] = amount;
      }

      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'authorizePin',
        parameters,
      );

      return AuthorizePinResult.fromMap(Map<String, dynamic>.from(result));
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error occurred');
    }
  }

  /// Create PIN (Create new PIN) - Processing Code: 920000
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
      // Validate input parameters
      if (!PinBlockUtil.isValidPin(newPin)) {
        throw CardReaderException('INVALID_PIN', 'PIN must be 4-12 digits');
      }

      if (cardNumber.isEmpty) {
        throw CardReaderException('INVALID_CARD_NUMBER', 'Card number cannot be empty');
      }

      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'createPin',
        {
          'newPin': newPin,
          'cardNumber': PinBlockUtil.formatCardNumber(cardNumber),
          'keyIndex': keyIndex,
          'encryptionType': encryptionType,
        },
      );

      return CreatePinResult.fromMap(Map<String, dynamic>.from(result));
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
      // Validate input parameters
      if (!PinBlockUtil.isValidHex(keyData)) {
        throw CardReaderException('INVALID_KEY_DATA', 'Key data must be valid hex string');
      }

      if (checkValue != null && !PinBlockUtil.isValidHex(checkValue)) {
        throw CardReaderException('INVALID_CHECK_VALUE', 'Check value must be valid hex string');
      }

      final Map<String, dynamic> parameters = {
        'keyIndex': keyIndex,
        'keyData': PinBlockUtil.formatPinBlockHex(keyData),
      };

      if (checkValue != null) {
        parameters['checkValue'] = PinBlockUtil.formatPinBlockHex(checkValue);
      }

      final bool result = await _channel.invokeMethod('loadMainKey', parameters);
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
      // Validate input parameters
      if (!PinBlockUtil.isValidHex(keyData)) {
        throw CardReaderException('INVALID_KEY_DATA', 'Key data must be valid hex string');
      }

      if (checkValue != null && !PinBlockUtil.isValidHex(checkValue)) {
        throw CardReaderException('INVALID_CHECK_VALUE', 'Check value must be valid hex string');
      }

      final Map<String, dynamic> parameters = {
        'keyType': keyType,
        'masterKeyId': masterKeyId,
        'workKeyId': workKeyId,
        'keyData': PinBlockUtil.formatPinBlockHex(keyData),
      };

      if (checkValue != null) {
        parameters['checkValue'] = PinBlockUtil.formatPinBlockHex(checkValue);
      }

      final bool result = await _channel.invokeMethod('loadWorkKey', parameters);
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

  /// Utility method to create PIN block from PIN and card number
  /// This is a convenience method that combines PIN block creation with proper formatting
  static Future<String> createPinBlockForOperation({
    required String pin,
    required String cardNumber,
    required PinOperationType operationType,
    int format = PIN_BLOCK_FORMAT_0,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      final result = await createPinBlock(
        pin: pin,
        cardNumber: cardNumber,
        format: format,
        keyIndex: keyIndex,
        encryptionType: encryptionType,
      );

      if (!result.isSuccessful) {
        throw CardReaderException(
          result.responseCode ?? 'UNKNOWN_ERROR',
          result.error ?? 'Failed to create PIN block',
        );
      }

      return result.pinBlock ?? '';
    } catch (e) {
      if (e is CardReaderException) {
        rethrow;
      }
      throw CardReaderException('PIN_BLOCK_ERROR', 'Error creating PIN block: $e');
    }
  }

  /// Utility method to perform complete PIN operation workflow
  /// This method handles the entire PIN operation process including PIN block creation
  static Future<Map<String, dynamic>> performPinOperation({
    required PinOperationType operationType,
    required String cardNumber,
    String? currentPin,
    String? newPin,
    int? amount,
    int format = PIN_BLOCK_FORMAT_0,
    int keyIndex = 0,
    int encryptionType = ENCRYPT_3DES,
  }) async {
    try {
      switch (operationType) {
        case PinOperationType.createPin:
          if (newPin == null) {
            throw CardReaderException('MISSING_PIN', 'New PIN is required for create operation');
          }

          final result = await createPin(
            newPin: newPin,
            cardNumber: cardNumber,
            keyIndex: keyIndex,
            encryptionType: encryptionType,
          );

          return {
            'success': result.isSuccessful,
            'operationType': operationType.description,
            'processingCode': operationType.processingCode,
            'responseCode': result.responseCode,
            'message': result.detailedMessage,
            'pinBlock': result.pinBlock,
            'error': result.error,
          };

        case PinOperationType.changePin:
          if (currentPin == null || newPin == null) {
            throw CardReaderException('MISSING_PIN', 'Both current and new PIN are required for change operation');
          }

          // Create PIN blocks for both old and new PINs
          final oldPinBlockResult = await createPinBlock(
            pin: currentPin,
            cardNumber: cardNumber,
            format: format,
            keyIndex: keyIndex,
            encryptionType: encryptionType,
          );

          if (!oldPinBlockResult.isSuccessful) {
            throw CardReaderException(
              oldPinBlockResult.responseCode ?? 'PIN_BLOCK_ERROR',
              'Failed to create old PIN block: ${oldPinBlockResult.error}',
            );
          }

          final newPinBlockResult = await createPinBlock(
            pin: newPin,
            cardNumber: cardNumber,
            format: format,
            keyIndex: keyIndex,
            encryptionType: encryptionType,
          );

          if (!newPinBlockResult.isSuccessful) {
            throw CardReaderException(
              newPinBlockResult.responseCode ?? 'PIN_BLOCK_ERROR',
              'Failed to create new PIN block: ${newPinBlockResult.error}',
            );
          }

          final result = await changePin(
            oldPinBlock: oldPinBlockResult.pinBlock!,
            newPinBlock: newPinBlockResult.pinBlock!,
            cardNumber: cardNumber,
            keyIndex: keyIndex,
            encryptionType: encryptionType,
          );

          return {
            'success': result.isSuccessful,
            'operationType': operationType.description,
            'processingCode': operationType.processingCode,
            'responseCode': result.responseCode,
            'message': result.detailedMessage,
            'pinBlock': result.pinBlock,
            'error': result.error,
          };

        case PinOperationType.authorization:
          if (currentPin == null) {
            throw CardReaderException('MISSING_PIN', 'PIN is required for authorization operation');
          }

          // Create PIN block for authorization
          final pinBlockResult = await createPinBlock(
            pin: currentPin,
            cardNumber: cardNumber,
            format: format,
            keyIndex: keyIndex,
            encryptionType: encryptionType,
          );

          if (!pinBlockResult.isSuccessful) {
            throw CardReaderException(
              pinBlockResult.responseCode ?? 'PIN_BLOCK_ERROR',
              'Failed to create PIN block: ${pinBlockResult.error}',
            );
          }

          final result = await authorizePin(
            pinBlock: pinBlockResult.pinBlock!,
            cardNumber: cardNumber,
            amount: amount,
            keyIndex: keyIndex,
            encryptionType: encryptionType,
          );

          return {
            'success': result.isAuthorized,
            'operationType': operationType.description,
            'processingCode': operationType.processingCode,
            'responseCode': result.responseCode,
            'message': result.detailedMessage,
            'pinBlock': result.pinBlock,
            'authorizationData': result.authorizationData,
            'remainingTries': result.remainingTries,
            'error': result.error,
          };
      }
    } catch (e) {
      if (e is CardReaderException) {
        rethrow;
      }
      throw CardReaderException('OPERATION_ERROR', 'Error performing PIN operation: $e');
    }
  }
}