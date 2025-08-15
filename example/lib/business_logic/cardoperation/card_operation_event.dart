part of 'card_operation_bloc.dart';

@freezed
class CardOperationEvent with _$CardOperationEvent {
  const factory CardOperationEvent.startReading({
    required CardOperationType operationType,
  }) = CardOperationStartReading;

  const factory CardOperationEvent.confirmCard() = CardOperationConfirmCard;

  const factory CardOperationEvent.enterPin({
    required String pin,
  }) = CardOperationEnterPin;

  const factory CardOperationEvent.confirmPin({
    String? currentPin,
    String? newPin,
  }) = CardOperationConfirmPin;

  const factory CardOperationEvent.processOperation({
    String? currentPin,
    String? newPin,
  }) = CardOperationProcessOperation;

  const factory CardOperationEvent.cancel() = CardOperationCancel;

  const factory CardOperationEvent.timeout() = CardOperationTimeout;

  const factory CardOperationEvent.reset() = CardOperationReset;

  const factory CardOperationEvent.decryptWorkingKey({
    required String encryptedWorkingKey,
  }) = CardOperationDecryptWorkingKey;

  const factory CardOperationEvent.processWorkingKeyFromLogon({
    required String workingKeyData,
  }) = CardOperationProcessWorkingKeyFromLogon;

  const factory CardOperationEvent.clearWorkingKey() = CardOperationClearWorkingKey;

  const factory CardOperationEvent.checkWorkingKeyStatus() = CardOperationCheckWorkingKeyStatus;
}