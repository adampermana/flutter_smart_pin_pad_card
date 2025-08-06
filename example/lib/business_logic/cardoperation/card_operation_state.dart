part of 'card_operation_bloc.dart';

@freezed
class CardOperationState with _$CardOperationState {
  const factory CardOperationState.initial() = _Initial;

  const factory CardOperationState.reading({
    required CardOperationType operationType,
    required int remainingTime,
  }) = _Reading;

  const factory CardOperationState.cardRead({
    required CardOperationType operationType,
    required CardData cardData,
  }) = _CardRead;

  const factory CardOperationState.pinEntry({
    required CardOperationType operationType,
    required CardData cardData,
    required PinEntryStep step,
    required int remainingTime,
    String? currentPin,
    String? newPin,
  }) = _PinEntry;

  const factory CardOperationState.processing({
    required CardOperationType operationType,
    required CardData cardData,
  }) = _Processing;

  const factory CardOperationState.success({
    required CardOperationType operationType,
    required PinOperationResponse response,
    required int remainingTime,
  }) = _Success;

  const factory CardOperationState.error({
    required String message,
  }) = _Error;

  const factory CardOperationState.cancelled() = _Cancelled;

  // Working Key States - NEW
  const factory CardOperationState.workingKeyProcessing({
    required String operation,
  }) = _WorkingKeyProcessing;

  const factory CardOperationState.workingKeySuccess({
    required String operation,
    required Map<String, dynamic> result,
  }) = _WorkingKeySuccess;

  const factory CardOperationState.workingKeyError({
    required String operation,
    required String message,
  }) = _WorkingKeyError;
}