class CardData {
  final String cardNumber;
  final String expiryDate;
  final String track1;
  final String track2;
  final String track3;
  final String cardType; // IC, RF, MAG
  final Map<String, dynamic>? emvData; // Untuk data tambahan EMV jika ada

  CardData({
    required this.cardNumber,
    required this.expiryDate,
    this.track1 = '',
    this.track2 = '',
    this.track3 = '',
    this.cardType = '',
    this.emvData,
  });

  factory CardData.fromMap(Map<String, dynamic> map) {
    return CardData(
      cardNumber: map['pan'] ?? map['cardNumber'] ?? '',
      expiryDate: map['expiryDate'] ?? '',
      track1: map['track1'] ?? '',
      track2: map['track2'] ?? '',
      track3: map['track3'] ?? '',
      cardType: map['cardType'] ?? '',
      emvData: map['emvData'] != null
          ? Map<String, dynamic>.from(map['emvData'])
          : null,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'cardNumber': cardNumber,
      'pan': cardNumber, // Untuk kompatibilitas dengan kedua format
      'expiryDate': expiryDate,
      'track1': track1,
      'track2': track2,
      'track3': track3,
      'cardType': cardType,
      if (emvData != null) 'emvData': emvData,
    };
  }

  /// Format nomor kartu dengan memaskir semua kecuali 4 digit terakhir
  String getFormattedCardNumber() {
    if (cardNumber.isEmpty) return '';
    if (cardNumber.length < 4) return cardNumber;

    final lastFour = cardNumber.substring(cardNumber.length - 4);
    return '•••• •••• •••• $lastFour';
  }

  /// Format tanggal kedaluwarsa dari format YYMMDD menjadi MM/YY
  String getFormattedExpiryDate() {
    if (expiryDate.isEmpty) return '';

    if (expiryDate.length >= 4) {
      // Ambil bulan (posisi 2-3)
      final month = expiryDate.substring(2, 4);
      // Ambil tahun (posisi 0-1)
      final year = expiryDate.substring(0, 2);
      return '$month/$year';
    }
    return expiryDate;
  }
}

/// Result class for PIN Block creation
class PinBlockResult {
  final bool success;
  final String? pinBlock;
  final int? format;
  final int? keyIndex;
  final int? encryptionType;
  final String? responseCode;
  final String? processingCode;
  final String? error;

  PinBlockResult({
    required this.success,
    this.pinBlock,
    this.format,
    this.keyIndex,
    this.encryptionType,
    this.responseCode,
    this.processingCode,
    this.error,
  });

  factory PinBlockResult.fromMap(Map<String, dynamic> map) {
    return PinBlockResult(
      success: map['success'] ?? false,
      pinBlock: map['pinBlock'],
      format: map['format'],
      keyIndex: map['keyIndex'],
      encryptionType: map['encryptionType'],
      responseCode: map['responseCode'],
      processingCode: map['processingCode'],
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
      'responseCode': responseCode,
      'processingCode': processingCode,
      'error': error,
    };
  }

  /// Check if the operation was successful based on response code
  bool get isSuccessful => success && responseCode == '00';

  /// Get response message based on response code
  String get responseMessage {
    switch (responseCode) {
      case '00':
        return 'Success';
      case '21':
        return 'No Action';
      case '55':
        return 'Incorrect PIN';
      case '91':
        return 'Host Down';
      default:
        return error ?? 'Unknown error';
    }
  }

  @override
  String toString() {
    return 'PinBlockResult{success: $success, pinBlock: $pinBlock, format: $format, keyIndex: $keyIndex, encryptionType: $encryptionType, responseCode: $responseCode, processingCode: $processingCode, error: $error}';
  }
}

/// Result class for PIN verification
class PinVerifyResult {
  final bool success;
  final String? pin;
  final int? pinLength;
  final String? responseCode;
  final String? error;

  PinVerifyResult({
    required this.success,
    this.pin,
    this.pinLength,
    this.responseCode,
    this.error,
  });

  factory PinVerifyResult.fromMap(Map<String, dynamic> map) {
    return PinVerifyResult(
      success: map['success'] ?? false,
      pin: map['pin'],
      pinLength: map['pinLength'],
      responseCode: map['responseCode'],
      error: map['error'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'success': success,
      'pin': pin,
      'pinLength': pinLength,
      'responseCode': responseCode,
      'error': error,
    };
  }

  /// Check if the verification was successful based on response code
  bool get isVerified => success && responseCode == '00';

  /// Get response message based on response code
  String get responseMessage {
    switch (responseCode) {
      case '00':
        return 'Success';
      case '21':
        return 'No Action';
      case '55':
        return 'Incorrect PIN';
      case '91':
        return 'Host Down';
      default:
        return error ?? 'Unknown error';
    }
  }

  @override
  String toString() {
    return 'PinVerifyResult{success: $success, pin: $pin, pinLength: $pinLength, responseCode: $responseCode, error: $error}';
  }
}

/// Result class for Change PIN operation (Processing Code: 930000)
class ChangePinResult {
  final bool success;
  final String? message;
  final String? responseCode;
  final String? processingCode;
  final int? newPinLength;
  final String? pinBlock;
  final String? error;

  ChangePinResult({
    required this.success,
    this.message,
    this.responseCode,
    this.processingCode,
    this.newPinLength,
    this.pinBlock,
    this.error,
  });

  factory ChangePinResult.fromMap(Map<String, dynamic> map) {
    return ChangePinResult(
      success: map['success'] ?? false,
      message: map['message'],
      responseCode: map['responseCode'],
      processingCode: map['processingCode'],
      newPinLength: map['newPinLength'],
      pinBlock: map['pinBlock'],
      error: map['error'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'success': success,
      'message': message,
      'responseCode': responseCode,
      'processingCode': processingCode,
      'newPinLength': newPinLength,
      'pinBlock': pinBlock,
      'error': error,
    };
  }

  /// Check if the operation was successful based on response code
  bool get isSuccessful => success && responseCode == '00';

  /// Get response message based on response code
  String get responseMessage {
    switch (responseCode) {
      case '00':
        return 'Success';
      case '21':
        return 'No Action';
      case '55':
        return 'Incorrect PIN';
      case '91':
        return 'Host Down';
      default:
        return error ?? 'Unknown error';
    }
  }

  /// Get detailed status message
  String get detailedMessage {
    return message ?? responseMessage;
  }

  @override
  String toString() {
    return 'ChangePinResult{success: $success, responseCode: $responseCode, processingCode: $processingCode, message: $message, error: $error}';
  }
}

/// Result class for PIN Authorization operation (Processing Code: 940000)
class AuthorizePinResult {
  final bool success;
  final String? message;
  final String? responseCode;
  final String? processingCode;
  final Map<String, dynamic>? authorizationData;
  final int? pinLength;
  final int? remainingTries;
  final String? pinBlock;
  final String? error;

  AuthorizePinResult({
    required this.success,
    this.message,
    this.responseCode,
    this.processingCode,
    this.authorizationData,
    this.pinLength,
    this.remainingTries,
    this.pinBlock,
    this.error,
  });

  factory AuthorizePinResult.fromMap(Map<String, dynamic> map) {
    return AuthorizePinResult(
      success: map['success'] ?? false,
      message: map['message'],
      responseCode: map['responseCode'],
      processingCode: map['processingCode'],
      authorizationData: map['authorizationData'] != null
          ? Map<String, dynamic>.from(map['authorizationData'])
          : null,
      pinLength: map['pinLength'],
      remainingTries: map['remainingTries'],
      pinBlock: map['pinBlock'],
      error: map['error'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'success': success,
      'message': message,
      'responseCode': responseCode,
      'processingCode': processingCode,
      'authorizationData': authorizationData,
      'pinLength': pinLength,
      'remainingTries': remainingTries,
      'pinBlock': pinBlock,
      'error': error,
    };
  }

  /// Check if the authorization was successful based on response code
  bool get isAuthorized => success && responseCode == '00';

  /// Get response message based on response code
  String get responseMessage {
    switch (responseCode) {
      case '00':
        return 'Success';
      case '21':
        return 'No Action';
      case '55':
        return 'Incorrect PIN';
      case '91':
        return 'Host Down';
      default:
        return error ?? 'Unknown error';
    }
  }

  /// Get detailed status message
  String get detailedMessage {
    return message ?? responseMessage;
  }

  @override
  String toString() {
    return 'AuthorizePinResult{success: $success, responseCode: $responseCode, processingCode: $processingCode, message: $message, error: $error}';
  }
}

/// Result class for Create PIN operation (Processing Code: 920000)
class CreatePinResult {
  final bool success;
  final String? message;
  final String? responseCode;
  final String? processingCode;
  final String? pinBlock;
  final int? pinLength;
  final String? error;

  CreatePinResult({
    required this.success,
    this.message,
    this.responseCode,
    this.processingCode,
    this.pinBlock,
    this.pinLength,
    this.error,
  });

  factory CreatePinResult.fromMap(Map<String, dynamic> map) {
    return CreatePinResult(
      success: map['success'] ?? false,
      message: map['message'],
      responseCode: map['responseCode'],
      processingCode: map['processingCode'],
      pinBlock: map['pinBlock'],
      pinLength: map['pinLength'],
      error: map['error'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'success': success,
      'message': message,
      'responseCode': responseCode,
      'processingCode': processingCode,
      'pinBlock': pinBlock,
      'pinLength': pinLength,
      'error': error,
    };
  }

  /// Check if the PIN creation was successful based on response code
  bool get isSuccessful => success && responseCode == '00';

  /// Get response message based on response code
  String get responseMessage {
    switch (responseCode) {
      case '00':
        return 'Success';
      case '21':
        return 'No Action';
      case '55':
        return 'Incorrect PIN';
      case '91':
        return 'Host Down';
      default:
        return error ?? 'Unknown error';
    }
  }

  /// Get detailed status message
  String get detailedMessage {
    return message ?? responseMessage;
  }

  @override
  String toString() {
    return 'CreatePinResult{success: $success, responseCode: $responseCode, processingCode: $processingCode, message: $message, error: $error}';
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

/// Enum for PIN operation types
enum PinOperationType {
  createPin,    // Processing Code: 920000
  changePin,    // Processing Code: 930000
  authorization // Processing Code: 940000
}

/// Extension for PinOperationType to get processing codes
extension PinOperationTypeExtension on PinOperationType {
  String get processingCode {
    switch (this) {
      case PinOperationType.createPin:
        return '920000';
      case PinOperationType.changePin:
        return '930000';
      case PinOperationType.authorization:
        return '940000';
    }
  }

  String get description {
    switch (this) {
      case PinOperationType.createPin:
        return 'Create PIN';
      case PinOperationType.changePin:
        return 'Change PIN';
      case PinOperationType.authorization:
        return 'PIN Authorization';
    }
  }
}

/// Class for PIN block format constants
class PinBlockFormat {
  static const int format0 = 0; // ISO 9564-1 Format 0 (most common)
  static const int format1 = 1; // ISO 9564-1 Format 1
  static const int format2 = 2; // ISO 9564-1 Format 2
  static const int format3 = 3; // ISO 9564-1 Format 3
}

/// Class for encryption type constants
class EncryptionType {
  static const int threeDES = 0; // 3DES encryption
  static const int aes = 1;      // AES encryption
}

/// Class for key type constants
class KeyType {
  static const int pik = 0; // PIN encryption key
  static const int mak = 1; // MAC key
  static const int dek = 2; // Data encryption key
}

/// Response code meanings for PIN operations
class ResponseCodes {
  static const String success = '00';
  static const String noAction = '21';
  static const String incorrectPin = '55';
  static const String hostDown = '91';

  static String getDescription(String code) {
    switch (code) {
      case success:
        return 'Success';
      case noAction:
        return 'No Action';
      case incorrectPin:
        return 'Incorrect PIN';
      case hostDown:
        return 'Host Down';
      default:
        return 'Unknown Response Code: $code';
    }
  }

  static bool isSuccess(String? code) {
    return code == success;
  }
}

/// PIN Block utility class
class PinBlockUtil {
  /// Validate PIN format
  static bool isValidPin(String pin) {
    if (pin.isEmpty) return false;
    if (pin.length < 4 || pin.length > 12) return false;

    // Check if all characters are digits
    return RegExp(r'^\d+$').hasMatch(pin);
  }

  /// Format card number for PIN block processing
  static String formatCardNumber(String cardNumber) {
    // Remove any non-numeric characters
    String cleanCardNumber = cardNumber.replaceAll(RegExp(r'[^0-9]'), '');

    // Ensure we have at least 13 digits
    if (cleanCardNumber.length < 13) {
      // Pad with zeros on the left
      cleanCardNumber = cleanCardNumber.padLeft(13, '0');
    }

    return cleanCardNumber;
  }

  /// Mask card number for display
  static String maskCardNumber(String cardNumber) {
    if (cardNumber.isEmpty || cardNumber.length < 4) {
      return '****';
    }

    String cleanCardNumber = cardNumber.replaceAll(RegExp(r'[^0-9]'), '');
    if (cleanCardNumber.length < 4) {
      return '****';
    }

    String lastFour = cleanCardNumber.substring(cleanCardNumber.length - 4);
    String masked = '*' * (cleanCardNumber.length - 4) + lastFour;

    return masked;
  }

  /// Format PIN block hex string
  static String formatPinBlockHex(String pinBlockHex) {
    if (pinBlockHex.isEmpty) return '';

    // Remove any spaces or separators
    String clean = pinBlockHex.replaceAll(RegExp(r'[\s-]'), '');

    // Convert to uppercase
    return clean.toUpperCase();
  }

  /// Validate hex string
  static bool isValidHex(String hex) {
    if (hex.isEmpty) return false;

    // Remove any spaces or separators
    String clean = hex.replaceAll(RegExp(r'[\s-]'), '');

    // Check if all characters are valid hex digits
    return RegExp(r'^[0-9A-Fa-f]+$').hasMatch(clean) && clean.length % 2 == 0;
  }
}