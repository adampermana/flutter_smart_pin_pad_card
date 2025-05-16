import 'package:flutter/material.dart';
import 'package:flutter_smart_pin_pad_cards/card_reader_exception.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';

class CardReaderDialog extends StatefulWidget {
  const CardReaderDialog({Key? key}) : super(key: key);

  @override
  State<CardReaderDialog> createState() => _CardReaderDialogState();
}

class _CardReaderDialogState extends State<CardReaderDialog> {
  String? _cardNumber;
  String? _expiryDate;
  String _errorMessage = '';
  bool _isReading = true;
  bool _isSubmitting = false;
  bool _oldPinEntered = false;

  final _oldPinController = TextEditingController();
  final _newPinController = TextEditingController();
  final _confirmPinController = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
    _startCardReading();
  }

  Future<void> _startCardReading() async {
    setState(() {
      _isReading = true;
      _errorMessage = '';
      _cardNumber = null;
      _expiryDate = null;
    });

    try {
      final result = await FlutterSmartPinPadCards.startInsertCardReading(
        enableMag: false,
        enableIcc: true,
        enableRf: false,
        timeout: 60000,
      );

      setState(() {
        _cardNumber = result['pan'] as String?;
        _expiryDate = result['expiryDate'] as String?;
        _isReading = false;
      });
    } on CardReaderException catch (e) {
      setState(() {
        _errorMessage = '${e.code}: ${e.message}';
        _isReading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Unexpected error: $e';
        _isReading = false;
      });
    }
  }

  void _verifyPins() {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isSubmitting = true;
      });

      Future.delayed(const Duration(seconds: 1), () {
        if (_newPinController.text != _confirmPinController.text) {
          setState(() {
            _errorMessage = 'New PIN and Confirm PIN must be the same';
            _isSubmitting = false;
          });
        } else {
          Navigator.of(context).pop(true);
        }
      });
    }
  }

  String _formatCardNumber(String? cardNumber) {
    if (cardNumber == null) return '';

    // Mask all but last 4 digits
    final lastFour = cardNumber.substring(cardNumber.length - 4);
    final masked = '•••• •••• •••• $lastFour';
    return masked;
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Row(
        children: [
          Icon(Icons.credit_card, color: Theme.of(context).primaryColor),
          const SizedBox(width: 10),
          const Text('Card Information'),
        ],
      ),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (_isReading) ...[
              const SizedBox(height: 20),
              Center(
                child: Column(
                  children: [
                    const CircularProgressIndicator(),
                    const SizedBox(height: 16),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Icon(Icons.info_outline, size: 16),
                        const SizedBox(width: 8),
                        Text(
                          'Reading card...',
                          style: TextStyle(
                            color: Theme.of(context).textTheme.bodyLarge?.color?.withOpacity(0.6),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ] else if (_errorMessage.isNotEmpty) ...[
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.red.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.error_outline, color: Colors.red),
                    const SizedBox(width: 10),
                    Expanded(
                      child: Text(
                        _errorMessage,
                        style: const TextStyle(color: Colors.red),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              Center(
                child: ElevatedButton.icon(
                  icon: const Icon(Icons.refresh),
                  label: const Text('Try Again'),
                  onPressed: _startCardReading,
                ),
              ),
            ] else if (_cardNumber != null) ...[
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Theme.of(context).primaryColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(
                    color: Theme.of(context).primaryColor.withOpacity(0.2),
                  ),
                ),
                child: Column(
                  children: [
                    Row(
                      children: [
                        const Icon(Icons.credit_card_outlined, size: 18),
                        const SizedBox(width: 8),
                        Text(
                          'Card Number:',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Theme.of(context).textTheme.bodyLarge?.color,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      _formatCardNumber(_cardNumber),
                      style: const TextStyle(fontSize: 16),
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        const Icon(Icons.date_range_outlined, size: 18),
                        const SizedBox(width: 8),
                        Text(
                          'Expiry Date:',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Theme.of(context).textTheme.bodyLarge?.color,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      _expiryDate ?? '',
                      style: const TextStyle(fontSize: 16),
                    ),
                  ],
                ),
              ),
              const Divider(height: 32),
              Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    if (!_oldPinEntered) ...[
                      const Text(
                        'Enter Your Current PIN',
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: _oldPinController,
                        decoration: InputDecoration(
                          labelText: 'Old PIN',
                          border: const OutlineInputBorder(),
                          prefixIcon: const Icon(Icons.lock_outline),
                          helperText: 'Enter your current 6-digit PIN',
                        ),
                        keyboardType: TextInputType.number,
                        obscureText: true,
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return 'Please enter old PIN';
                          } else if (value.length != 6) {
                            return 'PIN must be 6 digits';
                          }
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton.icon(
                          icon: const Icon(Icons.arrow_forward),
                          label: const Text('Lanjutkan'),
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 12),
                          ),
                          onPressed: () {
                            if (_oldPinController.text.isNotEmpty &&
                                _oldPinController.text.length == 6) {
                              setState(() {
                                _oldPinEntered = true;
                              });
                            }
                          },
                        ),
                      ),
                    ] else ...[
                      const Text(
                        'Create New PIN',
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: _newPinController,
                        decoration: InputDecoration(
                          labelText: 'New PIN',
                          border: const OutlineInputBorder(),
                          prefixIcon: const Icon(Icons.lock_outline),
                          helperText: 'Enter a new 6-digit PIN',
                        ),
                        keyboardType: TextInputType.number,
                        obscureText: true,
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return 'Please enter new PIN';
                          } else if (value.length != 6) {
                            return 'PIN must be 6 digits';
                          }
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _confirmPinController,
                        decoration: InputDecoration(
                          labelText: 'Confirm New PIN',
                          border: const OutlineInputBorder(),
                          prefixIcon: const Icon(Icons.lock_outline),
                          helperText: 'Re-enter your new 6-digit PIN',
                        ),
                        keyboardType: TextInputType.number,
                        obscureText: true,
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return 'Please confirm new PIN';
                          } else if (value.length != 6) {
                            return 'PIN must be 6 digits';
                          } else if (value != _newPinController.text) {
                            return 'PINs do not match';
                          }
                          return null;
                        },
                      ),
                      const SizedBox(height: 24),
                      SizedBox(
                        width: double.infinity,
                        child: _isSubmitting
                            ? ElevatedButton(
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 12),
                          ),
                          onPressed: null,
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: const [
                              SizedBox(
                                width: 20,
                                height: 20,
                                child: CircularProgressIndicator(
                                  strokeWidth: 2,
                                ),
                              ),
                              SizedBox(width: 10),
                              Text('Processing...'),
                            ],
                          ),
                        )
                            : ElevatedButton.icon(
                          icon: const Icon(Icons.check_circle_outline),
                          label: const Text('Submit'),
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 12),
                          ),
                          onPressed: _verifyPins,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
      actions: [
        TextButton.icon(
          icon: const Icon(Icons.cancel_outlined),
          label: const Text('Cancel'),
          onPressed: () => Navigator.of(context).pop(false),
        ),
      ],
    );
  }
}