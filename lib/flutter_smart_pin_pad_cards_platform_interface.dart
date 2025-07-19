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

  // Card Reader Methods
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

  // PinPad Methods
  Future<void> initPinpad() {
    throw UnimplementedError('initPinpad() has not been implemented.');
  }

  Future<void> closePinpad() {
    throw UnimplementedError('closePinpad() has not been implemented.');
  }

  Future<void> getPinpadStatus() {
    throw UnimplementedError('getPinpadStatus() has not been implemented.');
  }

  Future<void> createPinBlock() {
    throw UnimplementedError('createPinBlock() has not been implemented.');
  }

  Future<void> verifyPin() {
    throw UnimplementedError('verifyPin() has not been implemented.');
  }

  Future<void> loadMainKey() {
    throw UnimplementedError('loadMainKey() has not been implemented.');
  }

  Future<void> loadWorkKey() {
    throw UnimplementedError('loadWorkKey() has not been implemented.');
  }

  Future<void> getKeyState() {
    throw UnimplementedError('getKeyState() has not been implemented.');
  }

  Future<void> getMac() {
    throw UnimplementedError('getMac() has not been implemented.');
  }

  Future<void> getRandom() {
    throw UnimplementedError('getRandom() has not been implemented.');
  }

  Future<void> changePin() {
    throw UnimplementedError('changePin() has not been implemented.');
  }

  Future<void> authorizePin() {
    throw UnimplementedError('authorizePin() has not been implemented.');
  }

  Future<void> createPin() {
    throw UnimplementedError('createPin() has not been implemented.');
  }
}
