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
  Future<Map<dynamic, dynamic>> startCardReading() async {
    final result = await methodChannel.invokeMethod('startCardReading');
    return Map<dynamic, dynamic>.from(result);
  }

  @override
  Future<void> stopCardReading() async {
    await methodChannel.invokeMethod('stopCardReading');
  }
}