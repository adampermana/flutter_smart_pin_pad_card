import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_smart_pin_pad_cards_platform_interface.dart';

/// An implementation of [FlutterSmartPinPadCardsPlatform] that uses method channels.
class MethodChannelFlutterSmartPinPadCards extends FlutterSmartPinPadCardsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_smart_pin_pad_cards');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
