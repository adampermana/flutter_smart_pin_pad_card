import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';

import '../../business_logic/auth/auth_bloc.dart';
import '../../business_logic/cardoperation/card_operation_bloc.dart';
import '../../business_logic/network/network_bloc.dart';
import '../../network_service.dart';
import '../home_screen.dart';
import '../login_page.dart';
import '../splash_screen.dart';

class BankJatengApp extends StatelessWidget {
  const BankJatengApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(create: (context) => AuthBloc()),
        BlocProvider(create: (context) => NetworkBloc()),
        BlocProvider(create: (context) => CardOperationBloc()),
      ],
      child: MaterialApp(
        title: 'Bank Jateng PIN System',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          primarySwatch: const MaterialColor(
            0xFF0D4575,
            <int, Color>{
              50: Color(0xFFE3EAF0),
              100: Color(0xFFB9CADB),
              200: Color(0xFF8BA6C3),
              300: Color(0xFF5D82AB),
              400: Color(0xFF3B6799),
              500: Color(0xFF0D4575),
              600: Color(0xFF0B3E6A),
              700: Color(0xFF08355C),
              800: Color(0xFF062D4F),
              900: Color(0xFF031E39),
            },
          ),
          useMaterial3: true,
          fontFamily: 'Roboto',
        ),
        supportedLocales: const [
          Locale('en'), // English
          Locale('id'), // Indonesian
        ],
        home: const AppWrapper(),
        routes: {
          '/splash': (context) => const SplashScreen(),
          '/login': (context) => const LoginPage(),
          '/home': (context) => const HomeScreen(),
        },
      ),
    );
  }
}

// App Wrapper to handle authentication state
class AppWrapper extends StatelessWidget {
  const AppWrapper({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthBloc, AuthState>(
      listener: (context, state) {
        state.maybeWhen(
          sessionExpired: () {
            // Navigate to login when session expires
            Navigator.of(context).pushNamedAndRemoveUntil(
              '/login',
                  (route) => false,
            );
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Session expired. Please login again.'),
                backgroundColor: Colors.orange,
              ),
            );
          },
          orElse: () {},
        );
      },
      child: BlocBuilder<AuthBloc, AuthState>(
        builder: (context, state) {
          return state.when(
            initial: () {
              // Check session on app start
              context.read<AuthBloc>().add(const AuthEvent.checkSession());
              return const SplashScreen();
            },
            loading: () => const SplashScreen(),
            authenticated: (terminalId, workingKey) => const HomeScreen(),
            unauthenticated: () => const LoginPage(),
            sessionExpired: () => const LoginPage(),
            error: (message) => const LoginPage(),
          );
        },
      ),
    );
  }
}