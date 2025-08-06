// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'network_bloc.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models');

/// @nodoc
mixin _$NetworkEvent {
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() checkConnection,
    required TResult Function() checkCapabilities,
    required TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)
        sendPinOperation,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? checkConnection,
    TResult? Function()? checkCapabilities,
    TResult? Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? checkConnection,
    TResult Function()? checkCapabilities,
    TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
    required TResult orElse(),
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(NetworkCheckConnection value) checkConnection,
    required TResult Function(NetworkCheckCapabilities value) checkCapabilities,
    required TResult Function(NetworkSendPinOperation value) sendPinOperation,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(NetworkCheckConnection value)? checkConnection,
    TResult? Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult? Function(NetworkSendPinOperation value)? sendPinOperation,
  }) =>
      throw _privateConstructorUsedError;
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(NetworkCheckConnection value)? checkConnection,
    TResult Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult Function(NetworkSendPinOperation value)? sendPinOperation,
    required TResult orElse(),
  }) =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $NetworkEventCopyWith<$Res> {
  factory $NetworkEventCopyWith(
          NetworkEvent value, $Res Function(NetworkEvent) then) =
      _$NetworkEventCopyWithImpl<$Res, NetworkEvent>;
}

/// @nodoc
class _$NetworkEventCopyWithImpl<$Res, $Val extends NetworkEvent>
    implements $NetworkEventCopyWith<$Res> {
  _$NetworkEventCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;
}

/// @nodoc
abstract class _$$NetworkCheckConnectionImplCopyWith<$Res> {
  factory _$$NetworkCheckConnectionImplCopyWith(
          _$NetworkCheckConnectionImpl value,
          $Res Function(_$NetworkCheckConnectionImpl) then) =
      __$$NetworkCheckConnectionImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$NetworkCheckConnectionImplCopyWithImpl<$Res>
    extends _$NetworkEventCopyWithImpl<$Res, _$NetworkCheckConnectionImpl>
    implements _$$NetworkCheckConnectionImplCopyWith<$Res> {
  __$$NetworkCheckConnectionImplCopyWithImpl(
      _$NetworkCheckConnectionImpl _value,
      $Res Function(_$NetworkCheckConnectionImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$NetworkCheckConnectionImpl implements NetworkCheckConnection {
  const _$NetworkCheckConnectionImpl();

  @override
  String toString() {
    return 'NetworkEvent.checkConnection()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$NetworkCheckConnectionImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() checkConnection,
    required TResult Function() checkCapabilities,
    required TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)
        sendPinOperation,
  }) {
    return checkConnection();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? checkConnection,
    TResult? Function()? checkCapabilities,
    TResult? Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
  }) {
    return checkConnection?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? checkConnection,
    TResult Function()? checkCapabilities,
    TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
    required TResult orElse(),
  }) {
    if (checkConnection != null) {
      return checkConnection();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(NetworkCheckConnection value) checkConnection,
    required TResult Function(NetworkCheckCapabilities value) checkCapabilities,
    required TResult Function(NetworkSendPinOperation value) sendPinOperation,
  }) {
    return checkConnection(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(NetworkCheckConnection value)? checkConnection,
    TResult? Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult? Function(NetworkSendPinOperation value)? sendPinOperation,
  }) {
    return checkConnection?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(NetworkCheckConnection value)? checkConnection,
    TResult Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult Function(NetworkSendPinOperation value)? sendPinOperation,
    required TResult orElse(),
  }) {
    if (checkConnection != null) {
      return checkConnection(this);
    }
    return orElse();
  }
}

abstract class NetworkCheckConnection implements NetworkEvent {
  const factory NetworkCheckConnection() = _$NetworkCheckConnectionImpl;
}

/// @nodoc
abstract class _$$NetworkCheckCapabilitiesImplCopyWith<$Res> {
  factory _$$NetworkCheckCapabilitiesImplCopyWith(
          _$NetworkCheckCapabilitiesImpl value,
          $Res Function(_$NetworkCheckCapabilitiesImpl) then) =
      __$$NetworkCheckCapabilitiesImplCopyWithImpl<$Res>;
}

/// @nodoc
class __$$NetworkCheckCapabilitiesImplCopyWithImpl<$Res>
    extends _$NetworkEventCopyWithImpl<$Res, _$NetworkCheckCapabilitiesImpl>
    implements _$$NetworkCheckCapabilitiesImplCopyWith<$Res> {
  __$$NetworkCheckCapabilitiesImplCopyWithImpl(
      _$NetworkCheckCapabilitiesImpl _value,
      $Res Function(_$NetworkCheckCapabilitiesImpl) _then)
      : super(_value, _then);
}

/// @nodoc

class _$NetworkCheckCapabilitiesImpl implements NetworkCheckCapabilities {
  const _$NetworkCheckCapabilitiesImpl();

  @override
  String toString() {
    return 'NetworkEvent.checkCapabilities()';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$NetworkCheckCapabilitiesImpl);
  }

  @override
  int get hashCode => runtimeType.hashCode;

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() checkConnection,
    required TResult Function() checkCapabilities,
    required TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)
        sendPinOperation,
  }) {
    return checkCapabilities();
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? checkConnection,
    TResult? Function()? checkCapabilities,
    TResult? Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
  }) {
    return checkCapabilities?.call();
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? checkConnection,
    TResult Function()? checkCapabilities,
    TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
    required TResult orElse(),
  }) {
    if (checkCapabilities != null) {
      return checkCapabilities();
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(NetworkCheckConnection value) checkConnection,
    required TResult Function(NetworkCheckCapabilities value) checkCapabilities,
    required TResult Function(NetworkSendPinOperation value) sendPinOperation,
  }) {
    return checkCapabilities(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(NetworkCheckConnection value)? checkConnection,
    TResult? Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult? Function(NetworkSendPinOperation value)? sendPinOperation,
  }) {
    return checkCapabilities?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(NetworkCheckConnection value)? checkConnection,
    TResult Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult Function(NetworkSendPinOperation value)? sendPinOperation,
    required TResult orElse(),
  }) {
    if (checkCapabilities != null) {
      return checkCapabilities(this);
    }
    return orElse();
  }
}

abstract class NetworkCheckCapabilities implements NetworkEvent {
  const factory NetworkCheckCapabilities() = _$NetworkCheckCapabilitiesImpl;
}

/// @nodoc
abstract class _$$NetworkSendPinOperationImplCopyWith<$Res> {
  factory _$$NetworkSendPinOperationImplCopyWith(
          _$NetworkSendPinOperationImpl value,
          $Res Function(_$NetworkSendPinOperationImpl) then) =
      __$$NetworkSendPinOperationImplCopyWithImpl<$Res>;
  @useResult
  $Res call(
      {PinOperationType operationType,
      String systemsTraceNo,
      String terminalId,
      String merchantId,
      String track2Data,
      String? currentPinBlock,
      String? newPinBlock,
      String? newPin,
      int? amount});
}

/// @nodoc
class __$$NetworkSendPinOperationImplCopyWithImpl<$Res>
    extends _$NetworkEventCopyWithImpl<$Res, _$NetworkSendPinOperationImpl>
    implements _$$NetworkSendPinOperationImplCopyWith<$Res> {
  __$$NetworkSendPinOperationImplCopyWithImpl(
      _$NetworkSendPinOperationImpl _value,
      $Res Function(_$NetworkSendPinOperationImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? operationType = null,
    Object? systemsTraceNo = null,
    Object? terminalId = null,
    Object? merchantId = null,
    Object? track2Data = null,
    Object? currentPinBlock = freezed,
    Object? newPinBlock = freezed,
    Object? newPin = freezed,
    Object? amount = freezed,
  }) {
    return _then(_$NetworkSendPinOperationImpl(
      operationType: null == operationType
          ? _value.operationType
          : operationType // ignore: cast_nullable_to_non_nullable
              as PinOperationType,
      systemsTraceNo: null == systemsTraceNo
          ? _value.systemsTraceNo
          : systemsTraceNo // ignore: cast_nullable_to_non_nullable
              as String,
      terminalId: null == terminalId
          ? _value.terminalId
          : terminalId // ignore: cast_nullable_to_non_nullable
              as String,
      merchantId: null == merchantId
          ? _value.merchantId
          : merchantId // ignore: cast_nullable_to_non_nullable
              as String,
      track2Data: null == track2Data
          ? _value.track2Data
          : track2Data // ignore: cast_nullable_to_non_nullable
              as String,
      currentPinBlock: freezed == currentPinBlock
          ? _value.currentPinBlock
          : currentPinBlock // ignore: cast_nullable_to_non_nullable
              as String?,
      newPinBlock: freezed == newPinBlock
          ? _value.newPinBlock
          : newPinBlock // ignore: cast_nullable_to_non_nullable
              as String?,
      newPin: freezed == newPin
          ? _value.newPin
          : newPin // ignore: cast_nullable_to_non_nullable
              as String?,
      amount: freezed == amount
          ? _value.amount
          : amount // ignore: cast_nullable_to_non_nullable
              as int?,
    ));
  }
}

/// @nodoc

class _$NetworkSendPinOperationImpl implements NetworkSendPinOperation {
  const _$NetworkSendPinOperationImpl(
      {required this.operationType,
      required this.systemsTraceNo,
      required this.terminalId,
      required this.merchantId,
      required this.track2Data,
      this.currentPinBlock,
      this.newPinBlock,
      this.newPin,
      this.amount});

  @override
  final PinOperationType operationType;
  @override
  final String systemsTraceNo;
  @override
  final String terminalId;
  @override
  final String merchantId;
  @override
  final String track2Data;
  @override
  final String? currentPinBlock;
  @override
  final String? newPinBlock;
  @override
  final String? newPin;
  @override
  final int? amount;

  @override
  String toString() {
    return 'NetworkEvent.sendPinOperation(operationType: $operationType, systemsTraceNo: $systemsTraceNo, terminalId: $terminalId, merchantId: $merchantId, track2Data: $track2Data, currentPinBlock: $currentPinBlock, newPinBlock: $newPinBlock, newPin: $newPin, amount: $amount)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$NetworkSendPinOperationImpl &&
            (identical(other.operationType, operationType) ||
                other.operationType == operationType) &&
            (identical(other.systemsTraceNo, systemsTraceNo) ||
                other.systemsTraceNo == systemsTraceNo) &&
            (identical(other.terminalId, terminalId) ||
                other.terminalId == terminalId) &&
            (identical(other.merchantId, merchantId) ||
                other.merchantId == merchantId) &&
            (identical(other.track2Data, track2Data) ||
                other.track2Data == track2Data) &&
            (identical(other.currentPinBlock, currentPinBlock) ||
                other.currentPinBlock == currentPinBlock) &&
            (identical(other.newPinBlock, newPinBlock) ||
                other.newPinBlock == newPinBlock) &&
            (identical(other.newPin, newPin) || other.newPin == newPin) &&
            (identical(other.amount, amount) || other.amount == amount));
  }

  @override
  int get hashCode => Object.hash(
      runtimeType,
      operationType,
      systemsTraceNo,
      terminalId,
      merchantId,
      track2Data,
      currentPinBlock,
      newPinBlock,
      newPin,
      amount);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$NetworkSendPinOperationImplCopyWith<_$NetworkSendPinOperationImpl>
      get copyWith => __$$NetworkSendPinOperationImplCopyWithImpl<
          _$NetworkSendPinOperationImpl>(this, _$identity);

  @override
  @optionalTypeArgs
  TResult when<TResult extends Object?>({
    required TResult Function() checkConnection,
    required TResult Function() checkCapabilities,
    required TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)
        sendPinOperation,
  }) {
    return sendPinOperation(operationType, systemsTraceNo, terminalId,
        merchantId, track2Data, currentPinBlock, newPinBlock, newPin, amount);
  }

  @override
  @optionalTypeArgs
  TResult? whenOrNull<TResult extends Object?>({
    TResult? Function()? checkConnection,
    TResult? Function()? checkCapabilities,
    TResult? Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
  }) {
    return sendPinOperation?.call(operationType, systemsTraceNo, terminalId,
        merchantId, track2Data, currentPinBlock, newPinBlock, newPin, amount);
  }

  @override
  @optionalTypeArgs
  TResult maybeWhen<TResult extends Object?>({
    TResult Function()? checkConnection,
    TResult Function()? checkCapabilities,
    TResult Function(
            PinOperationType operationType,
            String systemsTraceNo,
            String terminalId,
            String merchantId,
            String track2Data,
            String? currentPinBlock,
            String? newPinBlock,
            String? newPin,
            int? amount)?
        sendPinOperation,
    required TResult orElse(),
  }) {
    if (sendPinOperation != null) {
      return sendPinOperation(operationType, systemsTraceNo, terminalId,
          merchantId, track2Data, currentPinBlock, newPinBlock, newPin, amount);
    }
    return orElse();
  }

  @override
  @optionalTypeArgs
  TResult map<TResult extends Object?>({
    required TResult Function(NetworkCheckConnection value) checkConnection,
    required TResult Function(NetworkCheckCapabilities value) checkCapabilities,
    required TResult Function(NetworkSendPinOperation value) sendPinOperation,
  }) {
    return sendPinOperation(this);
  }

  @override
  @optionalTypeArgs
  TResult? mapOrNull<TResult extends Object?>({
    TResult? Function(NetworkCheckConnection value)? checkConnection,
    TResult? Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult? Function(NetworkSendPinOperation value)? sendPinOperation,
  }) {
    return sendPinOperation?.call(this);
  }

  @override
  @optionalTypeArgs
  TResult maybeMap<TResult extends Object?>({
    TResult Function(NetworkCheckConnection value)? checkConnection,
    TResult Function(NetworkCheckCapabilities value)? checkCapabilities,
    TResult Function(NetworkSendPinOperation value)? sendPinOperation,
    required TResult orElse(),
  }) {
    if (sendPinOperation != null) {
      return sendPinOperation(this);
    }
    return orElse();
  }
}

abstract class NetworkSendPinOperation implements NetworkEvent {
  const factory NetworkSendPinOperation(
      {required final PinOperationType operationType,
      required final String systemsTraceNo,
      required final String terminalId,
      required final String merchantId,
      required final String track2Data,
      final String? currentPinBlock,
      final String? newPinBlock,
      final String? newPin,
      final int? amount}) = _$NetworkSendPinOperationImpl;

  PinOperationType get operationType;
  String get systemsTraceNo;
  String get terminalId;
  String get merchantId;
  String get track2Data;
  String? get currentPinBlock;
  String? get newPinBlock;
  String? get newPin;
  int? get amount;
  @JsonKey(ignore: true)
  _$$NetworkSendPinOperationImplCopyWith<_$NetworkSendPinOperationImpl>
      get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
mixin _$NetworkState {
  bool get isConnected => throw _privateConstructorUsedError;
  bool get isCheckingConnection => throw _privateConstructorUsedError;
  bool get isCheckingCapabilities => throw _privateConstructorUsedError;
  bool get isProcessingOperation => throw _privateConstructorUsedError;
  Set<String> get supportedOperations => throw _privateConstructorUsedError;
  String get capabilityStatus => throw _privateConstructorUsedError;
  PinOperationResponse? get lastOperationResponse =>
      throw _privateConstructorUsedError;
  String? get errorMessage => throw _privateConstructorUsedError;

  @JsonKey(ignore: true)
  $NetworkStateCopyWith<NetworkState> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $NetworkStateCopyWith<$Res> {
  factory $NetworkStateCopyWith(
          NetworkState value, $Res Function(NetworkState) then) =
      _$NetworkStateCopyWithImpl<$Res, NetworkState>;
  @useResult
  $Res call(
      {bool isConnected,
      bool isCheckingConnection,
      bool isCheckingCapabilities,
      bool isProcessingOperation,
      Set<String> supportedOperations,
      String capabilityStatus,
      PinOperationResponse? lastOperationResponse,
      String? errorMessage});
}

/// @nodoc
class _$NetworkStateCopyWithImpl<$Res, $Val extends NetworkState>
    implements $NetworkStateCopyWith<$Res> {
  _$NetworkStateCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? isConnected = null,
    Object? isCheckingConnection = null,
    Object? isCheckingCapabilities = null,
    Object? isProcessingOperation = null,
    Object? supportedOperations = null,
    Object? capabilityStatus = null,
    Object? lastOperationResponse = freezed,
    Object? errorMessage = freezed,
  }) {
    return _then(_value.copyWith(
      isConnected: null == isConnected
          ? _value.isConnected
          : isConnected // ignore: cast_nullable_to_non_nullable
              as bool,
      isCheckingConnection: null == isCheckingConnection
          ? _value.isCheckingConnection
          : isCheckingConnection // ignore: cast_nullable_to_non_nullable
              as bool,
      isCheckingCapabilities: null == isCheckingCapabilities
          ? _value.isCheckingCapabilities
          : isCheckingCapabilities // ignore: cast_nullable_to_non_nullable
              as bool,
      isProcessingOperation: null == isProcessingOperation
          ? _value.isProcessingOperation
          : isProcessingOperation // ignore: cast_nullable_to_non_nullable
              as bool,
      supportedOperations: null == supportedOperations
          ? _value.supportedOperations
          : supportedOperations // ignore: cast_nullable_to_non_nullable
              as Set<String>,
      capabilityStatus: null == capabilityStatus
          ? _value.capabilityStatus
          : capabilityStatus // ignore: cast_nullable_to_non_nullable
              as String,
      lastOperationResponse: freezed == lastOperationResponse
          ? _value.lastOperationResponse
          : lastOperationResponse // ignore: cast_nullable_to_non_nullable
              as PinOperationResponse?,
      errorMessage: freezed == errorMessage
          ? _value.errorMessage
          : errorMessage // ignore: cast_nullable_to_non_nullable
              as String?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$NetworkStateImplCopyWith<$Res>
    implements $NetworkStateCopyWith<$Res> {
  factory _$$NetworkStateImplCopyWith(
          _$NetworkStateImpl value, $Res Function(_$NetworkStateImpl) then) =
      __$$NetworkStateImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {bool isConnected,
      bool isCheckingConnection,
      bool isCheckingCapabilities,
      bool isProcessingOperation,
      Set<String> supportedOperations,
      String capabilityStatus,
      PinOperationResponse? lastOperationResponse,
      String? errorMessage});
}

/// @nodoc
class __$$NetworkStateImplCopyWithImpl<$Res>
    extends _$NetworkStateCopyWithImpl<$Res, _$NetworkStateImpl>
    implements _$$NetworkStateImplCopyWith<$Res> {
  __$$NetworkStateImplCopyWithImpl(
      _$NetworkStateImpl _value, $Res Function(_$NetworkStateImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? isConnected = null,
    Object? isCheckingConnection = null,
    Object? isCheckingCapabilities = null,
    Object? isProcessingOperation = null,
    Object? supportedOperations = null,
    Object? capabilityStatus = null,
    Object? lastOperationResponse = freezed,
    Object? errorMessage = freezed,
  }) {
    return _then(_$NetworkStateImpl(
      isConnected: null == isConnected
          ? _value.isConnected
          : isConnected // ignore: cast_nullable_to_non_nullable
              as bool,
      isCheckingConnection: null == isCheckingConnection
          ? _value.isCheckingConnection
          : isCheckingConnection // ignore: cast_nullable_to_non_nullable
              as bool,
      isCheckingCapabilities: null == isCheckingCapabilities
          ? _value.isCheckingCapabilities
          : isCheckingCapabilities // ignore: cast_nullable_to_non_nullable
              as bool,
      isProcessingOperation: null == isProcessingOperation
          ? _value.isProcessingOperation
          : isProcessingOperation // ignore: cast_nullable_to_non_nullable
              as bool,
      supportedOperations: null == supportedOperations
          ? _value._supportedOperations
          : supportedOperations // ignore: cast_nullable_to_non_nullable
              as Set<String>,
      capabilityStatus: null == capabilityStatus
          ? _value.capabilityStatus
          : capabilityStatus // ignore: cast_nullable_to_non_nullable
              as String,
      lastOperationResponse: freezed == lastOperationResponse
          ? _value.lastOperationResponse
          : lastOperationResponse // ignore: cast_nullable_to_non_nullable
              as PinOperationResponse?,
      errorMessage: freezed == errorMessage
          ? _value.errorMessage
          : errorMessage // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc

class _$NetworkStateImpl implements _NetworkState {
  const _$NetworkStateImpl(
      {required this.isConnected,
      required this.isCheckingConnection,
      required this.isCheckingCapabilities,
      required this.isProcessingOperation,
      required final Set<String> supportedOperations,
      required this.capabilityStatus,
      this.lastOperationResponse,
      this.errorMessage})
      : _supportedOperations = supportedOperations;

  @override
  final bool isConnected;
  @override
  final bool isCheckingConnection;
  @override
  final bool isCheckingCapabilities;
  @override
  final bool isProcessingOperation;
  final Set<String> _supportedOperations;
  @override
  Set<String> get supportedOperations {
    if (_supportedOperations is EqualUnmodifiableSetView)
      return _supportedOperations;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableSetView(_supportedOperations);
  }

  @override
  final String capabilityStatus;
  @override
  final PinOperationResponse? lastOperationResponse;
  @override
  final String? errorMessage;

  @override
  String toString() {
    return 'NetworkState(isConnected: $isConnected, isCheckingConnection: $isCheckingConnection, isCheckingCapabilities: $isCheckingCapabilities, isProcessingOperation: $isProcessingOperation, supportedOperations: $supportedOperations, capabilityStatus: $capabilityStatus, lastOperationResponse: $lastOperationResponse, errorMessage: $errorMessage)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$NetworkStateImpl &&
            (identical(other.isConnected, isConnected) ||
                other.isConnected == isConnected) &&
            (identical(other.isCheckingConnection, isCheckingConnection) ||
                other.isCheckingConnection == isCheckingConnection) &&
            (identical(other.isCheckingCapabilities, isCheckingCapabilities) ||
                other.isCheckingCapabilities == isCheckingCapabilities) &&
            (identical(other.isProcessingOperation, isProcessingOperation) ||
                other.isProcessingOperation == isProcessingOperation) &&
            const DeepCollectionEquality()
                .equals(other._supportedOperations, _supportedOperations) &&
            (identical(other.capabilityStatus, capabilityStatus) ||
                other.capabilityStatus == capabilityStatus) &&
            (identical(other.lastOperationResponse, lastOperationResponse) ||
                other.lastOperationResponse == lastOperationResponse) &&
            (identical(other.errorMessage, errorMessage) ||
                other.errorMessage == errorMessage));
  }

  @override
  int get hashCode => Object.hash(
      runtimeType,
      isConnected,
      isCheckingConnection,
      isCheckingCapabilities,
      isProcessingOperation,
      const DeepCollectionEquality().hash(_supportedOperations),
      capabilityStatus,
      lastOperationResponse,
      errorMessage);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$NetworkStateImplCopyWith<_$NetworkStateImpl> get copyWith =>
      __$$NetworkStateImplCopyWithImpl<_$NetworkStateImpl>(this, _$identity);
}

abstract class _NetworkState implements NetworkState {
  const factory _NetworkState(
      {required final bool isConnected,
      required final bool isCheckingConnection,
      required final bool isCheckingCapabilities,
      required final bool isProcessingOperation,
      required final Set<String> supportedOperations,
      required final String capabilityStatus,
      final PinOperationResponse? lastOperationResponse,
      final String? errorMessage}) = _$NetworkStateImpl;

  @override
  bool get isConnected;
  @override
  bool get isCheckingConnection;
  @override
  bool get isCheckingCapabilities;
  @override
  bool get isProcessingOperation;
  @override
  Set<String> get supportedOperations;
  @override
  String get capabilityStatus;
  @override
  PinOperationResponse? get lastOperationResponse;
  @override
  String? get errorMessage;
  @override
  @JsonKey(ignore: true)
  _$$NetworkStateImplCopyWith<_$NetworkStateImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
