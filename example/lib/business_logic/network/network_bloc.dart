import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

import '../../network_service.dart';

part 'network_event.dart';
part 'network_state.dart';
part 'network_bloc.freezed.dart';

// Replace the NetworkBloc class

class NetworkBloc extends Bloc<NetworkEvent, NetworkState> {
  NetworkBloc() : super(NetworkState.initial()) {
    on<NetworkCheckConnection>(_onCheckConnection);
    on<NetworkCheckCapabilities>(_onCheckCapabilities);
    on<NetworkSendPinOperation>(_onSendPinOperation);
  }

  /// Simple connection test - just check if server is reachable
  Future<void> _onCheckConnection(
      NetworkCheckConnection event,
      Emitter<NetworkState> emit,
      ) async {
    emit(state.copyWith(isCheckingConnection: true));

    try {
      // Simple connection test - no PIN operations
      final isConnected = await NetworkService.testConnection();

      emit(state.copyWith(
        isConnected: isConnected,
        isCheckingConnection: false,
        errorMessage: null, // Clear any previous errors
      ));

      print('Connection test result: ${isConnected ? "SUCCESS" : "FAILED"}');

    } catch (e) {
      emit(state.copyWith(
        isConnected: false,
        isCheckingConnection: false,
        errorMessage: 'Connection test failed: $e',
      ));

      print('Connection test exception: $e');
    }
  }

  /// Simplified capability testing - just check server availability
  /// No need to test PIN operations for basic capability check
  Future<void> _onCheckCapabilities(
      NetworkCheckCapabilities event,
      Emitter<NetworkState> emit,
      ) async {
    print('Starting capability testing...');

    emit(state.copyWith(
      isCheckingCapabilities: true,
      capabilityStatus: 'Checking server availability...',
      supportedOperations: {},
    ));

    try {
      // Simple connection test
      final isConnected = await NetworkService.testConnection();

      if (isConnected) {
        // If server is available, assume all standard PIN operations are supported
        // Real capability testing should be done through proper server API
        final Set<String> supportedOps = {
          '920000', // Create PIN
          '930000', // Change PIN
          '940000', // PIN Authorization
        };

        emit(state.copyWith(
          isConnected: true,
          isCheckingCapabilities: false,
          supportedOperations: supportedOps,
          capabilityStatus: '',
        ));

        print('✅ Server available - assuming all operations supported');

      } else {
        emit(state.copyWith(
          isConnected: false,
          isCheckingCapabilities: false,
          capabilityStatus: 'Server not available - all operations disabled',
          supportedOperations: {},
        ));

        print('❌ Server not available');
      }

    } catch (e) {
      emit(state.copyWith(
        isConnected: false,
        isCheckingCapabilities: false,
        capabilityStatus: 'Connection failed - capability test aborted',
        supportedOperations: {},
        errorMessage: 'Connection error: $e',
      ));

      print('Capability test failed: $e');
    }
  }

  /// Actual PIN operation - only used when really sending PIN data
  Future<void> _onSendPinOperation(
      NetworkSendPinOperation event,
      Emitter<NetworkState> emit,
      ) async {
    emit(state.copyWith(isProcessingOperation: true));

    try {
      print('Sending PIN operation: ${event.operationType}');

      final response = await NetworkService.sendPinOperation(
        operationType: event.operationType,
        systemsTraceNo: event.systemsTraceNo,
        terminalId: event.terminalId,
        merchantId: event.merchantId,
        track2Data: event.track2Data,
        currentPinBlock: event.currentPinBlock,
        newPinBlock: event.newPinBlock,
        newPin: event.newPin,
        amount: event.amount,
      );

      print('PIN operation response: ${response.responseCode}');

      emit(state.copyWith(
        isProcessingOperation: false,
        lastOperationResponse: response,
        errorMessage: null,
      ));

    } catch (e) {
      print('PIN operation failed: $e');

      emit(state.copyWith(
        isProcessingOperation: false,
        errorMessage: 'PIN operation failed: $e',
      ));
    }
  }
}