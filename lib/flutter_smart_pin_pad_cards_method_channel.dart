import 'package:flutter/services.dart';
import 'flutter_smart_pin_pad_cards_platform_interface.dart';

class MethodChannelFlutterSmartPinPadCards extends FlutterSmartPinPadCardsPlatform {
  final methodChannel = const MethodChannel('flutter_smart_pin_pad_cards');

  @override
  Future<Map<dynamic, dynamic>> startSwipeCardReading() async {
    final result = await methodChannel.invokeMethod('startSwipeCardReading');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<void> stopSwipeCardReading() async {
    await methodChannel.invokeMethod('stopSwipeCardReading');
  }

  @override
  Future<Map<dynamic, dynamic>> startInsertCardReading() async {
    final result = await methodChannel.invokeMethod('startInsertCardReading');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<void> stopInsertCardReading() async {
    await methodChannel.invokeMethod('stopInsertCardReading');
  }

//   PinPad
  @override
  Future<void> initPinpad() async {
    await methodChannel.invokeMethod('initPinpad');
  }

  @override
  Future<void> closePinpad() async {
    await methodChannel.invokeMethod('closePinpad');
  }

  @override
  Future<void> getPinpadStatus() async {
    await methodChannel.invokeMethod('getPinpadStatus');
  }

  @override
  Future<void> createPinBlock() async {
    await methodChannel.invokeMethod('createPinBlock');
  }

  @override
  Future<void> verifyPin() async {
    await methodChannel.invokeMethod('verifyPin');
  }

  @override
  Future<void> loadMainKey() async {
    await methodChannel.invokeMethod('loadMainKey');
  }

  @override
  Future<void> loadWorkKey() async {
    await methodChannel.invokeMethod('loadWorkKey');
  }

  @override
  Future<void> getKeyState() async {
    await methodChannel.invokeMethod('getKeyState');
  }

  @override
  Future<void> getMac() async {
    await methodChannel.invokeMethod('getMac');
  }

  @override
  Future<void> getRandom() async {
    await methodChannel.invokeMethod('getRandom');
  }

  @override
  Future<void> changePin() async {
    await methodChannel.invokeMethod('changePin');
  }

  @override
  Future<void> authorizePin() async {
    await methodChannel.invokeMethod('authorizePin');
  }

  @override
  Future<void> createPin() async {
    await methodChannel.invokeMethod('createPin');
  }
}