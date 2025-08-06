// Updated Card Reader Dialog with Bloc Integration
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';
import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';
import 'package:lottie/lottie.dart';

import 'business_logic/cardoperation/card_operation_bloc.dart';
import 'enum/pinpad.dart';

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
  // PIN entry controllers
  final TextEditingController _currentPinController = TextEditingController();
  final TextEditingController _pinController = TextEditingController();
  final TextEditingController _confirmPinController = TextEditingController();

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
    // Start card reading operation
    context.read<CardOperationBloc>().add(
      CardOperationEvent.startReading(operationType: widget.operationType),
    );
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
    super.dispose();
  }

  void _startPinTimer() {
    _pinEntryTimer?.cancel();
    _pinEntryTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_remainingPinTime > 0) {
          _remainingPinTime--;
        } else {
          timer.cancel();
          if (mounted) {
            context.read<CardOperationBloc>().add(const CardOperationEvent.timeout());
          }
        }
      });
    });
  }

  void _startSuccessTimer() {
    _successTimer?.cancel();
    _successTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_remainingSuccessTime > 0) {
          _remainingSuccessTime--;
        } else {
          timer.cancel();
          if (mounted) {
            Navigator.of(context).pop(true);
          }
        }
      });
    });
  }

  void _onCardInfoConfirmed() {
    context.read<CardOperationBloc>().add(const CardOperationEvent.confirmCard());
  }

  void _onCurrentPinSubmitted() {
    if (_currentPinController.text.length != 6) {
      return;
    }

    context.read<CardOperationBloc>().add(
      CardOperationEvent.enterPin(pin: _currentPinController.text),
    );
  }

  void _onPinSubmitted() {
    if (_pinController.text.length != 6) {
      return;
    }

    context.read<CardOperationBloc>().add(
      CardOperationEvent.enterPin(pin: _pinController.text),
    );
  }

  void _onConfirmPinSubmitted() {
    if (_confirmPinController.text.length != 6) {
      return;
    }

    context.read<CardOperationBloc>().add(
      CardOperationEvent.enterPin(pin: _confirmPinController.text),
    );
  }

  void _cancelOperation() {
    context.read<CardOperationBloc>().add(const CardOperationEvent.cancel());
    Navigator.of(context).pop();
  }

  @override
  Widget build(BuildContext context) {
    String dialogTitle = _getDialogTitle();

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      insetPadding: const EdgeInsets.symmetric(horizontal: 24, vertical: 24),
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 400, maxHeight: 600),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                dialogTitle,
                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              Expanded(
                child: BlocConsumer<CardOperationBloc, CardOperationState>(
                  listener: (context, state) {
                    state.maybeWhen(
                      pinEntry: (operationType, cardData, step, remainingTime, currentPin, newPin) {
                        setState(() {
                          _remainingPinTime = remainingTime;
                        });
                        _startPinTimer();

                        // Focus appropriate field
                        switch (step) {
                          case PinEntryStep.enterCurrentPin:
                            _currentPinFocusNode.requestFocus();
                            break;
                          case PinEntryStep.createPin:
                            _pinFocusNode.requestFocus();
                            break;
                          case PinEntryStep.confirmPin:
                            _confirmPinFocusNode.requestFocus();
                            break;
                        }
                      },
                      success: (operationType, response, remainingTime) {
                        _pinEntryTimer?.cancel();
                        setState(() {
                          _remainingSuccessTime = remainingTime;
                        });
                        _startSuccessTimer();
                      },
                      error: (message) {
                        _pinEntryTimer?.cancel();
                        _successTimer?.cancel();
                      },
                      cancelled: () {
                        _pinEntryTimer?.cancel();
                        _successTimer?.cancel();
                      },
                      workingKeyError: (operation, message) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                            content: Text('Working Key Error: $message'),
                            backgroundColor: Colors.red,
                          ),
                        );
                      },
                      orElse: () {},
                    );
                  },
                  builder: (context, state) {
                    return SingleChildScrollView(
                      child: _buildCurrentStepContent(state),
                    );
                  },
                ),
              ),
              const SizedBox(height: 16),
              BlocBuilder<CardOperationBloc, CardOperationState>(
                builder: (context, state) {
                  return Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: _buildDialogActions(state),
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  String _getDialogTitle() {
    switch (widget.operationType) {
      case CardOperationType.createPin:
        return 'Create PIN';
      case CardOperationType.changePin:
        return 'Change PIN';
      case CardOperationType.otorisation:
        return 'PIN Authorization';
    }
  }

  Widget _buildCurrentStepContent(CardOperationState state) {
    return state.when(
      initial: () => _buildLoadingScreen('Initializing...'),
      reading: (operationType, remainingTime) => _buildInsertCardScreen(),
      cardRead: (operationType, cardData) => _buildCardInfoScreen(cardData),
      pinEntry: (operationType, cardData, step, remainingTime, currentPin, newPin) {
        switch (step) {
          case PinEntryStep.enterCurrentPin:
            return _buildCurrentPinEntryScreen();
          case PinEntryStep.createPin:
            return _buildPinEntryScreen(isConfirmation: false);
          case PinEntryStep.confirmPin:
            return _buildPinEntryScreen(isConfirmation: true);
        }
      },
      processing: (operationType, cardData) => _buildLoadingScreen('Processing...'),
      success: (operationType, response, remainingTime) => _buildSuccessScreen(),
      error: (message) => _buildFailedScreen(message),
      cancelled: () => _buildFailedScreen('Operation cancelled'),
      // Working Key States - NEW
      workingKeyProcessing: (operation) => _buildLoadingScreen('$operation...'),
      workingKeySuccess: (operation, result) => _buildLoadingScreen('$operation completed'),
      workingKeyError: (operation, message) => _buildFailedScreen('$operation failed: $message'),
    );
  }

  List<Widget> _buildDialogActions(CardOperationState state) {
    return state.when(
      initial: () => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Cancel'),
        ),
      ],
      reading: (operationType, remainingTime) => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Cancel'),
        ),
      ],
      cardRead: (operationType, cardData) => [
        ElevatedButton(
          onPressed: () => Navigator.of(context).pop(),
          child: const Text('No'),
        ),
        ElevatedButton(
          onPressed: _onCardInfoConfirmed,
          child: const Text('Yes'),
        ),
      ],
      pinEntry: (operationType, cardData, step, remainingTime, currentPin, newPin) => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Cancel'),
        ),
      ],
      processing: (operationType, cardData) => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Cancel'),
        ),
      ],
      success: (operationType, response, remainingTime) => [
        ElevatedButton(
          onPressed: () => Navigator.of(context).pop(true),
          child: const Text('Done'),
        ),
      ],
      error: (message) => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Close'),
        ),
      ],
      cancelled: () => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Close'),
        ),
      ],
      // Working Key States - NEW
      workingKeyProcessing: (operation) => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Cancel'),
        ),
      ],
      workingKeySuccess: (operation, result) => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Close'),
        ),
      ],
      workingKeyError: (operation, message) => [
        ElevatedButton(
          onPressed: _cancelOperation,
          child: const Text('Close'),
        ),
      ],
    );
  }

  Widget _buildInsertCardScreen() {
    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Lottie.asset('assets/Animation - 1747811274780.json',
            repeat: true,
            animate: true,
          ),
          // const Icon(Icons.credit_card, size: 80, color: Colors.blue),
          const SizedBox(height: 16),
          Text(
            _getInsertCardMessage(),
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 16),
          ),
          const SizedBox(height: 8),
          // You can uncomment this if you have the Lottie animation file
          // Lottie.asset('assets/Animation - 1747811274780.json',
          //   repeat: true,
          //   animate: true,
          // ),
          const CircularProgressIndicator(),
        ],
      ),
    );
  }

  String _getInsertCardMessage() {
    switch (widget.operationType) {
      case CardOperationType.createPin:
        return 'Please insert your card to create a PIN';
      case CardOperationType.changePin:
        return 'Please insert your card to change your PIN';
      case CardOperationType.otorisation:
        return 'Please insert your card for PIN authorization';
    }
  }

  Widget _buildLoadingScreen(String message) {
    return SizedBox(
      width: 300,
      height: 200,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          const SizedBox(height: 24),
          Text(message, textAlign: TextAlign.center, style: const TextStyle(fontSize: 16)),
        ],
      ),
    );
  }

  Widget _buildCardInfoScreen(CardData cardData) {
    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text('Card Information', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
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
                _buildCardInfoRow('Card Number', cardData.getFormattedCardNumber()),
                const SizedBox(height: 8),
                _buildCardInfoRow('Expiry Date', cardData.getFormattedExpiryDate()),
                const SizedBox(height: 8),
                _buildCardInfoRow('Card Type', cardData.cardType),
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

  Widget _buildCardInfoRow(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text('$label:', style: const TextStyle(fontWeight: FontWeight.bold)),
        Text(value.isNotEmpty ? value : 'Not available'),
      ],
    );
  }

  Widget _buildCurrentPinEntryScreen() {
    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            widget.operationType == CardOperationType.otorisation
                ? 'Enter PIN for Authorization'
                : 'Enter Current PIN',
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 20),
          TextField(
            controller: _currentPinController,
            focusNode: _currentPinFocusNode,
            decoration: const InputDecoration(
              labelText: 'PIN',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.lock_outline),
              hintText: '• • • • • •',
            ),
            keyboardType: TextInputType.number,
            obscureText: true,
            maxLength: 6,
            textAlign: TextAlign.center,
            onChanged: (value) {
              if (value.length == 6) {
                _onCurrentPinSubmitted();
              }
              setState(() {});
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
            onPressed: _currentPinController.text.length == 6 ? _onCurrentPinSubmitted : null,
            style: ElevatedButton.styleFrom(minimumSize: const Size(double.infinity, 48)),
            child: const Text('Continue'),
          ),
        ],
      ),
    );
  }

  Widget _buildPinEntryScreen({bool isConfirmation = false}) {
    final screenTitle = isConfirmation
        ? 'Confirm your PIN'
        : widget.operationType == CardOperationType.createPin
        ? 'Create your PIN'
        : 'Enter new PIN';

    return SizedBox(
      width: 300,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(screenTitle, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
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
              if (value.length == 6) {
                if (isConfirmation) {
                  _onConfirmPinSubmitted();
                } else {
                  _onPinSubmitted();
                }
              }
              setState(() {});
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
                _onConfirmPinSubmitted();
              } else {
                _onPinSubmitted();
              }
            }
                : null,
            style: ElevatedButton.styleFrom(minimumSize: const Size(double.infinity, 48)),
            child: Text(isConfirmation ? 'Confirm PIN' : 'Continue'),
          ),
        ],
      ),
    );
  }

  Widget _buildSuccessScreen() {
    final successMessage = _getSuccessMessage();

    return SizedBox(
      width: 300,
      height: 200,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.check_circle, color: Colors.green, size: 60),
          const SizedBox(height: 24),
          Text(
            successMessage,
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 12),
          Text(
            'Dialog will close in $_remainingSuccessTime seconds',
            style: const TextStyle(color: Colors.grey, fontSize: 14),
          ),
        ],
      ),
    );
  }

  String _getSuccessMessage() {
    switch (widget.operationType) {
      case CardOperationType.createPin:
        return 'PIN created successfully!';
      case CardOperationType.changePin:
        return 'PIN changed successfully!';
      case CardOperationType.otorisation:
        return 'PIN authorization successful!';
    }
  }

  Widget _buildFailedScreen(String errorMessage) {
    return SizedBox(
      width: 300,
      height: 200,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error, color: Colors.red, size: 60),
          const SizedBox(height: 16),
          const Text(
            'Operation Failed',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 12),
          Text(
            errorMessage,
            style: const TextStyle(color: Colors.red),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}