class CardData {
  final String cardNumber;
  final String expiryDate;
  final String track1;
  final String track2;
  final String track3;
  final String cardType; // IC, RF, MAG
  final Map<String, dynamic>? emvData; // Untuk data tambahan EMV jika ada

  CardData({
    required this.cardNumber,
    required this.expiryDate,
    this.track1 = '',
    this.track2 = '',
    this.track3 = '',
    this.cardType = '',
    this.emvData,
  });

  factory CardData.fromMap(Map<String, dynamic> map) {
    return CardData(
      cardNumber: map['pan'] ?? map['cardNumber'] ?? '',
      expiryDate: map['expiryDate'] ?? '',
      track1: map['track1'] ?? '',
      track2: map['track2'] ?? '',
      track3: map['track3'] ?? '',
      cardType: map['cardType'] ?? '',
      emvData: map['emvData'] != null
          ? Map<String, dynamic>.from(map['emvData'])
          : null,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'cardNumber': cardNumber,
      'pan': cardNumber, // Untuk kompatibilitas dengan kedua format
      'expiryDate': expiryDate,
      'track1': track1,
      'track2': track2,
      'track3': track3,
      'cardType': cardType,
      if (emvData != null) 'emvData': emvData,
    };
  }

  /// Format nomor kartu dengan memaskir semua kecuali 4 digit terakhir
  String getFormattedCardNumber() {
    if (cardNumber.isEmpty) return '';
    if (cardNumber.length < 4) return cardNumber;

    final lastFour = cardNumber.substring(cardNumber.length - 4);
    return '•••• •••• •••• $lastFour';
  }

  /// Format tanggal kedaluwarsa dari format YYMMDD menjadi MM/YY
  String getFormattedExpiryDate() {
    if (expiryDate.isEmpty) return '';

    if (expiryDate.length >= 4) {
      // Ambil bulan (posisi 2-3)
      final month = expiryDate.substring(2, 4);
      // Ambil tahun (posisi 0-1)
      final year = expiryDate.substring(0, 2);
      return '$month/$year';
    }
    return expiryDate;
  }
}