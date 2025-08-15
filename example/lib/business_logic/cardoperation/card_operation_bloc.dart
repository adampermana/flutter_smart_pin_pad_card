import 'package:flutter/cupertino.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';
import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../enum/pinpad.dart';
import '../../network_service.dart';

part 'card_operation_event.dart';

part 'card_operation_state.dart';

part 'card_operation_bloc.freezed.dart';

class CardOperationBloc extends Bloc<CardOperationEvent, CardOperationState> {
  CardOperationBloc() : super(const CardOperationState.initial()) {
    on<CardOperationStartReading>(_onStartReading);
    on<CardOperationConfirmCard>(_onConfirmCard);
    on<CardOperationEnterPin>(_onEnterPin);
    on<CardOperationConfirmPin>(_onConfirmPin);
    on<CardOperationProcessOperation>(_onProcessOperation);
    on<CardOperationCancel>(_onCancel);
    on<CardOperationTimeout>(_onTimeout);
    on<CardOperationReset>(_onReset);
  }

  Future<void> _onStartReading(
    CardOperationStartReading event,
    Emitter<CardOperationState> emit,
  ) async {
    debugPrint('🔍 [CardOperationBloc] Starting card reading...');
    debugPrint('   └── Operation Type: ${event.operationType}');
    emit(CardOperationState.reading(
      operationType: event.operationType,
      remainingTime: 60,
    ));

    try {
      debugPrint(
          '🔍 [CardOperationBloc] Calling FlutterSmartPinPadCards.startInsertCardReading...');
      final cardData = await FlutterSmartPinPadCards.startInsertCardReading(
        enableMag: true,
        enableIcc: true,
        enableRf: false,
        timeout: 60000,
      );

      if (cardData != null) {
        debugPrint('✅ [CardOperationBloc] Card data received successfully');
        debugPrint('   └── Card Number: ${cardData.cardNumber}');
        debugPrint('   └── Track2 Available: ${cardData.track2 != null}');
        debugPrint('   └── Card Type: ${cardData.cardType}');
        emit(CardOperationState.cardRead(
          operationType: event.operationType,
          cardData: cardData,
        ));
      } else {
        emit(const CardOperationState.error(
          message: 'No card data received',
        ));
      }
    } catch (e) {
      debugPrint('[CardOperation] Card reading failed: $e');
      await FlutterSmartPinPadCards.stopInsertCardReading();
      emit(CardOperationState.error(
        message: 'Card reading failed: $e',
      ));
    }
  }

  Future<void> _onConfirmCard(
    CardOperationConfirmCard event,
    Emitter<CardOperationState> emit,
  ) async {
    state.maybeWhen(
      cardRead: (operationType, cardData) {
        debugPrint('   └── Operation Type: $operationType');
        debugPrint('   └── Transitioning to PIN entry...');
        switch (operationType) {
          case CardOperationType.createPin:
            debugPrint('   └── PIN Entry Step: Create New PIN');
            emit(CardOperationState.pinEntry(
              operationType: operationType,
              cardData: cardData,
              step: PinEntryStep.createPin,
              remainingTime: 30,
            ));
            break;
          case CardOperationType.changePin:
            debugPrint('   └── PIN Entry Step: Enter Current PIN');
            emit(CardOperationState.pinEntry(
              operationType: operationType,
              cardData: cardData,
              step: PinEntryStep.enterCurrentPin,
              remainingTime: 30,
            ));
            break;
          case CardOperationType.otorisation:
            debugPrint('   └── PIN Entry Step: Enter PIN for Authorization');
            emit(CardOperationState.pinEntry(
              operationType: operationType,
              cardData: cardData,
              step: PinEntryStep.enterCurrentPin,
              remainingTime: 30,
            ));
            break;
        }
      },
      orElse: () {},
    );
  }

  Future<void> _onEnterPin(
    CardOperationEnterPin event,
    Emitter<CardOperationState> emit,
  ) async {
    debugPrint('🔢 [CardOperationBloc] PIN entered');
    debugPrint('   └── PIN Length: ${event.pin.length} digits');

    state.maybeWhen(
      pinEntry:
          (operationType, cardData, step, remainingTime, currentPin, newPin) {
        debugPrint('   └── Current Step: $step');
        debugPrint('   └── Operation Type: $operationType');
        switch (step) {
          case PinEntryStep.enterCurrentPin:
            if (operationType == CardOperationType.otorisation) {
              debugPrint(
                  '   └── Authorization detected, processing immediately...');
              // For authorization, process immediately
              add(CardOperationEvent.processOperation(
                currentPin: event.pin,
                newPin: null,
              ));
            } else {
              debugPrint(
                  '   └── Change PIN detected, moving to new PIN entry...');
              // For change PIN, move to new PIN entry
              emit(CardOperationState.pinEntry(
                operationType: operationType,
                cardData: cardData,
                step: PinEntryStep.createPin,
                remainingTime: 30,
                currentPin: event.pin,
              ));
            }
            break;
          case PinEntryStep.createPin:
            debugPrint('   └── New PIN entered, moving to confirmation...');
            emit(CardOperationState.pinEntry(
              operationType: operationType,
              cardData: cardData,
              step: PinEntryStep.confirmPin,
              remainingTime: 30,
              currentPin: currentPin,
              newPin: event.pin,
            ));
            break;
          case PinEntryStep.confirmPin:
            debugPrint('   └── Confirming PIN...');
            if (event.pin == newPin) {
              debugPrint('   └── ✅ PINs match! Processing operation...');
              add(CardOperationEvent.processOperation(
                currentPin: currentPin,
                newPin: newPin,
              ));
            } else {
              debugPrint('   └── ❌ PINs do not match!');
              emit(const CardOperationState.error(
                message: 'PINs do not match. Please try again.',
              ));
            }
            break;
        }
      },
      orElse: () {},
    );
  }

  Future<void> _onConfirmPin(
    CardOperationConfirmPin event,
    Emitter<CardOperationState> emit,
  ) async {
    add(CardOperationEvent.processOperation(
      currentPin: event.currentPin,
      newPin: event.newPin,
    ));
  }

  Future<void> _onProcessOperation(
    CardOperationProcessOperation event,
    Emitter<CardOperationState> emit,
  ) async {
    await state.maybeWhen(
      pinEntry: (operationType, cardData, step, remainingTime, currentPin,
          newPin) async {
        debugPrint('🔄 [CardOperationBloc] Processing operation...');
        debugPrint('   └── Operation Type: $operationType');
        debugPrint('   └── Has Current PIN: ${event.currentPin != null}');
        debugPrint('   └── Has New PIN: ${event.newPin != null}');
        emit(CardOperationState.processing(
          operationType: operationType,
          cardData: cardData,
        ));

        try {
          // Check working key status first
          await _ensureWorkingKeyAvailable();

          // Generate PIN blocks using working key
          String? currentPinBlock;
          String? newPinBlock;

          debugPrint('[CardOperation] Processing operation: $operationType');

          switch (operationType) {
            case CardOperationType.createPin:
              debugPrint('   └── Generating PIN block for new PIN...');
              newPinBlock = await _generatePinBlockWithWorkingKey(
                  event.newPin!, cardData);
              debugPrint('   └── New PIN block generated: ${(newPinBlock)}');
              break;
            case CardOperationType.changePin:
              debugPrint('   └── Generating PIN blocks for PIN change...');
              currentPinBlock = await _generatePinBlockWithWorkingKey(
                  event.currentPin!, cardData);
              debugPrint(
                  '   └── Current PIN block generated: ${(currentPinBlock)}');
              newPinBlock = await _generatePinBlockWithWorkingKey(
                  event.newPin!, cardData);
              debugPrint('   └── New PIN block generated: ${(newPinBlock)}');
              break;
            case CardOperationType.otorisation:
              currentPinBlock = await _generatePinBlockWithWorkingKey(
                  event.currentPin!, cardData);
              break;
          }

          // Convert to PinOperationType
          PinOperationType pinOperationType;
          switch (operationType) {
            case CardOperationType.createPin:
              pinOperationType = PinOperationType.createPin;
              break;
            case CardOperationType.changePin:
              pinOperationType = PinOperationType.changePin;
              break;
            case CardOperationType.otorisation:
              pinOperationType = PinOperationType.authorization;
              break;
          }

          debugPrint('📡 [CardOperationBloc] Sending to server...');
          debugPrint('   └── Operation Type: $pinOperationType');

          // Send to server
          final response = await NetworkService.sendPinOperation(
            operationType: pinOperationType,
            systemsTraceNo:
                DateTime.now().millisecondsSinceEpoch.toString().substring(7),
            terminalId: 'T3000001',
            merchantId: 'BANKJATENG00001',
            track2Data: cardData.track2 ?? '',
            currentPinBlock: currentPinBlock,
            newPinBlock: newPinBlock,
            newPin: operationType == CardOperationType.createPin
                ? event.newPin
                : null,
          );

          if (response.success) {
            debugPrint('✅ [CardOperationBloc] Operation successful!');
            debugPrint('   └── Response Code: ${response.responseCode}');
            emit(CardOperationState.success(
              operationType: operationType,
              response: response,
              remainingTime: 10,
            ));
          } else {
            debugPrint('❌ [CardOperationBloc] Operation failed!');
            debugPrint('   └── Error: ${response.errorMessage}');
            debugPrint('   └── Response Code: ${response.responseCode}');
            emit(CardOperationState.error(
              message: response.errorMessage ?? 'Operation failed',
            ));
          }
        } catch (e) {
          emit(CardOperationState.error(
            message: 'Error processing PIN: $e',
          ));
        }
      },
      orElse: () async {},
    );
  }

  Future<String> _getEncryptedWorkingKeyFromSession() async {
    final prefs = await SharedPreferences.getInstance();
    final encryptedKey = prefs.getString('working_key');
    if (encryptedKey == null) {
      throw Exception('No working key found in session');
    }
    return encryptedKey;
  }


  Future<void> _onCancel(
    CardOperationCancel event,
    Emitter<CardOperationState> emit,
  ) async {
    await FlutterSmartPinPadCards.stopInsertCardReading();
    emit(const CardOperationState.cancelled());
  }

  Future<void> _onTimeout(
    CardOperationTimeout event,
    Emitter<CardOperationState> emit,
  ) async {
    await FlutterSmartPinPadCards.stopInsertCardReading();
    emit(const CardOperationState.error(
      message: 'Operation timed out',
    ));
  }

  Future<void> _onReset(
    CardOperationReset event,
    Emitter<CardOperationState> emit,
  ) async {
    emit(const CardOperationState.initial());
  }

  /// Ensure working key is available for PIN block generation
  Future<void> _ensureWorkingKeyAvailable() async {
    try {
      final status = await FlutterSmartPinPadCards.getWorkingKeyStatus();

      final bool workingKeyCached = status['workingKeyCached'] ?? false;
      final bool workingKeyCacheValid = status['workingKeyCacheValid'] ?? false;

      debugPrint('   └── Working Key Cached: $workingKeyCached');
      debugPrint('   └── Working Key Valid: $workingKeyCacheValid');

      if (!workingKeyCached || !workingKeyCacheValid) {
        print(
            'Working key not available or expired, operations will use default key');
        // In a real implementation, you might want to trigger working key retrieval here
        // For now, the PIN block generation will fallback to the provided encryption key
      } else {
        print('Working key is available and valid');
      }
    } catch (e) {
      print('Error checking working key status: $e');
      // Continue with operation, will fallback to default key
    }
  }

  /// Generate PIN block with working key support
  Future<String?> _generatePinBlockWithWorkingKey(
      String pin, CardData cardData) async {
    try {
      debugPrint('🔐 [CardOperationBloc] Generating PIN block...');
      debugPrint('   └── PIN Length: ${pin.length}');
      debugPrint('   └── Card Number: ${(cardData.cardNumber)}');

      final encryptedKey = await _getEncryptedWorkingKeyFromSession();
      final decryptResult = await FlutterSmartPinPadCards.decryptWorkingKey(
        encryptedWorkingKey: encryptedKey,
      );

      if (!decryptResult.success) {
        throw Exception('Failed to decrypt working key: ${decryptResult.error}');
      }
      // Try dynamic PIN block generation with working key
      // The createDynamicPinBlock method will automatically use working key if available
      final result = await FlutterSmartPinPadCards.createDynamicPinBlock(
        pin: pin,
        cardNumber: cardData.cardNumber,
        format: FlutterSmartPinPadCards.PIN_BLOCK_FORMAT_0,
        encryptionType: FlutterSmartPinPadCards.ENCRYPT_3DES,
        useHardwareEncryption: true,
        encryptionKey: decryptResult.decryptedKey!,
      );

      if (result.success && result.pinBlock != null) {
        // Log if working key was used
        final bool usedWorkingKey =
            result.additionalData?['usedWorkingKey'] ?? false;
        final String keySource =
            result.additionalData?['keySource'] ?? 'Unknown';
        debugPrint('   └── ✅ Dynamic PIN block generated successfully');
        return result.pinBlock;
      }

      // Last resort fallback
      return _generateFallbackPinBlock(pin, cardData.cardNumber);
    } catch (e) {
      print('Error generating PIN block: $e');
      return _generateFallbackPinBlock(pin, cardData.cardNumber);
    }
  }


  /// Fallback PIN block generation (software-based)
  String _generateFallbackPinBlock(String pin, String cardNumber) {
    print('Using fallback PIN block generation');

    String pinPart = '0${pin.length}$pin';
    while (pinPart.length < 16) {
      pinPart += 'F';
    }
    debugPrint('   └── PIN Part: $pinPart');

    String panDigits = cardNumber.replaceAll(RegExp(r'[^0-9]'), '');
    String panPart = '0000${panDigits.substring(panDigits.length - 13, panDigits.length - 1)}';
    while (panPart.length < 16) {
      panPart = '0$panPart';
    }
    debugPrint('   └── PAN Part: ${(panPart)}');

    List<int> pinBytes = [];
    List<int> panBytes = [];

    for (int i = 0; i < 16; i += 2) {
      pinBytes.add(int.parse(pinPart.substring(i, i + 2), radix: 16));
      panBytes.add(int.parse(panPart.substring(i, i + 2), radix: 16));
    }

    String result = '';
    for (int i = 0; i < 8; i++) {
      int xor = pinBytes[i] ^ panBytes[i];
      result += xor.toRadixString(16).padLeft(2, '0').toUpperCase();
    }
    debugPrint('   └── Generated PIN Block: ${(result)}');
    return result;
  }

  /// Clear working key cache
  Future<void> clearWorkingKey() async {
    try {
      await FlutterSmartPinPadCards.clearWorkingKeyCache();
      print('Working key cache cleared');
    } catch (e) {
      print('Error clearing working key cache: $e');
    }
  }
}
