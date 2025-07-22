// import 'dart:async';
// import 'package:flutter/material.dart';
// import 'package:flutter_smart_pin_pad_cards/flutter_smart_pin_pad_cards.dart';
// import 'package:flutter_smart_pin_pad_cards/pinpad_model.dart';
// import 'package:lottie/lottie.dart';
//
// // Menghapus enum dan case terkait createPin dan changePin
// enum CardOperationStep {
//   insertCard,
//   loading,
//   cardInfo,
//   confirmPin,
//   connecting,
//   success,
//   failed
// }
//
// class AutorisasiPin extends StatefulWidget {
//   const AutorisasiPin({Key? key}) : super(key: key);
//
//   @override
//   State<AutorisasiPin> createState() => _AutorisasiPinState();
// }
//
// class _AutorisasiPinState extends State<AutorisasiPin> {
//   CardOperationStep _currentStep = CardOperationStep.insertCard;
//   CardData? _cardData;
//   String _errorMessage = '';
//   bool _isLoading = false;
//
//   final TextEditingController _pinController = TextEditingController();
//   final FocusNode _pinFocusNode = FocusNode();
//   Timer? _pinEntryTimer;
//   Timer? _successTimer;
//   int _remainingPinTime = 30;
//   int _remainingSuccessTime = 10;
//
//   @override
//   void initState() {
//     super.initState();
//     _startCardReading();
//   }
//
//   @override
//   void dispose() {
//     _pinEntryTimer?.cancel();
//     _successTimer?.cancel();
//     _pinController.dispose();
//     _pinFocusNode.dispose();
//     FlutterSmartPinPadCards.stopCardReading();
//     super.dispose();
//   }
//
//   Future<void> _startCardReading() async {
//     setState(() {
//       _currentStep = CardOperationStep.insertCard;
//       _isLoading = true;
//     });
//
//     try {
//       final cardData = await FlutterSmartPinPadCards.startInsertCardReading(
//         enableMag: true,
//         enableIcc: true,
//         enableRf: false,
//         timeout: 60000,
//       );
//
//       setState(() {
//         _isLoading = false;
//         _cardData = cardData;
//         _currentStep = CardOperationStep.cardInfo;
//       });
//     } catch (e) {
//       setState(() {
//         _isLoading = false;
//         _errorMessage = e.toString();
//         _currentStep = CardOperationStep.failed;
//       });
//
//       await FlutterSmartPinPadCards.stopCardReading();
//     }
//   }
//
//   void _onCardInfoConfirmed() {
//     setState(() {
//       _currentStep = CardOperationStep.confirmPin;
//       _remainingPinTime = 30;
//     });
//
//     _pinEntryTimer?.cancel();
//     _pinEntryTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
//       setState(() {
//         if (_remainingPinTime > 0) {
//           _remainingPinTime--;
//         } else {
//           timer.cancel();
//           if (mounted) {
//             _showTimeoutErrorAndClose('PIN entry timed out.');
//           }
//         }
//       });
//     });
//
//     _pinFocusNode.requestFocus();
//   }
//
//   void _showTimeoutErrorAndClose(String message) {
//     showDialog(
//       context: context,
//       barrierDismissible: false,
//       builder: (context) => AlertDialog(
//         title: const Text('Timeout'),
//         content: Text(message),
//         actions: [
//           TextButton(
//             onPressed: () {
//               Navigator.of(context).pop();
//               _cancelOperation();
//             },
//             child: const Text('OK'),
//           ),
//         ],
//       ),
//     );
//   }
//
//   Future<void> _processPin() async {
//     _pinEntryTimer?.cancel();
//
//     setState(() {
//       _currentStep = CardOperationStep.connecting;
//     });
//
//     await Future.delayed(const Duration(seconds: 2));
//
//     setState(() {
//       _currentStep = CardOperationStep.success;
//       _remainingSuccessTime = 10;
//     });
//
//     _successTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
//       setState(() {
//         if (_remainingSuccessTime > 0) {
//           _remainingSuccessTime--;
//         } else {
//           timer.cancel();
//           if (mounted) {
//             Navigator.of(context).pop(true);
//           }
//         }
//       });
//     });
//   }
//
//   void _cancelOperation() async {
//     _pinEntryTimer?.cancel();
//     _successTimer?.cancel();
//     await FlutterSmartPinPadCards.stopCardReading();
//     if (mounted) Navigator.of(context).pop(false);
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     String dialogTitle = 'PIN Authorization';
//     if (_currentStep == CardOperationStep.success) {
//       dialogTitle = 'Success';
//     } else if (_currentStep == CardOperationStep.failed) {
//       dialogTitle = 'Error';
//     }
//
//     return Dialog(
//       shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
//       insetPadding: const EdgeInsets.all(24),
//       child: ConstrainedBox(
//         constraints: const BoxConstraints(maxWidth: 400, maxHeight: 600),
//         child: Padding(
//           padding: const EdgeInsets.all(20),
//           child: Column(
//             mainAxisSize: MainAxisSize.min,
//             children: [
//               Text(dialogTitle, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
//               const SizedBox(height: 16),
//               Expanded(child: _buildCurrentStepContent()),
//               const SizedBox(height: 16),
//               Row(
//                 mainAxisAlignment: MainAxisAlignment.spaceEvenly,
//                 children: _buildDialogActions(),
//               ),
//             ],
//           ),
//         ),
//       ),
//     );
//   }
//
//   Widget _buildCurrentStepContent() {
//     switch (_currentStep) {
//       case CardOperationStep.insertCard:
//         return _buildInsertCardScreen();
//       case CardOperationStep.loading:
//         return _buildLoadingScreen('Reading card...');
//       case CardOperationStep.cardInfo:
//         return _buildCardInfoScreen();
//       case CardOperationStep.confirmPin:
//         return _buildPinEntryScreen();
//       case CardOperationStep.connecting:
//         return _buildLoadingScreen('Connecting...');
//       case CardOperationStep.success:
//         return _buildSuccessScreen();
//       case CardOperationStep.failed:
//         return _buildFailedScreen();
//     }
//   }
//
//   List<Widget> _buildDialogActions() {
//     switch (_currentStep) {
//       case CardOperationStep.insertCard:
//       case CardOperationStep.failed:
//         return [
//           ElevatedButton(onPressed: _cancelOperation, child: const Text('Close')),
//         ];
//       case CardOperationStep.cardInfo:
//         return [
//           ElevatedButton(onPressed: () => Navigator.of(context).pop(false), child: const Text('No')),
//           ElevatedButton(onPressed: _onCardInfoConfirmed, child: const Text('Yes')),
//         ];
//       case CardOperationStep.success:
//         return [
//           ElevatedButton(onPressed: () => Navigator.of(context).pop(true), child: const Text('Done')),
//         ];
//       default:
//         return [
//           ElevatedButton(onPressed: () => Navigator.of(context).pop(false), child: const Text('Cancel')),
//         ];
//     }
//   }
//
//   Widget _buildInsertCardScreen() {
//     return Column(
//       mainAxisSize: MainAxisSize.min,
//       children: [
//         SizedBox(
//           height: 200,
//           child: Lottie.asset(
//             'assets/Animation - 1747811274780.json',
//             repeat: true,
//             animate: true,
//           ),
//         ),
//         const SizedBox(height: 16),
//         const Text('Please insert your card to confirm PIN', textAlign: TextAlign.center, style: TextStyle(fontSize: 16)),
//         const SizedBox(height: 8),
//         if (_isLoading) const CircularProgressIndicator(),
//       ],
//     );
//   }
//
//   Widget _buildLoadingScreen(String message) {
//     return Column(
//       mainAxisAlignment: MainAxisAlignment.center,
//       children: [
//         const CircularProgressIndicator(),
//         const SizedBox(height: 24),
//         Text(message, textAlign: TextAlign.center, style: const TextStyle(fontSize: 16)),
//       ],
//     );
//   }
//
//   Widget _buildCardInfoScreen() {
//     return Column(
//       children: [
//         const Text('Card Information', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
//         const SizedBox(height: 20),
//         Container(
//           padding: const EdgeInsets.all(16),
//           decoration: BoxDecoration(
//             color: Colors.blue.withOpacity(0.1),
//             borderRadius: BorderRadius.circular(8),
//             border: Border.all(color: Colors.blue.withOpacity(0.3)),
//           ),
//           child: Column(
//             children: [
//               _buildCardInfoRow('Card Number', _cardData?.getFormattedCardNumber() ?? 'Not available'),
//               const SizedBox(height: 8),
//               _buildCardInfoRow('Expiry Date', _cardData?.getFormattedExpiryDate() ?? 'Not available'),
//             ],
//           ),
//         ),
//         const SizedBox(height: 24),
//         const Text('Do you want to continue with this card?', textAlign: TextAlign.center, style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
//       ],
//     );
//   }
//
//   Widget _buildCardInfoRow(String label, String value) {
//     return Row(
//       mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       children: [
//         Text('$label:', style: const TextStyle(fontWeight: FontWeight.bold)),
//         Text(value),
//       ],
//     );
//   }
//
//   Widget _buildPinEntryScreen() {
//     return Column(
//       children: [
//         const Text('Enter PIN', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
//         const SizedBox(height: 8),
//         const Text('Enter your 6-digit PIN to confirm', textAlign: TextAlign.center, style: TextStyle(fontSize: 14, color: Colors.grey)),
//         const SizedBox(height: 20),
//         TextField(
//           controller: _pinController,
//           focusNode: _pinFocusNode,
//           decoration: const InputDecoration(
//             labelText: 'PIN',
//             border: OutlineInputBorder(),
//             prefixIcon: Icon(Icons.lock_outline),
//             hintText: '• • • • • •',
//           ),
//           keyboardType: TextInputType.number,
//           obscureText: true,
//           maxLength: 6,
//           textAlign: TextAlign.center,
//           onChanged: (value) {
//             setState(() {
//               if (value.isNotEmpty && value.length == 6) {
//                 _processPin();
//               }
//             });
//           },
//         ),
//         const SizedBox(height: 16),
//         Text('Time remaining: $_remainingPinTime seconds', style: TextStyle(color: _remainingPinTime <= 10 ? Colors.red : Colors.grey)),
//         const SizedBox(height: 16),
//         // ElevatedButton(
//         //   onPressed: _pinController.text.length == 6 ? _processPin : null,
//         //   style: ElevatedButton.styleFrom(minimumSize: const Size(double.infinity, 48)),
//         //   child: const Text('Submit'),
//         // ),
//       ],
//     );
//   }
//
//   Widget _buildSuccessScreen() {
//     return Column(
//       mainAxisAlignment: MainAxisAlignment.center,
//       children: [
//         const Icon(Icons.check_circle, color: Colors.green, size: 60),
//         const SizedBox(height: 24),
//         const Text('PIN confirmed successfully!', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
//         const SizedBox(height: 12),
//         Text('Dialog will close in $_remainingSuccessTime seconds', style: const TextStyle(color: Colors.grey)),
//       ],
//     );
//   }
//
//   Widget _buildFailedScreen() {
//     return Column(
//       mainAxisAlignment: MainAxisAlignment.center,
//       children: [
//         const Icon(Icons.error, color: Colors.red, size: 60),
//         const SizedBox(height: 16),
//         const Text('Operation Failed', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
//         const SizedBox(height: 12),
//         Text(_errorMessage, style: const TextStyle(color: Colors.red), textAlign: TextAlign.center),
//       ],
//     );
//   }
// }
