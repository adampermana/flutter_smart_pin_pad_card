part of 'auth_bloc.dart';

@freezed
class AuthEvent with _$AuthEvent {
  const factory AuthEvent.loginRequested({
    required String terminalId,
    required String password,
  }) = AuthLoginRequested;

  const factory AuthEvent.logoutRequested() = AuthLogoutRequested;

  const factory AuthEvent.checkSession() = AuthCheckSession;

  const factory AuthEvent.workingKeyValidation({
    required String workingKey,
  }) = AuthWorkingKeyValidation;
}