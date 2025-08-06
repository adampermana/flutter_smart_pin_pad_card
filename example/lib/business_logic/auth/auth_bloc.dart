import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../network_service.dart';


part 'auth_event.dart';
part 'auth_state.dart';
part 'auth_bloc.freezed.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  static const String _workingKeyKey = 'working_key';
  static const String _terminalIdKey = 'terminal_id';
  static const String _isLoggedInKey = 'is_logged_in';

  AuthBloc() : super(const AuthState.initial()) {
    on<AuthLoginRequested>(_onLoginRequested);
    on<AuthLogoutRequested>(_onLogoutRequested);
    on<AuthCheckSession>(_onCheckSession);
    on<AuthWorkingKeyValidation>(_onWorkingKeyValidation);
  }

  Future<void> _onLoginRequested(
      AuthLoginRequested event,
      Emitter<AuthState> emit,
      ) async {
    emit(const AuthState.loading());

    try {
      final response = await NetworkService.sendLogon(
        terminalId: event.terminalId,
        password: event.password,
      );

      if (response.success) {
        // Extract working key from response if available
        final workingKey = response.additionalData?['workingKey'] ??
            DateTime.now().millisecondsSinceEpoch.toString();

        print('Working key received from logon: $workingKey');

        // CRITICAL FIX: Set working key to hardware plugin
        try {
          final setResult = await FlutterSmartPinPadCards.setWorkingKey(
            workingKey: workingKey,
          );

          if (setResult.success) {
            print('✅ Working key successfully set to hardware plugin');
          } else {
            print('❌ Failed to set working key to hardware: ${setResult.error}');
            // Continue anyway, PIN operations can fallback to default key
          }
        } catch (e) {
          print('❌ Exception setting working key to hardware: $e');
          // Continue anyway, PIN operations can fallback to default key
        }

        // Save session data
        await _saveSessionData(event.terminalId, workingKey);

        emit(AuthState.authenticated(
          terminalId: event.terminalId,
          workingKey: workingKey,
        ));
      } else {
        emit(AuthState.error(
          message: response.errorMessage ?? 'Login failed',
        ));
      }
    } catch (e) {
      emit(AuthState.error(message: 'Network error: $e'));
    }
  }

// Also update the _onCheckSession method to restore working key to hardware

  Future<void> _onCheckSession(
      AuthCheckSession event,
      Emitter<AuthState> emit,
      ) async {
    emit(const AuthState.loading());

    try {
      final prefs = await SharedPreferences.getInstance();
      final isLoggedIn = prefs.getBool(_isLoggedInKey) ?? false;
      final terminalId = prefs.getString(_terminalIdKey);
      final workingKey = prefs.getString(_workingKeyKey);

      if (isLoggedIn && terminalId != null && workingKey != null) {
        // Validate session with server
        final isValid = await _validateWorkingKey(workingKey);

        if (isValid) {
          // CRITICAL FIX: Restore working key to hardware plugin
          try {
            final setResult = await FlutterSmartPinPadCards.setWorkingKey(
              workingKey: workingKey,
            );

            if (setResult.success) {
              print('✅ Working key restored to hardware plugin');
            } else {
              print('❌ Failed to restore working key to hardware: ${setResult.error}');
            }
          } catch (e) {
            print('❌ Exception restoring working key to hardware: $e');
          }

          emit(AuthState.authenticated(
            terminalId: terminalId,
            workingKey: workingKey,
          ));
        } else {
          await _clearSessionData();
          emit(const AuthState.sessionExpired());
        }
      } else {
        emit(const AuthState.unauthenticated());
      }
    } catch (e) {
      emit(AuthState.error(message: 'Session check error: $e'));
    }
  }

  Future<void> _onLogoutRequested(
      AuthLogoutRequested event,
      Emitter<AuthState> emit,
      ) async {
    emit(const AuthState.loading());

    try {
      // Clear session data
      await _clearSessionData();
      emit(const AuthState.unauthenticated());
    } catch (e) {
      emit(AuthState.error(message: 'Logout error: $e'));
    }
  }


  Future<void> _onWorkingKeyValidation(
      AuthWorkingKeyValidation event,
      Emitter<AuthState> emit,
      ) async {
    try {
      final isValid = await _validateWorkingKey(event.workingKey);

      if (!isValid) {
        await _clearSessionData();
        emit(const AuthState.sessionExpired());
      }
    } catch (e) {
      emit(AuthState.error(message: 'Working key validation error: $e'));
    }
  }

  Future<void> _saveSessionData(String terminalId, String workingKey) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_terminalIdKey, terminalId);
    await prefs.setString(_workingKeyKey, workingKey);
    await prefs.setBool(_isLoggedInKey, true);
  }

  Future<void> _clearSessionData() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_terminalIdKey);
    await prefs.remove(_workingKeyKey);
    await prefs.setBool(_isLoggedInKey, false);
  }

  Future<bool> _validateWorkingKey(String workingKey) async {
    try {
      // Send a test request to validate working key
      final response = await NetworkService.testConnection();
      return response;
    } catch (e) {
      return false;
    }
  }
}