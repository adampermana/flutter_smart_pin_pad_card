package com.adpstore.flutter_smart_pin_pad_cards;

import android.os.RemoteException;
import android.util.Log;
import android.os.Bundle;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.data.AidlErrorCode;
import java.util.HashMap;
import java.util.Map;

public class PinpadManager {
    private static final String TAG = "PinpadManager";
    private AidlPinpad pinpad;
    private static PinpadManager instance;

    // PIN Block format constants
    public static final int PIN_BLOCK_FORMAT_0 = 0; // ISO 9564-1 Format 0
    public static final int PIN_BLOCK_FORMAT_1 = 1; // ISO 9564-1 Format 1
    public static final int PIN_BLOCK_FORMAT_2 = 2; // ISO 9564-1 Format 2
    public static final int PIN_BLOCK_FORMAT_3 = 3; // ISO 9564-1 Format 3

    // Encryption algorithms
    public static final int ENCRYPT_3DES = 0;
    public static final int ENCRYPT_AES = 1;

    // Key types
    public static final int KEY_TYPE_PIK = 0;  // PIN encryption key
    public static final int KEY_TYPE_MAK = 1;  // MAC key
    public static final int KEY_TYPE_DEK = 2;  // Data encryption key

    // Encryption modes
    public static final byte MODE_ENCRYPT = 0;
    public static final byte MODE_DECRYPT = 1;

    private PinpadManager() {
        this.pinpad = DeviceServiceManagers.getInstance().getPinpadManager(0);
    }

    public static synchronized PinpadManager getInstance() {
        if (instance == null) {
            instance = new PinpadManager();
        }
        return instance;
    }

    /**
     * Initialize PIN pad
     * @return true if successful, false otherwise
     */
    public boolean initPinpad() {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad service not available");
                return false;
            }

            // Test if pinpad is accessible by checking key state
            boolean result = pinpad.getKeyState(KEY_TYPE_PIK, 0);
            Log.d(TAG, "Pinpad initialized successfully, key state check: " + result);
            return true;

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during pinpad init: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create PIN Block - Used for Create PIN operation (Processing Code 920000)
     * @param pin User entered PIN
     * @param cardNumber Card number (PAN)
     * @param format PIN Block format (0, 1, 2, 3)
     * @param keyIndex Key index for encryption
     * @param encryptionType Encryption algorithm (3DES, AES)
     * @return Map containing PIN Block data or error information
     */
    public Map<String, Object> createPinBlock(String pin, String cardNumber,
                                              int format, int keyIndex, int encryptionType) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (pinpad == null) {
                result.put("success", false);
                result.put("error", "Pinpad not initialized");
                result.put("responseCode", "91"); // Host Down
                return result;
            }

            // Validate PIN
            if (pin == null || pin.isEmpty()) {
                result.put("success", false);
                result.put("error", "PIN cannot be empty");
                result.put("responseCode", "21"); // No Action
                return result;
            }

            if (pin.length() < 4 || pin.length() > 12) {
                result.put("success", false);
                result.put("error", "PIN length must be between 4 and 12 digits");
                result.put("responseCode", "55"); // Incorrect PIN
                return result;
            }

            // Validate card number
            if (cardNumber == null || cardNumber.isEmpty()) {
                result.put("success", false);
                result.put("error", "Card number cannot be empty");
                result.put("responseCode", "21"); // No Action
                return result;
            }

            // Format card number (take last 12 digits and pad with zeros if needed)
            String formattedCardNumber = formatCardNumber(cardNumber);

            // Create PIN Block based on format
            byte[] pinBlock = null;

            switch (format) {
                case PIN_BLOCK_FORMAT_0:
                    pinBlock = createPinBlockFormat0(pin, formattedCardNumber, keyIndex, encryptionType);
                    break;
                case PIN_BLOCK_FORMAT_1:
                    pinBlock = createPinBlockFormat1(pin, keyIndex, encryptionType);
                    break;
                case PIN_BLOCK_FORMAT_2:
                    pinBlock = createPinBlockFormat2(pin, keyIndex, encryptionType);
                    break;
                case PIN_BLOCK_FORMAT_3:
                    pinBlock = createPinBlockFormat3(pin, formattedCardNumber, keyIndex, encryptionType);
                    break;
                default:
                    result.put("success", false);
                    result.put("error", "Unsupported PIN Block format: " + format);
                    result.put("responseCode", "21"); // No Action
                    return result;
            }

            if (pinBlock != null && pinBlock.length > 0) {
                result.put("success", true);
                result.put("pinBlock", bytesToHex(pinBlock));
                result.put("format", format);
                result.put("keyIndex", keyIndex);
                result.put("encryptionType", encryptionType);
                result.put("responseCode", "00"); // Success
                result.put("processingCode", "920000"); // Create PIN processing code
                Log.d(TAG, "PIN Block created successfully for Create PIN operation");
            } else {
                result.put("success", false);
                result.put("error", "Failed to create PIN Block");
                result.put("responseCode", "91"); // Host Down
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception during PIN Block creation: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91"); // Host Down
        }

        return result;
    }

    /**
     * Create PIN Block Format 0 (ISO 9564-1 Format 0) - Most common format
     */
    private byte[] createPinBlockFormat0(String pin, String cardNumber, int keyIndex, int encryptionType) {
        try {
            // Format: 0 + PIN_LENGTH + PIN + PADDING + XOR with PAN
            String pinLength = String.format("%01d", pin.length());
            String pinData = "0" + pinLength + pin;

            // Pad with F to make 16 characters (8 bytes)
            while (pinData.length() < 16) {
                pinData += "F";
            }

            // Get PAN block (rightmost 12 digits of PAN, excluding check digit, padded with zeros)
            String panBlock = "0000" + cardNumber.substring(Math.max(0, cardNumber.length() - 13), cardNumber.length() - 1);
            if (panBlock.length() > 16) {
                panBlock = panBlock.substring(panBlock.length() - 16);
            }
            while (panBlock.length() < 16) {
                panBlock = "0" + panBlock;
            }

            // XOR PIN block with PAN block
            byte[] pinBytes = hexToBytes(pinData);
            byte[] panBytes = hexToBytes(panBlock);
            byte[] xorResult = new byte[8];

            for (int i = 0; i < 8; i++) {
                xorResult[i] = (byte) (pinBytes[i] ^ panBytes[i]);
            }

            // Encrypt with specified algorithm
            return encryptPinBlock(xorResult, keyIndex, encryptionType);

        } catch (Exception e) {
            Log.e(TAG, "Error creating PIN Block Format 0: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create PIN Block Format 1 (ISO 9564-1 Format 1)
     */
    private byte[] createPinBlockFormat1(String pin, int keyIndex, int encryptionType) {
        try {
            // Format: 1 + PIN_LENGTH + PIN + RANDOM_PADDING
            String pinLength = String.format("%01d", pin.length());
            String pinData = "1" + pinLength + pin;

            // Pad with random hex digits to make 16 characters
            while (pinData.length() < 16) {
                pinData += String.format("%01X", (int) (Math.random() * 16));
            }

            byte[] pinBytes = hexToBytes(pinData);
            return encryptPinBlock(pinBytes, keyIndex, encryptionType);

        } catch (Exception e) {
            Log.e(TAG, "Error creating PIN Block Format 1: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create PIN Block Format 2 (ISO 9564-1 Format 2)
     */
    private byte[] createPinBlockFormat2(String pin, int keyIndex, int encryptionType) {
        try {
            // Format: 2 + PIN_LENGTH + PIN + PADDING_WITH_F
            String pinLength = String.format("%01d", pin.length());
            String pinData = "2" + pinLength + pin;

            // Pad with F to make 16 characters
            while (pinData.length() < 16) {
                pinData += "F";
            }

            byte[] pinBytes = hexToBytes(pinData);
            return encryptPinBlock(pinBytes, keyIndex, encryptionType);

        } catch (Exception e) {
            Log.e(TAG, "Error creating PIN Block Format 2: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create PIN Block Format 3 (ISO 9564-1 Format 3)
     */
    private byte[] createPinBlockFormat3(String pin, String cardNumber, int keyIndex, int encryptionType) {
        try {
            // Format: 3 + PIN_LENGTH + PIN + RANDOM_PADDING + XOR with PAN
            String pinLength = String.format("%01d", pin.length());
            String pinData = "3" + pinLength + pin;

            // Pad with random hex digits to make 16 characters
            while (pinData.length() < 16) {
                pinData += String.format("%01X", (int) (Math.random() * 16));
            }

            // Get PAN block
            String panBlock = "0000" + cardNumber.substring(Math.max(0, cardNumber.length() - 13), cardNumber.length() - 1);
            if (panBlock.length() > 16) {
                panBlock = panBlock.substring(panBlock.length() - 16);
            }
            while (panBlock.length() < 16) {
                panBlock = "0" + panBlock;
            }

            // XOR PIN block with PAN block
            byte[] pinBytes = hexToBytes(pinData);
            byte[] panBytes = hexToBytes(panBlock);
            byte[] xorResult = new byte[8];

            for (int i = 0; i < 8; i++) {
                xorResult[i] = (byte) (pinBytes[i] ^ panBytes[i]);
            }

            return encryptPinBlock(xorResult, keyIndex, encryptionType);

        } catch (Exception e) {
            Log.e(TAG, "Error creating PIN Block Format 3: " + e.getMessage());
            return null;
        }
    }

    /**
     * Encrypt PIN Block using encryptByTdk method
     */
    private byte[] encryptPinBlock(byte[] pinBlock, int keyIndex, int encryptionType) {
        try {
            byte[] encryptedBlock = new byte[8];
            byte[] iv = new byte[8]; // Initialization vector

            // Use encryptByTdk method from AidlPinpad
            int result = pinpad.encryptByTdk(keyIndex, MODE_ENCRYPT, iv, pinBlock, encryptedBlock);

            if (result == 0) { // Success
                return encryptedBlock;
            } else {
                Log.e(TAG, "Encryption failed with code: " + result + " - " + getErrorDescription(result));
                return null;
            }

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during encryption: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verify PIN with PIN Block
     */
    public Map<String, Object> verifyPin(String pinBlock, String cardNumber,
                                         int format, int keyIndex, int encryptionType) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (pinpad == null) {
                result.put("success", false);
                result.put("error", "Pinpad not initialized");
                result.put("responseCode", "91"); // Host Down
                return result;
            }

            byte[] pinBlockBytes = hexToBytes(pinBlock);
            String formattedCardNumber = formatCardNumber(cardNumber);

            // Decrypt PIN Block first
            byte[] decryptedBlock = decryptPinBlock(pinBlockBytes, keyIndex, encryptionType);

            if (decryptedBlock != null) {
                // Extract PIN from decrypted block based on format
                String extractedPin = extractPinFromBlock(decryptedBlock, formattedCardNumber, format);

                if (extractedPin != null) {
                    result.put("success", true);
                    result.put("pin", extractedPin);
                    result.put("pinLength", extractedPin.length());
                    result.put("responseCode", "00"); // Success
                } else {
                    result.put("success", false);
                    result.put("error", "Failed to extract PIN from block");
                    result.put("responseCode", "55"); // Incorrect PIN
                }
            } else {
                result.put("success", false);
                result.put("error", "Failed to decrypt PIN Block");
                result.put("responseCode", "55"); // Incorrect PIN
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception during PIN verification: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91"); // Host Down
        }

        return result;
    }

    /**
     * Decrypt PIN Block using cryptByTdk method
     */
    private byte[] decryptPinBlock(byte[] encryptedBlock, int keyIndex, int encryptionType) {
        try {
            byte[] decryptedBlock = new byte[8];
            byte[] iv = new byte[8]; // Initialization vector for decryption

            // Use cryptByTdk method from AidlPinpad for decryption
            int result = pinpad.cryptByTdk(keyIndex, MODE_DECRYPT, encryptedBlock, iv, decryptedBlock);

            if (result == 0) { // Success
                return decryptedBlock;
            } else {
                Log.e(TAG, "Decryption failed with code: " + result + " - " + getErrorDescription(result));
                return null;
            }

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during decryption: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract PIN from decrypted block based on format
     */
    private String extractPinFromBlock(byte[] decryptedBlock, String cardNumber, int format) {
        try {
            String blockHex = bytesToHex(decryptedBlock);

            if (format == PIN_BLOCK_FORMAT_0 || format == PIN_BLOCK_FORMAT_3) {
                // Need to XOR with PAN block first
                String panBlock = "0000" + cardNumber.substring(Math.max(0, cardNumber.length() - 13), cardNumber.length() - 1);
                if (panBlock.length() > 16) {
                    panBlock = panBlock.substring(panBlock.length() - 16);
                }
                while (panBlock.length() < 16) {
                    panBlock = "0" + panBlock;
                }

                byte[] panBytes = hexToBytes(panBlock);
                byte[] xorResult = new byte[8];

                for (int i = 0; i < 8; i++) {
                    xorResult[i] = (byte) (decryptedBlock[i] ^ panBytes[i]);
                }

                blockHex = bytesToHex(xorResult);
            }

            // Extract PIN length and PIN
            int pinLength = Integer.parseInt(blockHex.substring(1, 2));
            if (pinLength >= 4 && pinLength <= 12) {
                return blockHex.substring(2, 2 + pinLength);
            } else {
                Log.e(TAG, "Invalid PIN length: " + pinLength);
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error extracting PIN from block: " + e.getMessage());
            return null;
        }
    }

    /**
     * Change PIN (Ganti PIN) - Processing Code: 930000
     * @param oldPinBlock Encrypted old PIN block
     * @param newPinBlock Encrypted new PIN block
     * @param cardNumber Card number (PAN)
     * @param keyIndex Key index for encryption/decryption
     * @param encryptionType Encryption type (3DES/AES)
     * @return Map containing change PIN result
     */
    public Map<String, Object> changePin(String oldPinBlock, String newPinBlock,
                                         String cardNumber, int keyIndex, int encryptionType) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (pinpad == null) {
                result.put("success", false);
                result.put("error", "Pinpad not initialized");
                result.put("responseCode", "91"); // Host Down
                return result;
            }

            // Validate parameters
            if (oldPinBlock == null || newPinBlock == null || cardNumber == null) {
                result.put("success", false);
                result.put("error", "Missing required parameters");
                result.put("responseCode", "21"); // No Action
                return result;
            }

            // Verify old PIN first
            Map<String, Object> oldPinVerify = verifyPin(oldPinBlock, cardNumber,
                    PIN_BLOCK_FORMAT_0, keyIndex, encryptionType);

            if (!(Boolean) oldPinVerify.get("success")) {
                result.put("success", false);
                result.put("error", "Old PIN verification failed");
                result.put("responseCode", "55"); // Incorrect PIN
                return result;
            }

            // Decrypt new PIN to validate format
            byte[] newPinBlockBytes = hexToBytes(newPinBlock);
            byte[] decryptedNewPin = decryptPinBlock(newPinBlockBytes, keyIndex, encryptionType);

            if (decryptedNewPin == null) {
                result.put("success", false);
                result.put("error", "Failed to decrypt new PIN block");
                result.put("responseCode", "55"); // Incorrect PIN
                return result;
            }

            String formattedCardNumber = formatCardNumber(cardNumber);
            String extractedNewPin = extractPinFromBlock(decryptedNewPin, formattedCardNumber, PIN_BLOCK_FORMAT_0);

            if (extractedNewPin == null || extractedNewPin.length() < 4 || extractedNewPin.length() > 12) {
                result.put("success", false);
                result.put("error", "Invalid new PIN format");
                result.put("responseCode", "55"); // Incorrect PIN
                return result;
            }

            // In real implementation, this would communicate with host
            // For now, we'll assume success and return appropriate response

            result.put("success", true);
            result.put("message", "PIN changed successfully");
            result.put("responseCode", "00"); // Success
            result.put("processingCode", "930000"); // Change PIN processing code
            result.put("newPinLength", extractedNewPin.length());
            result.put("pinBlock", newPinBlock); // Return the new PIN block

            Log.d(TAG, "PIN change successful for card: " + maskCardNumber(cardNumber));

        } catch (Exception e) {
            Log.e(TAG, "Exception during PIN change: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91"); // Host Down
        }

        return result;
    }

    /**
     * PIN Authorization/Verification (Otorisasi PIN) - Processing Code: 940000
     * @param pinBlock Encrypted PIN block
     * @param cardNumber Card number (PAN)
     * @param amount Transaction amount (optional)
     * @param keyIndex Key index for decryption
     * @param encryptionType Encryption type (3DES/AES)
     * @return Map containing PIN authorization result
     */
    public Map<String, Object> authorizePin(String pinBlock, String cardNumber,
                                            Long amount, int keyIndex, int encryptionType) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (pinpad == null) {
                result.put("success", false);
                result.put("error", "Pinpad not initialized");
                result.put("responseCode", "91"); // Host Down
                return result;
            }

            // Validate parameters
            if (pinBlock == null || cardNumber == null) {
                result.put("success", false);
                result.put("error", "PIN block and card number are required");
                result.put("responseCode", "21"); // No Action
                return result;
            }

            // Verify PIN format and decrypt
            Map<String, Object> pinVerifyResult = verifyPin(pinBlock, cardNumber,
                    PIN_BLOCK_FORMAT_0, keyIndex, encryptionType);

            if (!(Boolean) pinVerifyResult.get("success")) {
                result.put("success", false);
                result.put("error", "PIN verification failed");
                result.put("responseCode", "55"); // Incorrect PIN
                result.put("remainingTries", getRemainingPinTries(cardNumber));
                return result;
            }

            String pin = (String) pinVerifyResult.get("pin");
            Integer pinLength = (Integer) pinVerifyResult.get("pinLength");

            // Validate PIN length
            if (pinLength < 4 || pinLength > 12) {
                result.put("success", false);
                result.put("error", "Invalid PIN length");
                result.put("responseCode", "55"); // Incorrect PIN
                return result;
            }

            // Create authorization request data
            Map<String, Object> authData = new HashMap<>();
            authData.put("cardNumber", maskCardNumber(cardNumber));
            authData.put("pinLength", pinLength);
            authData.put("processingCode", "940000"); // PIN authorization processing code
            if (amount != null) {
                authData.put("amount", amount);
            }
            authData.put("timestamp", System.currentTimeMillis());

            // In real implementation, this would send to host for authorization
            // For simulation, we'll assume successful authorization

            result.put("success", true);
            result.put("message", "PIN authorization successful");
            result.put("responseCode", "00"); // Success
            result.put("processingCode", "940000");
            result.put("authorizationData", authData);
            result.put("pinLength", pinLength);
            result.put("pinBlock", pinBlock); // Return the PIN block used

            Log.d(TAG, "PIN authorization successful for card: " + maskCardNumber(cardNumber));

        } catch (Exception e) {
            Log.e(TAG, "Exception during PIN authorization: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91"); // Host Down
        }

        return result;
    }

    /**
     * Load main key into the pinpad
     */
    public boolean loadMainKey(int keyIndex, byte[] keyData, byte[] checkValue) {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return false;
            }

            return pinpad.loadMainkey(keyIndex, keyData, checkValue);

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during key loading: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load work key into the pinpad
     */
    public boolean loadWorkKey(int keyType, int masterKeyId, int workKeyId,
                               byte[] keyData, byte[] checkValue) {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return false;
            }

            return pinpad.loadWorkKey(keyType, masterKeyId, workKeyId, keyData, checkValue);

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during work key loading: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get key state
     */
    public boolean getKeyState(int keyType, int keyIndex) {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return false;
            }

            return pinpad.getKeyState(keyType, keyIndex);

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during key state check: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get MAC (Message Authentication Code)
     */
    public Map<String, Object> getMac(Bundle param) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (pinpad == null) {
                result.put("success", false);
                result.put("error", "Pinpad not initialized");
                return result;
            }

            byte[] mac = new byte[8];
            int macResult = pinpad.getMac(param, mac);

            if (macResult == 0) {
                result.put("success", true);
                result.put("mac", bytesToHex(mac));
            } else {
                result.put("success", false);
                result.put("error", "Failed to get MAC, code: " + macResult + " - " + getErrorDescription(macResult));
            }

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during MAC generation: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get random number from pinpad
     */
    public byte[] getRandom() {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return null;
            }

            return pinpad.getRandom();

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException during random number generation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get remaining PIN tries (simulation)
     */
    private int getRemainingPinTries(String cardNumber) {
        // Simulate remaining tries - in real implementation,
        // this would be retrieved from card or host system
        return 3; // Default remaining tries
    }

    /**
     * Format card number for PIN Block processing
     */
    private String formatCardNumber(String cardNumber) {
        // Remove any non-numeric characters
        String cleanCardNumber = cardNumber.replaceAll("[^0-9]", "");

        // Ensure we have at least 13 digits
        if (cleanCardNumber.length() < 13) {
            // Pad with zeros on the left
            while (cleanCardNumber.length() < 13) {
                cleanCardNumber = "0" + cleanCardNumber;
            }
        }

        return cleanCardNumber;
    }

    /**
     * Mask card number for logging
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }

        String cleanCardNumber = cardNumber.replaceAll("[^0-9]", "");
        if (cleanCardNumber.length() < 4) {
            return "****";
        }

        String lastFour = cleanCardNumber.substring(cleanCardNumber.length() - 4);
        StringBuilder masked = new StringBuilder();

        for (int i = 0; i < cleanCardNumber.length() - 4; i++) {
            masked.append("*");
        }
        masked.append(lastFour);

        return masked.toString();
    }

    /**
     * Convert byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    /**
     * Convert hex string to byte array
     */
    private byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }

        // Remove any spaces or separators
        hex = hex.replaceAll("\\s+", "").replaceAll("-", "");

        int len = hex.length();
        if (len % 2 != 0) {
            hex = "0" + hex; // Pad with leading zero if odd length
            len = hex.length();
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Get error description based on error code
     */
    private String getErrorDescription(int errorCode) {
        switch (errorCode) {
            case 0:
                return "Success";
            case AidlErrorCode.Pinpad.ERROR_NODEV:
                return "No Device";
            case AidlErrorCode.Pinpad.ERROR_INPUTTIMES:
                return "Input Times Error";
            case AidlErrorCode.Pinpad.ERROR_KEYTYPE:
                return "Key Type Error";
            case AidlErrorCode.Pinpad.ERROR_TIMEOUT:
                return "Timeout";
            case AidlErrorCode.Pinpad.ERROR_UNKNOWN:
                return "Unknown Error";
            case AidlErrorCode.Pinpad.ERROR_MAC:
                return "MAC Error";
            case AidlErrorCode.Pinpad.ERROR_ENCRYPT:
                return "Encryption Error";
            case AidlErrorCode.Pinpad.ERROR_INPUT_LEN:
                return "Input Length Error";
            case AidlErrorCode.Pinpad.ERROR_INPUT_PARAM:
                return "Input Parameter Error";
            case AidlErrorCode.Pinpad.ERROR_PINPAD_NO_SUPPORT:
                return "Pinpad Not Supported";
            default:
                return "Unknown Status: " + errorCode;
        }
    }

    /**
     * Close PIN pad - not available in AidlPinpad interface
     */
    public void closePinpad() {
        Log.d(TAG, "Pinpad closed (no actual close method in AidlPinpad)");
    }

    /**
     * Get PIN pad status
     */
    public Map<String, Object> getPinpadStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            if (pinpad == null) {
                status.put("available", false);
                status.put("error", "Pinpad service not available");
                return status;
            }

            // Test if pinpad is working by checking a key state
            boolean keyState = pinpad.getKeyState(KEY_TYPE_PIK, 0);
            status.put("available", true);
            status.put("keyState", keyState);
            status.put("statusText", "Pinpad service available");

        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException getting pinpad status: " + e.getMessage());
            status.put("available", false);
            status.put("error", "RemoteException: " + e.getMessage());
        }

        return status;
    }
}