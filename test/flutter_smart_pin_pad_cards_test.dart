// import 'package:flutter_test/flutter_test.dart';
// import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';
// import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards_platform_interface.dart';
// import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';
//
// class MockFlutterSmartPinPadCardsPlatform
//     with MockPlatformInterfaceMixin
//     implements FlutterSmartPinPadCardsPlatform {
//
//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }
//
// void main() {
//   final FlutterSmartPinPadCardsPlatform initialPlatform = FlutterSmartPinPadCardsPlatform.instance;
//
//   test('$MethodChannelFlutterSmartPinPadCards is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelFlutterSmartPinPadCards>());
//   });
//
//   test('getPlatformVersion', () async {
//     FlutterSmartPinPadCards flutterSmartPinPadCardsPlugin = FlutterSmartPinPadCards();
//     MockFlutterSmartPinPadCardsPlatform fakePlatform = MockFlutterSmartPinPadCardsPlatform();
//     FlutterSmartPinPadCardsPlatform.instance = fakePlatform;
//
//     expect(await flutterSmartPinPadCardsPlugin.getPlatformVersion(), '42');
//   });
// }
