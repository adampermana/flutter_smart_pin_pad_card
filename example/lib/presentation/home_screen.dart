// Updated Home Screen with Bloc Integration
import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../business_logic/auth/auth_bloc.dart';
import '../business_logic/cardoperation/card_operation_bloc.dart';
import '../business_logic/network/network_bloc.dart';
import '../card_reader_dialog.dart';
import '../enum/pinpad.dart';
import '../network_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> with WidgetsBindingObserver {
  // Statistics
  int _totalTransactions = 0;
  int _successfulTransactions = 0;
  String _lastTransactionTime = '';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);

    // Check capabilities on home screen load
    context.read<NetworkBloc>().add(const NetworkEvent.checkCapabilities());

    // Validate working key periodically
    _startWorkingKeyValidation();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    if (state == AppLifecycleState.resumed) {
      context.read<NetworkBloc>().add(const NetworkEvent.checkCapabilities());
    }
  }

  void _startWorkingKeyValidation() {
    // Validate working key every 5 minutes
    Timer.periodic(const Duration(minutes: 5), (timer) {
      final authState = context.read<AuthBloc>().state;
      authState.maybeWhen(
        authenticated: (terminalId, workingKey) {
          context.read<AuthBloc>().add(
            AuthEvent.workingKeyValidation(workingKey: workingKey),
          );
        },
        orElse: () => timer.cancel(),
      );
    });
  }

  bool _isOperationSupported(String processingCode, NetworkState networkState) {
    return networkState.supportedOperations.contains(processingCode);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Bank JATENG'),
        backgroundColor: const Color(0xFF0D4575),
        foregroundColor: Colors.white,
        elevation: 2,
        actions: [
          BlocBuilder<NetworkBloc, NetworkState>(
            builder: (context, state) {
              return IconButton(
                icon: Icon(state.isConnected ? Icons.cloud_done : Icons.cloud_off),
                onPressed: () {
                  _showServerStatusDialog(state);
                },
                tooltip: state.isConnected ? 'Server Connected' : 'Server Disconnected',
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              context.read<NetworkBloc>().add(const NetworkEvent.checkCapabilities());
            },
            tooltip: 'Refresh Capabilities',
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _showLogoutDialog,
          ),
        ],
      ),
      body: SafeArea(
        child: _buildMainContent(),
      ),
    );
  }

  Widget _buildMainContent() {
    return RefreshIndicator(
      onRefresh: () async {
        context.read<NetworkBloc>().add(const NetworkEvent.checkCapabilities());
      },
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(24),
        child: Column(
          children: [
            // Server Status Card
            BlocBuilder<NetworkBloc, NetworkState>(
              builder: (context, state) {
                if (state.isCheckingCapabilities) {
                  return _buildCapabilityCheckCard(state);
                }
                return const SizedBox.shrink();
              },
            ),

            // PIN Operations Card
            BlocBuilder<NetworkBloc, NetworkState>(
              builder: (context, state) {
                return _buildPinOperationsCard(state);
              },
            ),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }

  Widget _buildCapabilityCheckCard(NetworkState networkState) {
    return Card(
      color: Colors.blue.shade50,
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            Row(
              children: [
                const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    networkState.capabilityStatus,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                      overflow: TextOverflow.ellipsis,
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            const Text(
              'Checking which PIN operations are supported by the server...',
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPinOperationsCard(NetworkState networkState) {
    return Card(
      color: Colors.grey.shade50,
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                const Text(
                  'PIN OPERATIONS',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF0D4575),
                  ),
                ),
                if (!networkState.isCheckingCapabilities)
                  Text(
                    networkState.capabilityStatus,
                    style: TextStyle(
                      fontSize: 9,
                      color: networkState.supportedOperations.isEmpty ? Colors.red : Colors.green,
                      fontWeight: FontWeight.w500,
                      overflow: TextOverflow.ellipsis,
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
              ],
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _buildOperationButton(
                  icon: Icons.add_circle_outline,
                  color: Colors.blue,
                  label: 'Create PIN',
                  subtitle: 'Buat PIN baru',
                  processingCode: '920000',
                  networkState: networkState,
                  onPressed: () {
                    _showCreatePinDialog();
                  },
                ),
                _buildOperationButton(
                  icon: Icons.loop,
                  color: Colors.green,
                  label: 'Change PIN',
                  subtitle: 'Ubah PIN lama',
                  processingCode: '930000',
                  networkState: networkState,
                  onPressed: () {
                    _showChangePinDialog();
                  },
                ),
                _buildOperationButton(
                  icon: Icons.security,
                  color: Colors.orange,
                  label: 'Otorisasi PIN',
                  subtitle: 'Verifikasi PIN',
                  processingCode: '940000',
                  networkState: networkState,
                  onPressed: () {
                    _showOtorisasiDialog();
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildOperationButton({
    required IconData icon,
    required String label,
    required String subtitle,
    required Color color,
    required String processingCode,
    required NetworkState networkState,
    required VoidCallback onPressed,
  }) {
    final isSupported = _isOperationSupported(processingCode, networkState);
    final isEnabled = isSupported && !networkState.isCheckingCapabilities;

    return Expanded(
      child: Stack(
        children: [
          InkWell(
            borderRadius: BorderRadius.circular(12),
            onTap: isEnabled ? onPressed : () {
              _showUnsupportedOperationDialog(label, processingCode);
            },
            child: Container(
              padding: const EdgeInsets.all(12),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Container(
                    height: 70,
                    width: 70,
                    decoration: BoxDecoration(
                      color: isEnabled ? color : Colors.grey,
                      borderRadius: BorderRadius.circular(12),
                      boxShadow: [
                        BoxShadow(
                          color: (isEnabled ? color : Colors.grey).withOpacity(0.3),
                          blurRadius: 8,
                          offset: const Offset(0, 4),
                        ),
                      ],
                    ),
                    child: Icon(
                      icon,
                      size: 32,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 12),
                  Text(
                    label,
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                      color: isEnabled ? Colors.black : Colors.grey,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    subtitle,
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      fontSize: 10,
                      color: isEnabled ? Colors.grey.shade600 : Colors.grey.shade400,
                    ),
                  ),
                ],
              ),
            ),
          ),
          if (!isSupported && !networkState.isCheckingCapabilities)
            Positioned(
              top: 8,
              right: 8,
              child: Container(
                padding: const EdgeInsets.all(2),
                decoration: const BoxDecoration(
                  color: Colors.red,
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.block,
                  size: 16,
                  color: Colors.white,
                ),
              ),
            ),
          if (networkState.isCheckingCapabilities)
            Positioned(
              top: 8,
              right: 8,
              child: Container(
                padding: const EdgeInsets.all(2),
                decoration: const BoxDecoration(
                  color: Colors.orange,
                  shape: BoxShape.circle,
                ),
                child: const SizedBox(
                  width: 12,
                  height: 12,
                  child: CircularProgressIndicator(
                    strokeWidth: 2,
                    valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildStatItem(String label, String value, Color color) {
    return Column(
      children: [
        Container(
          width: 60,
          height: 60,
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: color.withOpacity(0.3)),
          ),
          child: Center(
            child: Text(
              value,
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
          ),
        ),
        const SizedBox(height: 8),
        Text(
          label,
          textAlign: TextAlign.center,
          style: const TextStyle(
            fontSize: 11,
            fontWeight: FontWeight.w500,
            color: Colors.grey,
          ),
        ),
      ],
    );
  }

  void _showUnsupportedOperationDialog(String operationName, String processingCode) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Row(
          children: [
            const Icon(Icons.block, color: Colors.red),
            const SizedBox(width: 8),
            const Text('Operation Not Supported'),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('The server does not support "$operationName" operation.'),
            const SizedBox(height: 8),
            Text('Processing Code: $processingCode'),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.red.withOpacity(0.1),
                borderRadius: BorderRadius.circular(4),
                border: Border.all(color: Colors.red.withOpacity(0.3)),
              ),
              child: const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Possible reasons:',
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
                  ),
                  SizedBox(height: 4),
                  Text(
                    '• Server configuration disabled this operation',
                    style: TextStyle(fontSize: 12),
                  ),
                  Text(
                    '• Processing code not configured in server',
                    style: TextStyle(fontSize: 12),
                  ),
                  Text(
                    '• Server returned Format Error (30) for this request',
                    style: TextStyle(fontSize: 12),
                  ),
                ],
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Close'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              context.read<NetworkBloc>().add(const NetworkEvent.checkCapabilities());
            },
            child: const Text('Retry Check'),
          ),
        ],
      ),
    );
  }

  // PIN Dialog Methods with Bloc integration
  void _showCreatePinDialog() async {
    final networkState = context.read<NetworkBloc>().state;
    if (!_isOperationSupported('920000', networkState)) {
      _showUnsupportedOperationDialog('Create PIN', '920000');
      return;
    }

    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => BlocProvider.value(
        value: context.read<CardOperationBloc>(),
        child: const CardReaderDialog(
          operationType: CardOperationType.createPin,
        ),
      ),
    );

    setState(() {
      _totalTransactions++;
      if (result == true) {
        _successfulTransactions++;
        _lastTransactionTime = _formatCurrentTime();
      }
    });

    if (result == true && mounted) {
      _showSnackBar('PIN created successfully!', Colors.green);
    } else if (result == false && mounted) {
      _showSnackBar('PIN creation failed', Colors.red);
    }
  }

  void _showChangePinDialog() async {
    final networkState = context.read<NetworkBloc>().state;
    if (!_isOperationSupported('930000', networkState)) {
      _showUnsupportedOperationDialog('Change PIN', '930000');
      return;
    }

    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => BlocProvider.value(
        value: context.read<CardOperationBloc>(),
        child: const CardReaderDialog(
          operationType: CardOperationType.changePin,
        ),
      ),
    );

    setState(() {
      _totalTransactions++;
      if (result == true) {
        _successfulTransactions++;
        _lastTransactionTime = _formatCurrentTime();
      }
    });

    if (result == true && mounted) {
      _showSnackBar('PIN changed successfully!', Colors.green);
    } else if (result == false && mounted) {
      _showSnackBar('PIN change failed', Colors.red);
    }
  }

  void _showOtorisasiDialog() async {
    final networkState = context.read<NetworkBloc>().state;
    if (!_isOperationSupported('940000', networkState)) {
      _showUnsupportedOperationDialog('PIN Authorization', '940000');
      return;
    }

    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => BlocProvider.value(
        value: context.read<CardOperationBloc>(),
        child: const CardReaderDialog(
          operationType: CardOperationType.otorisation,
        ),
      ),
    );

    setState(() {
      _totalTransactions++;
      if (result == true) {
        _successfulTransactions++;
        _lastTransactionTime = _formatCurrentTime();
      }
    });

    if (result == true && mounted) {
      _showSnackBar('PIN authorization successful!', Colors.green);
    } else if (result == false && mounted) {
      _showSnackBar('PIN authorization failed', Colors.red);
    }
  }

  String _formatCurrentTime() {
    final now = DateTime.now();
    return '${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}';
  }

  void _showServerStatusDialog(NetworkState networkState) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Row(
          children: [
            Icon(
              networkState.isConnected ? Icons.cloud_done : Icons.cloud_off,
              color: networkState.isConnected ? Colors.green : Colors.red,
            ),
            const SizedBox(width: 8),
            Text(
              'Server Status',
              style: TextStyle(
                color: networkState.isConnected ? Colors.green : Colors.red,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Host: ${NetworkService.serverHost}'),
            Text('Port: ${NetworkService.serverPort}'),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: (networkState.isConnected ? Colors.green : Colors.red).withOpacity(0.1),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                networkState.isConnected ? 'Connected' : 'Disconnected',
                style: TextStyle(
                  color: networkState.isConnected ? Colors.green : Colors.red,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const SizedBox(height: 12),
            const Text('Supported Operations:', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            ...networkState.supportedOperations.map((code) {
              String operationName;
              switch (code) {
                case '920000': operationName = 'Create PIN'; break;
                case '930000': operationName = 'Change PIN'; break;
                case '940000': operationName = 'PIN Authorization'; break;
                default: operationName = 'Unknown';
              }
              return Row(
                children: [
                  const Icon(Icons.check, color: Colors.green, size: 16),
                  const SizedBox(width: 4),
                  Text('$operationName ($code)'),
                ],
              );
            }).toList(),
            if (networkState.supportedOperations.isEmpty)
              const Row(
                children: [
                  Icon(Icons.error, color: Colors.red, size: 16),
                  SizedBox(width: 4),
                  Text('No operations supported'),
                ],
              ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () async {
              Navigator.pop(context);
              context.read<NetworkBloc>().add(const NetworkEvent.checkCapabilities());
            },
            child: const Text('Refresh'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Close'),
          ),
        ],
      ),
    );
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
          action: SnackBarAction(
            label: 'OK',
            textColor: Colors.white,
            onPressed: () {
              ScaffoldMessenger.of(context).hideCurrentSnackBar();
            },
          ),
        ),
      );
    }
  }

  void _showLogoutDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text(
          'LOGOFF',
          style: TextStyle(
            color: Color(0xFF0D4575),
            fontWeight: FontWeight.bold,
          ),
        ),
        content: const Text('Are you sure you want to LOGOFF?'),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              _handleLogout();
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0D4575),
              foregroundColor: Colors.white,
            ),
            child: const Text('LOGOFF'),
          ),
        ],
      ),
    );
  }

  void _handleLogout() async {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 12),
              decoration: BoxDecoration(
                color: const Color(0xFF0D4575),
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Text(
                'LOGOFF',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ),
            const SizedBox(height: 20),
            const Text(
              'CONNECTING\nPROCESSING',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            const CircularProgressIndicator(
              valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF0D4575)),
            ),
            const SizedBox(height: 16),
            const Text(
              'Terminal sedang melakukan logoff dari server',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );

    await Future.delayed(const Duration(seconds: 2));

    if (mounted) {
      Navigator.pop(context); // Close loading dialog
      context.read<AuthBloc>().add(const AuthEvent.logoutRequested());
    }
  }
}