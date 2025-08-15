import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../business_logic/auth/auth_bloc.dart';
import '../business_logic/network/network_bloc.dart';
import '../network_service.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _passwordController = TextEditingController();
  final FocusNode _passwordFocus = FocusNode();
  bool _isObscured = true;

  @override
  void initState() {
    super.initState();
    // Check network capabilities on login page load
    context.read<NetworkBloc>().add(const NetworkEvent.checkConnection());
  }

  @override
  void dispose() {
    _passwordController.dispose();
    _passwordFocus.dispose();
    super.dispose();
  }

  void _handleLogin() {
    if (_passwordController.text.isEmpty) {
      _showSnackBar('Please enter password', Colors.orange);
      return;
    }

    context.read<AuthBloc>().add(AuthEvent.loginRequested(
      terminalId: 'T3000001',
      password: _passwordController.text,
    ));
  }

  void _showSnackBar(String message, Color color) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(message),
          backgroundColor: color,
          behavior: SnackBarBehavior.floating,
          margin: const EdgeInsets.all(16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Card(
                  elevation: 8,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const Text(
                          'LOGON',
                          style: TextStyle(
                            color: Color(0xFF0D4575),
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 20),

                        // Auth Bloc Builder
                        BlocConsumer<AuthBloc, AuthState>(
                          listener: (context, state) {
                            state.maybeWhen(
                              authenticated: (terminalId, workingKey) {
                                _showSnackBar('Logon successful!', Colors.green);
                                Navigator.pushReplacementNamed(context, '/home');
                              },
                              error: (message) {
                                _showSnackBar(message, Colors.red);
                                _passwordController.clear();
                                _passwordFocus.requestFocus();
                              },
                              orElse: () {},
                            );
                          },
                          builder: (context, authState) {
                            return authState.maybeWhen(
                              loading: () => const Column(
                                children: [
                                  Text(
                                    'CONNECTING\nPROCESSING',
                                    style: TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.bold,
                                    ),
                                    textAlign: TextAlign.center,
                                  ),
                                  SizedBox(height: 16),
                                  CircularProgressIndicator(
                                    valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF0D4575)),
                                  ),
                                  SizedBox(height: 16),
                                  Text(
                                    'Terminal sedang melakukan koneksi dengan server',
                                    style: TextStyle(fontSize: 14, color: Colors.grey),
                                    textAlign: TextAlign.center,
                                  ),
                                ],
                              ),
                              orElse: () => Column(
                                children: [
                                  const Align(
                                    alignment: Alignment.centerLeft,
                                    child: Text(
                                      'INPUT PASSWORD',
                                      style: TextStyle(
                                        fontSize: 14,
                                        fontWeight: FontWeight.bold,
                                        color: Colors.grey,
                                      ),
                                    ),
                                  ),
                                  const SizedBox(height: 8),
                                  TextField(
                                    controller: _passwordController,
                                    focusNode: _passwordFocus,
                                    obscureText: _isObscured,
                                    keyboardType: TextInputType.number,
                                    decoration: InputDecoration(
                                      hintText: '****',
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(8),
                                      ),
                                      focusedBorder: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(8),
                                        borderSide: const BorderSide(
                                            width: 2,
                                            color: Color(0xFF0D4575)
                                        ),
                                      ),
                                      contentPadding: const EdgeInsets.symmetric(
                                        horizontal: 16,
                                        vertical: 12,
                                      ),
                                      prefixIcon: const Icon(Icons.lock_outline),
                                      suffixIcon: IconButton(
                                        icon: Icon(
                                          _isObscured ? Icons.visibility : Icons.visibility_off,
                                        ),
                                        onPressed: () {
                                          setState(() {
                                            _isObscured = !_isObscured;
                                          });
                                        },
                                      ),
                                    ),
                                    onSubmitted: (_) => _handleLogin(),
                                  ),
                                  const SizedBox(height: 8),
                                  const Align(
                                    alignment: Alignment.centerLeft,
                                    child: Text(
                                      'Input password untuk logon',
                                      style: TextStyle(fontSize: 12, color: Colors.grey),
                                    ),
                                  ),
                                  const SizedBox(height: 20),
                                  SizedBox(
                                    width: double.infinity,
                                    child: ElevatedButton(
                                      onPressed: _handleLogin,
                                      style: ElevatedButton.styleFrom(
                                        backgroundColor: const Color(0xFF0D4575),
                                        foregroundColor: Colors.white,
                                        padding: const EdgeInsets.symmetric(vertical: 16),
                                        shape: RoundedRectangleBorder(
                                          borderRadius: BorderRadius.circular(8),
                                        ),
                                        elevation: 2,
                                      ),
                                      child: const Text(
                                        'LOGON',
                                        style: TextStyle(
                                          fontSize: 16,
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    ),
                                  ),
                                  const SizedBox(height: 16),

                                  // Network Status Card
                                  // BlocBuilder<NetworkBloc, NetworkState>(
                                  //   builder: (context, networkState) {
                                  //     return Container(
                                  //       padding: const EdgeInsets.all(12),
                                  //       decoration: BoxDecoration(
                                  //         color: Colors.grey.shade100,
                                  //         borderRadius: BorderRadius.circular(8),
                                  //         border: Border.all(color: Colors.grey.shade300),
                                  //       ),
                                  //       child: Column(
                                  //         children: [
                                  //           const Text(
                                  //             'Default password: 1234',
                                  //             style: TextStyle(
                                  //               fontSize: 12,
                                  //               color: Colors.grey,
                                  //               fontStyle: FontStyle.italic,
                                  //             ),
                                  //           ),
                                  //           const SizedBox(height: 8),
                                  //           Text(
                                  //             '${networkState.isConnected ? '✅' : '❌'} Server: ${NetworkService.serverHost}:${NetworkService.serverPort}',
                                  //             style: const TextStyle(
                                  //               fontSize: 11,
                                  //               fontWeight: FontWeight.w500,
                                  //             ),
                                  //             textAlign: TextAlign.center,
                                  //           ),
                                  //           const SizedBox(height: 8),
                                  //           ElevatedButton.icon(
                                  //             onPressed: networkState.isCheckingConnection ? null : () {
                                  //               context.read<NetworkBloc>().add(
                                  //                 const NetworkEvent.checkConnection(),
                                  //               );
                                  //             },
                                  //             icon: networkState.isCheckingConnection
                                  //                 ? const SizedBox(
                                  //               width: 16,
                                  //               height: 16,
                                  //               child: CircularProgressIndicator(strokeWidth: 2),
                                  //             )
                                  //                 : const Icon(Icons.refresh, size: 16),
                                  //             label: const Text('Test System'),
                                  //             style: ElevatedButton.styleFrom(
                                  //               backgroundColor: Colors.grey.shade600,
                                  //               foregroundColor: Colors.white,
                                  //               padding: const EdgeInsets.symmetric(
                                  //                   horizontal: 12,
                                  //                   vertical: 8
                                  //               ),
                                  //               minimumSize: const Size(0, 0),
                                  //             ),
                                  //           ),
                                  //         ],
                                  //       ),
                                  //     );
                                  //   },
                                  // ),
                                ],
                              ),
                            );
                          },
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}