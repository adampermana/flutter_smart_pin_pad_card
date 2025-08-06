part of 'network_bloc.dart';

@freezed
class NetworkEvent with _$NetworkEvent {
  const factory NetworkEvent.checkConnection() = NetworkCheckConnection;

  const factory NetworkEvent.checkCapabilities() = NetworkCheckCapabilities;

  const factory NetworkEvent.sendPinOperation({
    required PinOperationType operationType,
    required String systemsTraceNo,
    required String terminalId,
    required String merchantId,
    required String track2Data,
    String? currentPinBlock,
    String? newPinBlock,
    String? newPin,
    int? amount,
  }) = NetworkSendPinOperation;
}
