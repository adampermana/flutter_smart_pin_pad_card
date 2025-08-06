part of 'network_bloc.dart';

@freezed
class NetworkState with _$NetworkState {
  const factory NetworkState({
    required bool isConnected,
    required bool isCheckingConnection,
    required bool isCheckingCapabilities,
    required bool isProcessingOperation,
    required Set<String> supportedOperations,
    required String capabilityStatus,
    PinOperationResponse? lastOperationResponse,
    String? errorMessage,
  }) = _NetworkState;

  factory NetworkState.initial() => const NetworkState(
    isConnected: false,
    isCheckingConnection: false,
    isCheckingCapabilities: false,
    isProcessingOperation: false,
    supportedOperations: {},
    capabilityStatus: 'Not checked',
  );
}
