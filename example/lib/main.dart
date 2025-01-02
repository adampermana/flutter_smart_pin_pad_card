import 'package:flutter/material.dart';
import 'package:flutter_smart_pin_pad_cards/card_reader_exception.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Smart Pin Pad Cards Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
      ),
      home: const CardReaderScreen(),
    );
  }
}

class CardReaderScreen extends StatefulWidget {
  const CardReaderScreen({super.key});

  @override
  State<CardReaderScreen> createState() => _CardReaderScreenState();
}

class _CardReaderScreenState extends State<CardReaderScreen> {
  final FlutterSmartPinPadCards _cardReader = FlutterSmartPinPadCards();
  bool _isReading = false;
  String _status = 'Ready to read card';
  Map<String, String> _cardData = {};
  String _errorMessage = '';

  Future<void> _startReading() async {
    setState(() {
      _isReading = true;
      _status = 'Waiting for card...';
      _errorMessage = '';
      _cardData = {};
    });

    try {
      final result = await _cardReader.startSwipeCardReading();
      setState(() {
        _cardData = Map<String, String>.from({
          'Card Number': result['cardNumber'] ?? '',
          'Expiry Date': result['expiryDate'] ?? '',
          'Service Code': result['serviceCode'] ?? '',
          'Track 1': result['firstTrack'] ?? '',
          'Track 2': result['secondTrack'] ?? '',
          'Track 3': result['thirdTrack'] ?? '',
        });
        _status = 'Card read successfully';
      });
    } on CardReaderException catch (e) {
      setState(() {
        _errorMessage = 'Error: ${e.message} (${e.code})';
        _status = 'Failed to read card';
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Unexpected error: $e';
        _status = 'Failed to read card';
      });
    } finally {
      setState(() {
        _isReading = false;
      });
    }
  }

  Future<void> _stopReading() async {
    try {
      await _cardReader.stopSwipeCardReading();
      setState(() {
        _isReading = false;
        _status = 'Card reading stopped';
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Error stopping card reader: $e';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Card Reader Demo'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Status Card
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    Text(
                      _status,
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    if (_errorMessage.isNotEmpty)
                      Padding(
                        padding: const EdgeInsets.only(top: 8.0),
                        child: Text(
                          _errorMessage,
                          style: TextStyle(
                            color: Theme.of(context).colorScheme.error,
                          ),
                        ),
                      ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Action Buttons
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: _isReading ? null : _startReading,
                    child: const Text('Start Reading'),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: ElevatedButton(
                    onPressed: _isReading ? _stopReading : null,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Theme.of(context).colorScheme.error,
                      foregroundColor: Colors.white,
                    ),
                    child: const Text('Stop Reading'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // Card Data Display
            if (_cardData.isNotEmpty) ...[
              Text(
                'Card Data:',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 8),
              Expanded(
                child: Card(
                  child: ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _cardData.length,
                    separatorBuilder: (context, index) => const Divider(),
                    itemBuilder: (context, index) {
                      final key = _cardData.keys.elementAt(index);
                      final value = _cardData[key];
                      return Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Expanded(
                            flex: 2,
                            child: Text(
                              key,
                              style: const TextStyle(fontWeight: FontWeight.bold),
                            ),
                          ),
                          Expanded(
                            flex: 3,
                            child: Text(value ?? 'N/A'),
                          ),
                        ],
                      );
                    },
                  ),
                ),
              ),
            ],

            // Loading Indicator
            if (_isReading)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 16.0),
                child: Center(
                  child: CircularProgressIndicator(),
                ),
              ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _stopReading();
    super.dispose();
  }
}