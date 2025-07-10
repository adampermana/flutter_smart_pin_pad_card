// // login_page.dart
// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';
//
// class LoginPage extends StatefulWidget {
//   const LoginPage({Key? key}) : super(key: key);
//
//   @override
//   State<LoginPage> createState() => _LoginPageState();
// }
//
// class _LoginPageState extends State<LoginPage> with TickerProviderStateMixin {
//   final TextEditingController _passwordController = TextEditingController();
//   final FocusNode _passwordFocusNode = FocusNode();
//
//   bool _isLoading = false;
//   bool _isObscured = true;
//   bool _showSuccess = false;
//
//   late AnimationController _animationController;
//   late Animation<double> _fadeAnimation;
//
//   @override
//   void initState() {
//     super.initState();
//     _animationController = AnimationController(
//       duration: const Duration(milliseconds: 1000),
//       vsync: this,
//     );
//     _fadeAnimation = Tween<double>(
//       begin: 0.0,
//       end: 1.0,
//     ).animate(CurvedAnimation(
//       parent: _animationController,
//       curve: Curves.easeInOut,
//     ));
//     _animationController.forward();
//   }
//
//   @override
//   void dispose() {
//     _passwordController.dispose();
//     _passwordFocusNode.dispose();
//     _animationController.dispose();
//     super.dispose();
//   }
//
//   void _handleLogin() async {
//     // Unfocus keyboard
//     _passwordFocusNode.unfocus();
//
//     if (_passwordController.text.isEmpty) {
//       _showSnackBar('Please enter password', Colors.orange);
//       return;
//     }
//
//     setState(() {
//       _isLoading = true;
//       _showSuccess = false;
//     });
//
//     // Simulate server connection
//     await Future.delayed(const Duration(seconds: 2));
//
//     // Simple password validation (replace with actual authentication)
//     if (_passwordController.text == '1234') {
//       setState(() {
//         _isLoading = false;
//         _showSuccess = true;
//       });
//
//       // Show success state briefly
//       await Future.delayed(const Duration(seconds: 1));
//
//       // Navigate to home screen
//       if (mounted) {
//         Navigator.pushReplacementNamed(context, '/home');
//       }
//     } else {
//       setState(() {
//         _isLoading = false;
//       });
//       _showSnackBar('Invalid password. Please try again.', Colors.red);
//       _passwordController.clear();
//     }
//   }
//
//   void _showSnackBar(String message, Color color) {
//     if (mounted) {
//       ScaffoldMessenger.of(context).showSnackBar(
//         SnackBar(
//           content: Text(message),
//           backgroundColor: color,
//           duration: const Duration(seconds: 3),
//           behavior: SnackBarBehavior.floating,
//         ),
//       );
//     }
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       backgroundColor: Colors.grey.shade50,
//       body: SafeArea(
//         child: FadeTransition(
//           opacity: _fadeAnimation,
//           child: Center(
//             child: SingleChildScrollView(
//               padding: const EdgeInsets.all(24),
//               child: Column(
//                 mainAxisSize: MainAxisSize.min,
//                 crossAxisAlignment: CrossAxisAlignment.center,
//                 children: [
//                   _buildHeader(),
//                   const SizedBox(height: 40),
//                   _buildLoginCard(),
//                   const SizedBox(height: 20),
//                   _buildFooter(),
//                 ],
//               ),
//             ),
//           ),
//         ),
//       ),
//     );
//   }
//
//   Widget _buildHeader() {
//     return Column(
//       children: [
//         Container(
//           padding: const EdgeInsets.all(20),
//           decoration: BoxDecoration(
//             color: Colors.white,
//             shape: BoxShape.circle,
//             boxShadow: [
//               BoxShadow(
//                 color: Colors.black.withOpacity(0.1),
//                 blurRadius: 10,
//                 offset: const Offset(0, 5),
//               ),
//             ],
//           ),
//           child: const Icon(
//             Icons.credit_card,
//             size: 60,
//             color: Color(0xFF0D4575),
//           ),
//         ),
//         const SizedBox(height: 24),
//         const Text(
//           'BANK JATENG',
//           style: TextStyle(
//             fontSize: 32,
//             fontWeight: FontWeight.bold,
//             color: Color(0xFF0D4575),
//             letterSpacing: 2,
//           ),
//           textAlign: TextAlign.center,
//         ),
//         const SizedBox(height: 8),
//         Text(
//           'Smart Pin Pad System',
//           style: TextStyle(
//             fontSize: 18,
//             color: Colors.grey.shade600,
//             fontWeight: FontWeight.w500,
//           ),
//           textAlign: TextAlign.center,
//         ),
//       ],
//     );
//   }
//
//   Widget _buildLoginCard() {
//     return Card(
//       elevation: 8,
//       shadowColor: Colors.black.withOpacity(0.3),
//       shape: RoundedRectangleBorder(
//         borderRadius: BorderRadius.circular(20),
//       ),
//       child: Container(
//         width: double.infinity,
//         padding: const EdgeInsets.all(28),
//         child: Column(
//           mainAxisSize: MainAxisSize.min,
//           children: [
//             _buildCardHeader(),
//             const SizedBox(height: 24),
//             if (_isLoading)
//               _buildLoadingState()
//             else if (_showSuccess)
//               _buildSuccessState()
//             else
//               _buildInputState(),
//           ],
//         ),
//       ),
//     );
//   }
//
//   Widget _buildCardHeader() {
//     return Container(
//       width: double.infinity,
//       padding: const EdgeInsets.symmetric(vertical: 16),
//       decoration: BoxDecoration(
//         gradient: const LinearGradient(
//           colors: [Color(0xFF0D4575), Color(0xFF1a5490)],
//           begin: Alignment.topLeft,
//           end: Alignment.bottomRight,
//         ),
//         borderRadius: BorderRadius.circular(12),
//         boxShadow: [
//           BoxShadow(
//             color: const Color(0xFF0D4575).withOpacity(0.3),
//             blurRadius: 8,
//             offset: const Offset(0, 4),
//           ),
//         ],
//       ),
//       child: Row(
//         mainAxisAlignment: MainAxisAlignment.center,
//         children: [
//           const Icon(
//             Icons.login,
//             color: Colors.white,
//             size: 20,
//           ),
//           const SizedBox(width: 8),
//           const Text(
//             'LOGON',
//             style: TextStyle(
//               color: Colors.white,
//               fontSize: 18,
//               fontWeight: FontWeight.bold,
//               letterSpacing: 1.5,
//             ),
//           ),
//         ],
//       ),
//     );
//   }
//
//   Widget _buildLoadingState() {
//     return Column(
//       children: [
//         Container(
//           padding: const EdgeInsets.all(20),
//           decoration: BoxDecoration(
//             color: Colors.blue.shade50,
//             borderRadius: BorderRadius.circular(12),
//             border: Border.all(color: Colors.blue.shade200),
//           ),
//           child: Column(
//             children: [
//               const Text(
//                 'CONNECTING',
//                 style: TextStyle(
//                   fontSize: 18,
//                   fontWeight: FontWeight.bold,
//                   color: Color(0xFF0D4575),
//                 ),
//                 textAlign: TextAlign.center,
//               ),
//               const Text(
//                 'PROCESSING',
//                 style: TextStyle(
//                   fontSize: 18,
//                   fontWeight: FontWeight.bold,
//                   color: Color(0xFF0D4575),
//                 ),
//                 textAlign: TextAlign.center,
//               ),
//               const SizedBox(height: 20),
//               SizedBox(
//                 width: 40,
//                 height: 40,
//                 child: CircularProgressIndicator(
//                   valueColor: AlwaysStoppedAnimation<Color>(
//                     Theme.of(context).primaryColor,
//                   ),
//                   strokeWidth: 3,
//                 ),
//               ),
//             ],
//           ),
//         ),
//         const SizedBox(height: 16),
//         Container(
//           padding: const EdgeInsets.all(12),
//           decoration: BoxDecoration(
//             color: Colors.orange.shade50,
//             borderRadius: BorderRadius.circular(8),
//             border: Border.all(color: Colors.orange.shade200),
//           ),
//           child: Row(
//             children: [
//               Icon(
//                 Icons.info_outline,
//                 color: Colors.orange.shade700,
//                 size: 20,
//               ),
//               const SizedBox(width: 8),
//               Expanded(
//                 child: Text(
//                   'Terminal sedang melakukan koneksi dengan server',
//                   style: TextStyle(
//                     fontSize: 14,
//                     color: Colors.orange.shade700,
//                   ),
//                 ),
//               ),
//             ],
//           ),
//         ),
//       ],
//     );
//   }
//
//   Widget _buildSuccessState() {
//     return Column(
//       children: [
//         Container(
//           padding: const EdgeInsets.all(20),
//           decoration: BoxDecoration(
//             color: Colors.green.shade50,
//             borderRadius: BorderRadius.circular(12),
//             border: Border.all(color: Colors.green.shade200),
//           ),
//           child: Column(
//             children: [
//               Icon(
//                 Icons.check_circle_outline,
//                 color: Colors.green.shade600,
//                 size: 50,
//               ),
//               const SizedBox(height: 16),
//               const Text(
//                 'LOGON',
//                 style: TextStyle(
//                   fontSize: 18,
//                   fontWeight: FontWeight.bold,
//                   color: Colors.green,
//                 ),
//                 textAlign: TextAlign.center,
//               ),
//               const Text(
//                 'SUCCESS',
//                 style: TextStyle(
//                   fontSize: 18,
//                   fontWeight: FontWeight.bold,
//                   color: Colors.green,
//                 ),
//                 textAlign: TextAlign.center,
//               ),
//             ],
//           ),
//         ),
//         const SizedBox(height: 16),
//         Container(
//           padding: const EdgeInsets.all(12),
//           decoration: BoxDecoration(
//             color: Colors.green.shade50,
//             borderRadius: BorderRadius.circular(8),
//             border: Border.all(color: Colors.green.shade200),
//           ),
//           child: Row(
//             children: [
//               Icon(
//                 Icons.check_circle,
//                 color: Colors.green.shade700,
//                 size: 20,
//               ),
//               const SizedBox(width: 8),
//               Expanded(
//                 child: Text(
//                   'Jika Logon berhasil, terminal akan menampilkan tulisan success',
//                   style: TextStyle(
//                     fontSize: 14,
//                     color: Colors.green.shade700,
//                   ),
//                 ),
//               ),
//             ],
//           ),
//         ),
//       ],
//     );
//   }
//
//   Widget _buildInputState() {
//     return Column(
//       crossAxisAlignment: CrossAxisAlignment.start,
//       children: [
//         const Text(
//           'INPUT PASSWORD',
//           style: TextStyle(
//             fontSize: 14,
//             fontWeight: FontWeight.bold,
//             color: Colors.grey,
//             letterSpacing: 0.5,
//           ),
//         ),
//         const SizedBox(height: 12),
//         Container(
//           decoration: BoxDecoration(
//             borderRadius: BorderRadius.circular(12),
//             boxShadow: [
//               BoxShadow(
//                 color: Colors.black.withOpacity(0.1),
//                 blurRadius: 4,
//                 offset: const Offset(0, 2),
//               ),
//             ],
//           ),
//           child: TextField(
//             controller: _passwordController,
//             focusNode: _passwordFocusNode,
//             obscureText: _isObscured,
//             keyboardType: TextInputType.number,
//             inputFormatters: [
//               FilteringTextInputFormatter.digitsOnly,
//               LengthLimitingTextInputFormatter(6),
//             ],
//             style: const TextStyle(
//               fontSize: 18,
//               fontWeight: FontWeight.w500,
//               letterSpacing: 2,
//             ),
//             decoration: InputDecoration(
//               hintText: '••••',
//               hintStyle: TextStyle(
//                 color: Colors.grey.shade400,
//                 fontSize: 20,
//               ),
//               filled: true,
//               fillColor: Colors.white,
//               border: OutlineInputBorder(
//                 borderRadius: BorderRadius.circular(12),
//                 borderSide: BorderSide.none,
//               ),
//               enabledBorder: OutlineInputBorder(
//                 borderRadius: BorderRadius.circular(12),
//                 borderSide: BorderSide(color: Colors.grey.shade300),
//               ),
//               focusedBorder: OutlineInputBorder(
//                 borderRadius: BorderRadius.circular(12),
//                 borderSide: const BorderSide(color: Color(0xFF0D4575), width: 2),
//               ),
//               contentPadding: const EdgeInsets.symmetric(
//                 horizontal: 20,
//                 vertical: 16,
//               ),
//               suffixIcon: IconButton(
//                 icon: Icon(
//                   _isObscured ? Icons.visibility_off : Icons.visibility,
//                   color: Colors.grey.shade600,
//                 ),
//                 onPressed: () {
//                   setState(() {
//                     _isObscured = !_isObscured;
//                   });
//                 },
//               ),
//             ),
//             onSubmitted: (_) => _handleLogin(),
//           ),
//         ),
//         const SizedBox(height: 12),
//         Container(
//           padding: const EdgeInsets.all(10),
//           decoration: BoxDecoration(
//             color: Colors.blue.shade50,
//             borderRadius: BorderRadius.circular(8),
//             border: Border.all(color: Colors.blue.shade200),
//           ),
//           child: Row(
//             children: [
//               Icon(
//                 Icons.info_outline,
//                 color: Colors.blue.shade700,
//                 size: 16,
//               ),
//               const SizedBox(width: 8),
//               Expanded(
//                 child: Text(
//                   'Input password untuk logon',
//                   style: TextStyle(
//                     fontSize: 13,
//                     color: Colors.blue.shade700,
//                   ),
//                 ),
//               ),
//             ],
//           ),
//         ),
//         const SizedBox(height: 24),
//         SizedBox(
//           width: double.infinity,
//           child: ElevatedButton(
//             onPressed: _handleLogin,
//             style: ElevatedButton.styleFrom(
//               backgroundColor: const Color(0xFF0D4575),
//               foregroundColor: Colors.white,
//               padding: const EdgeInsets.symmetric(vertical: 16),
//               shape: RoundedRectangleBorder(
//                 borderRadius: BorderRadius.circular(12),
//               ),
//               elevation: 4,
//             ),
//             child: const Row(
//               mainAxisAlignment: MainAxisAlignment.center,
//               children: [
//                 Icon(Icons.login, size: 20),
//                 SizedBox(width: 8),
//                 Text(
//                   'LOGIN',
//                   style: TextStyle(
//                     fontSize: 16,
//                     fontWeight: FontWeight.bold,
//                     letterSpacing: 1,
//                   ),
//                 ),
//               ],
//             ),
//           ),
//         ),
//       ],
//     );
//   }
//
//   Widget _buildFooter() {
//     return Column(
//       children: [
//         Container(
//           padding: const EdgeInsets.all(16),
//           decoration: BoxDecoration(
//             color: Colors.amber.shade50,
//             borderRadius: BorderRadius.circular(12),
//             border: Border.all(color: Colors.amber.shade200),
//           ),
//           child: Column(
//             children: [
//               Row(
//                 mainAxisAlignment: MainAxisAlignment.center,
//                 children: [
//                   Icon(
//                     Icons.key,
//                     color: Colors.amber.shade700,
//                     size: 20,
//                   ),
//                   const SizedBox(width: 8),
//                   Text(
//                     'Demo Information',
//                     style: TextStyle(
//                       fontSize: 14,
//                       fontWeight: FontWeight.bold,
//                       color: Colors.amber.shade700,
//                     ),
//                   ),
//                 ],
//               ),
//               const SizedBox(height: 8),
//               Text(
//                 'Default password: 1234',
//                 style: TextStyle(
//                   fontSize: 13,
//                   color: Colors.amber.shade700,
//                   fontStyle: FontStyle.italic,
//                 ),
//               ),
//             ],
//           ),
//         ),
//         const SizedBox(height: 16),
//         Text(
//           'Bank Jateng Smart Pin Pad System v1.0',
//           style: TextStyle(
//             fontSize: 12,
//             color: Colors.grey.shade500,
//           ),
//         ),
//       ],
//     );
//   }
// }
//
// // Alternative simpler version if you prefer minimal design
// class SimpleLoginPage extends StatefulWidget {
//   const SimpleLoginPage({Key? key}) : super(key: key);
//
//   @override
//   State<SimpleLoginPage> createState() => _SimpleLoginPageState();
// }
//
// class _SimpleLoginPageState extends State<SimpleLoginPage> {
//   final TextEditingController _passwordController = TextEditingController();
//   bool _isLoading = false;
//   bool _isObscured = true;
//
//   @override
//   void dispose() {
//     _passwordController.dispose();
//     super.dispose();
//   }
//
//   void _handleLogin() async {
//     if (_passwordController.text.isEmpty) {
//       ScaffoldMessenger.of(context).showSnackBar(
//         const SnackBar(
//           content: Text('Please enter password'),
//           backgroundColor: Colors.orange,
//         ),
//       );
//       return;
//     }
//
//     setState(() {
//       _isLoading = true;
//     });
//
//     await Future.delayed(const Duration(seconds: 2));
//
//     if (_passwordController.text == '1234') {
//       Navigator.pushReplacementNamed(context, '/home');
//     } else {
//       setState(() {
//         _isLoading = false;
//       });
//       ScaffoldMessenger.of(context).showSnackBar(
//         const SnackBar(
//           content: Text('Invalid password'),
//           backgroundColor: Colors.red,
//         ),
//       );
//       _passwordController.clear();
//     }
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: SafeArea(
//         child: Center(
//           child: SingleChildScrollView(
//             padding: const EdgeInsets.all(24),
//             child: Column(
//               mainAxisSize: MainAxisSize.min,
//               children: [
//                 const Icon(
//                   Icons.credit_card,
//                   size: 80,
//                   color: Color(0xFF0D4575),
//                 ),
//                 const SizedBox(height: 24),
//                 const Text(
//                   'BANK JATENG',
//                   style: TextStyle(
//                     fontSize: 28,
//                     fontWeight: FontWeight.bold,
//                     color: Color(0xFF0D4575),
//                   ),
//                 ),
//                 const SizedBox(height: 40),
//                 Card(
//                   elevation: 4,
//                   shape: RoundedRectangleBorder(
//                     borderRadius: BorderRadius.circular(16),
//                   ),
//                   child: Padding(
//                     padding: const EdgeInsets.all(24),
//                     child: Column(
//                       children: [
//                         Container(
//                           width: double.infinity,
//                           padding: const EdgeInsets.symmetric(vertical: 12),
//                           decoration: BoxDecoration(
//                             color: Colors.black87,
//                             borderRadius: BorderRadius.circular(8),
//                           ),
//                           child: const Text(
//                             'LOGON',
//                             style: TextStyle(
//                               color: Colors.white,
//                               fontSize: 16,
//                               fontWeight: FontWeight.bold,
//                             ),
//                             textAlign: TextAlign.center,
//                           ),
//                         ),
//                         const SizedBox(height: 20),
//                         if (_isLoading) ...[
//                           const Text(
//                             'CONNECTING\nPROCESSING',
//                             style: TextStyle(
//                               fontSize: 16,
//                               fontWeight: FontWeight.bold,
//                             ),
//                             textAlign: TextAlign.center,
//                           ),
//                           const SizedBox(height: 16),
//                           const CircularProgressIndicator(),
//                         ] else ...[
//                           const Align(
//                             alignment: Alignment.centerLeft,
//                             child: Text(
//                               'INPUT PASSWORD',
//                               style: TextStyle(
//                                 fontSize: 14,
//                                 fontWeight: FontWeight.bold,
//                                 color: Colors.grey,
//                               ),
//                             ),
//                           ),
//                           const SizedBox(height: 8),
//                           TextField(
//                             controller: _passwordController,
//                             obscureText: _isObscured,
//                             decoration: InputDecoration(
//                               hintText: '****',
//                               border: OutlineInputBorder(
//                                 borderRadius: BorderRadius.circular(8),
//                               ),
//                               suffixIcon: IconButton(
//                                 icon: Icon(
//                                   _isObscured ? Icons.visibility : Icons.visibility_off,
//                                 ),
//                                 onPressed: () {
//                                   setState(() {
//                                     _isObscured = !_isObscured;
//                                   });
//                                 },
//                               ),
//                             ),
//                             onSubmitted: (_) => _handleLogin(),
//                           ),
//                           const SizedBox(height: 20),
//                           SizedBox(
//                             width: double.infinity,
//                             child: ElevatedButton(
//                               onPressed: _handleLogin,
//                               style: ElevatedButton.styleFrom(
//                                 backgroundColor: const Color(0xFF0D4575),
//                                 foregroundColor: Colors.white,
//                                 padding: const EdgeInsets.symmetric(vertical: 16),
//                               ),
//                               child: const Text(
//                                 'LOGIN',
//                                 style: TextStyle(
//                                   fontSize: 16,
//                                   fontWeight: FontWeight.bold,
//                                 ),
//                               ),
//                             ),
//                           ),
//                         ],
//                       ],
//                     ),
//                   ),
//                 ),
//               ],
//             ),
//           ),
//         ),
//       ),
//     );
//   }
// }