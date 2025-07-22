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

  // ============================================================================
  // CARD READER METHODS
  // ============================================================================

  Future<Map<dynamic, dynamic>> startSwipeCardReading() {
    throw UnimplementedError('startSwipeCardReading() has not been implemented.');
  }

  Future<void> stopSwipeCardReading() {
    throw UnimplementedError('stopSwipeCardReading() has not been implemented.');
  }

  Future<Map<dynamic, dynamic>> startInsertCardReading() {
    throw UnimplementedError('startInsertCardReading() has not been implemented.');
  }

  Future<void> stopInsertCardReading() {
    throw UnimplementedError('stopInsertCardReading() has not been implemented.');
  }

  Future<Map<dynamic, dynamic>> createDynamicPinBlock() {
    throw UnimplementedError('createDynamicPinBlock() has not been implemented.');
  }

  Future<Map<dynamic, dynamic>> createPinDynamic() {
    throw UnimplementedError('createPinDynamic() has not been implemented.');
  }

  Future<Map<dynamic, dynamic>> changePinDynamic() {
    throw UnimplementedError('changePinDynamic() has not been implemented.');
  }

  Future<Map<dynamic, dynamic>> authorizePinDynamic() {
    throw UnimplementedError('authorizePinDynamic() has not been implemented.');
  }
  Future<Map<dynamic, dynamic>> testAllPinBlockFormats() {
    throw UnimplementedError('testAllPinBlockFormats() has not been implemented.');
  }

  Future<Map<dynamic, dynamic>> createPinBlock() {
    throw UnimplementedError('createPinBlock() has not been implemented.');
  }

  Future<bool> initPinpad() {
    throw UnimplementedError('initPinpad() has not been implemented.');
  }

  Future<bool> closePinpad() {
    throw UnimplementedError('closePinpad() has not been implemented.');
  }

  Future<void> getPinpadStatus() {
    throw UnimplementedError('getPinpadStatus() has not been implemented.');
  }


}