import 'dart:async';
import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';

import '../network_service.dart';

class AppBlocObserver extends BlocObserver {
  AppBlocObserver();

  @override
  void onChange(BlocBase<dynamic> bloc, Change<dynamic> change) {
    super.onChange(bloc, change);
    log('onChange(${bloc.runtimeType}, $change)');
  }

  @override
  void onError(BlocBase<dynamic> bloc, Object error, StackTrace stackTrace) {
    log('onError(${bloc.runtimeType}, $error, $stackTrace)');
    super.onError(bloc, error, stackTrace);
  }
}

Future<void> bootstrap(FutureOr<Widget> Function() builder) async {
  try {
    WidgetsFlutterBinding.ensureInitialized();
    // await FlutterSmartPinPadCards.initPinpad();

    // Optional: observer untuk BLoC (hanya saat debug)
    if (kDebugMode) {
      Bloc.observer = AppBlocObserver();
    }

    await SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);

    final app = await builder();
    runApp(app);

  } catch (error, stackTrace) {
    debugPrint('Fatal Error: $error');
    debugPrint('Stack Trace: $stackTrace');
  }
}
