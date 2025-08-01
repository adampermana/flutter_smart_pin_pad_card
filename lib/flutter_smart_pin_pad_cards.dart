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
  static Future<Map<String, PinBlockResult>> testAllPinBlockFormats({
    required String pin,
    required String cardNumber,
    String? encryptionKey,
  }) async {
    try {
      final result = await _channel.invokeMethod('testAllPinBlockFormats', {
        'pin': pin,
        'cardNumber': cardNumber,
        'encryptionKey': encryptionKey ?? "404142434445464748494A4B4C4D4E4F",
      });

      final Map<String, dynamic> resultMap = Map<String, dynamic>.from(result);
      final Map<String, PinBlockResult> formattedResults = {};

      for (final entry in resultMap.entries) {
        formattedResults[entry.key] = PinBlockResult.fromMap(
            Map<String, dynamic>.from(entry.value)
        );
      }

      return formattedResults;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

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



  ///---------------------------------------
  /// Master Key Management Methods

  /// Set master key
  static Future<bool> setMasterKey(String masterKey) async {
    try {
      final result = await _channel.invokeMethod('setMasterKey', {
        'masterKey': masterKey,
      });
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Get master key (masked for security)
  static Future<String?> getMasterKey() async {
    try {
      final result = await _channel.invokeMethod('getMasterKey');
      return result as String?;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Check if master key is set
  static Future<bool> isMasterKeySet() async {
    try {
      final result = await _channel.invokeMethod('isMasterKeySet');
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Clear master key
  static Future<void> clearMasterKey() async {
    try {
      await _channel.invokeMethod('clearMasterKey');
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Set default master key (1234567890ABCDEF1234567890ABCDEF)
  static Future<bool> setDefaultMasterKey() async {
    try {
      final result = await _channel.invokeMethod('setDefaultMasterKey');
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Validate master key format
  static Future<Map<String, dynamic>> validateMasterKey(String masterKey) async {
    try {
      final result = await _channel.invokeMethod('validateMasterKey', {
        'masterKey': masterKey,
      });
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Get comprehensive master key information
  static Future<Map<String, dynamic>> getMasterKeyInfo() async {
    try {
      final result = await _channel.invokeMethod('getMasterKeyInfo');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Get detailed system diagnostics
  static Future<Map<String, dynamic>> getSystemDiagnostics() async {
    try {
      final result = await _channel.invokeMethod('getSystemDiagnostics');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Enhanced Master Key Management Methods

  /// Initialize pinpad with master key
  static Future<bool> initPinpadWithMasterKey(String masterKey) async {
    try {
      // First set the master key
      bool keySet = await setMasterKey(masterKey);
      if (!keySet) {
        throw CardReaderException('MASTER_KEY_ERROR', 'Failed to set master key');
      }

      // Then initialize pinpad
      bool initialized = await initPinpad();
      return initialized;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Load working key (encrypted with master key)
  static Future<bool> loadEncryptedWorkingKey({
    required String encryptedWorkingKey,
    int keyType = KEY_TYPE_PIK,
    int workKeyId = 0,
    String? checkValue,
  }) async {
    try {
      // Check if master key is set
      bool masterKeySet = await isMasterKeySet();
      if (!masterKeySet) {
        throw CardReaderException('MASTER_KEY_NOT_SET', 'Master key must be set before loading working key');
      }

      final result = await _channel.invokeMethod('loadWorkKey', {
        'keyType': keyType,
        'masterKeyId': 0, // Always use master key index 0
        'workKeyId': workKeyId,
        'keyData': encryptedWorkingKey,
        'checkValue': checkValue,
      });
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Enhanced validation with master key check
  static Future<Map<String, dynamic>> validateMasterKeySetup() async {
    try {
      // Check if master key is set
      bool masterKeySet = await isMasterKeySet();

      // Get pinpad status
      final status = await getPinpadStatus();

      // Get master key info
      final masterKeyInfo = await getMasterKeyInfo();

      return {
        'isValid': status['initialized'] == true && masterKeySet,
        'masterKeySet': masterKeySet,
        'pinpadStatus': status,
        'masterKeyInfo': masterKeyInfo,
        'timestamp': DateTime.now().millisecondsSinceEpoch,
      };
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Enhanced PIN block creation with master key validation
  static Future<PinBlockResult> createPinBlockWithMasterKey({
    required String pin,
    required String cardNumber,
    required String masterKey,
    required String encryptedWorkingKey,
    int format = PIN_BLOCK_FORMAT_0,
    int encryptionType = ENCRYPT_3DES,
    String fillerChar = "F",
    bool useHardwareEncryption = true,
  }) async {
    try {
      // Set master key if different from current
      String? currentMasterKey = await getMasterKey();
      if (currentMasterKey == null || !_isSameMasterKey(currentMasterKey, masterKey)) {
        bool keySet = await setMasterKey(masterKey);
        if (!keySet) {
          throw CardReaderException('MASTER_KEY_ERROR', 'Failed to set master key');
        }

        // Re-initialize pinpad with new master key
        bool reinitialized = await initPinpad();
        if (!reinitialized) {
          throw CardReaderException('PINPAD_ERROR', 'Failed to reinitialize pinpad with new master key');
        }
      }

      // Load the working key
      bool keyLoaded = await loadEncryptedWorkingKey(
        encryptedWorkingKey: encryptedWorkingKey,
      );

      if (!keyLoaded) {
        throw CardReaderException('KEY_LOAD_ERROR', 'Failed to load working key');
      }

      // Create PIN block using the loaded working key
      final result = await createDynamicPinBlock(
        pin: pin,
        cardNumber: cardNumber,
        format: format,
        encryptionKey: encryptedWorkingKey,
        encryptionType: encryptionType,
        fillerChar: fillerChar,
        useHardwareEncryption: useHardwareEncryption,
      );

      return result;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

  /// Helper method to compare master keys (comparing masked versions)
  static bool _isSameMasterKey(String maskedKey1, String plainKey2) {
    if (maskedKey1.length != plainKey2.length) return false;

    // Compare first 4 and last 4 characters (unmasked parts)
    if (maskedKey1.length > 8) {
      String prefix1 = maskedKey1.substring(0, 4);
      String suffix1 = maskedKey1.substring(maskedKey1.length - 4);
      String prefix2 = plainKey2.substring(0, 4);
      String suffix2 = plainKey2.substring(plainKey2.length - 4);

      return prefix1.toUpperCase() == prefix2.toUpperCase() &&
          suffix1.toUpperCase() == suffix2.toUpperCase();
    }

    return false; // Can't compare if too short
  }
  ///



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
  static Future<bool> getKeyState({
    required int keyType,
    required int keyIndex,
  }) async {
    try {
      final result = await _channel.invokeMethod('getKeyState', {
        'keyType': keyType,
        'keyIndex': keyIndex,
      });
      return result as bool;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

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
  static Future<String> getRandom() async {
    try {
      final result = await _channel.invokeMethod('getRandom');
      return result as String;
    } on PlatformException catch (e) {
      throw CardReaderException(e.code, e.message ?? 'Unknown error');
    }
  }

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