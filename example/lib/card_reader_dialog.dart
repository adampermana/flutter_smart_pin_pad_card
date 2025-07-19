import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';
import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';
import 'package:lottie/lottie.dart';

enum CardOperationType { createPin, changePin }

enum CardOperationStep {
  insertCard,
  loading,
  cardInfo,
  enterCurrentPin, // Untuk PIN Change - masukkan PIN lama
  createPin,       // Untuk PIN Create dan PIN Change (PIN baru)
  confirmPin,      // Konfirmasi PIN
  connecting,
  success,
  failed
}

class CardReaderDialog extends StatefulWidget {
  final CardOperationType operationType;

  const CardReaderDialog({
    Key? key,
    required this.operationType,
  }) : super(key: key);

  @override
  State<CardReaderDialog> createState() => _CardReaderDialogState();
}

class _CardReaderDialogState extends State<CardReaderDialog> {
  CardOperationStep _currentStep = CardOperationStep.insertCard;
  CardData? _cardData;
  String _errorMessage = '';
  bool _isLoading = false;

  // PIN entry
  final TextEditingController _currentPinController = TextEditingController(); // PIN lama untuk Change PIN
  final TextEditingController _pinController = TextEditingController(); // PIN baru
  final TextEditingController _confirmPinController = TextEditingController(); // Konfirmasi PIN baru

  final FocusNode _currentPinFocusNode = FocusNode();
  final FocusNode _pinFocusNode = FocusNode();
  final FocusNode _confirmPinFocusNode = FocusNode();

  // Timers
  Timer? _pinEntryTimer;
  Timer? _successTimer;
  int _remainingPinTime = 30;
  int _remainingSuccessTime = 10;

  @override
  void initState() {
    super.initState();
    // Langsung mulai pembacaan kartu
    _startCardReading();
  }

  @override
  void dispose() {
    _pinEntryTimer?.cancel();
    _successTimer?.cancel();

    _currentPinController.dispose();
    _pinController.dispose();
    _confirmPinController.dispose();

    _currentPinFocusNode.dispose();
    _pinFocusNode.dispose();
    _confirmPinFocusNode.dispose();

    // Make sure to stop card reading on dispose to free resources
    FlutterSmartPinPadCards.stopCardReading();
    super.dispose();
  }

  // Start reading the card
  Future<void> _startCardReading() async {
    setState(() {
      _currentStep = CardOperationStep.insertCard;
      _isLoading = true;
    });

    try {
      // Start the card reading process with all options enabled
      final cardData = await FlutterSmartPinPadCards.startInsertCardReading(
        enableMag: true,
        enableIcc: true,
        enableRf: false,
        timeout: 60000, // 60 seconds timeout
      );

      setState(() {
        _isLoading = false;
        _cardData = cardData;
        _currentStep = CardOperationStep.cardInfo;
      });
    } catch (e) {
      setState(() {
        _isLoading = false;
        _errorMessage = e.toString();
        _currentStep = CardOperationStep.failed;
      });

      // Stop card reading on error
      await FlutterSmartPinPadCards.stopCardReading();
    }
  }

  // Proceed after card info confirmation
  void _onCardInfoConfirmed() {
    if (widget.operationType == CardOperationType.createPin) {
      // For PIN Create, go directly to create PIN step
      _startPinCreation();
    } else {
      // For PIN Change, start with entering current PIN
      _startEnterCurrentPin();
    }
  }

  // Start entering current PIN (for PIN Change only)
  void _startEnterCurrentPin() {
    setState(() {
      _currentStep = CardOperationStep.enterCurrentPin;
      _remainingPinTime = 30;
    });

    // Start a 30-second timer for PIN entry with countdown
    _pinEntryTimer?.cancel();
    _pinEntryTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_remainingPinTime > 0) {
          _remainingPinTime--;
        } else {
          timer.cancel();
          if (mounted) {
            // Show timeout message and close
            _showTimeoutErrorAndClose('Current PIN entry timed out.');
          }
        }
      });
    });

    // Focus the current PIN field for immediate input
    _currentPinFocusNode.requestFocus();
  }

  // Move to PIN creation screen (new PIN for both Create and Change)
  void _startPinCreation() {
    setState(() {
      _currentStep = CardOperationStep.createPin;
      _remainingPinTime = 30;
    });

    // Start a 30-second timer for PIN entry with countdown
    _pinEntryTimer?.cancel();
    _pinEntryTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_remainingPinTime > 0) {
          _remainingPinTime--;
        } else {
          timer.cancel();
          if (mounted) {
            // Show timeout message and close
            _showTimeoutErrorAndClose('PIN entry timed out.');
          }
        }
      });
    });

    // Focus the PIN field for immediate input
    _pinFocusNode.requestFocus();
  }

  // Move to PIN confirmation screen
  void _confirmPin() {
    // Cancel existing timer
    _pinEntryTimer?.cancel();

    setState(() {
      _currentStep = CardOperationStep.confirmPin;
      _remainingPinTime = 30;
    });

    // Start new 30-second timer for PIN confirmation with countdown
    _pinEntryTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_remainingPinTime > 0) {
          _remainingPinTime--;
        } else {
          timer.cancel();
          if (mounted) {
            _showTimeoutErrorAndClose('PIN confirmation timed out.');
          }
        }
      });
    });

    // Focus the confirmation PIN field
    _confirmPinFocusNode.requestFocus();
  }

  // Show timeout error dialog and close
  void _showTimeoutErrorAndClose(String message) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Text('Timeout'),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop(); // Close the alert
              _cancelOperation(); // Close the main dialog
            },
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  // Process the PIN (submit to backend)
  Future<void> _processPin() async {
    _pinEntryTimer?.cancel();

    // Validate PIN match if this is confirmation step
    if (_currentStep == CardOperationStep.confirmPin &&
        _pinController.text != _confirmPinController.text) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('PINs do not match. Please try again.')),
      );
      // Clear confirmation field and stay on confirmation screen
      _confirmPinController.clear();
      _confirmPinFocusNode.requestFocus();

      // Restart the timeout timer
      setState(() {
        _remainingPinTime = 30;
      });
      _pinEntryTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
        setState(() {
          if (_remainingPinTime > 0) {
            _remainingPinTime--;
          } else {
            timer.cancel();
            if (mounted) {
              _showTimeoutErrorAndClose('PIN confirmation timed out.');
            }
          }
        });
      });

      return;
    }

    setState(() {
      _currentStep = CardOperationStep.connecting;
    });

    // Simulate pin processing with a delay
    // In real app, replace with actual API call
    await Future.delayed(const Duration(seconds: 2));

    // Simulate success (replace with actual success/failure logic)
    setState(() {
      _currentStep = CardOperationStep.success;
      _remainingSuccessTime = 10;
    });

    // Start timer with countdown for success screen
    _successTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_remainingSuccessTime > 0) {
          _remainingSuccessTime--;
        } else {
          timer.cancel();
          if (mounted) {
            Navigator.of(context).pop(true); // Return success
          }
        }
      });
    });
  }

  // Handle current PIN submission (PIN Change flow)
  void _onCurrentPinSubmitted() {
    if (_currentPinController.text.length != 6) {
      return; // PIN must be 6 digits
    }

    // Here you would validate the current PIN with your backend
    // For demo, we just proceed to the new PIN creation step

    _pinEntryTimer?.cancel(); // Cancel the current timer
    _startPinCreation(); // Move to creating a new PIN
  }

  // Cancel the operation and close the dialog
  void _cancelOperation() async {
    _pinEntryTimer?.cancel();
    _successTimer?.cancel();

    // Stop card reading
    await FlutterSmartPinPadCards.stopCardReading();

    if (mounted) {
      Navigator.of(context).pop(false); // Return failure
    }
  }

  @override
  Widget build(BuildContext context) {
    // Determine dialog title based on operation type and current step
    String dialogTitle = widget.operationType == CardOperationType.createPin
        ? 'Create PIN'
        : 'Change PIN';

    if (_currentStep == CardOperationStep.success) {
      dialogTitle = 'Success';
    } else if (_currentStep == CardOperationStep.failed) {
      dialogTitle = 'Error';
    }

    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      insetPadding: const EdgeInsets.symmetric(horizontal: 24, vertical: 24),
      child: ConstrainedBox(
        constraints: const BoxConstraints(
          maxWidth: 400,
          maxHeight: 600,
        ),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                dialogTitle,
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              Expanded(
                child: SingleChildScrollView(
                  child: _buildCurrentStepContent(),
                ),
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: _buildDialogActions(),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCurrentStepContent() {
    switch (_currentStep) {
      case CardOperationStep.insertCard:
        return _buildInsertCardScreen();
      case CardOperationStep.loading:
        return _buildLoadingScreen('Reading card...');
      case CardOperationStep.cardInfo:
        return _buildCardInfoScreen();
      case CardOperationStep.enterCurrentPin:
        return _buildCurrentPinEntryScreen();
      case CardOperationStep.createPin:
        return _buildPinEntryScreen(isConfirmation: false);
      case CardOperationStep.confirmPin:
        return _buildPinEntryScreen(isConfirmation: true);
      case CardOperationStep.connecting:
        return _buildLoadingScreen('Connecting...');
      case CardOperationStep.success:
        return _buildSuccessScreen();
      case CardOperationStep.failed:
        return _buildFailedScreen();
    }
  }

  List<Widget> _buildDialogActions() {
    switch (_currentStep) {
      case CardOperationStep.insertCard:
        return [
          ElevatedButton(
            onPressed: _cancelOperation,
            child: const Text('Cancel'),
          ),
        ];
      case CardOperationStep.cardInfo:
        return [
          ElevatedButton(
            onPressed: () {
              Navigator.of(context).pop(false); // Return failure
            },
            child: const Text('No'),
          ),
          ElevatedButton(
            onPressed: _onCardInfoConfirmed,
            child: const Text('Yes'),
          ),
        ];
      case CardOperationStep.success:
        return [
          ElevatedButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: const Text('Done'),
          ),
        ];
      case CardOperationStep.failed:
        return [
          ElevatedButton(
            onPressed: _cancelOperation,
            child: const Text('Close'),
          ),
        ];
      default:
        return [
          ElevatedButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
        ];
    }
  }


  // Widget for the insert card screen with Lottie animation
  Widget _buildInsertCardScreen() {
    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // Card insertion animation
          SizedBox(
            height: 200,
            child: Lottie.asset(
              'assets/Animation - 1747811274780.json',
              repeat: true,
              animate: true,
            ),
          ),
          const SizedBox(height: 16),
          Text(
            widget.operationType == CardOperationType.createPin
                ? 'Please insert your card to create a PIN'
                : 'Please insert your card to change your PIN',
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 16),
          ),
          const SizedBox(height: 8),
          if (_isLoading) const CircularProgressIndicator(),
        ],
      ),
    );
  }

  // Widget for loading screens
  Widget _buildLoadingScreen(String message) {
    return SizedBox(
      width: 300,
      height: 200,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          const SizedBox(height: 24),
          Text(
            message,
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 16),
          ),
        ],
      ),
    );
  }

  // Widget for the card info screen
  Widget _buildCardInfoScreen() {
    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
            'Card Information',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 20),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.blue.withOpacity(0.1),
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: Colors.blue.withOpacity(0.3)),
            ),
            child: Column(
              children: [
                _buildCardInfoRow('Card Number',
                    _cardData?.getFormattedCardNumber() ?? 'Not available'),
                const SizedBox(height: 8),
                _buildCardInfoRow('Expiry Date',
                    _cardData?.getFormattedExpiryDate() ?? 'Not available'),
                // if (_cardData?.cardType.isNotEmpty == true) ...[
                //   const SizedBox(height: 8),
                //   _buildCardInfoRow('Card Type', _cardData?.cardType ?? 'Not available'),
                // ],
              ],
            ),
          ),
          const SizedBox(height: 24),
          const Text(
            'Do you want to continue with this card?',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  // Helper for card info rows
  Widget _buildCardInfoRow(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          '$label:',
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        Text(value),
      ],
    );
  }

  // Widget for current PIN entry screen (PIN Change flow)
  Widget _buildCurrentPinEntryScreen() {
    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
            'Enter Current PIN',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Please enter your existing PIN',
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 14, color: Colors.grey),
          ),
          const SizedBox(height: 20),
          TextField(
            controller: _currentPinController,
            focusNode: _currentPinFocusNode,
            decoration: const InputDecoration(
              labelText: 'Current PIN',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.lock_outline),
              hintText: '• • • • • •',
            ),
            keyboardType: TextInputType.number,
            obscureText: true,
            maxLength: 6,
            textAlign: TextAlign.center,
            onChanged: (value) {
              // Auto-submit when 6 digits entered
              if (value.length == 6) {
                _onCurrentPinSubmitted();
              }
              setState(() {}); // Refresh to update button state
            },
          ),
          const SizedBox(height: 16),
          Text(
            'Time remaining: $_remainingPinTime seconds',
            style: TextStyle(
              color: _remainingPinTime <= 10 ? Colors.red : Colors.grey,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _currentPinController.text.length == 6
                ? _onCurrentPinSubmitted
                : null,
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(double.infinity, 48),
            ),
            child: const Text('Continue'),
          ),
        ],
      ),
    );
  }

  // Widget for PIN entry and confirmation screens
  Widget _buildPinEntryScreen({bool isConfirmation = false}) {
    final screenTitle = isConfirmation
        ? 'Confirm your PIN'
        : widget.operationType == CardOperationType.createPin
        ? 'Create your PIN'
        : 'Enter new PIN';

    final buttonLabel = isConfirmation
        ? 'Confirm PIN'
        : widget.operationType == CardOperationType.changePin && !isConfirmation
        ? 'Continue'
        : 'Create PIN';

    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            screenTitle,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            isConfirmation
                ? 'Re-enter your PIN to confirm'
                : 'Enter a 6-digit PIN',
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 14, color: Colors.grey),
          ),
          const SizedBox(height: 20),
          TextField(
            controller: isConfirmation ? _confirmPinController : _pinController,
            focusNode: isConfirmation ? _confirmPinFocusNode : _pinFocusNode,
            decoration: InputDecoration(
              labelText: isConfirmation ? 'Confirm PIN' : 'PIN',
              border: const OutlineInputBorder(),
              prefixIcon: const Icon(Icons.lock_outline),
              hintText: '• • • • • •',
            ),
            keyboardType: TextInputType.number,
            obscureText: true,
            maxLength: 6,
            textAlign: TextAlign.center,
            onChanged: (value) {
              // Auto-submit when 6 digits entered
              if (value.length == 6) {
                if (isConfirmation) {
                  _processPin();
                } else {
                  _confirmPin();
                }
              }
              setState(() {}); // Refresh to update button state
            },
          ),
          const SizedBox(height: 16),
          Text(
            'Time remaining: $_remainingPinTime seconds',
            style: TextStyle(
              color: _remainingPinTime <= 10 ? Colors.red : Colors.grey,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: (isConfirmation ? _confirmPinController.text.length == 6 : _pinController.text.length == 6)
                ? () {
              if (isConfirmation) {
                _processPin();
              } else {
                _confirmPin();
              }
            }
                : null,
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(double.infinity, 48),
            ),
            child: Text(buttonLabel),
          ),
        ],
      ),
    );
  }

  // Widget for success screen
  Widget _buildSuccessScreen() {
    final successMessage = widget.operationType == CardOperationType.createPin
        ? 'PIN created successfully!'
        : 'PIN changed successfully!';

    return SizedBox(
      width: 300,
      height: 200,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(
            Icons.check_circle,
            color: Colors.green,
            size: 60,
          ),
          const SizedBox(height: 24),
          Text(
            successMessage,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 12),
          Text(
            'Dialog will close in $_remainingSuccessTime seconds',
            style: const TextStyle(
              color: Colors.grey,
              fontSize: 14,
            ),
          ),
        ],
      ),
    );
  }

  // Widget for failure screen
  Widget _buildFailedScreen() {
    return SizedBox(
      width: 300,
      height: 200,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(
            Icons.error,
            color: Colors.red,
            size: 60,
          ),
          const SizedBox(height: 16),
          const Text(
            'Operation Failed',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            _errorMessage,
            style: const TextStyle(
              color: Colors.red,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}