import 'package:flutter/material.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';

// Import the card reader dialog
// (Assume this is saved in a file named card_reader_dialog.dart)
import 'card_reader_dialog.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Smart Pin Pad Cards Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key}) : super(key: key);

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool _dialogShown = false;
  bool _isProcessComplete = false;
  bool _isServiceRunning = false;

  @override
  void initState() {
    super.initState();
    // Service starts immediately when app opens
    _isServiceRunning = true;

    // Start the card reading process when the app opens
    // Slight delay to ensure the context is ready
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _showCardReaderDialog();
    });
  }

  Future<void> _showCardReaderDialog() async {
    if (_dialogShown) return;

    setState(() {
      _dialogShown = true;
    });

    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false, // User must respond to the dialog
      builder: (context) => const CardReaderDialog(),
    );

    setState(() {
      _isProcessComplete = result == true;
      _dialogShown = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Smart Pin Pad Cards Demo'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (_isProcessComplete) ...[
              const Icon(
                Icons.check_circle,
                color: Colors.green,
                size: 80,
              ),
              const SizedBox(height: 16),
              const Text(
                'PIN Verification Successful!',
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _showCardReaderDialog,
                child: const Text('Read Another Card'),
              ),
            ] else if (_dialogShown) ...[
              const CircularProgressIndicator(),
              const SizedBox(height: 16),
              const Text(
                'Card Reader Active',
                style: TextStyle(
                  fontSize: 20,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                'Please insert or swipe your card...',
                style: TextStyle(
                  fontSize: 16,
                ),
              ),
            ] else ...[
              Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Icon(
                    Icons.credit_card,
                    color: Colors.blue,
                    size: 32,
                  ),
                  const SizedBox(width: 12),
                  Text(
                    _isServiceRunning
                        ? 'Card Reader Service Running'
                        : 'Card Reader Service Inactive',
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),
              const Text(
                'Insert your card to begin the verification process',
                style: TextStyle(fontSize: 16),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 16),
              if (!_dialogShown)
                ElevatedButton(
                  onPressed: _showCardReaderDialog,
                  child: const Text('Open Card Reader Dialog'),
                ),
            ],
          ],
        ),
      ),
    );
  }
}