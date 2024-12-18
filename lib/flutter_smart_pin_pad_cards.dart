
import 'flutter_smart_pin_pad_cards_platform_interface.dart';

class FlutterSmartPinPadCards {
  Future<String?> getPlatformVersion() {
    return FlutterSmartPinPadCardsPlatform.instance.getPlatformVersion();
  }
}
