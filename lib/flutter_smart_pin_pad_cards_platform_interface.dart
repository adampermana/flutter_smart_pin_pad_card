import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_smart_pin_pad_cards_method_channel.dart';

abstract class FlutterSmartPinPadCardsPlatform extends PlatformInterface {
  /// Constructs a FlutterSmartPinPadCardsPlatform.
  FlutterSmartPinPadCardsPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterSmartPinPadCardsPlatform _instance = MethodChannelFlutterSmartPinPadCards();

  /// The default instance of [FlutterSmartPinPadCardsPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterSmartPinPadCards].
  static FlutterSmartPinPadCardsPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterSmartPinPadCardsPlatform] when
  /// they register themselves.
  static set instance(FlutterSmartPinPadCardsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
