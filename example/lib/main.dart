// example/lib/main.dart

import 'package:flutter/material.dart';
import 'package:flutter_smart_pin_pad_cards/card_reader_exception.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';

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
  Map<String, dynamic> _cardData = {};
  String _errorMessage = '';
  bool _isReading = false;

  // Start reading card with swipe method
  Future<void> _startSwipeCardReading() async {
    if (_isReading) return;

    setState(() {
      _isReading = true;
      _errorMessage = '';
      _cardData = {};
    });

    try {
      final result = await FlutterSmartPinPadCards.startSwipeCardReading();
      setState(() {
        _cardData = Map<String, dynamic>.from(result);
      });
    } on CardReaderException catch (e) {
      setState(() {
        _errorMessage = '${e.code}: ${e.message}';
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Unexpected error: $e';
      });
    } finally {
      setState(() {
        _isReading = false;
      });
    }
  }

  // Start reading inserted card
  Future<void> _startCardReading() async {
    if (_isReading) return;

    setState(() {
      _isReading = true;
      _errorMessage = '';
      _cardData = {};
    });

    try {
      final result = await FlutterSmartPinPadCards.startCardReading();
      setState(() {
        _cardData = Map<String, dynamic>.from(result);
      });
    } on CardReaderException catch (e) {
      setState(() {
        _errorMessage = '${e.code}: ${e.message}';
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Unexpected error: $e';
      });
    } finally {
      setState(() {
        _isReading = false;
      });
    }
  }

  // Stop card reading
  Future<void> _stopReading() async {
    if (!_isReading) return;

    try {
      await FlutterSmartPinPadCards.stopCardReading();
    } on CardReaderException catch (e) {
      setState(() {
        _errorMessage = '${e.code}: ${e.message}';
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Unexpected error: $e';
      });
    } finally {
      setState(() {
        _isReading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Smart Pin Pad Cards Demo'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Buttons
            ElevatedButton(
              onPressed: _isReading ? null : _startSwipeCardReading,
              child: Text(_isReading ? 'Reading...' : 'Start Swipe Card Reading'),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _isReading ? null : _startCardReading,
              child: Text(_isReading ? 'Reading...' : 'Read Inserted Card'),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _isReading ? _stopReading : null,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
              ),
              child: const Text('Stop Reading'),
            ),
            const SizedBox(height: 16),

            // Error message
            if (_errorMessage.isNotEmpty)
              Container(
                padding: const EdgeInsets.all(8),
                color: Colors.red[100],
                child: Text(
                  _errorMessage,
                  style: const TextStyle(color: Colors.red),
                ),
              ),

            const SizedBox(height: 16),

            // Card data display
            if (_cardData.isNotEmpty) ...[
              const Text(
                'Card Data:',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Expanded(
                child: Card(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: _cardData.entries.map((entry) {
                        return Padding(
                          padding: const EdgeInsets.symmetric(vertical: 4),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                entry.key,
                                style: const TextStyle(
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              Text(entry.value?.toString() ?? 'N/A'),
                              const Divider(),
                            ],
                          ),
                        );
                      }).toList(),
                    ),
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}