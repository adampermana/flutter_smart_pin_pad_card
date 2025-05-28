import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

// import 'package:flutter_localizations/flutter_localizations.dart';
import 'card_reader_dialog.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  // Set preferred orientations
  SystemChrome.setPreferredOrientations(
      [DeviceOrientation.portraitUp, DeviceOrientation.portraitDown]);

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Smart Pin Pad Demo',
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
      ),

      // localizationsDelegates: const [
      //   GlobalMaterialLocalizations.delegate,
      //   GlobalWidgetsLocalizations.delegate,
      //   GlobalCupertinoLocalizations.delegate,
      // ],
      supportedLocales: const [
        Locale('en'), // English
        Locale('id'), // Indonesian
      ],
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  bool _pinCreated = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(2),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const Icon(
                  Icons.credit_card,
                  size: 60,
                  color: Colors.blue,
                ),
                const SizedBox(height: 16),
                const Text(
                  'BANK JATENG',
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                _buildCardManagementWidget(),
              ],
            ),
          ),
        ),
      ),

    );
  }

  Widget _buildCardManagementWidget() {
    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _buildIconButtonWithText(
                  icon: Icons.add_circle_outline,
                  color: Colors.blue,
                  label: 'Create PIN',
                  onPressed: _showCreatePinDialog,
                ),
                _buildIconButtonWithText(
                  icon: Icons.loop,
                  color: Colors.green,
                  label: 'Change PIN',
                  onPressed: _showChangePinDialog,
                ),
              ],
            ),
            const SizedBox(height: 16),
            const Divider(),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: _pinCreated
                    ? Colors.green.withOpacity(0.1)
                    : Colors.orange.withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(
                  color: _pinCreated ? Colors.green : Colors.orange,
                  width: 1,
                ),
              ),
              child: Row(
                children: [
                  Icon(
                    _pinCreated ? Icons.check_circle : Icons.info_outline,
                    color: _pinCreated ? Colors.green : Colors.orange,
                    size: 20,
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      _pinCreated
                          ? 'PIN has been set up successfully'
                          : 'No PIN has been set up yet',
                      style: TextStyle(
                        color: _pinCreated
                            ? Colors.green.shade800
                            : Colors.orange.shade800,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            if (_pinCreated) ...[
              const SizedBox(height: 12),
              TextButton.icon(
                onPressed: () {
                  setState(() {
                    _pinCreated = false;
                  });
                },
                icon: const Icon(Icons.refresh, size: 16),
                label: const Text('Reset Demo'),
                style: TextButton.styleFrom(
                  foregroundColor: Colors.grey.shade700,
                  padding:
                      const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  minimumSize: const Size(0, 0),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildIconButtonWithText({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onPressed,
  }) {
    return Expanded(
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: onPressed,
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                height: 70,
                width: 70,
                decoration: BoxDecoration(
                  color: color,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  icon,
                  size: 40,
                  color: Colors.white,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                label,
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showCreatePinDialog() async {
    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => const CardReaderDialog(
        operationType: CardOperationType.createPin,
      ),
    );

    if (result == true) {
      setState(() {
        _pinCreated = true;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('PIN created successfully!'),
            backgroundColor: Colors.green,
          ),
        );
      }
    }
  }

  void _showChangePinDialog() async {
    if (!_pinCreated) {
      // Show message if PIN not created yet
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please create a PIN first'),
          backgroundColor: Colors.orange,
        ),
      );
      return;
    }

    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => const CardReaderDialog(
        operationType: CardOperationType.changePin,
      ),
    );

    if (result == true && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('PIN changed successfully!'),
          backgroundColor: Colors.green,
        ),
      );
    }
  }
}
