// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'card_operation_bloc.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models');

/// @nodoc
mixin _$CardOperationEvent {
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $CardOperationEventCopyWith<$Res> {
  factory $CardOperationEventCopyWith(
          CardOperationEvent value, $Res Function(CardOperationEvent) then) =
      _$CardOperationEventCopyWithImpl<$Res, CardOperationEvent>;
}

/// @nodoc
class _$CardOperationEventCopyWithImpl<$Res, $Val extends CardOperationEvent>
    implements $CardOperationEventCopyWith<$Res> {
  _$CardOperationEventCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;
}

/// @nodoc
abstract class _$$CardOperationStartReadingImplCopyWith<$Res> {
  factory _$$CardOperationStartReadingImplCopyWith(
          _$CardOperationStartReadingImpl value,
          $Res Function(_$CardOperationStartReadingImpl) then) =
      __$$CardOperationStartReadingImplCopyWithImpl<$Res>;
  @useResult
  $Res call({CardOperationType operationType});
}

/// @nodoc
class __$$CardOperationStartReadingImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationStartReadingImpl>
    implements _$$CardOperationStartReadingImplCopyWith<$Res> {
  __$$CardOperationStartReadingImplCopyWithImpl(
      _$CardOperationStartReadingImpl _value,
      $Res Function(_$CardOperationStartReadingImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operationType = null,
  }) {
    return _then(_$CardOperationStartReadingImpl(
      operationType: null == operationType
          ? _value.operationType
          : operationType // ignore: cast_nullable_to_non_nullable
              as CardOperationType,
    ));
  }
}

/// @nodoc

class _$CardOperationStartReadingImpl implements CardOperationStartReading {
  const _$CardOperationStartReadingImpl({required this.operationType});

  @override
  final CardOperationType operationType;

  @override
  String toString() {
    return 'CardOperationEvent.startReading(operationType: $operationType)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationStartReadingImpl &&
            (identical(other.operationType, operationType) ||
                other.operationType == operationType));
  }

  @override
  int get hashCode => Object.hash(runtimeType, operationType);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardOperationStartReadingImplCopyWith<_$CardOperationStartReadingImpl>
      get copyWith => __$$CardOperationStartReadingImplCopyWithImpl<
          _$CardOperationStartReadingImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return startReading(operationType);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return startReading?.call(operationType);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (startReading != null) {
      return startReading(operationType);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return startReading(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return startReading?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (startReading != null) {
      return startReading(this);
    }
    return orElse();
  }
}

abstract class CardOperationStartReading implements CardOperationEvent {
  const factory CardOperationStartReading(
          {required final CardOperationType operationType}) =
      _$CardOperationStartReadingImpl;

  CardOperationType get operationType;
  @JsonKey(ignore: true)
  _$$CardOperationStartReadingImplCopyWith<_$CardOperationStartReadingImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardOperationConfirmCardImplCopyWith<$Res> {
  factory _$$CardOperationConfirmCardImplCopyWith(
          _$CardOperationConfirmCardImpl value,
          $Res Function(_$CardOperationConfirmCardImpl) then) =
      __$$CardOperationConfirmCardImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$CardOperationConfirmCardImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationConfirmCardImpl>
    implements _$$CardOperationConfirmCardImplCopyWith<$Res> {
  __$$CardOperationConfirmCardImplCopyWithImpl(
      _$CardOperationConfirmCardImpl _value,
      $Res Function(_$CardOperationConfirmCardImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$CardOperationConfirmCardImpl implements CardOperationConfirmCard {
  const _$CardOperationConfirmCardImpl();

  @override
  String toString() {
    return 'CardOperationEvent.confirmCard()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationConfirmCardImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return confirmCard();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return confirmCard?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (confirmCard != null) {
      return confirmCard();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return confirmCard(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return confirmCard?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (confirmCard != null) {
      return confirmCard(this);
    }
    return orElse();
  }
}

abstract class CardOperationConfirmCard implements CardOperationEvent {
  const factory CardOperationConfirmCard() = _$CardOperationConfirmCardImpl;
}

/// @nodoc
abstract class _$$CardOperationEnterPinImplCopyWith<$Res> {
  factory _$$CardOperationEnterPinImplCopyWith(
          _$CardOperationEnterPinImpl value,
          $Res Function(_$CardOperationEnterPinImpl) then) =
      __$$CardOperationEnterPinImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String pin});
}

/// @nodoc
class __$$CardOperationEnterPinImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res, _$CardOperationEnterPinImpl>
    implements _$$CardOperationEnterPinImplCopyWith<$Res> {
  __$$CardOperationEnterPinImplCopyWithImpl(_$CardOperationEnterPinImpl _value,
      $Res Function(_$CardOperationEnterPinImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? pin = null,
  }) {
    return _then(_$CardOperationEnterPinImpl(
      pin: null == pin
          ? _value.pin
          : pin // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$CardOperationEnterPinImpl implements CardOperationEnterPin {
  const _$CardOperationEnterPinImpl({required this.pin});

  @override
  final String pin;

  @override
  String toString() {
    return 'CardOperationEvent.enterPin(pin: $pin)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationEnterPinImpl &&
            (identical(other.pin, pin) || other.pin == pin));
  }

  @override
  int get hashCode => Object.hash(runtimeType, pin);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardOperationEnterPinImplCopyWith<_$CardOperationEnterPinImpl>
      get copyWith => __$$CardOperationEnterPinImplCopyWithImpl<
          _$CardOperationEnterPinImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return enterPin(pin);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return enterPin?.call(pin);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (enterPin != null) {
      return enterPin(pin);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return enterPin(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return enterPin?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (enterPin != null) {
      return enterPin(this);
    }
    return orElse();
  }
}

abstract class CardOperationEnterPin implements CardOperationEvent {
  const factory CardOperationEnterPin({required final String pin}) =
      _$CardOperationEnterPinImpl;

  String get pin;
  @JsonKey(ignore: true)
  _$$CardOperationEnterPinImplCopyWith<_$CardOperationEnterPinImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardOperationConfirmPinImplCopyWith<$Res> {
  factory _$$CardOperationConfirmPinImplCopyWith(
          _$CardOperationConfirmPinImpl value,
          $Res Function(_$CardOperationConfirmPinImpl) then) =
      __$$CardOperationConfirmPinImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String? currentPin, String? newPin});
}

/// @nodoc
class __$$CardOperationConfirmPinImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationConfirmPinImpl>
    implements _$$CardOperationConfirmPinImplCopyWith<$Res> {
  __$$CardOperationConfirmPinImplCopyWithImpl(
      _$CardOperationConfirmPinImpl _value,
      $Res Function(_$CardOperationConfirmPinImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? currentPin = freezed,
    Object? newPin = freezed,
  }) {
    return _then(_$CardOperationConfirmPinImpl(
      currentPin: freezed == currentPin
          ? _value.currentPin
          : currentPin // ignore: cast_nullable_to_non_nullable
              as String?,
      newPin: freezed == newPin
          ? _value.newPin
          : newPin // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc

class _$CardOperationConfirmPinImpl implements CardOperationConfirmPin {
  const _$CardOperationConfirmPinImpl({this.currentPin, this.newPin});

  @override
  final String? currentPin;
  @override
  final String? newPin;

  @override
  String toString() {
    return 'CardOperationEvent.confirmPin(currentPin: $currentPin, newPin: $newPin)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationConfirmPinImpl &&
            (identical(other.currentPin, currentPin) ||
                other.currentPin == currentPin) &&
            (identical(other.newPin, newPin) || other.newPin == newPin));
  }

  @override
  int get hashCode => Object.hash(runtimeType, currentPin, newPin);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardOperationConfirmPinImplCopyWith<_$CardOperationConfirmPinImpl>
      get copyWith => __$$CardOperationConfirmPinImplCopyWithImpl<
          _$CardOperationConfirmPinImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return confirmPin(currentPin, newPin);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return confirmPin?.call(currentPin, newPin);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (confirmPin != null) {
      return confirmPin(currentPin, newPin);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return confirmPin(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return confirmPin?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (confirmPin != null) {
      return confirmPin(this);
    }
    return orElse();
  }
}

abstract class CardOperationConfirmPin implements CardOperationEvent {
  const factory CardOperationConfirmPin(
      {final String? currentPin,
      final String? newPin}) = _$CardOperationConfirmPinImpl;

  String? get currentPin;
  String? get newPin;
  @JsonKey(ignore: true)
  _$$CardOperationConfirmPinImplCopyWith<_$CardOperationConfirmPinImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardOperationProcessOperationImplCopyWith<$Res> {
  factory _$$CardOperationProcessOperationImplCopyWith(
          _$CardOperationProcessOperationImpl value,
          $Res Function(_$CardOperationProcessOperationImpl) then) =
      __$$CardOperationProcessOperationImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String? currentPin, String? newPin});
}

/// @nodoc
class __$$CardOperationProcessOperationImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationProcessOperationImpl>
    implements _$$CardOperationProcessOperationImplCopyWith<$Res> {
  __$$CardOperationProcessOperationImplCopyWithImpl(
      _$CardOperationProcessOperationImpl _value,
      $Res Function(_$CardOperationProcessOperationImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? currentPin = freezed,
    Object? newPin = freezed,
  }) {
    return _then(_$CardOperationProcessOperationImpl(
      currentPin: freezed == currentPin
          ? _value.currentPin
          : currentPin // ignore: cast_nullable_to_non_nullable
              as String?,
      newPin: freezed == newPin
          ? _value.newPin
          : newPin // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc

class _$CardOperationProcessOperationImpl
    implements CardOperationProcessOperation {
  const _$CardOperationProcessOperationImpl({this.currentPin, this.newPin});

  @override
  final String? currentPin;
  @override
  final String? newPin;

  @override
  String toString() {
    return 'CardOperationEvent.processOperation(currentPin: $currentPin, newPin: $newPin)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationProcessOperationImpl &&
            (identical(other.currentPin, currentPin) ||
                other.currentPin == currentPin) &&
            (identical(other.newPin, newPin) || other.newPin == newPin));
  }

  @override
  int get hashCode => Object.hash(runtimeType, currentPin, newPin);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardOperationProcessOperationImplCopyWith<
          _$CardOperationProcessOperationImpl>
      get copyWith => __$$CardOperationProcessOperationImplCopyWithImpl<
          _$CardOperationProcessOperationImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return processOperation(currentPin, newPin);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return processOperation?.call(currentPin, newPin);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (processOperation != null) {
      return processOperation(currentPin, newPin);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return processOperation(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return processOperation?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (processOperation != null) {
      return processOperation(this);
    }
    return orElse();
  }
}

abstract class CardOperationProcessOperation implements CardOperationEvent {
  const factory CardOperationProcessOperation(
      {final String? currentPin,
      final String? newPin}) = _$CardOperationProcessOperationImpl;

  String? get currentPin;
  String? get newPin;
  @JsonKey(ignore: true)
  _$$CardOperationProcessOperationImplCopyWith<
          _$CardOperationProcessOperationImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardOperationCancelImplCopyWith<$Res> {
  factory _$$CardOperationCancelImplCopyWith(_$CardOperationCancelImpl value,
          $Res Function(_$CardOperationCancelImpl) then) =
      __$$CardOperationCancelImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$CardOperationCancelImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res, _$CardOperationCancelImpl>
    implements _$$CardOperationCancelImplCopyWith<$Res> {
  __$$CardOperationCancelImplCopyWithImpl(_$CardOperationCancelImpl _value,
      $Res Function(_$CardOperationCancelImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$CardOperationCancelImpl implements CardOperationCancel {
  const _$CardOperationCancelImpl();

  @override
  String toString() {
    return 'CardOperationEvent.cancel()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationCancelImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return cancel();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return cancel?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (cancel != null) {
      return cancel();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return cancel(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return cancel?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (cancel != null) {
      return cancel(this);
    }
    return orElse();
  }
}

abstract class CardOperationCancel implements CardOperationEvent {
  const factory CardOperationCancel() = _$CardOperationCancelImpl;
}

/// @nodoc
abstract class _$$CardOperationTimeoutImplCopyWith<$Res> {
  factory _$$CardOperationTimeoutImplCopyWith(_$CardOperationTimeoutImpl value,
          $Res Function(_$CardOperationTimeoutImpl) then) =
      __$$CardOperationTimeoutImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$CardOperationTimeoutImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res, _$CardOperationTimeoutImpl>
    implements _$$CardOperationTimeoutImplCopyWith<$Res> {
  __$$CardOperationTimeoutImplCopyWithImpl(_$CardOperationTimeoutImpl _value,
      $Res Function(_$CardOperationTimeoutImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$CardOperationTimeoutImpl implements CardOperationTimeout {
  const _$CardOperationTimeoutImpl();

  @override
  String toString() {
    return 'CardOperationEvent.timeout()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationTimeoutImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return timeout();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return timeout?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (timeout != null) {
      return timeout();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return timeout(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return timeout?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (timeout != null) {
      return timeout(this);
    }
    return orElse();
  }
}

abstract class CardOperationTimeout implements CardOperationEvent {
  const factory CardOperationTimeout() = _$CardOperationTimeoutImpl;
}

/// @nodoc
abstract class _$$CardOperationResetImplCopyWith<$Res> {
  factory _$$CardOperationResetImplCopyWith(_$CardOperationResetImpl value,
          $Res Function(_$CardOperationResetImpl) then) =
      __$$CardOperationResetImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$CardOperationResetImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res, _$CardOperationResetImpl>
    implements _$$CardOperationResetImplCopyWith<$Res> {
  __$$CardOperationResetImplCopyWithImpl(_$CardOperationResetImpl _value,
      $Res Function(_$CardOperationResetImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$CardOperationResetImpl implements CardOperationReset {
  const _$CardOperationResetImpl();

  @override
  String toString() {
    return 'CardOperationEvent.reset()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType && other is _$CardOperationResetImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return reset();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return reset?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (reset != null) {
      return reset();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return reset(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return reset?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (reset != null) {
      return reset(this);
    }
    return orElse();
  }
}

abstract class CardOperationReset implements CardOperationEvent {
  const factory CardOperationReset() = _$CardOperationResetImpl;
}

/// @nodoc
abstract class _$$CardOperationSetWorkingKeyImplCopyWith<$Res> {
  factory _$$CardOperationSetWorkingKeyImplCopyWith(
          _$CardOperationSetWorkingKeyImpl value,
          $Res Function(_$CardOperationSetWorkingKeyImpl) then) =
      __$$CardOperationSetWorkingKeyImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String workingKey});
}

/// @nodoc
class __$$CardOperationSetWorkingKeyImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationSetWorkingKeyImpl>
    implements _$$CardOperationSetWorkingKeyImplCopyWith<$Res> {
  __$$CardOperationSetWorkingKeyImplCopyWithImpl(
      _$CardOperationSetWorkingKeyImpl _value,
      $Res Function(_$CardOperationSetWorkingKeyImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? workingKey = null,
  }) {
    return _then(_$CardOperationSetWorkingKeyImpl(
      workingKey: null == workingKey
          ? _value.workingKey
          : workingKey // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$CardOperationSetWorkingKeyImpl implements CardOperationSetWorkingKey {
  const _$CardOperationSetWorkingKeyImpl({required this.workingKey});

  @override
  final String workingKey;

  @override
  String toString() {
    return 'CardOperationEvent.setWorkingKey(workingKey: $workingKey)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationSetWorkingKeyImpl &&
            (identical(other.workingKey, workingKey) ||
                other.workingKey == workingKey));
  }

  @override
  int get hashCode => Object.hash(runtimeType, workingKey);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardOperationSetWorkingKeyImplCopyWith<_$CardOperationSetWorkingKeyImpl>
      get copyWith => __$$CardOperationSetWorkingKeyImplCopyWithImpl<
          _$CardOperationSetWorkingKeyImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return setWorkingKey(workingKey);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return setWorkingKey?.call(workingKey);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (setWorkingKey != null) {
      return setWorkingKey(workingKey);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return setWorkingKey(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return setWorkingKey?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (setWorkingKey != null) {
      return setWorkingKey(this);
    }
    return orElse();
  }
}

abstract class CardOperationSetWorkingKey implements CardOperationEvent {
  const factory CardOperationSetWorkingKey({required final String workingKey}) =
      _$CardOperationSetWorkingKeyImpl;

  String get workingKey;
  @JsonKey(ignore: true)
  _$$CardOperationSetWorkingKeyImplCopyWith<_$CardOperationSetWorkingKeyImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardOperationDecryptWorkingKeyImplCopyWith<$Res> {
  factory _$$CardOperationDecryptWorkingKeyImplCopyWith(
          _$CardOperationDecryptWorkingKeyImpl value,
          $Res Function(_$CardOperationDecryptWorkingKeyImpl) then) =
      __$$CardOperationDecryptWorkingKeyImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String encryptedWorkingKey});
}

/// @nodoc
class __$$CardOperationDecryptWorkingKeyImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationDecryptWorkingKeyImpl>
    implements _$$CardOperationDecryptWorkingKeyImplCopyWith<$Res> {
  __$$CardOperationDecryptWorkingKeyImplCopyWithImpl(
      _$CardOperationDecryptWorkingKeyImpl _value,
      $Res Function(_$CardOperationDecryptWorkingKeyImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? encryptedWorkingKey = null,
  }) {
    return _then(_$CardOperationDecryptWorkingKeyImpl(
      encryptedWorkingKey: null == encryptedWorkingKey
          ? _value.encryptedWorkingKey
          : encryptedWorkingKey // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$CardOperationDecryptWorkingKeyImpl
    implements CardOperationDecryptWorkingKey {
  const _$CardOperationDecryptWorkingKeyImpl(
      {required this.encryptedWorkingKey});

  @override
  final String encryptedWorkingKey;

  @override
  String toString() {
    return 'CardOperationEvent.decryptWorkingKey(encryptedWorkingKey: $encryptedWorkingKey)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationDecryptWorkingKeyImpl &&
            (identical(other.encryptedWorkingKey, encryptedWorkingKey) ||
                other.encryptedWorkingKey == encryptedWorkingKey));
  }

  @override
  int get hashCode => Object.hash(runtimeType, encryptedWorkingKey);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardOperationDecryptWorkingKeyImplCopyWith<
          _$CardOperationDecryptWorkingKeyImpl>
      get copyWith => __$$CardOperationDecryptWorkingKeyImplCopyWithImpl<
          _$CardOperationDecryptWorkingKeyImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return decryptWorkingKey(encryptedWorkingKey);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return decryptWorkingKey?.call(encryptedWorkingKey);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (decryptWorkingKey != null) {
      return decryptWorkingKey(encryptedWorkingKey);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return decryptWorkingKey(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return decryptWorkingKey?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (decryptWorkingKey != null) {
      return decryptWorkingKey(this);
    }
    return orElse();
  }
}

abstract class CardOperationDecryptWorkingKey implements CardOperationEvent {
  const factory CardOperationDecryptWorkingKey(
          {required final String encryptedWorkingKey}) =
      _$CardOperationDecryptWorkingKeyImpl;

  String get encryptedWorkingKey;
  @JsonKey(ignore: true)
  _$$CardOperationDecryptWorkingKeyImplCopyWith<
          _$CardOperationDecryptWorkingKeyImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardOperationProcessWorkingKeyFromLogonImplCopyWith<$Res> {
  factory _$$CardOperationProcessWorkingKeyFromLogonImplCopyWith(
          _$CardOperationProcessWorkingKeyFromLogonImpl value,
          $Res Function(_$CardOperationProcessWorkingKeyFromLogonImpl) then) =
      __$$CardOperationProcessWorkingKeyFromLogonImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String workingKeyData});
}

/// @nodoc
class __$$CardOperationProcessWorkingKeyFromLogonImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationProcessWorkingKeyFromLogonImpl>
    implements _$$CardOperationProcessWorkingKeyFromLogonImplCopyWith<$Res> {
  __$$CardOperationProcessWorkingKeyFromLogonImplCopyWithImpl(
      _$CardOperationProcessWorkingKeyFromLogonImpl _value,
      $Res Function(_$CardOperationProcessWorkingKeyFromLogonImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? workingKeyData = null,
  }) {
    return _then(_$CardOperationProcessWorkingKeyFromLogonImpl(
      workingKeyData: null == workingKeyData
          ? _value.workingKeyData
          : workingKeyData // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$CardOperationProcessWorkingKeyFromLogonImpl
    implements CardOperationProcessWorkingKeyFromLogon {
  const _$CardOperationProcessWorkingKeyFromLogonImpl(
      {required this.workingKeyData});

  @override
  final String workingKeyData;

  @override
  String toString() {
    return 'CardOperationEvent.processWorkingKeyFromLogon(workingKeyData: $workingKeyData)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationProcessWorkingKeyFromLogonImpl &&
            (identical(other.workingKeyData, workingKeyData) ||
                other.workingKeyData == workingKeyData));
  }

  @override
  int get hashCode => Object.hash(runtimeType, workingKeyData);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardOperationProcessWorkingKeyFromLogonImplCopyWith<
          _$CardOperationProcessWorkingKeyFromLogonImpl>
      get copyWith =>
          __$$CardOperationProcessWorkingKeyFromLogonImplCopyWithImpl<
              _$CardOperationProcessWorkingKeyFromLogonImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return processWorkingKeyFromLogon(workingKeyData);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return processWorkingKeyFromLogon?.call(workingKeyData);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (processWorkingKeyFromLogon != null) {
      return processWorkingKeyFromLogon(workingKeyData);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return processWorkingKeyFromLogon(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return processWorkingKeyFromLogon?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (processWorkingKeyFromLogon != null) {
      return processWorkingKeyFromLogon(this);
    }
    return orElse();
  }
}

abstract class CardOperationProcessWorkingKeyFromLogon
    implements CardOperationEvent {
  const factory CardOperationProcessWorkingKeyFromLogon(
          {required final String workingKeyData}) =
      _$CardOperationProcessWorkingKeyFromLogonImpl;

  String get workingKeyData;
  @JsonKey(ignore: true)
  _$$CardOperationProcessWorkingKeyFromLogonImplCopyWith<
          _$CardOperationProcessWorkingKeyFromLogonImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardOperationClearWorkingKeyImplCopyWith<$Res> {
  factory _$$CardOperationClearWorkingKeyImplCopyWith(
          _$CardOperationClearWorkingKeyImpl value,
          $Res Function(_$CardOperationClearWorkingKeyImpl) then) =
      __$$CardOperationClearWorkingKeyImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$CardOperationClearWorkingKeyImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationClearWorkingKeyImpl>
    implements _$$CardOperationClearWorkingKeyImplCopyWith<$Res> {
  __$$CardOperationClearWorkingKeyImplCopyWithImpl(
      _$CardOperationClearWorkingKeyImpl _value,
      $Res Function(_$CardOperationClearWorkingKeyImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$CardOperationClearWorkingKeyImpl
    implements CardOperationClearWorkingKey {
  const _$CardOperationClearWorkingKeyImpl();

  @override
  String toString() {
    return 'CardOperationEvent.clearWorkingKey()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationClearWorkingKeyImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return clearWorkingKey();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return clearWorkingKey?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (clearWorkingKey != null) {
      return clearWorkingKey();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return clearWorkingKey(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return clearWorkingKey?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (clearWorkingKey != null) {
      return clearWorkingKey(this);
    }
    return orElse();
  }
}

abstract class CardOperationClearWorkingKey implements CardOperationEvent {
  const factory CardOperationClearWorkingKey() =
      _$CardOperationClearWorkingKeyImpl;
}

/// @nodoc
abstract class _$$CardOperationCheckWorkingKeyStatusImplCopyWith<$Res> {
  factory _$$CardOperationCheckWorkingKeyStatusImplCopyWith(
          _$CardOperationCheckWorkingKeyStatusImpl value,
          $Res Function(_$CardOperationCheckWorkingKeyStatusImpl) then) =
      __$$CardOperationCheckWorkingKeyStatusImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$CardOperationCheckWorkingKeyStatusImplCopyWithImpl<$Res>
    extends _$CardOperationEventCopyWithImpl<$Res,
        _$CardOperationCheckWorkingKeyStatusImpl>
    implements _$$CardOperationCheckWorkingKeyStatusImplCopyWith<$Res> {
  __$$CardOperationCheckWorkingKeyStatusImplCopyWithImpl(
      _$CardOperationCheckWorkingKeyStatusImpl _value,
      $Res Function(_$CardOperationCheckWorkingKeyStatusImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$CardOperationCheckWorkingKeyStatusImpl
    implements CardOperationCheckWorkingKeyStatus {
  const _$CardOperationCheckWorkingKeyStatusImpl();

  @override
  String toString() {
    return 'CardOperationEvent.checkWorkingKeyStatus()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardOperationCheckWorkingKeyStatusImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function(CardOperationType operationType) startReading,
    required TResult Function() confirmCard,
    required TResult Function(String pin) enterPin,
    required TResult Function(String? currentPin, String? newPin) confirmPin,
    required TResult Function(String? currentPin, String? newPin)
        processOperation,
    required TResult Function() cancel,
    required TResult Function() timeout,
    required TResult Function() reset,
    required TResult Function(String workingKey) setWorkingKey,
    required TResult Function(String encryptedWorkingKey) decryptWorkingKey,
    required TResult Function(String workingKeyData) processWorkingKeyFromLogon,
    required TResult Function() clearWorkingKey,
    required TResult Function() checkWorkingKeyStatus,
  }) {
    return checkWorkingKeyStatus();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function(CardOperationType operationType)? startReading,
    TResult? Function()? confirmCard,
    TResult? Function(String pin)? enterPin,
    TResult? Function(String? currentPin, String? newPin)? confirmPin,
    TResult? Function(String? currentPin, String? newPin)? processOperation,
    TResult? Function()? cancel,
    TResult? Function()? timeout,
    TResult? Function()? reset,
    TResult? Function(String workingKey)? setWorkingKey,
    TResult? Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult? Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult? Function()? clearWorkingKey,
    TResult? Function()? checkWorkingKeyStatus,
  }) {
    return checkWorkingKeyStatus?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function(CardOperationType operationType)? startReading,
    TResult Function()? confirmCard,
    TResult Function(String pin)? enterPin,
    TResult Function(String? currentPin, String? newPin)? confirmPin,
    TResult Function(String? currentPin, String? newPin)? processOperation,
    TResult Function()? cancel,
    TResult Function()? timeout,
    TResult Function()? reset,
    TResult Function(String workingKey)? setWorkingKey,
    TResult Function(String encryptedWorkingKey)? decryptWorkingKey,
    TResult Function(String workingKeyData)? processWorkingKeyFromLogon,
    TResult Function()? clearWorkingKey,
    TResult Function()? checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (checkWorkingKeyStatus != null) {
      return checkWorkingKeyStatus();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(CardOperationStartReading value) startReading,
    required TResult Function(CardOperationConfirmCard value) confirmCard,
    required TResult Function(CardOperationEnterPin value) enterPin,
    required TResult Function(CardOperationConfirmPin value) confirmPin,
    required TResult Function(CardOperationProcessOperation value)
        processOperation,
    required TResult Function(CardOperationCancel value) cancel,
    required TResult Function(CardOperationTimeout value) timeout,
    required TResult Function(CardOperationReset value) reset,
    required TResult Function(CardOperationSetWorkingKey value) setWorkingKey,
    required TResult Function(CardOperationDecryptWorkingKey value)
        decryptWorkingKey,
    required TResult Function(CardOperationProcessWorkingKeyFromLogon value)
        processWorkingKeyFromLogon,
    required TResult Function(CardOperationClearWorkingKey value)
        clearWorkingKey,
    required TResult Function(CardOperationCheckWorkingKeyStatus value)
        checkWorkingKeyStatus,
  }) {
    return checkWorkingKeyStatus(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(CardOperationStartReading value)? startReading,
    TResult? Function(CardOperationConfirmCard value)? confirmCard,
    TResult? Function(CardOperationEnterPin value)? enterPin,
    TResult? Function(CardOperationConfirmPin value)? confirmPin,
    TResult? Function(CardOperationProcessOperation value)? processOperation,
    TResult? Function(CardOperationCancel value)? cancel,
    TResult? Function(CardOperationTimeout value)? timeout,
    TResult? Function(CardOperationReset value)? reset,
    TResult? Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult? Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult? Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult? Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult? Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
  }) {
    return checkWorkingKeyStatus?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(CardOperationStartReading value)? startReading,
    TResult Function(CardOperationConfirmCard value)? confirmCard,
    TResult Function(CardOperationEnterPin value)? enterPin,
    TResult Function(CardOperationConfirmPin value)? confirmPin,
    TResult Function(CardOperationProcessOperation value)? processOperation,
    TResult Function(CardOperationCancel value)? cancel,
    TResult Function(CardOperationTimeout value)? timeout,
    TResult Function(CardOperationReset value)? reset,
    TResult Function(CardOperationSetWorkingKey value)? setWorkingKey,
    TResult Function(CardOperationDecryptWorkingKey value)? decryptWorkingKey,
    TResult Function(CardOperationProcessWorkingKeyFromLogon value)?
        processWorkingKeyFromLogon,
    TResult Function(CardOperationClearWorkingKey value)? clearWorkingKey,
    TResult Function(CardOperationCheckWorkingKeyStatus value)?
        checkWorkingKeyStatus,
    required TResult orElse(),
  }) {
    if (checkWorkingKeyStatus != null) {
      return checkWorkingKeyStatus(this);
    }
    return orElse();
  }
}

abstract class CardOperationCheckWorkingKeyStatus
    implements CardOperationEvent {
  const factory CardOperationCheckWorkingKeyStatus() =
      _$CardOperationCheckWorkingKeyStatusImpl;
}

/// @nodoc
mixin _$CardOperationState {
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $CardOperationStateCopyWith<$Res> {
  factory $CardOperationStateCopyWith(
          CardOperationState value, $Res Function(CardOperationState) then) =
      _$CardOperationStateCopyWithImpl<$Res, CardOperationState>;
}

/// @nodoc
class _$CardOperationStateCopyWithImpl<$Res, $Val extends CardOperationState>
    implements $CardOperationStateCopyWith<$Res> {
  _$CardOperationStateCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;
}

/// @nodoc
abstract class _$$InitialImplCopyWith<$Res> {
  factory _$$InitialImplCopyWith(
          _$InitialImpl value, $Res Function(_$InitialImpl) then) =
      __$$InitialImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$InitialImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$InitialImpl>
    implements _$$InitialImplCopyWith<$Res> {
  __$$InitialImplCopyWithImpl(
      _$InitialImpl _value, $Res Function(_$InitialImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$InitialImpl implements _Initial {
  const _$InitialImpl();

  @override
  String toString() {
    return 'CardOperationState.initial()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType && other is _$InitialImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return initial();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return initial?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (initial != null) {
      return initial();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return initial(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return initial?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (initial != null) {
      return initial(this);
    }
    return orElse();
  }
}

abstract class _Initial implements CardOperationState {
  const factory _Initial() = _$InitialImpl;
}

/// @nodoc
abstract class _$$ReadingImplCopyWith<$Res> {
  factory _$$ReadingImplCopyWith(
          _$ReadingImpl value, $Res Function(_$ReadingImpl) then) =
      __$$ReadingImplCopyWithImpl<$Res>;
  @useResult
  $Res call({CardOperationType operationType, int remainingTime});
}

/// @nodoc
class __$$ReadingImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$ReadingImpl>
    implements _$$ReadingImplCopyWith<$Res> {
  __$$ReadingImplCopyWithImpl(
      _$ReadingImpl _value, $Res Function(_$ReadingImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operationType = null,
    Object? remainingTime = null,
  }) {
    return _then(_$ReadingImpl(
      operationType: null == operationType
          ? _value.operationType
          : operationType // ignore: cast_nullable_to_non_nullable
              as CardOperationType,
      remainingTime: null == remainingTime
          ? _value.remainingTime
          : remainingTime // ignore: cast_nullable_to_non_nullable
              as int,
    ));
  }
}

/// @nodoc

class _$ReadingImpl implements _Reading {
  const _$ReadingImpl(
      {required this.operationType, required this.remainingTime});

  @override
  final CardOperationType operationType;
  @override
  final int remainingTime;

  @override
  String toString() {
    return 'CardOperationState.reading(operationType: $operationType, remainingTime: $remainingTime)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ReadingImpl &&
            (identical(other.operationType, operationType) ||
                other.operationType == operationType) &&
            (identical(other.remainingTime, remainingTime) ||
                other.remainingTime == remainingTime));
  }

  @override
  int get hashCode => Object.hash(runtimeType, operationType, remainingTime);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$ReadingImplCopyWith<_$ReadingImpl> get copyWith =>
      __$$ReadingImplCopyWithImpl<_$ReadingImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return reading(operationType, remainingTime);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return reading?.call(operationType, remainingTime);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (reading != null) {
      return reading(operationType, remainingTime);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return reading(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return reading?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (reading != null) {
      return reading(this);
    }
    return orElse();
  }
}

abstract class _Reading implements CardOperationState {
  const factory _Reading(
      {required final CardOperationType operationType,
      required final int remainingTime}) = _$ReadingImpl;

  CardOperationType get operationType;
  int get remainingTime;
  @JsonKey(ignore: true)
  _$$ReadingImplCopyWith<_$ReadingImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CardReadImplCopyWith<$Res> {
  factory _$$CardReadImplCopyWith(
          _$CardReadImpl value, $Res Function(_$CardReadImpl) then) =
      __$$CardReadImplCopyWithImpl<$Res>;
  @useResult
  $Res call({CardOperationType operationType, CardData cardData});
}

/// @nodoc
class __$$CardReadImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$CardReadImpl>
    implements _$$CardReadImplCopyWith<$Res> {
  __$$CardReadImplCopyWithImpl(
      _$CardReadImpl _value, $Res Function(_$CardReadImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operationType = null,
    Object? cardData = null,
  }) {
    return _then(_$CardReadImpl(
      operationType: null == operationType
          ? _value.operationType
          : operationType // ignore: cast_nullable_to_non_nullable
              as CardOperationType,
      cardData: null == cardData
          ? _value.cardData
          : cardData // ignore: cast_nullable_to_non_nullable
              as CardData,
    ));
  }
}

/// @nodoc

class _$CardReadImpl implements _CardRead {
  const _$CardReadImpl({required this.operationType, required this.cardData});

  @override
  final CardOperationType operationType;
  @override
  final CardData cardData;

  @override
  String toString() {
    return 'CardOperationState.cardRead(operationType: $operationType, cardData: $cardData)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$CardReadImpl &&
            (identical(other.operationType, operationType) ||
                other.operationType == operationType) &&
            (identical(other.cardData, cardData) ||
                other.cardData == cardData));
  }

  @override
  int get hashCode => Object.hash(runtimeType, operationType, cardData);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$CardReadImplCopyWith<_$CardReadImpl> get copyWith =>
      __$$CardReadImplCopyWithImpl<_$CardReadImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return cardRead(operationType, cardData);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return cardRead?.call(operationType, cardData);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (cardRead != null) {
      return cardRead(operationType, cardData);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return cardRead(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return cardRead?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (cardRead != null) {
      return cardRead(this);
    }
    return orElse();
  }
}

abstract class _CardRead implements CardOperationState {
  const factory _CardRead(
      {required final CardOperationType operationType,
      required final CardData cardData}) = _$CardReadImpl;

  CardOperationType get operationType;
  CardData get cardData;
  @JsonKey(ignore: true)
  _$$CardReadImplCopyWith<_$CardReadImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$PinEntryImplCopyWith<$Res> {
  factory _$$PinEntryImplCopyWith(
          _$PinEntryImpl value, $Res Function(_$PinEntryImpl) then) =
      __$$PinEntryImplCopyWithImpl<$Res>;
  @useResult
  $Res call(
      {CardOperationType operationType,
      CardData cardData,
      PinEntryStep step,
      int remainingTime,
      String? currentPin,
      String? newPin});
}

/// @nodoc
class __$$PinEntryImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$PinEntryImpl>
    implements _$$PinEntryImplCopyWith<$Res> {
  __$$PinEntryImplCopyWithImpl(
      _$PinEntryImpl _value, $Res Function(_$PinEntryImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operationType = null,
    Object? cardData = null,
    Object? step = null,
    Object? remainingTime = null,
    Object? currentPin = freezed,
    Object? newPin = freezed,
  }) {
    return _then(_$PinEntryImpl(
      operationType: null == operationType
          ? _value.operationType
          : operationType // ignore: cast_nullable_to_non_nullable
              as CardOperationType,
      cardData: null == cardData
          ? _value.cardData
          : cardData // ignore: cast_nullable_to_non_nullable
              as CardData,
      step: null == step
          ? _value.step
          : step // ignore: cast_nullable_to_non_nullable
              as PinEntryStep,
      remainingTime: null == remainingTime
          ? _value.remainingTime
          : remainingTime // ignore: cast_nullable_to_non_nullable
              as int,
      currentPin: freezed == currentPin
          ? _value.currentPin
          : currentPin // ignore: cast_nullable_to_non_nullable
              as String?,
      newPin: freezed == newPin
          ? _value.newPin
          : newPin // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc

class _$PinEntryImpl implements _PinEntry {
  const _$PinEntryImpl(
      {required this.operationType,
      required this.cardData,
      required this.step,
      required this.remainingTime,
      this.currentPin,
      this.newPin});

  @override
  final CardOperationType operationType;
  @override
  final CardData cardData;
  @override
  final PinEntryStep step;
  @override
  final int remainingTime;
  @override
  final String? currentPin;
  @override
  final String? newPin;

  @override
  String toString() {
    return 'CardOperationState.pinEntry(operationType: $operationType, cardData: $cardData, step: $step, remainingTime: $remainingTime, currentPin: $currentPin, newPin: $newPin)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$PinEntryImpl &&
            (identical(other.operationType, operationType) ||
                other.operationType == operationType) &&
            (identical(other.cardData, cardData) ||
                other.cardData == cardData) &&
            (identical(other.step, step) || other.step == step) &&
            (identical(other.remainingTime, remainingTime) ||
                other.remainingTime == remainingTime) &&
            (identical(other.currentPin, currentPin) ||
                other.currentPin == currentPin) &&
            (identical(other.newPin, newPin) || other.newPin == newPin));
  }

  @override
  int get hashCode => Object.hash(runtimeType, operationType, cardData, step,
      remainingTime, currentPin, newPin);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$PinEntryImplCopyWith<_$PinEntryImpl> get copyWith =>
      __$$PinEntryImplCopyWithImpl<_$PinEntryImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return pinEntry(
        operationType, cardData, step, remainingTime, currentPin, newPin);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return pinEntry?.call(
        operationType, cardData, step, remainingTime, currentPin, newPin);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (pinEntry != null) {
      return pinEntry(
          operationType, cardData, step, remainingTime, currentPin, newPin);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return pinEntry(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return pinEntry?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (pinEntry != null) {
      return pinEntry(this);
    }
    return orElse();
  }
}

abstract class _PinEntry implements CardOperationState {
  const factory _PinEntry(
      {required final CardOperationType operationType,
      required final CardData cardData,
      required final PinEntryStep step,
      required final int remainingTime,
      final String? currentPin,
      final String? newPin}) = _$PinEntryImpl;

  CardOperationType get operationType;
  CardData get cardData;
  PinEntryStep get step;
  int get remainingTime;
  String? get currentPin;
  String? get newPin;
  @JsonKey(ignore: true)
  _$$PinEntryImplCopyWith<_$PinEntryImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$ProcessingImplCopyWith<$Res> {
  factory _$$ProcessingImplCopyWith(
          _$ProcessingImpl value, $Res Function(_$ProcessingImpl) then) =
      __$$ProcessingImplCopyWithImpl<$Res>;
  @useResult
  $Res call({CardOperationType operationType, CardData cardData});
}

/// @nodoc
class __$$ProcessingImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$ProcessingImpl>
    implements _$$ProcessingImplCopyWith<$Res> {
  __$$ProcessingImplCopyWithImpl(
      _$ProcessingImpl _value, $Res Function(_$ProcessingImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operationType = null,
    Object? cardData = null,
  }) {
    return _then(_$ProcessingImpl(
      operationType: null == operationType
          ? _value.operationType
          : operationType // ignore: cast_nullable_to_non_nullable
              as CardOperationType,
      cardData: null == cardData
          ? _value.cardData
          : cardData // ignore: cast_nullable_to_non_nullable
              as CardData,
    ));
  }
}

/// @nodoc

class _$ProcessingImpl implements _Processing {
  const _$ProcessingImpl({required this.operationType, required this.cardData});

  @override
  final CardOperationType operationType;
  @override
  final CardData cardData;

  @override
  String toString() {
    return 'CardOperationState.processing(operationType: $operationType, cardData: $cardData)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ProcessingImpl &&
            (identical(other.operationType, operationType) ||
                other.operationType == operationType) &&
            (identical(other.cardData, cardData) ||
                other.cardData == cardData));
  }

  @override
  int get hashCode => Object.hash(runtimeType, operationType, cardData);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$ProcessingImplCopyWith<_$ProcessingImpl> get copyWith =>
      __$$ProcessingImplCopyWithImpl<_$ProcessingImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return processing(operationType, cardData);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return processing?.call(operationType, cardData);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (processing != null) {
      return processing(operationType, cardData);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return processing(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return processing?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (processing != null) {
      return processing(this);
    }
    return orElse();
  }
}

abstract class _Processing implements CardOperationState {
  const factory _Processing(
      {required final CardOperationType operationType,
      required final CardData cardData}) = _$ProcessingImpl;

  CardOperationType get operationType;
  CardData get cardData;
  @JsonKey(ignore: true)
  _$$ProcessingImplCopyWith<_$ProcessingImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$SuccessImplCopyWith<$Res> {
  factory _$$SuccessImplCopyWith(
          _$SuccessImpl value, $Res Function(_$SuccessImpl) then) =
      __$$SuccessImplCopyWithImpl<$Res>;
  @useResult
  $Res call(
      {CardOperationType operationType,
      PinOperationResponse response,
      int remainingTime});
}

/// @nodoc
class __$$SuccessImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$SuccessImpl>
    implements _$$SuccessImplCopyWith<$Res> {
  __$$SuccessImplCopyWithImpl(
      _$SuccessImpl _value, $Res Function(_$SuccessImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operationType = null,
    Object? response = null,
    Object? remainingTime = null,
  }) {
    return _then(_$SuccessImpl(
      operationType: null == operationType
          ? _value.operationType
          : operationType // ignore: cast_nullable_to_non_nullable
              as CardOperationType,
      response: null == response
          ? _value.response
          : response // ignore: cast_nullable_to_non_nullable
              as PinOperationResponse,
      remainingTime: null == remainingTime
          ? _value.remainingTime
          : remainingTime // ignore: cast_nullable_to_non_nullable
              as int,
    ));
  }
}

/// @nodoc

class _$SuccessImpl implements _Success {
  const _$SuccessImpl(
      {required this.operationType,
      required this.response,
      required this.remainingTime});

  @override
  final CardOperationType operationType;
  @override
  final PinOperationResponse response;
  @override
  final int remainingTime;

  @override
  String toString() {
    return 'CardOperationState.success(operationType: $operationType, response: $response, remainingTime: $remainingTime)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SuccessImpl &&
            (identical(other.operationType, operationType) ||
                other.operationType == operationType) &&
            (identical(other.response, response) ||
                other.response == response) &&
            (identical(other.remainingTime, remainingTime) ||
                other.remainingTime == remainingTime));
  }

  @override
  int get hashCode =>
      Object.hash(runtimeType, operationType, response, remainingTime);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$SuccessImplCopyWith<_$SuccessImpl> get copyWith =>
      __$$SuccessImplCopyWithImpl<_$SuccessImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return success(operationType, response, remainingTime);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return success?.call(operationType, response, remainingTime);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (success != null) {
      return success(operationType, response, remainingTime);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return success(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return success?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (success != null) {
      return success(this);
    }
    return orElse();
  }
}

abstract class _Success implements CardOperationState {
  const factory _Success(
      {required final CardOperationType operationType,
      required final PinOperationResponse response,
      required final int remainingTime}) = _$SuccessImpl;

  CardOperationType get operationType;
  PinOperationResponse get response;
  int get remainingTime;
  @JsonKey(ignore: true)
  _$$SuccessImplCopyWith<_$SuccessImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$ErrorImplCopyWith<$Res> {
  factory _$$ErrorImplCopyWith(
          _$ErrorImpl value, $Res Function(_$ErrorImpl) then) =
      __$$ErrorImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String message});
}

/// @nodoc
class __$$ErrorImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$ErrorImpl>
    implements _$$ErrorImplCopyWith<$Res> {
  __$$ErrorImplCopyWithImpl(
      _$ErrorImpl _value, $Res Function(_$ErrorImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? message = null,
  }) {
    return _then(_$ErrorImpl(
      message: null == message
          ? _value.message
          : message // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$ErrorImpl implements _Error {
  const _$ErrorImpl({required this.message});

  @override
  final String message;

  @override
  String toString() {
    return 'CardOperationState.error(message: $message)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ErrorImpl &&
            (identical(other.message, message) || other.message == message));
  }

  @override
  int get hashCode => Object.hash(runtimeType, message);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$ErrorImplCopyWith<_$ErrorImpl> get copyWith =>
      __$$ErrorImplCopyWithImpl<_$ErrorImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return error(message);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return error?.call(message);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (error != null) {
      return error(message);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return error(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return error?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (error != null) {
      return error(this);
    }
    return orElse();
  }
}

abstract class _Error implements CardOperationState {
  const factory _Error({required final String message}) = _$ErrorImpl;

  String get message;
  @JsonKey(ignore: true)
  _$$ErrorImplCopyWith<_$ErrorImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$CancelledImplCopyWith<$Res> {
  factory _$$CancelledImplCopyWith(
          _$CancelledImpl value, $Res Function(_$CancelledImpl) then) =
      __$$CancelledImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$CancelledImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$CancelledImpl>
    implements _$$CancelledImplCopyWith<$Res> {
  __$$CancelledImplCopyWithImpl(
      _$CancelledImpl _value, $Res Function(_$CancelledImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$CancelledImpl implements _Cancelled {
  const _$CancelledImpl();

  @override
  String toString() {
    return 'CardOperationState.cancelled()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType && other is _$CancelledImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return cancelled();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return cancelled?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (cancelled != null) {
      return cancelled();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return cancelled(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return cancelled?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (cancelled != null) {
      return cancelled(this);
    }
    return orElse();
  }
}

abstract class _Cancelled implements CardOperationState {
  const factory _Cancelled() = _$CancelledImpl;
}

/// @nodoc
abstract class _$$WorkingKeyProcessingImplCopyWith<$Res> {
  factory _$$WorkingKeyProcessingImplCopyWith(_$WorkingKeyProcessingImpl value,
          $Res Function(_$WorkingKeyProcessingImpl) then) =
      __$$WorkingKeyProcessingImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String operation});
}

/// @nodoc
class __$$WorkingKeyProcessingImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$WorkingKeyProcessingImpl>
    implements _$$WorkingKeyProcessingImplCopyWith<$Res> {
  __$$WorkingKeyProcessingImplCopyWithImpl(_$WorkingKeyProcessingImpl _value,
      $Res Function(_$WorkingKeyProcessingImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operation = null,
  }) {
    return _then(_$WorkingKeyProcessingImpl(
      operation: null == operation
          ? _value.operation
          : operation // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$WorkingKeyProcessingImpl implements _WorkingKeyProcessing {
  const _$WorkingKeyProcessingImpl({required this.operation});

  @override
  final String operation;

  @override
  String toString() {
    return 'CardOperationState.workingKeyProcessing(operation: $operation)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$WorkingKeyProcessingImpl &&
            (identical(other.operation, operation) ||
                other.operation == operation));
  }

  @override
  int get hashCode => Object.hash(runtimeType, operation);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$WorkingKeyProcessingImplCopyWith<_$WorkingKeyProcessingImpl>
      get copyWith =>
          __$$WorkingKeyProcessingImplCopyWithImpl<_$WorkingKeyProcessingImpl>(
              this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return workingKeyProcessing(operation);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return workingKeyProcessing?.call(operation);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (workingKeyProcessing != null) {
      return workingKeyProcessing(operation);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return workingKeyProcessing(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return workingKeyProcessing?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (workingKeyProcessing != null) {
      return workingKeyProcessing(this);
    }
    return orElse();
  }
}

abstract class _WorkingKeyProcessing implements CardOperationState {
  const factory _WorkingKeyProcessing({required final String operation}) =
      _$WorkingKeyProcessingImpl;

  String get operation;
  @JsonKey(ignore: true)
  _$$WorkingKeyProcessingImplCopyWith<_$WorkingKeyProcessingImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$WorkingKeySuccessImplCopyWith<$Res> {
  factory _$$WorkingKeySuccessImplCopyWith(_$WorkingKeySuccessImpl value,
          $Res Function(_$WorkingKeySuccessImpl) then) =
      __$$WorkingKeySuccessImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String operation, Map<String, dynamic> result});
}

/// @nodoc
class __$$WorkingKeySuccessImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$WorkingKeySuccessImpl>
    implements _$$WorkingKeySuccessImplCopyWith<$Res> {
  __$$WorkingKeySuccessImplCopyWithImpl(_$WorkingKeySuccessImpl _value,
      $Res Function(_$WorkingKeySuccessImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operation = null,
    Object? result = null,
  }) {
    return _then(_$WorkingKeySuccessImpl(
      operation: null == operation
          ? _value.operation
          : operation // ignore: cast_nullable_to_non_nullable
              as String,
      result: null == result
          ? _value._result
          : result // ignore: cast_nullable_to_non_nullable
              as Map<String, dynamic>,
    ));
  }
}

/// @nodoc

class _$WorkingKeySuccessImpl implements _WorkingKeySuccess {
  const _$WorkingKeySuccessImpl(
      {required this.operation, required final Map<String, dynamic> result})
      : _result = result;

  @override
  final String operation;
  final Map<String, dynamic> _result;
  @override
  Map<String, dynamic> get result {
    if (_result is EqualUnmodifiableMapView) return _result;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(_result);
  }

  @override
  String toString() {
    return 'CardOperationState.workingKeySuccess(operation: $operation, result: $result)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$WorkingKeySuccessImpl &&
            (identical(other.operation, operation) ||
                other.operation == operation) &&
            const DeepCollectionEquality().equals(other._result, _result));
  }

  @override
  int get hashCode => Object.hash(
      runtimeType, operation, const DeepCollectionEquality().hash(_result));

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$WorkingKeySuccessImplCopyWith<_$WorkingKeySuccessImpl> get copyWith =>
      __$$WorkingKeySuccessImplCopyWithImpl<_$WorkingKeySuccessImpl>(
          this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return workingKeySuccess(operation, result);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return workingKeySuccess?.call(operation, result);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (workingKeySuccess != null) {
      return workingKeySuccess(operation, result);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return workingKeySuccess(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return workingKeySuccess?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (workingKeySuccess != null) {
      return workingKeySuccess(this);
    }
    return orElse();
  }
}

abstract class _WorkingKeySuccess implements CardOperationState {
  const factory _WorkingKeySuccess(
      {required final String operation,
      required final Map<String, dynamic> result}) = _$WorkingKeySuccessImpl;

  String get operation;
  Map<String, dynamic> get result;
  @JsonKey(ignore: true)
  _$$WorkingKeySuccessImplCopyWith<_$WorkingKeySuccessImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class _$$WorkingKeyErrorImplCopyWith<$Res> {
  factory _$$WorkingKeyErrorImplCopyWith(_$WorkingKeyErrorImpl value,
          $Res Function(_$WorkingKeyErrorImpl) then) =
      __$$WorkingKeyErrorImplCopyWithImpl<$Res>;
  @useResult
  $Res call({String operation, String message});
}

/// @nodoc
class __$$WorkingKeyErrorImplCopyWithImpl<$Res>
    extends _$CardOperationStateCopyWithImpl<$Res, _$WorkingKeyErrorImpl>
    implements _$$WorkingKeyErrorImplCopyWith<$Res> {
  __$$WorkingKeyErrorImplCopyWithImpl(
      _$WorkingKeyErrorImpl _value, $Res Function(_$WorkingKeyErrorImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operation = null,
    Object? message = null,
  }) {
    return _then(_$WorkingKeyErrorImpl(
      operation: null == operation
          ? _value.operation
          : operation // ignore: cast_nullable_to_non_nullable
              as String,
      message: null == message
          ? _value.message
          : message // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$WorkingKeyErrorImpl implements _WorkingKeyError {
  const _$WorkingKeyErrorImpl({required this.operation, required this.message});

  @override
  final String operation;
  @override
  final String message;

  @override
  String toString() {
    return 'CardOperationState.workingKeyError(operation: $operation, message: $message)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$WorkingKeyErrorImpl &&
            (identical(other.operation, operation) ||
                other.operation == operation) &&
            (identical(other.message, message) || other.message == message));
  }

  @override
  int get hashCode => Object.hash(runtimeType, operation, message);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$WorkingKeyErrorImplCopyWith<_$WorkingKeyErrorImpl> get copyWith =>
      __$$WorkingKeyErrorImplCopyWithImpl<_$WorkingKeyErrorImpl>(
          this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() initial,
    required TResult Function(
            CardOperationType operationType, int remainingTime)
        reading,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        cardRead,
    required TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)
        pinEntry,
    required TResult Function(
            CardOperationType operationType, CardData cardData)
        processing,
    required TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)
        success,
    required TResult Function(String message) error,
    required TResult Function() cancelled,
    required TResult Function(String operation) workingKeyProcessing,
    required TResult Function(String operation, Map<String, dynamic> result)
        workingKeySuccess,
    required TResult Function(String operation, String message) workingKeyError,
  }) {
    return workingKeyError(operation, message);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? initial,
    TResult? Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult? Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult? Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult? Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult? Function(String message)? error,
    TResult? Function()? cancelled,
    TResult? Function(String operation)? workingKeyProcessing,
    TResult? Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult? Function(String operation, String message)? workingKeyError,
  }) {
    return workingKeyError?.call(operation, message);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? initial,
    TResult Function(CardOperationType operationType, int remainingTime)?
        reading,
    TResult Function(CardOperationType operationType, CardData cardData)?
        cardRead,
    TResult Function(
            CardOperationType operationType,
            CardData cardData,
            PinEntryStep step,
            int remainingTime,
            String? currentPin,
            String? newPin)?
        pinEntry,
    TResult Function(CardOperationType operationType, CardData cardData)?
        processing,
    TResult Function(CardOperationType operationType,
            PinOperationResponse response, int remainingTime)?
        success,
    TResult Function(String message)? error,
    TResult Function()? cancelled,
    TResult Function(String operation)? workingKeyProcessing,
    TResult Function(String operation, Map<String, dynamic> result)?
        workingKeySuccess,
    TResult Function(String operation, String message)? workingKeyError,
    required TResult orElse(),
  }) {
    if (workingKeyError != null) {
      return workingKeyError(operation, message);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(_Initial value) initial,
    required TResult Function(_Reading value) reading,
    required TResult Function(_CardRead value) cardRead,
    required TResult Function(_PinEntry value) pinEntry,
    required TResult Function(_Processing value) processing,
    required TResult Function(_Success value) success,
    required TResult Function(_Error value) error,
    required TResult Function(_Cancelled value) cancelled,
    required TResult Function(_WorkingKeyProcessing value) workingKeyProcessing,
    required TResult Function(_WorkingKeySuccess value) workingKeySuccess,
    required TResult Function(_WorkingKeyError value) workingKeyError,
  }) {
    return workingKeyError(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(_Initial value)? initial,
    TResult? Function(_Reading value)? reading,
    TResult? Function(_CardRead value)? cardRead,
    TResult? Function(_PinEntry value)? pinEntry,
    TResult? Function(_Processing value)? processing,
    TResult? Function(_Success value)? success,
    TResult? Function(_Error value)? error,
    TResult? Function(_Cancelled value)? cancelled,
    TResult? Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult? Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult? Function(_WorkingKeyError value)? workingKeyError,
  }) {
    return workingKeyError?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(_Initial value)? initial,
    TResult Function(_Reading value)? reading,
    TResult Function(_CardRead value)? cardRead,
    TResult Function(_PinEntry value)? pinEntry,
    TResult Function(_Processing value)? processing,
    TResult Function(_Success value)? success,
    TResult Function(_Error value)? error,
    TResult Function(_Cancelled value)? cancelled,
    TResult Function(_WorkingKeyProcessing value)? workingKeyProcessing,
    TResult Function(_WorkingKeySuccess value)? workingKeySuccess,
    TResult Function(_WorkingKeyError value)? workingKeyError,
    required TResult orElse(),
  }) {
    if (workingKeyError != null) {
      return workingKeyError(this);
    }
    return orElse();
  }
}

abstract class _WorkingKeyError implements CardOperationState {
  const factory _WorkingKeyError(
      {required final String operation,
      required final String message}) = _$WorkingKeyErrorImpl;

  String get operation;
  String get message;
  @JsonKey(ignore: true)
  _$$WorkingKeyErrorImplCopyWith<_$WorkingKeyErrorImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
