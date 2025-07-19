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


/// Result class for Change PIN operation
class ChangePinResult {
  final bool success;
  final String? message;
  final String? responseCode;
  final String? processingCode;
  final int? newPinLength;
  final String? error;

  ChangePinResult({
    required this.success,
    this.message,
    this.responseCode,
    this.processingCode,
    this.newPinLength,
    this.error,
  });

  factory ChangePinResult.fromMap(Map<String, dynamic> map) {
    return ChangePinResult(
      success: map['success'] ?? false,
      message: map['message'],
      responseCode: map['responseCode'],
      processingCode: map['processingCode'],
      newPinLength: map['newPinLength'],
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
    return 'ChangePinResult{success: $success, responseCode: $responseCode, message: $message, error: $error}';
  }
}

/// Result class for PIN Authorization operation
class AuthorizePinResult {
  final bool success;
  final String? message;
  final String? responseCode;
  final String? processingCode;
  final Map<String, dynamic>? authorizationData;
  final int? pinLength;
  final int? remainingTries;
  final String? error;

  AuthorizePinResult({
    required this.success,
    this.message,
    this.responseCode,
    this.processingCode,
    this.authorizationData,
    this.pinLength,
    this.remainingTries,
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

  @override
  String toString() {
    return 'AuthorizePinResult{success: $success, responseCode: $responseCode, message: $message, error: $error}';
  }
}

/// Result class for Create PIN operation
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

  @override
  String toString() {
    return 'CreatePinResult{success: $success, responseCode: $responseCode, message: $message, error: $error}';
  }
}