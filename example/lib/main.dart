// main.dart
import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'card_reader_dialog.dart';
import 'dummy.dart';

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
      supportedLocales: const [
        Locale('en'), // English
        Locale('id'), // Indonesian
      ],
      home: const LoginPage(), // Start with login page
      routes: {
        '/login': (context) => const LoginPage(),
        '/home': (context) => const HomeScreen(),
      },
    );
  }
}

// login_page.dart

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _passwordController = TextEditingController();
  bool _isLoading = false;
  bool _isObscured = true;

  @override
  void dispose() {
    _passwordController.dispose();
    super.dispose();
  }

  void _handleLogin() async {
    if (_passwordController.text.isEmpty) {
      _showSnackBar('Please enter password', Colors.orange);
      return;
    }

    setState(() {
      _isLoading = true;
    });

    // Simulate server connection
    await Future.delayed(const Duration(seconds: 2));

    // Simple password validation (you can replace this with actual authentication)
    if (_passwordController.text == '1234') {
      setState(() {
        _isLoading = false;
      });

      // Navigate to home screen
      if (mounted) {
        Navigator.pushReplacementNamed(context, '/home');
      }
    } else {
      setState(() {
        _isLoading = false;
      });
      _showSnackBar('Invalid password', Colors.red);
    }
  }

  void _showSnackBar(String message, Color color) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(message),
          backgroundColor: color,
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
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const Icon(
                  Icons.credit_card,
                  size: 80,
                  color: Colors.blue,
                ),
                const SizedBox(height: 24),
                const Text(
                  'BANK JATENG',
                  style: TextStyle(
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                Text(
                  'Smart Pin Pad System',
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.grey.shade600,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 40),
                _buildLoginCard(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLoginCard() {
    return Card(
      elevation: 4,
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
                color: Colors.black,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 20),
            if (_isLoading) ...[
              const Text(
                'CONNECTING\nPROCESSING',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 16),
              const CircularProgressIndicator(),
              const SizedBox(height: 16),
              const Text(
                'Terminal sedang melakukan koneksi dengan server',
                style: TextStyle(
                  fontSize: 14,
                  color: Colors.grey,
                ),
                textAlign: TextAlign.center,
              ),
            ] else ...[
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
                obscureText: _isObscured,
                decoration: InputDecoration(
                  hintText: '****',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 12,
                  ),
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
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey,
                  ),
                ),
              ),
              const SizedBox(height: 20),
              // SizedBox(
              //   width: double.infinity,
              //   child: ElevatedButton(
              //     onPressed: _handleLogin,
              //     style: ElevatedButton.styleFrom(
              //       backgroundColor: const Color(0xFF0D4575),
              //       foregroundColor: Colors.white,
              //       padding: const EdgeInsets.symmetric(vertical: 16),
              //       shape: RoundedRectangleBorder(
              //         borderRadius: BorderRadius.circular(8),
              //       ),
              //     ),
              //     child: const Text(
              //       'LOGON',
              //       style: TextStyle(
              //         fontSize: 16,
              //         fontWeight: FontWeight.bold,
              //       ),
              //     ),
              //   ),
              // ),
              const SizedBox(height: 12),
              const Text(
                'Default password: 1234',
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey,
                  fontStyle: FontStyle.italic,
                ),
              ),
            ],
          ],
        ),
      ),
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
  bool _isLoggingOut = false;

  // Card reader workflow states
  bool _showCardReaderWorkflow = false;
  int _currentStep = 1;
  String _operationType = '';
  final TextEditingController _pinController = TextEditingController();
  bool _isProcessing = false;
  bool _pinObscured = true;

  // Timer untuk timeout workflow
  Timer? _workflowTimer;
  int _remainingSeconds = 60;
  Timer? _countdownTimer;

  // Timer untuk idle timeout di HomeScreen
  Timer? _idleTimer;
  bool _showIdleScreen = false;
  bool _showAuthWorkflow = false;
  int _authStep = 2; // Mulai dari step 2 (swipe card)
  bool _authProcessing = false;

  @override
  void initState() {
    super.initState();
    _startIdleTimer();
  }

  @override
  void dispose() {
    _pinController.dispose();
    _workflowTimer?.cancel();
    _countdownTimer?.cancel();
    _idleTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _showIdleScreen || _showAuthWorkflow ? null : AppBar(
        title: const Text('Smart Pin Pad'),
        backgroundColor: const Color(0xFF0D4575),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _showLogoutDialog,
          ),
        ],
      ),
      body: SafeArea(
        child: _showIdleScreen
            ? _buildIdleScreen()
            : _showAuthWorkflow
            ? _buildAuthWorkflow()
            : _showCardReaderWorkflow
            ? _buildCardReaderWorkflow()
            : _buildMainContent(),
      ),
    );
  }

  // void _showChangePinDialog() async {
  //   if (!_pinCreated) {
  //     ScaffoldMessenger.of(context).showSnackBar(
  //       const SnackBar(
  //         content: Text('Please create a PIN first'),
  //         backgroundColor: Colors.orange,
  //       ),
  //     );
  //     return;
  //   }
  //
  //   final result = await showDialog<bool>(
  //     context: context,
  //     barrierDismissible: false,
  //     builder: (context) => const CardReaderDialog(
  //       operationType: CardOperationType.changePin,
  //     ),
  //   );
  //
  //   if (result == true && mounted) {
  //     ScaffoldMessenger.of(context).showSnackBar(
  //       const SnackBar(
  //         content: Text('PIN changed successfully!'),
  //         backgroundColor: Colors.green,
  //       ),
  //     );
  //   }
  // }
  Widget _buildMainContent() {
    return GestureDetector(
      onTap: _resetIdleTimer,
      onPanDown: (_) => _resetIdleTimer(),
      child: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
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
    );
  }

  Widget _buildIdleScreen() {
    return GestureDetector(
      onTap: _onIdleScreenTap,
      onPanDown: (_) => _onIdleScreenTap(),
      child: Container(
        width: double.infinity,
        height: double.infinity,
        color: const Color(0xFF0D4575),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [

              const SizedBox(height: 30),
             Image.asset('assets/images/splash_screen.png'),
              const SizedBox(height: 40),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: Colors.grey.shade600),
                ),
                child: const Text(
                  'Tap anywhere to continue',
                  style: TextStyle(
                    color: Colors.black,
                    fontSize: 14,
                    fontStyle: FontStyle.italic,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAuthWorkflow() {
    return Center(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 16),
              decoration: BoxDecoration(
                color: const Color(0xFF0D4575),
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Text(
                'AUTHENTICATION REQUIRED',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ),
            const SizedBox(height: 20),

            // Step Indicator for Auth
            _buildAuthStepIndicator(),
            const SizedBox(height: 20),

            // Current Step Content
            _buildCurrentAuthStepWidget(),
            const SizedBox(height: 20),

            // Action Buttons (hanya jika tidak processing)
            if (!_authProcessing) _buildAuthActionButtons(),
          ],
        ),
      ),
    );
  }

  Widget _buildAuthStepIndicator() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(3, (index) {
        final stepNumber = index + 2; // Step 2, 3, 4
        final isActive = stepNumber == _authStep;
        final isCompleted = stepNumber < _authStep;

        return Container(
          margin: const EdgeInsets.symmetric(horizontal: 4),
          width: 50,
          height: 50,
          decoration: BoxDecoration(
            color: isActive || isCompleted ? const Color(0xFF0D4575) : Colors.grey.shade300,
            shape: BoxShape.circle,
          ),
          child: Center(
            child: Text(
              stepNumber.toString(),
              style: TextStyle(
                color: isActive || isCompleted ? Colors.white : Colors.grey.shade600,
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
            ),
          ),
        );
      }),
    );
  }

  Widget _buildCurrentAuthStepWidget() {
    switch (_authStep) {
      case 2:
        return _buildAuthSwipeCard();
      case 3:
        return _buildAuthEnterPin();
      case 4:
        return _buildAuthProcessing();
      default:
        return Container();
    }
  }

  Widget _buildAuthSwipeCard() {
    return Card(
      elevation: 6,
      child: Container(
        width: double.infinity,
        height: 250,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.credit_card,
              size: 70,
              color: Color(0xFF0D4575),
            ),
            const SizedBox(height: 20),
            const Text(
              'SWIPE',
              style: TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
                color: Color(0xFF0D4575),
              ),
            ),
            const Text(
              'SUPERVISOR CARD',
              style: TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
                color: Color(0xFF0D4575),
              ),
            ),
            const SizedBox(height: 20),
            Text(
              'Pada saat command dikirim, terminal akan\nmeminta swipe Supervisor card',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey.shade600,
                fontStyle: FontStyle.italic,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAuthEnterPin() {
    return Card(
      elevation: 6,
      child: Container(
        width: double.infinity,
        height: 250,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'ENTER PIN',
              style: TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
                color: Color(0xFF0D4575),
              ),
            ),
            const SizedBox(height: 20),
            const Text(
              'INPUT PIN:',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: 180,
              child: TextField(
                controller: _pinController,
                obscureText: _pinObscured,
                textAlign: TextAlign.center,
                maxLength: 6,
                keyboardType: TextInputType.number,
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 3,
                ),
                decoration: InputDecoration(
                  hintText: '****',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(width: 2),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(width: 2, color: Color(0xFF0D4575)),
                  ),
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 16,
                  ),
                  counterText: '',
                  suffixIcon: IconButton(
                    icon: Icon(
                      _pinObscured ? Icons.visibility : Icons.visibility_off,
                      size: 20,
                    ),
                    onPressed: () {
                      setState(() {
                        _pinObscured = !_pinObscured;
                      });
                    },
                  ),
                ),
                onChanged: (value) {
                  if (value.length >= 4) {
                    // Auto-proceed when PIN is entered
                    Future.delayed(const Duration(milliseconds: 1000), () {
                      if (mounted && value.length >= 4) {
                        _nextAuthStep();
                      }
                    });
                  }
                },
              ),
            ),
            const SizedBox(height: 16),
            Text(
              'Input pin',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey.shade600,
                fontStyle: FontStyle.italic,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAuthProcessing() {
    return Card(
      elevation: 6,
      child: Container(
        width: double.infinity,
        height: 250,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'CONNECTING',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Color(0xFF0D4575),
              ),
            ),
            const Text(
              'PROCESSING',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Color(0xFF0D4575),
              ),
            ),
            const SizedBox(height: 24),
            const CircularProgressIndicator(
              valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF0D4575)),
              strokeWidth: 4,
            ),
            const SizedBox(height: 20),
            Text(
              'Terminal sedang melakukan koneksi dengan server',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey.shade600,
                fontStyle: FontStyle.italic,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAuthActionButtons() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        SizedBox(
          width: 140,
          child: ElevatedButton(
            onPressed: _backToIdle,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.grey.shade600,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 12),
            ),
            child: const Text(
              'BACK TO IDLE',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 12,
              ),
            ),
          ),
        ),
        SizedBox(
          width: 140,
          child: ElevatedButton(
            onPressed: _handleAuthNext,
            style: ElevatedButton.styleFrom(
              backgroundColor: Color(0xFF0D4575),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 12),
            ),
            child: Text(
              _getAuthNextButtonText(),
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 12,
              ),
            ),
          ),
        ),
      ],
    );
  }

  String _getAuthNextButtonText() {
    switch (_authStep) {
      case 2:
        return 'SWIPE DONE';
      case 3:
        return 'ENTER PIN';
      default:
        return 'NEXT';
    }
  }

  Widget _buildCardReaderWorkflow() {
    return Center(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header with Timer
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 16),
              decoration: BoxDecoration(
                color: const Color(0xFF0D4575),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                children: [
                  Text(
                    _operationType,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                    decoration: BoxDecoration(
                      color: _remainingSeconds <= 10 ? Color(0xFF0D4575) : Colors.orange,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      'Timeout: ${_remainingSeconds}s',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Step Indicator
            _buildStepIndicator(),
            const SizedBox(height: 20),

            // Current Step Content
            _buildCurrentStepWidget(),
            const SizedBox(height: 20),

            // Action Buttons
            if (_currentStep != 4) _buildActionButtons(),
          ],
        ),
      ),
    );
  }

  Widget _buildStepIndicator() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(4, (index) {
        final stepNumber = index + 1;
        final isActive = stepNumber == _currentStep;
        final isCompleted = stepNumber < _currentStep;

        return Container(
          margin: const EdgeInsets.symmetric(horizontal: 4),
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: isActive || isCompleted ? const Color(0xFF0D4575) : Colors.grey.shade300,
            shape: BoxShape.circle,
          ),
          child: Center(
            child: Text(
              stepNumber.toString(),
              style: TextStyle(
                color: isActive || isCompleted ? Colors.white : Colors.grey.shade600,
                fontWeight: FontWeight.bold,
                fontSize: 16,
              ),
            ),
          ),
        );
      }),
    );
  }

  Widget _buildCurrentStepWidget() {
    switch (_currentStep) {
      case 1:
        return _buildIdleStep();
      case 2:
        return _buildSwipeCardStep();
      case 3:
        return _buildEnterPinStep();
      case 4:
        return _buildProcessingStep();
      default:
        return Container();
    }
  }

  Widget _buildIdleStep() {
    return Card(
      elevation: 4,
      child: Container(
        width: double.infinity,
        height: 200,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.credit_card,
              size: 50,
              color: Colors.blue,
            ),
            const SizedBox(height: 12),
            const Text(
              'BANK JATENG',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              'Smart Pin Pad System',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey.shade600,
              ),
            ),
            const SizedBox(height: 16),
            Text(
              'Tampilan EDC pada saat status idle',
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey.shade500,
                fontStyle: FontStyle.italic,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSwipeCardStep() {
    return Card(
      elevation: 4,
      child: Container(
        width: double.infinity,
        height: 200,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.credit_card,
              size: 50,
              color: Colors.orange,
            ),
            const SizedBox(height: 12),
            const Text(
              'SWIPE',
              style: TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
            const Text(
              'SUPERVISOR CARD',
              style: TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            Text(
              'Pada saat command dikirim, terminal akan\nmeminta swipe Supervisor card',
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey.shade500,
                fontStyle: FontStyle.italic,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEnterPinStep() {
    return Card(
      elevation: 4,
      child: Container(
        width: double.infinity,
        height: 200,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'ENTER PIN',
              style: TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            const Text(
              'INPUT PIN:',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            SizedBox(
              width: 150,
              child: TextField(
                controller: _pinController,
                obscureText: _pinObscured,
                textAlign: TextAlign.center,
                maxLength: 6,
                keyboardType: TextInputType.number,
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 2,
                ),
                decoration: InputDecoration(
                  hintText: '****',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(width: 2),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(width: 2, color: Color(0xFF0D4575)),
                  ),
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 12,
                  ),
                  counterText: '',
                  suffixIcon: IconButton(
                    icon: Icon(
                      _pinObscured ? Icons.visibility : Icons.visibility_off,
                      size: 20,
                    ),
                    onPressed: () {
                      // Reset timer saat user interact
                      _resetWorkflowTimer();
                      setState(() {
                        _pinObscured = !_pinObscured;
                      });
                    },
                  ),
                ),
                onChanged: (value) {
                  // Reset timer setiap ada input
                  _resetWorkflowTimer();

                  if (value.length >= 4) {
                    // Auto-proceed when PIN is entered
                    Future.delayed(const Duration(milliseconds: 1000), () {
                      if (mounted && value.length >= 4) {
                        _nextStep();
                      }
                    });
                  }
                },
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'Input pin',
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey.shade500,
                fontStyle: FontStyle.italic,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProcessingStep() {
    return Card(
      elevation: 4,
      child: Container(
        width: double.infinity,
        height: 200,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'CONNECTING',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const Text(
              'PROCESSING',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            const CircularProgressIndicator(
              valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF0D4575)),
            ),
            const SizedBox(height: 16),
            Text(
              'Terminal sedang melakukan koneksi dengan server',
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey.shade500,
                fontStyle: FontStyle.italic,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActionButtons() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        SizedBox(
          width: 120,
          child: ElevatedButton(
            onPressed: _isProcessing ? null : _handleCancel,
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0D4575),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 12),
            ),
            child: const Text(
              'CANCEL',
              style: TextStyle(
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
        SizedBox(
          width: 120,
          child: ElevatedButton(
            onPressed: _isProcessing ? null : _handleNext,
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0D4575),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 12),
            ),
            child: Text(
              _getNextButtonText(),
              style: const TextStyle(
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
      ],
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
                  onPressed: () {
                    _resetIdleTimer();
                    _showCreatePinDialog();
                    // _startCardReaderWorkflow('CREATE PIN');
                  },
                ),
                _buildIconButtonWithText(
                  icon: Icons.loop,
                  color: Colors.green,
                  label: 'Change PIN',
                  onPressed: () {
                    _resetIdleTimer();
                    _showChangePinDialog();
                    // _startCardReaderWorkflow('CHANGE PIN');
                  },
                ),
                          _buildIconButtonWithText(
                  icon: Icons.security,
                  color: Colors.green,
                  label: 'OTORISASI PIN',
                  onPressed: () {
                    _resetIdleTimer();
                      showDialog<bool>(
                      context: context,
                      barrierDismissible: false,
                      builder: (context) => const AutorisasiPin(),
                    );
                    // _startCardReaderWorkflow('CHANGE PIN');
                  },
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
                  _resetIdleTimer();
                  setState(() {
                    _pinCreated = false;
                  });
                },
                icon: const Icon(Icons.refresh, size: 16),
                label: const Text('Reset Demo'),
                style: TextButton.styleFrom(
                  foregroundColor: Colors.grey.shade700,
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
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
        onTap: () {
          _resetIdleTimer();
          onPressed();
        },
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

  // Idle Timer Methods
  void _startIdleTimer() {
    _idleTimer?.cancel();
    _idleTimer = Timer(const Duration(seconds: 40), () {
      if (!_showCardReaderWorkflow && !_showIdleScreen && !_showAuthWorkflow && mounted) {
        _showIdleMode();
      }
    });
  }

  void _resetIdleTimer() {
    if (!_showIdleScreen && !_showAuthWorkflow) {
      _startIdleTimer();
    }
  }

  void _showIdleMode() {
    setState(() {
      _showIdleScreen = true;
    });
  }

  void _onIdleScreenTap() {
    // Pindah dari idle screen ke authentication workflow
    setState(() {
      _showIdleScreen = false;
      _showAuthWorkflow = true;
      _authStep = 2;
      _authProcessing = false;
      _pinController.clear();
    });
  }

  void _backToIdle() {
    setState(() {
      _showAuthWorkflow = false;
      _showIdleScreen = true;
      _authStep = 2;
      _authProcessing = false;
      _pinController.clear();
    });
  }

  // Authentication Workflow Methods
  void _handleAuthNext() {
    if (_authStep == 3) {
      if (_pinController.text.isEmpty) {
        _showSnackBar('Please enter PIN', Colors.orange);
        return;
      }
      if (_pinController.text.length < 4) {
        _showSnackBar('PIN must be at least 4 digits', Colors.orange);
        return;
      }
    }

    _nextAuthStep();
  }

  void _nextAuthStep() {
    if (_authStep < 4) {
      setState(() {
        _authStep++;
      });

      if (_authStep == 4) {
        _processAuthWorkflow();
      }
    }
  }

  void _processAuthWorkflow() async {
    setState(() {
      _authProcessing = true;
    });

    // Simulate server processing
    await Future.delayed(const Duration(seconds: 3));

    if (mounted) {
      // Kembali ke main screen setelah auth berhasil
      setState(() {
        _showAuthWorkflow = false;
        _authStep = 2;
        _authProcessing = false;
        _pinController.clear();
      });

      _showSnackBar(
        'Authentication successful!',
        Colors.green,
      );

      // Restart idle timer
      _startIdleTimer();
    }
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

  // Card Reader Workflow Methods
  // void _startCardReaderWorkflow(String operationType) {
  //   if (operationType == 'CHANGE PIN' && !_pinCreated) {
  //     _showSnackBar('Please create a PIN first', Colors.orange);
  //     return;
  //   }
  //
  //   // Stop idle timer saat masuk workflow
  //   _idleTimer?.cancel();
  //
  //   setState(() {
  //     _showCardReaderWorkflow = true;
  //     _currentStep = 1;
  //     _operationType = operationType;
  //     _pinController.clear();
  //     _isProcessing = false;
  //     _pinObscured = true;
  //     _remainingSeconds = 60;
  //   });
  //
  //   _startWorkflowTimer();
  // }

  void _startWorkflowTimer() {
    // Cancel existing timers
    _workflowTimer?.cancel();
    _countdownTimer?.cancel();

    // Start countdown timer untuk update UI
    _countdownTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_remainingSeconds > 0) {
        setState(() {
          _remainingSeconds--;
        });
      }
    });

    // Start main workflow timer (1 menit)
    _workflowTimer = Timer(const Duration(minutes: 1), () {
      if (_showCardReaderWorkflow && !_isProcessing) {
        _handleWorkflowTimeout();
      }
    });
  }

  void _resetWorkflowTimer() {
    if (_showCardReaderWorkflow && !_isProcessing) {
      setState(() {
        _remainingSeconds = 60;
      });
      _startWorkflowTimer();
    }
  }

  void _handleWorkflowTimeout() {
    if (mounted) {
      _workflowTimer?.cancel();
      _countdownTimer?.cancel();

      setState(() {
        _showCardReaderWorkflow = false;
        _currentStep = 1;
        _pinController.clear();
        _isProcessing = false;
      });

      _showSnackBar(
        'Transaction timeout. Please try again.',
        Color(0xFF0D4575),
      );

      // Restart idle timer setelah timeout
      _startIdleTimer();
    }
  }

  String _getNextButtonText() {
    switch (_currentStep) {
      case 1:
        return 'START';
      case 2:
        return 'SWIPE DONE';
      case 3:
        return 'ENTER PIN';
      default:
        return 'NEXT';
    }
  }

  void _handleNext() {
    // Reset timer setiap ada aktivitas user
    _resetWorkflowTimer();

    if (_currentStep == 3) {
      if (_pinController.text.isEmpty) {
        _showSnackBar('Please enter PIN', Colors.orange);
        return;
      }
      if (_pinController.text.length < 4) {
        _showSnackBar('PIN must be at least 4 digits', Colors.orange);
        return;
      }
    }

    _nextStep();
  }

  void _nextStep() {
    if (_currentStep < 4) {
      setState(() {
        _currentStep++;
      });

      if (_currentStep == 4) {
        _processOperation();
      } else {
        // Reset timer untuk step berikutnya
        _resetWorkflowTimer();
      }
    }
  }

  void _processOperation() async {
    // Cancel timer saat processing
    _workflowTimer?.cancel();
    _countdownTimer?.cancel();

    setState(() {
      _isProcessing = true;
    });

    // Simulate server processing
    await Future.delayed(const Duration(seconds: 3));

    if (mounted) {
      setState(() {
        _showCardReaderWorkflow = false;
        _pinCreated = true;
        _currentStep = 1;
        _isProcessing = false;
        _remainingSeconds = 60;
      });

      _showSnackBar(
        _operationType == 'CREATE PIN'
            ? 'PIN created successfully!'
            : 'PIN changed successfully!',
        Colors.green,
      );

      // Restart idle timer setelah workflow selesai
      _startIdleTimer();
    }
  }

  void _handleCancel() {
    _workflowTimer?.cancel();
    _countdownTimer?.cancel();

    setState(() {
      _showCardReaderWorkflow = false;
      _currentStep = 1;
      _pinController.clear();
      _isProcessing = false;
      _remainingSeconds = 60;
    });

    // Restart idle timer setelah cancel
    _startIdleTimer();
  }

  void _showSnackBar(String message, Color color) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(message),
          backgroundColor: color,
        ),
      );
    }
  }

  // Existing logout methods remain the same
  void _showLogoutDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('LOGOFF'),
        content: const Text('Are you sure you want to LOGOFF?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              _handleLogout();
            },
            child: const Text('LOGOFF'),
          ),
        ],
      ),
    );
  }

  void _handleLogout() async {
    setState(() {
      _isLoggingOut = true;
    });

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => const AlertDialog(
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'LOGOFF',
              style: TextStyle(
                color: Colors.black,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 20),
            Text(
              'CONNECTING\nPROCESSING',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 16),
            CircularProgressIndicator(),
            SizedBox(height: 16),
            Text(
              'Terminal sedang melakukan koneksi dengan server',
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
      Navigator.pop(context);
      Navigator.pushReplacementNamed(context, '/login');
    }
  }
}
// logout_dialog.dart (Optional separate file for logout dialog)


class LogoutDialog extends StatefulWidget {
  const LogoutDialog({Key? key}) : super(key: key);

  @override
  State<LogoutDialog> createState() => _LogoutDialogState();
}

class _LogoutDialogState extends State<LogoutDialog> {
  bool _isProcessing = false;

  @override
  void initState() {
    super.initState();
    _startLogoutProcess();
  }

  void _startLogoutProcess() async {
    setState(() {
      _isProcessing = true;
    });

    await Future.delayed(const Duration(seconds: 2));

    if (mounted) {
      Navigator.pop(context, true);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(vertical: 12),
            decoration: BoxDecoration(
              color: Colors.black87,
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
          const CircularProgressIndicator(),
          const SizedBox(height: 16),
          const Text(
            'Terminal sedang melakukan koneksi dengan server',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 20),
          if (!_isProcessing) ...[
            const Text(
              'LOGOFF\nSUCCESS',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            const Text(
              'Jika Logoff berhasil, terminal akan menampilkan tulisan success',
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ],
      ),
    );
  }
}