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

  @override
  Future<Map<dynamic, dynamic>> createDynamicPinBlock() async {
    final result = await methodChannel.invokeMethod('createDynamicPinBlock');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<Map<dynamic, dynamic>> createPinDynamic() async {
    final result = await methodChannel.invokeMethod('createPinDynamic');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<Map<dynamic, dynamic>> changePinDynamic() async {
    final result = await methodChannel.invokeMethod('changePinDynamic');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<Map<dynamic, dynamic>> authorizePinDynamic() async {
    final result = await methodChannel.invokeMethod('authorizePinDynamic');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<Map<dynamic, dynamic>> testAllPinBlockFormats() async {
    final result = await methodChannel.invokeMethod('testAllPinBlockFormats');
    return Map<dynamic, dynamic>.from(result);
  }
  @override
  Future<Map<dynamic, dynamic>> createPinBlock() async {
    final result = await methodChannel.invokeMethod('createPinBlock');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<bool> initPinpad() async {
    await methodChannel.invokeMethod('initPinpad');
    return false;
  }

  @override
  Future<bool> closePinpad() async {
    await methodChannel.invokeMethod('closePinpad');
    return false;
  }

  @override
  Future<void> getPinpadStatus() async {
    await methodChannel.invokeMethod('getPinpadStatus');
  }


}