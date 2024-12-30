import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'flutter_smart_pin_pad_cards_method_channel.dart';

abstract class FlutterSmartPinPadCardsPlatform extends PlatformInterface {
  FlutterSmartPinPadCardsPlatform() : super(token: _token);

  static final Object _token = Object();
  static FlutterSmartPinPadCardsPlatform _instance = MethodChannelFlutterSmartPinPadCards();

  static FlutterSmartPinPadCardsPlatform get instance => _instance;

  static set instance(FlutterSmartPinPadCardsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<Map<dynamic, dynamic>> startCardReading() {
    throw UnimplementedError('startCardReading() has not been implemented.');
  }

  Future<void> stopCardReading() {
    throw UnimplementedError('stopCardReading() has not been implemented.');
  }
}
