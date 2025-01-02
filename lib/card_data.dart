// class CardData {
//   final String cardNumber;
//   final String expiryDate;
//   final String track1;
//   final String track2;
//   final String track3;
//
//   CardData({
//     required this.cardNumber,
//     required this.expiryDate,
//     required this.track1,
//     required this.track2,
//     required this.track3,
//   });
//
//   factory CardData.fromMap(Map<String, dynamic> map) {
//     return CardData(
//       cardNumber: map['cardNumber'] ?? '',
//       expiryDate: map['expiryDate'] ?? '',
//       track1: map['track1'] ?? '',
//       track2: map['track2'] ?? '',
//       track3: map['track3'] ?? '',
//     );
//   }
//
//   Map<String, dynamic> toMap() {
//     return {
//       'cardNumber': cardNumber,
//       'expiryDate': expiryDate,
//       'track1': track1,
//       'track2': track2,
//       'track3': track3,
//     };
//   }
// }
