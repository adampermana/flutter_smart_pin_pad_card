import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';
import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';

/// Network service for handling ISO 8583 messages
class NetworkService {
  static const String serverHost = '192.168.88.68';
  static const int serverPort = 8082;
  static const int timeoutSeconds = 30;

  static bool _initialized = false;

  /// Initialize the network service
  static void initialize() {
    _initialized = true;
    print('NetworkService initialized with host: $serverHost:$serverPort');
  }

  /// Test connection to the server
  static Future<bool> testConnection() async {
    try {
      print('Testing connection to $serverHost:$serverPort...');

      final socket = await Socket.connect(
        serverHost,
        serverPort,
        timeout: const Duration(seconds: 5),
      );

      await socket.close();
      print('✅ Connection test successful');
      return true;
    } catch (e) {
      print('❌ Connection test failed: $e');
      return false;
    }
  }

  /// Send logon request to server
  static Future<LogonResponse> sendLogon({
    required String terminalId,
    required String password,
  }) async {
    try {
      print('Sending logon request...');

      final socket = await Socket.connect(
        serverHost,
        serverPort,
        timeout: const Duration(seconds: timeoutSeconds),
      );

      // Build logon message (simplified)
      final message = _buildLogonMessage(terminalId, password);
      final messageBytes = utf8.encode(message);

      // Send length header + message
      final lengthBytes = Uint8List(2);
      lengthBytes[0] = (messageBytes.length >> 8) & 0xFF;
      lengthBytes[1] = messageBytes.length & 0xFF;

      socket.add(lengthBytes);
      socket.add(messageBytes);

      print('Logon message sent: $message');

      // Read response
      final responseData = await socket.first.timeout(
        const Duration(seconds: timeoutSeconds),
      );

      await socket.close();

      // Parse response (skip length header)
      final responseMessage = utf8.decode(responseData.skip(2).toList());
      print('Logon response received: $responseMessage');

      return _parseLogonResponse(responseMessage);

    } catch (e) {
      print('Logon error: $e');
      return LogonResponse(
        success: false,
        responseCode: '91', // Host Down
        errorMessage: e.toString(),
      );
    }
  }

  /// Send PIN operation request to server
  static Future<PinOperationResponse> sendPinOperation({
    required PinOperationType operationType,
    required String systemsTraceNo,
    required String terminalId,
    required String merchantId,
    required String track2Data,
    String? currentPinBlock,
    String? newPinBlock,
    String? newPin,
    int? amount,
  }) async {
    try {
      print('Sending PIN operation: ${operationType.description}');

      final socket = await Socket.connect(
        serverHost,
        serverPort,
        timeout: const Duration(seconds: timeoutSeconds),
      );

      // Build ISO 8583 message for PIN operation
      final message = _buildPinOperationMessage(
        operationType: operationType,
        systemsTraceNo: systemsTraceNo,
        terminalId: terminalId,
        merchantId: merchantId,
        track2Data: track2Data,
        currentPinBlock: currentPinBlock,
        newPinBlock: newPinBlock,
        newPin: newPin,
        amount: amount,
      );

      final messageBytes = utf8.encode(message);

      // Send length header + message
      final lengthBytes = Uint8List(2);
      lengthBytes[0] = (messageBytes.length >> 8) & 0xFF;
      lengthBytes[1] = messageBytes.length & 0xFF;

      socket.add(lengthBytes);
      socket.add(messageBytes);

      print('PIN operation message sent: $message');

      // Read response
      final responseData = await socket.first.timeout(
        const Duration(seconds: timeoutSeconds),
      );

      await socket.close();

      // Parse response (skip length header)
      final responseMessage = utf8.decode(responseData.skip(2).toList());
      print('PIN operation response received: $responseMessage');

      return _parsePinOperationResponse(responseMessage, operationType);

    } catch (e) {
      print('PIN operation error: $e');
      return PinOperationResponse(
        success: false,
        operationType: operationType,
        responseCode: '91', // Host Down
        errorMessage: e.toString(),
      );
    }
  }

  /// Build logon ISO 8583 message
  static String _buildLogonMessage(String terminalId, String password) {
    final StringBuffer message = StringBuffer();

    // MTI for logon
    message.write('0800');

    // Build bitmap and fields for logon
    final fields = <int, String>{};
    fields[11] = DateTime.now().millisecondsSinceEpoch.toString().substring(7).padLeft(6, '0');
    fields[41] = terminalId.padRight(8, ' ');
    fields[70] = '001'; // Network management information code for logon

    // Add password in private use field if needed
    if (password.isNotEmpty) {
      fields[48] = 'PWD=$password';
    }

    // Build bitmap
    final bitmap = _buildBitmap(fields.keys.toList());
    message.write(bitmap);

    // Add fields in order
    for (int i = 1; i <= 64; i++) {
      if (fields.containsKey(i)) {
        final fieldData = _formatField(i, fields[i]!);
        message.write(fieldData);
      }
    }

    return message.toString();
  }

  /// Build PIN operation ISO 8583 message
  static String _buildPinOperationMessage({
    required PinOperationType operationType,
    required String systemsTraceNo,
    required String terminalId,
    required String merchantId,
    required String track2Data,
    String? currentPinBlock,
    String? newPinBlock,
    String? newPin,
    int? amount,
  }) {
    final StringBuffer message = StringBuffer();

    // MTI
    message.write('0100');

    // Build bitmap and fields
    final fields = <int, String>{};

    // Processing Code based on operation type
    fields[3] = operationType.processingCode;

    // Amount (if provided)
    if (amount != null) {
      fields[4] = amount.toString().padLeft(12, '0');
    }

    // Systems Trace Number
    fields[11] = systemsTraceNo.padLeft(6, '0');

    // Track 2 Data
    if (track2Data.isNotEmpty) {
      fields[35] = track2Data;
    }

    // Terminal ID
    fields[41] = terminalId.padRight(8, ' ');

    // Merchant ID
    fields[42] = merchantId.padRight(15, ' ');

    // Private Use Fields for PIN data
    switch (operationType) {
      case PinOperationType.createPin:
        if (newPinBlock != null) {
          fields[52] = newPinBlock; // PIN Block for new PIN
        } else if (newPin != null) {
          fields[48] = newPin; //NEWPIN
        }
        break;

      case PinOperationType.changePin:
        if (currentPinBlock != null && newPinBlock != null) {
          fields[48] = '$currentPinBlock$newPinBlock'; //OLDPIN
          fields[52] = newPinBlock; // Primary PIN block
        } else {
          fields[48] = '1';
        }
        break;

      case PinOperationType.authorization:
        if (currentPinBlock != null) {
          fields[52] = currentPinBlock; // PIN Block for authorization
        }
        break;
    }

    // Build bitmap
    final bitmap = _buildBitmap(fields.keys.toList());
    message.write(bitmap);

    // Add fields in order
    for (int i = 1; i <= 64; i++) {
      if (fields.containsKey(i)) {
        final fieldData = _formatField(i, fields[i]!);
        message.write(fieldData);
      }
    }

    return message.toString();
  }

  /// Build bitmap in hex format
  static String _buildBitmap(List<int> fieldNumbers) {
    final bitmap = List<int>.filled(8, 0);

    for (final fieldNum in fieldNumbers) {
      if (fieldNum >= 1 && fieldNum <= 64) {
        final byteIndex = (fieldNum - 1) ~/ 8;
        final bitIndex = 7 - ((fieldNum - 1) % 8);
        bitmap[byteIndex] |= (1 << bitIndex);
      }
    }

    return bitmap.map((b) => b.toRadixString(16).padLeft(2, '0')).join().toUpperCase();
  }

  /// Format field based on field number
  static String _formatField(int fieldNum, String value) {
    switch (fieldNum) {
      case 3:  // Processing Code
      case 11: // Systems Trace Number
      case 41: // Terminal ID
      case 42: // Merchant ID
      case 70: // Network Management Information Code
        return value;

      case 4:  // Amount with length prefix
        return value;

      case 35: // Track 2 with length prefix
        return '${value.length.toString().padLeft(2, '0')}$value';

      case 48: // Private use with length prefix
        return '${value.length.toString().padLeft(4, '0')}$value';

      case 52: // PIN block (8 bytes = 16 hex characters)
        return value.length == 16 ? value : value.padRight(16, '0');

      default:
        return value;
    }
  }

  /// Parse logon response - FIXED VERSION
  static LogonResponse _parseLogonResponse(String response) {
    try {
      if (response.length < 20) {
        return LogonResponse(
          success: false,
          responseCode: '30',
          errorMessage: 'Invalid response format',
        );
      }

      // Extract MTI
      final mti = response.substring(0, 4);
      if (mti != '0810') {
        return LogonResponse(
          success: false,
          responseCode: '30',
          errorMessage: 'Invalid response MTI: $mti',
        );
      }

      // Extract bitmap
      final bitmapHex = response.substring(4, 20);
      final bitmap = _hexToBytes(bitmapHex);

      // Parse fields
      int offset = 20;
      String? responseCode;
      String? workingKey;
      Map<String, dynamic> additionalData = {};

      for (int i = 1; i <= 64; i++) {
        if (_isBitSet(bitmap, i - 1)) {
          switch (i) {
            case 3: // Processing Code
              final processingCode = response.substring(offset, offset + 6);
              additionalData['processingCode'] = processingCode;
              offset += 6;
              break;

            case 11: // Systems Trace Number
              final traceNo = response.substring(offset, offset + 6);
              additionalData['systemsTraceNo'] = traceNo;
              offset += 6;
              break;

            case 39: // Response Code
              responseCode = response.substring(offset, offset + 2);
              offset += 2;
              break;

            case 41: // Terminal ID
              final terminalId = response.substring(offset, offset + 8);
              additionalData['terminalId'] = terminalId.trim();
              offset += 8;
              break;

            case 62: // Working Key - FIXED PARSING
            // Server sends format: "032" + 32-character hex key = 35 total chars
              if (offset + 35 <= response.length) {
                final workingKeyData = response.substring(offset, offset + 35);
                print('Raw working key data from server: $workingKeyData');

                // Extract working key (skip "032" prefix)
                if (workingKeyData.length == 35 && workingKeyData.startsWith('032')) {
                  workingKey = workingKeyData.substring(3); // Remove "032" prefix
                  additionalData['workingKey'] = workingKey;
                  print('Extracted working key: $workingKey');
                  print('Extracted working key length: ${workingKey.length}');

                  // Validate extracted key
                  if (workingKey.length != 32) {
                    print('⚠️ Working key length issue: ${workingKey.length} (expected 32)');
                    // Try to fix the length
                    if (workingKey.length > 32) {
                      workingKey = workingKey.substring(0, 32);
                      print('🔧 Truncated working key to 32 chars: $workingKey');
                    } else if (workingKey.length < 32) {
                      workingKey = workingKey.padRight(32, '0');
                      print('🔧 Padded working key to 32 chars: $workingKey');
                    }
                    additionalData['workingKey'] = workingKey;
                  }
                } else {
                  print('❌ Invalid working key format: $workingKeyData');
                }
                offset += 35;
              } else {
                print('❌ Not enough data for field 62');
              }
              break;

            // case 62: // Working Key - FIXED PARSING
            // // Format: "016" + 32-character hex key
            //   final workingKeyData = response.substring(offset, offset + 35);
            //   print('Raw working key data from server: $workingKeyData');
            //
            //   if (workingKeyData.startsWith('016') && workingKeyData.length == 35) {
            //     workingKey = workingKeyData.substring(3); // Remove "016" prefix
            //     additionalData['workingKey'] = workingKey;
            //     print('Extracted working key: $workingKey');
            //   } else {
            //     print('Invalid working key format: $workingKeyData');
            //   }
            //   offset += 35;
            //   break;

            default:
            // Skip other fields by getting their length
              final fieldLength = _getFieldLength(i, response, offset);
              if (fieldLength > 0) {
                offset += fieldLength;
              }
              break;
          }
        }
      }

      final success = responseCode == '00';

      return LogonResponse(
        success: success,
        responseCode: responseCode ?? '30',
        errorMessage: success ? null : 'Logon failed with code: $responseCode',
        additionalData: additionalData,
      );
    } catch (e) {
      print('Error parsing logon response: $e');
      return LogonResponse(
        success: false,
        responseCode: '91',
        errorMessage: 'Error parsing response: $e',
      );
    }
  }

  /// Parse PIN operation response
  static PinOperationResponse _parsePinOperationResponse(String response, PinOperationType operationType) {
    try {
      if (response.length < 20) {
        return PinOperationResponse(
          success: false,
          operationType: operationType,
          responseCode: '30',
          errorMessage: 'Invalid response format',
        );
      }

      // Extract MTI
      final mti = response.substring(0, 4);
      if (mti != '0110') {
        return PinOperationResponse(
          success: false,
          operationType: operationType,
          responseCode: '30',
          errorMessage: 'Invalid response MTI: $mti',
        );
      }

      // Extract bitmap
      final bitmapHex = response.substring(4, 20);
      final bitmap = _hexToBytes(bitmapHex);

      // Parse response fields
      int offset = 20;
      String? responseCode;
      String? pinBlock;
      Map<String, dynamic> additionalData = {};

      for (int i = 1; i <= 64; i++) {
        if (_isBitSet(bitmap, i - 1)) {
          switch (i) {
            case 3: // Processing Code
              final processingCode = response.substring(offset, offset + 6);
              additionalData['processingCode'] = processingCode;
              offset += 6;
              break;

            case 11: // Systems Trace Number
              final traceNo = response.substring(offset, offset + 6);
              additionalData['systemsTraceNo'] = traceNo;
              offset += 6;
              break;

            case 39: // Response Code
              responseCode = response.substring(offset, offset + 2);
              offset += 2;
              break;

            case 48: // Private Use Data
              final lengthStr = response.substring(offset, offset + 4);
              final length = int.parse(lengthStr);
              offset += 4;
              final privateData = response.substring(offset, offset + length);
              additionalData['privateData'] = privateData;
              offset += length;
              break;

            case 52: // PIN Block
              pinBlock = response.substring(offset, offset + 16);
              offset += 16;
              break;

            default:
            // Skip other fields
              final fieldLength = _getFieldLength(i, response, offset);
              if (fieldLength > 0) {
                offset += fieldLength;
              }
              break;
          }
        }
      }

      final success = responseCode == '00';

      return PinOperationResponse(
        success: success,
        operationType: operationType,
        responseCode: responseCode ?? '30',
        pinBlock: pinBlock,
        additionalData: additionalData,
        errorMessage: success ? null : 'Operation failed with code: $responseCode',
      );

    } catch (e) {
      return PinOperationResponse(
        success: false,
        operationType: operationType,
        responseCode: '91',
        errorMessage: 'Error parsing response: $e',
      );
    }
  }

  /// Helper methods
  static List<int> _hexToBytes(String hex) {
    final bytes = <int>[];
    for (int i = 0; i < hex.length; i += 2) {
      bytes.add(int.parse(hex.substring(i, i + 2), radix: 16));
    }
    return bytes;
  }

  static bool _isBitSet(List<int> bitmap, int position) {
    final byteIndex = position ~/ 8;
    final bitIndex = 7 - (position % 8);
    return (bitmap[byteIndex] & (1 << bitIndex)) != 0;
  }

  static int _getFieldLength(int fieldNum, String response, int offset) {
    switch (fieldNum) {
      case 3: return 6;   // Processing Code
      case 4: return 12;  // Amount
      case 11: return 6;  // Systems Trace Number
      case 37: return 12; // Retrieval Reference Number
      case 38: return 6;  // Authorization Code
      case 39: return 2;  // Response Code
      case 41: return 8;  // Terminal ID
      case 42: return 15; // Merchant ID
      case 52: return 16; // PIN Block (8 bytes = 16 hex chars)
      case 62: return 35; // Working Key ("016" + 32 hex chars)
      case 70: return 3;  // Network Management Information Code

    // Variable length fields
      case 35: // Track 2 Data
        if (offset + 2 <= response.length) {
          final lengthStr = response.substring(offset, offset + 2);
          return 2 + int.parse(lengthStr);
        }
        return 0;

      case 48: // Private Use
        if (offset + 4 <= response.length) {
          final lengthStr = response.substring(offset, offset + 4);
          return 4 + int.parse(lengthStr);
        }
        return 0;

      default: return 0;
    }
  }
}

/// Response class for logon operations
class LogonResponse {
  final bool success;
  final String responseCode;
  final String? errorMessage;
  final Map<String, dynamic>? additionalData;

  LogonResponse({
    required this.success,
    required this.responseCode,
    this.errorMessage,
    this.additionalData,
  });

  @override
  String toString() {
    return 'LogonResponse{success: $success, responseCode: $responseCode, errorMessage: $errorMessage}';
  }
}

/// Response class for PIN operations
class PinOperationResponse {
  final bool success;
  final PinOperationType operationType;
  final String responseCode;
  final String? pinBlock;
  final String? errorMessage;
  final Map<String, dynamic>? additionalData;

  PinOperationResponse({
    required this.success,
    required this.operationType,
    required this.responseCode,
    this.pinBlock,
    this.errorMessage,
    this.additionalData,
  });

  /// Get response message based on response code
  String get responseMessage {
    return ResponseCodes.getDescription(responseCode);
  }

  /// Check if operation was successful
  bool get isSuccessful => success && ResponseCodes.isSuccess(responseCode);

  @override
  String toString() {
    return 'PinOperationResponse{success: $success, operationType: ${operationType.description}, responseCode: $responseCode, errorMessage: $errorMessage}';
  }
}