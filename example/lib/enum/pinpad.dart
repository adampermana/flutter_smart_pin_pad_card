enum CardOperationType { createPin, changePin, otorisation }

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

enum PinEntryStep {
  enterCurrentPin,
  createPin,
  confirmPin,
}