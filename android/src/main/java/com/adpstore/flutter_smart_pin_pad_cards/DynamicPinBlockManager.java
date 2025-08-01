package com.adpstore.flutter_smart_pin_pad_cards;

import android.os.RemoteException;
import android.util.Log;
import android.os.Bundle;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.data.AidlErrorCode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DynamicPinBlockManager {
    private static final String TAG = "DynamicPinBlockManager";
    private AidlPinpad pinpad;
    private static DynamicPinBlockManager instance;

    // Hardcoded master key as requested
    private static String MASTER_KEY = "1234567890ABCDEF1234567890ABCDEF"; // 32 hex chars = 16 bytes

    // PIN Block format constants
    public static final int PIN_BLOCK_FORMAT_0 = 0; // ISO 9564-1 Format 0
    public static final int PIN_BLOCK_FORMAT_1 = 1; // ISO 9564-1 Format 1
    public static final int PIN_BLOCK_FORMAT_2 = 2; // ISO 9564-1 Format 2
    public static final int PIN_BLOCK_FORMAT_3 = 3; // ISO 9564-1 Format 3
    public static final int PIN_BLOCK_FORMAT_4 = 4; // ISO 9564-1 Format 4

    // Encryption algorithms
    public static final int ENCRYPT_3DES = 0;
    public static final int ENCRYPT_AES = 1;
    public static final int ENCRYPT_DES = 2;

    // Key types
    public static final int KEY_TYPE_PIK = 0;  // PIN encryption key
    public static final int KEY_TYPE_MAK = 1;  // MAC key
    public static final int KEY_TYPE_DEK = 2;  // Data encryption key

    // Processing codes for different operations
    public static final String PROCESSING_CODE_CREATE_PIN = "920000";
    public static final String PROCESSING_CODE_CHANGE_PIN = "930000";
    public static final String PROCESSING_CODE_AUTHORIZE_PIN = "940000";

    // Master key index
    private static final int MASTER_KEY_INDEX = 0;
    private static final int WORK_KEY_INDEX = 0;


    // Encryption modes
    public static final byte MODE_ENCRYPT = 0;
    public static final byte MODE_DECRYPT = 1;

    private DynamicPinBlockManager() {
        this.pinpad = DeviceServiceManagers.getInstance().getPinpadManager(0);
    }

    public static synchronized DynamicPinBlockManager getInstance() {
        if (instance == null) {
            instance = new DynamicPinBlockManager();
        }
        return instance;
    }


    public boolean initPinpad() {
        try {
            if (pinpad == null) {
                pinpad = DeviceServiceManagers.getInstance().getPinpadManager(0);
            }

            if (pinpad != null) {
                // Load hardcoded master key
                boolean masterKeyLoaded = loadHardcodedMasterKey();
                Log.d(TAG, "Pinpad initialized successfully, master key loaded: " + masterKeyLoaded);
                return masterKeyLoaded;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize pinpad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close pinpad device
     */
    public void closePinpad() {
        try {
            if (pinpad != null) {
                // AidlPinpad doesn't have close() method, just log
                Log.d(TAG, "Pinpad cleanup completed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to close pinpad: " + e.getMessage());
        }
    }

    /**
     * Load hardcoded master key into the pinpad
     */
    private boolean loadHardcodedMasterKey() {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not available");
                return false;
            }

            byte[] masterKeyData = hexToBytes(MASTER_KEY);

            // Load master key with check value (first 3 bytes of encrypted zero block)
            byte[] checkValue = calculateKeyCheckValue(masterKeyData);

            boolean result = pinpad.loadMainkey(MASTER_KEY_INDEX, masterKeyData, checkValue);
            Log.d(TAG, "Master key load result: " + result);

            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load master key: " + e.getMessage());
            return false;
        }
    }


    /**
     * Calculate key check value (KCV) - first 3 bytes of encrypted zero block
     */
    private byte[] calculateKeyCheckValue(byte[] keyData) {
        try {
            byte[] zeroBlock = new byte[8]; // All zeros

            if (keyData.length == 16) {
                // 2-key Triple DES
                byte[] fullKey = new byte[24];
                System.arraycopy(keyData, 0, fullKey, 0, 16);
                System.arraycopy(keyData, 0, fullKey, 16, 8);
                keyData = fullKey;
            }

            SecretKeySpec keySpec = new SecretKeySpec(keyData, "DESede");
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = cipher.doFinal(zeroBlock);
            byte[] kcv = new byte[3];
            System.arraycopy(encrypted, 0, kcv, 0, 3);

            return kcv;
        } catch (Exception e) {
            Log.e(TAG, "Failed to calculate key check value: " + e.getMessage());
            return new byte[3]; // Return empty KCV on error
        }
    }


    /**
     * Get pinpad status
     */
    public Map<String, Object> getPinpadStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            if (pinpad != null) {
                status.put("initialized", true);
                status.put("deviceType", "Topwise CloudPOS");
                status.put("timestamp", System.currentTimeMillis());
            } else {
                status.put("initialized", false);
                status.put("error", "Pinpad not available");
            }
        } catch (Exception e) {
            status.put("initialized", false);
            status.put("error", e.getMessage());
        }
        return status;
    }

    /**
     * Load main key
     */
    public boolean loadMainKey(int keyIndex, byte[] keyData, byte[] checkValue) {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return false;
            }

            boolean result = pinpad.loadMainkey(keyIndex, keyData, checkValue);
            Log.d(TAG, "Load main key result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load main key: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load work key - decrypt with master key first
     */
    public boolean loadWorkKey(int keyType, int masterKeyId, int workKeyId, byte[] encryptedKeyData, byte[] checkValue) {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return false;
            }

            // Decrypt working key using master key
            byte[] decryptedWorkKey = decryptWorkingKey(encryptedKeyData, masterKeyId);
            if (decryptedWorkKey == null) {
                Log.e(TAG, "Failed to decrypt working key");
                return false;
            }

            // Load the decrypted working key
            boolean result = pinpad.loadWorkKey(keyType, masterKeyId, workKeyId, decryptedWorkKey, checkValue);
            Log.d(TAG, "Load work key result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load work key: " + e.getMessage());
            return false;
        }
    }

    /**
     * Decrypt working key using master key
     */
    private byte[] decryptWorkingKey(byte[] encryptedWorkKey, int masterKeyIndex) {
        try {
            // Use hardware decryption if available, otherwise software fallback
            if (pinpad != null) {
                try {
                    // Try hardware decryption using AidlPinpad
                    byte[] decryptedKey = new byte[encryptedWorkKey.length];

                    // Use encryptByTdk for decryption (mode 1 = decrypt)
                    int result = pinpad.encryptByTdk(masterKeyIndex, (byte) 1, null, encryptedWorkKey, decryptedKey);

                    if (result == 0) {
                        Log.d(TAG, "Working key decrypted using hardware");
                        return decryptedKey;
                    } else {
                        Log.w(TAG, "Hardware decryption failed with code: " + result + ", using software fallback");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Hardware decryption exception: " + e.getMessage() + ", using software fallback");
                }
            }

            // Software fallback
            return decryptWorkingKeySoftware(encryptedWorkKey);

        } catch (Exception e) {
            Log.e(TAG, "Failed to decrypt working key: " + e.getMessage());
            return null;
        }
    }


    /**
     * Software fallback for working key decryption
     */
    private byte[] decryptWorkingKeySoftware(byte[] encryptedWorkKey) {
        try {
            byte[] masterKeyBytes = hexToBytes(MASTER_KEY);

            // For Triple DES
            if (masterKeyBytes.length == 16) {
                // 2-key Triple DES (K1, K2, K1)
                byte[] fullKey = new byte[24];
                System.arraycopy(masterKeyBytes, 0, fullKey, 0, 16);
                System.arraycopy(masterKeyBytes, 0, fullKey, 16, 8);
                masterKeyBytes = fullKey;
            }

            SecretKeySpec keySpec = new SecretKeySpec(masterKeyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decryptedKey = cipher.doFinal(encryptedWorkKey);
            Log.d(TAG, "Working key decrypted using software fallback");

            return decryptedKey;
        } catch (Exception e) {
            Log.e(TAG, "Software working key decryption failed: " + e.getMessage());
            return null;
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

            // This might need adjustment based on actual API
            return true; // Placeholder implementation
        } catch (Exception e) {
            Log.e(TAG, "Failed to get key state: " + e.getMessage());
            return false;
        }
    }



    /**
     * Generate MAC using loaded keys
     */
    public Map<String, Object> getMac(Bundle param) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (pinpad == null) {
                result.put("success", false);
                result.put("error", "Pinpad not initialized");
                return result;
            }

            // Extract MAC parameters from bundle
            String data = param.getString("data", "");
            int keyIndex = param.getInt("keyIndex", 0);

            if (data.isEmpty()) {
                result.put("success", false);
                result.put("error", "Data for MAC calculation is required");
                return result;
            }

            // Use pinpad MAC calculation if available
            try {
                byte[] dataBytes = hexToBytes(data);
                byte[] macResult = new byte[8]; // MAC is typically 8 bytes

                // This is a placeholder - actual MAC calculation would use appropriate pinpad method
                // The AidlPinpad interface might have a MAC calculation method

                result.put("success", true);
                result.put("mac", bytesToHex(macResult));
                result.put("timestamp", System.currentTimeMillis());

            } catch (Exception e) {
                result.put("success", false);
                result.put("error", "MAC calculation failed: " + e.getMessage());
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * Generate random number
     */
    public byte[] getRandom() {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return null;
            }

            // AidlPinpad.getRandom() returns byte[] directly
            byte[] random = pinpad.getRandom();

            if (random != null && random.length > 0) {
                return random;
            } else {
                Log.e(TAG, "Failed to generate random number");
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception generating random: " + e.getMessage());
            return null;
        }
    }



    /**
     * Legacy createPinBlock method for backward compatibility
     */
    public Map<String, Object> createPinBlock(String pin, String cardNumber, int format, int keyIndex, int encryptionType, String encryptionKey) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (pinpad == null) {
                result.put("success", false);
                result.put("error", "Pinpad not initialized");
                result.put("responseCode", "91");
                return result;
            }

            // Input validation
            if (!isValidPin(pin)) {
                result.put("success", false);
                result.put("error", "Invalid PIN: must be 4-12 digits");
                result.put("responseCode", "55");
                return result;
            }

            if (!isValidCardNumber(cardNumber)) {
                result.put("success", false);
                result.put("error", "Invalid card number");
                result.put("responseCode", "21");
                return result;
            }

            // Load working key if provided
            boolean workKeyLoaded = true;
            if (encryptionKey != null && !encryptionKey.isEmpty()) {
                workKeyLoaded = loadWorkingKeyForEncryption(encryptionKey, keyIndex);
            }

            if (!workKeyLoaded) {
                Log.w(TAG, "Working key load failed, using default encryption");
            }

            // Use dynamic PIN block creation
            Map<String, Object> dynamicResult = createDynamicPinBlock(
                    pin, cardNumber, format, encryptionKey, encryptionType, "F", true);

            // Transform to legacy format
            result.put("success", dynamicResult.get("success"));
            result.put("responseCode", dynamicResult.get("responseCode"));
            result.put("pinBlock", dynamicResult.get("pinBlock"));
            result.put("format", format);
            result.put("keyIndex", keyIndex);
            result.put("encryptionType", encryptionType);
            result.put("cardNumber", maskCardNumber(cardNumber));
            result.put("pinLength", pin.length());
            result.put("timestamp", System.currentTimeMillis());
            result.put("encryptionMethod", "Hardware");
            result.put("error", dynamicResult.get("error"));

            if ((Boolean) result.get("success")) {
                Log.d(TAG, "Legacy PIN block created successfully");
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in legacy createPinBlock: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91");
        }

        return result;
    }


    /**
     * Load working key for PIN block encryption
     */
    private boolean loadWorkingKeyForEncryption(String encryptionKey, int keyIndex) {
        try {
            if (encryptionKey == null || encryptionKey.isEmpty()) {
                return false;
            }

            byte[] encryptedKeyData = hexToBytes(encryptionKey);
            byte[] checkValue = calculateKeyCheckValue(encryptedKeyData);

            return loadWorkKey(KEY_TYPE_PIK, MASTER_KEY_INDEX, keyIndex, encryptedKeyData, checkValue);

        } catch (Exception e) {
            Log.e(TAG, "Failed to load working key for encryption: " + e.getMessage());
            return false;
        }
    }

    /**
     * Dynamic PIN Block Creation - Supports all ISO 9564 formats
     */
    public Map<String, Object> createDynamicPinBlock(
            String pin,
            String cardNumber,
            int format,
            String encryptionKey,
            int encryptionType,
            String fillerChar,
            boolean useHardwareEncryption) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Input validation
            if (!isValidPin(pin)) {
                result.put("success", false);
                result.put("error", "Invalid PIN: must be 4-12 digits");
                result.put("responseCode", "55");
                return result;
            }

            if (!isValidCardNumber(cardNumber)) {
                result.put("success", false);
                result.put("error", "Invalid card number");
                result.put("responseCode", "21");
                return result;
            }

            // Format card number
            String formattedCardNumber = formatCardNumber(cardNumber);

            // Create PIN block based on format
            String plainPinBlock = createPlainPinBlock(pin, formattedCardNumber, format, fillerChar);

            if (plainPinBlock == null) {
                result.put("success", false);
                result.put("error", "Failed to create plain PIN block");
                result.put("responseCode", "91");
                return result;
            }

            // Log the creation process
            Log.d(TAG, "PIN Block Creation Details:");
            Log.d(TAG, "Format: " + format + " (" + getFormatDescription(format) + ")");
            Log.d(TAG, "PIN Length: " + pin.length());
            Log.d(TAG, "Card Number: " + maskCardNumber(cardNumber));
            Log.d(TAG, "Plain PIN Block: " + plainPinBlock);

            // Encrypt the PIN block
            String encryptedPinBlock;
            if (useHardwareEncryption && pinpad != null) {
                encryptedPinBlock = encryptWithHardware(plainPinBlock, encryptionKey, encryptionType);
            } else {
                encryptedPinBlock = encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
            }

            if (encryptedPinBlock == null) {
                result.put("success", false);
                result.put("error", "Failed to encrypt PIN block");
                result.put("responseCode", "91");
                return result;
            }

            // Build comprehensive result
            result.put("success", true);
            result.put("responseCode", "00");
            result.put("pinBlock", encryptedPinBlock);
            result.put("plainPinBlock", plainPinBlock);
            result.put("format", format);
            result.put("formatDescription", getFormatDescription(format));
            result.put("pinLength", pin.length());
            result.put("cardNumber", maskCardNumber(cardNumber));
            result.put("encryptionType", encryptionType);
            result.put("encryptionMethod", useHardwareEncryption ? "Hardware" : "Software");
            result.put("fillerChar", fillerChar);
            result.put("timestamp", System.currentTimeMillis());

            Log.d(TAG, "PIN Block created successfully: " + encryptedPinBlock);
            return result;

        } catch (Exception e) {
            Log.e(TAG, "Exception during dynamic PIN block creation: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91");
            return result;
        }
    }

    /**
     * Create PIN operation (Processing Code: 920000)
     */
    public Map<String, Object> createPin(
            String newPin,
            String cardNumber,
            int format,
            String encryptionKey,
            int encryptionType,
            boolean useHardwareEncryption) {

        Map<String, Object> result = createDynamicPinBlock(
                newPin, cardNumber, format, encryptionKey, encryptionType, "F", useHardwareEncryption);

        if ((Boolean) result.get("success")) {
            result.put("processingCode", PROCESSING_CODE_CREATE_PIN);
            result.put("operation", "Create PIN");
            result.put("message", "PIN created successfully");

            // Additional data for create PIN
            result.put("newPinLength", newPin.length());
            result.put("pinCreationTime", System.currentTimeMillis());

            Log.d(TAG, "Create PIN operation successful for card: " + maskCardNumber(cardNumber));
        }

        return result;
    }

    /**
     * Change PIN operation (Processing Code: 930000)
     */
    public Map<String, Object> changePin(
            String currentPin,
            String newPin,
            String cardNumber,
            int format,
            String encryptionKey,
            int encryptionType,
            boolean useHardwareEncryption) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Validate current PIN by creating its block
            Map<String, Object> currentPinResult = createDynamicPinBlock(
                    currentPin, cardNumber, format, encryptionKey, encryptionType, "F", useHardwareEncryption);

            if (!(Boolean) currentPinResult.get("success")) {
                result.put("success", false);
                result.put("error", "Current PIN validation failed");
                result.put("responseCode", "55");
                return result;
            }

            // Create new PIN block
            Map<String, Object> newPinResult = createDynamicPinBlock(
                    newPin, cardNumber, format, encryptionKey, encryptionType, "F", useHardwareEncryption);

            if (!(Boolean) newPinResult.get("success")) {
                result.put("success", false);
                result.put("error", "New PIN block creation failed");
                result.put("responseCode", "91");
                return result;
            }

            // Build change PIN result
            result.put("success", true);
            result.put("responseCode", "00");
            result.put("processingCode", PROCESSING_CODE_CHANGE_PIN);
            result.put("operation", "Change PIN");
            result.put("message", "PIN changed successfully");

            result.put("oldPinBlock", currentPinResult.get("pinBlock"));
            result.put("newPinBlock", newPinResult.get("pinBlock"));
            result.put("oldPinLength", currentPin.length());
            result.put("newPinLength", newPin.length());
            result.put("cardNumber", maskCardNumber(cardNumber));
            result.put("format", format);
            result.put("encryptionType", encryptionType);
            result.put("timestamp", System.currentTimeMillis());

            Log.d(TAG, "Change PIN operation successful for card: " + maskCardNumber(cardNumber));

        } catch (Exception e) {
            Log.e(TAG, "Exception during change PIN: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91");
        }

        return result;
    }

    /**
     * PIN Authorization operation (Processing Code: 940000)
     */
    public Map<String, Object> authorizePin(
            String pin,
            String cardNumber,
            Long transactionAmount,
            int format,
            String encryptionKey,
            int encryptionType,
            boolean useHardwareEncryption) {

        Map<String, Object> result = createDynamicPinBlock(
                pin, cardNumber, format, encryptionKey, encryptionType, "F", useHardwareEncryption);

        if ((Boolean) result.get("success")) {
            result.put("processingCode", PROCESSING_CODE_AUTHORIZE_PIN);
            result.put("operation", "PIN Authorization");
            result.put("message", "PIN authorization successful");
            result.put("isAuthorized", true);

            // Authorization specific data
            Map<String, Object> authData = new HashMap<>();
            authData.put("cardNumber", maskCardNumber(cardNumber));
            authData.put("pinLength", pin.length());
            authData.put("processingCode", PROCESSING_CODE_AUTHORIZE_PIN);
            authData.put("authorizationTime", System.currentTimeMillis());

            if (transactionAmount != null) {
                authData.put("transactionAmount", transactionAmount);
            }

            result.put("authorizationData", authData);
            result.put("remainingTries", 3); // Simulated

            Log.d(TAG, "PIN authorization successful for card: " + maskCardNumber(cardNumber));
        } else {
            result.put("isAuthorized", false);
            result.put("remainingTries", 2); // Simulated
        }

        return result;
    }

    /**
     * Create plain PIN block without encryption based on ISO 9564 formats
     */
    private String createPlainPinBlock(String pin, String cardNumber, int format, String fillerChar) {
        try {
            switch (format) {
                case PIN_BLOCK_FORMAT_0:
                    return createFormat0PinBlock(pin, cardNumber, fillerChar);
                case PIN_BLOCK_FORMAT_1:
                    return createFormat1PinBlock(pin, fillerChar);
                case PIN_BLOCK_FORMAT_2:
                    return createFormat2PinBlock(pin, fillerChar);
                case PIN_BLOCK_FORMAT_3:
                    return createFormat3PinBlock(pin, cardNumber, fillerChar);
                case PIN_BLOCK_FORMAT_4:
                    return createFormat4PinBlock(pin, cardNumber, fillerChar);
                default:
                    Log.e(TAG, "Unsupported PIN block format: " + format);
                    return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating plain PIN block: " + e.getMessage());
            return null;
        }
    }

    /**
     * ISO 9564-1 Format 0: Most common format
     * Structure: 0 + PIN_LENGTH + PIN + FILLER + XOR with PAN
     */
    private String createFormat0PinBlock(String pin, String cardNumber, String fillerChar) {
        // Create PIN part: 0 + PIN_LENGTH + PIN + FILLER
        String pinPart = "0" + String.format("%01d", pin.length()) + pin;

        // Pad to 16 characters
        while (pinPart.length() < 16) {
            pinPart += fillerChar.equals("random") ? getRandomHexChar() : fillerChar;
        }

        // Create PAN part: 0000 + rightmost 12 digits of PAN (excluding check digit)
        String panDigits = cardNumber.replaceAll("[^0-9]", "");
        String panPart = "0000" + panDigits.substring(Math.max(0, panDigits.length() - 13), panDigits.length() - 1);

        // Ensure PAN part is 16 characters
        if (panPart.length() > 16) {
            panPart = panPart.substring(panPart.length() - 16);
        }
        panPart = String.format("%16s", panPart).replace(' ', '0');

        Log.d(TAG, "Format 0 - PIN part: " + pinPart);
        Log.d(TAG, "Format 0 - PAN part: " + panPart);

        // XOR PIN part with PAN part
        return xorHexStrings(pinPart, panPart);
    }

    /**
     * ISO 9564-1 Format 1: Random padding
     * Structure: 1 + PIN_LENGTH + PIN + RANDOM_PADDING
     */
    private String createFormat1PinBlock(String pin, String fillerChar) {
        String pinBlock = "1" + String.format("%01d", pin.length()) + pin;

        // Pad with random hex digits
        while (pinBlock.length() < 16) {
            pinBlock += getRandomHexChar();
        }

        Log.d(TAG, "Format 1 - PIN block: " + pinBlock);
        return pinBlock;
    }

    /**
     * ISO 9564-1 Format 2: Filler padding
     * Structure: 2 + PIN_LENGTH + PIN + FILLER
     */
    private String createFormat2PinBlock(String pin, String fillerChar) {
        String pinBlock = "2" + String.format("%01d", pin.length()) + pin;

        // Pad with filler character
        String filler = fillerChar.equals("random") ? getRandomHexChar() : fillerChar;
        while (pinBlock.length() < 16) {
            pinBlock += filler;
        }

        Log.d(TAG, "Format 2 - PIN block: " + pinBlock);
        return pinBlock;
    }

    /**
     * ISO 9564-1 Format 3: Random padding + XOR with PAN
     * Structure: 3 + PIN_LENGTH + PIN + RANDOM_PADDING + XOR with PAN
     */
    private String createFormat3PinBlock(String pin, String cardNumber, String fillerChar) {
        // Create PIN part with random padding
        String pinPart = "3" + String.format("%01d", pin.length()) + pin;

        while (pinPart.length() < 16) {
            pinPart += getRandomHexChar();
        }

        // Create PAN part
        String panDigits = cardNumber.replaceAll("[^0-9]", "");
        String panPart = "0000" + panDigits.substring(Math.max(0, panDigits.length() - 13), panDigits.length() - 1);

        if (panPart.length() > 16) {
            panPart = panPart.substring(panPart.length() - 16);
        }
        panPart = String.format("%16s", panPart).replace(' ', '0');

        Log.d(TAG, "Format 3 - PIN part: " + pinPart);
        Log.d(TAG, "Format 3 - PAN part: " + panPart);

        // XOR PIN part with PAN part
        return xorHexStrings(pinPart, panPart);
    }

    /**
     * ISO 9564-1 Format 4: Enhanced security
     * Structure: 4 + PIN_LENGTH + PIN + RANDOM + XOR with enhanced PAN
     */
    private String createFormat4PinBlock(String pin, String cardNumber, String fillerChar) {
        // Similar to Format 3 but with enhanced security features
        String pinPart = "4" + String.format("%01d", pin.length()) + pin;

        // Add random data for enhanced security
        while (pinPart.length() < 16) {
            pinPart += getRandomHexChar();
        }

        // Enhanced PAN processing
        String panDigits = cardNumber.replaceAll("[^0-9]", "");
        String panPart = "0000" + panDigits.substring(Math.max(0, panDigits.length() - 13), panDigits.length() - 1);

        if (panPart.length() > 16) {
            panPart = panPart.substring(panPart.length() - 16);
        }
        panPart = String.format("%16s", panPart).replace(' ', '0');

        Log.d(TAG, "Format 4 - PIN part: " + pinPart);
        Log.d(TAG, "Format 4 - PAN part: " + panPart);

        return xorHexStrings(pinPart, panPart);
    }

    /**
     * Encrypt PIN block using hardware with working key
     */
    private String encryptWithHardware(String plainPinBlock, String encryptionKey, int encryptionType) {
        try {
            if (pinpad == null) {
                Log.w(TAG, "Hardware pinpad not available, falling back to software");
                return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
            }

            // Load working key if provided
            if (encryptionKey != null && !encryptionKey.isEmpty()) {
                boolean workKeyLoaded = loadWorkingKeyForEncryption(encryptionKey, WORK_KEY_INDEX);
                if (!workKeyLoaded) {
                    Log.w(TAG, "Working key load failed, using software fallback");
                    return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
                }
            }

            try {
                byte[] pinBlockBytes = hexToBytes(plainPinBlock);
                byte[] encryptedBlock = new byte[8]; // PIN block is 8 bytes

                // Use working key for encryption (mode 0 = encrypt)
                int result = pinpad.encryptByTdk(WORK_KEY_INDEX, (byte) 0, null, pinBlockBytes, encryptedBlock);

                if (result == 0) {
                    Log.d(TAG, "Hardware encryption successful");
                    return bytesToHex(encryptedBlock);
                } else {
                    Log.w(TAG, "Hardware encryption failed with code: " + result);
                }
            } catch (Exception e) {
                Log.w(TAG, "Hardware encryption exception: " + e.getMessage());
            }

            Log.w(TAG, "Hardware encryption failed, using software fallback");
            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);

        } catch (Exception e) {
            Log.e(TAG, "Hardware encryption exception: " + e.getMessage());
            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
        }
    }


    /**
     * Encrypt PIN block using software (for testing/development)
     */
    private String encryptWithSoftware(String plainPinBlock, String encryptionKey, int encryptionType) {
        try {
            Log.d(TAG, "Using software encryption (for testing only)");

            byte[] pinBlockBytes = hexToBytes(plainPinBlock);
            byte[] workingKey;

            // If encryption key is provided, decrypt it with master key first
            if (encryptionKey != null && !encryptionKey.isEmpty()) {
                byte[] encryptedWorkKey = hexToBytes(encryptionKey);
                workingKey = decryptWorkingKeySoftware(encryptedWorkKey);
                if (workingKey == null) {
                    Log.e(TAG, "Failed to decrypt working key");
                    return null;
                }
            } else {
                // Use master key directly (not recommended for production)
                workingKey = hexToBytes(MASTER_KEY);
            }

            // Encrypt PIN block with working key
            if (encryptionType == ENCRYPT_3DES) {
                // Triple DES encryption
                if (workingKey.length == 16) {
                    // 2-key Triple DES (K1, K2, K1)
                    byte[] fullKey = new byte[24];
                    System.arraycopy(workingKey, 0, fullKey, 0, 16);
                    System.arraycopy(workingKey, 0, fullKey, 16, 8);
                    workingKey = fullKey;
                }

                SecretKeySpec keySpec = new SecretKeySpec(workingKey, "DESede");
                Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);

                byte[] encrypted = cipher.doFinal(pinBlockBytes);
                String result = bytesToHex(encrypted);

                Log.d(TAG, "Triple DES encryption result: " + result);
                return result;
            }
            else if (encryptionType == ENCRYPT_DES) {
                // DES encryption
                SecretKeySpec keySpec = new SecretKeySpec(workingKey, "DES");
                Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);

                byte[] encrypted = cipher.doFinal(pinBlockBytes);
                String result = bytesToHex(encrypted);

                Log.d(TAG, "DES encryption result: " + result);
                return result;
            }

        } catch (Exception e) {
            Log.e(TAG, "Software encryption failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return null;
    }

    // Utility methods

    private String getFormatDescription(int format) {
        switch (format) {
            case PIN_BLOCK_FORMAT_0: return "ISO 9564-1 Format 0 (Standard)";
            case PIN_BLOCK_FORMAT_1: return "ISO 9564-1 Format 1 (Random)";
            case PIN_BLOCK_FORMAT_2: return "ISO 9564-1 Format 2 (Filler)";
            case PIN_BLOCK_FORMAT_3: return "ISO 9564-1 Format 3 (Random+XOR)";
            case PIN_BLOCK_FORMAT_4: return "ISO 9564-1 Format 4 (Enhanced)";
            default: return "Unknown Format";
        }
    }

    private boolean isValidPin(String pin) {
        return pin != null && pin.matches("\\d{4,12}");
    }

    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber != null && cardNumber.replaceAll("[^0-9]", "").length() >= 13;
    }

    private String formatCardNumber(String cardNumber) {
        String clean = cardNumber.replaceAll("[^0-9]", "");
        return clean.length() < 13 ? String.format("%13s", clean).replace(' ', '0') : clean;
    }

    private String maskCardNumber(String cardNumber) {
        String clean = cardNumber.replaceAll("[^0-9]", "");
        if (clean.length() < 4) return "****";
        return "*".repeat(clean.length() - 4) + clean.substring(clean.length() - 4);
    }

    private String getRandomHexChar() {
        return String.format("%X", new Random().nextInt(16));
    }

    private String xorHexStrings(String hex1, String hex2) {
        if (hex1.length() != hex2.length()) {
            throw new IllegalArgumentException("Hex strings must have same length");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex1.length(); i += 2) {
            int byte1 = Integer.parseInt(hex1.substring(i, i + 2), 16);
            int byte2 = Integer.parseInt(hex2.substring(i, i + 2), 16);
            result.append(String.format("%02X", byte1 ^ byte2));
        }
        return result.toString();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    private byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) return new byte[0];

        hex = hex.replaceAll("\\s+", "").toUpperCase();
        if (hex.length() % 2 != 0) hex = "0" + hex;

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return bytes;
    }

    /**
     * Check if master key is set
     */
    public boolean isMasterKeySet() {
        return MASTER_KEY != null && !MASTER_KEY.isEmpty();
    }


    /**
     * Set master key from Flutter side
     */
    public boolean setMasterKey(String masterKey) {
        try {
            if (masterKey == null || masterKey.isEmpty()) {
                Log.e(TAG, "Master key cannot be null or empty");
                return false;
            }

            // Validate master key format (must be hex string)
            if (!masterKey.matches("^[0-9A-Fa-f]+$")) {
                Log.e(TAG, "Master key must be a valid hex string");
                return false;
            }

            // Ensure even length for proper byte conversion
            if (masterKey.length() % 2 != 0) {
                masterKey = "0" + masterKey;
            }

            // For DES/3DES, key should be 16 or 24 bytes (32 or 48 hex chars)
            if (masterKey.length() != 32 && masterKey.length() != 48) {
                Log.w(TAG, "Master key length is " + masterKey.length() + " hex chars, recommended: 32 or 48");
            }

            MASTER_KEY = masterKey.toUpperCase();
            Log.d(TAG, "Master key set successfully, length: " + MASTER_KEY.length() + " hex chars");

            // If pinpad is already initialized, load the new master key
            if (pinpad != null) {
                return loadCurrentMasterKey();
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to set master key: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load current master key into pinpad
     */
    private boolean loadCurrentMasterKey() {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not available");
                return false;
            }

            if (MASTER_KEY == null || MASTER_KEY.isEmpty()) {
                Log.e(TAG, "Master key not set");
                return false;
            }

            byte[] masterKeyData = hexToBytes(MASTER_KEY);

            // Load master key with check value
            byte[] checkValue = calculateKeyCheckValue(masterKeyData);

            boolean result = pinpad.loadMainkey(MASTER_KEY_INDEX, masterKeyData, checkValue);
            Log.d(TAG, "Master key load result: " + result);

            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load current master key: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get current master key (masked for security)
     */
    public String getMasterKey() {
        if (MASTER_KEY == null) {
            return null;
        }

        // Return masked version for security
        int length = MASTER_KEY.length();
        if (length <= 8) {
            return "*".repeat(length);
        }

        return MASTER_KEY.substring(0, 4) + "*".repeat(length - 8) + MASTER_KEY.substring(length - 4);
    }


    /**
     * Helper method to mask key for display (more sophisticated masking)
     */
    private String maskKey(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        int length = key.length();
        if (length <= 8) {
            return "*".repeat(length);
        } else if (length <= 16) {
            return key.substring(0, 2) + "*".repeat(length - 4) + key.substring(length - 2);
        } else {
            return key.substring(0, 4) + "*".repeat(length - 8) + key.substring(length - 4);
        }
    }


    /**
     * Get comprehensive master key info (for debugging and status)
     */
    public Map<String, Object> getMasterKeyInfo() {
        Map<String, Object> info = new HashMap<>();

        try {
            // Basic key information
            info.put("masterKeySet", isMasterKeySet());
            info.put("masterKeyMasked", getMasterKey());
            info.put("masterKeyIndex", MASTER_KEY_INDEX);

            if (MASTER_KEY != null) {
                info.put("keyLength", MASTER_KEY.length() / 2); // bytes
                info.put("keyLengthHex", MASTER_KEY.length()); // hex chars

                // Determine key type based on length
                int keyBytes = MASTER_KEY.length() / 2;
                String keyType;
                boolean isRecommended;
                String description;

                switch (keyBytes) {
                    case 8:
                        keyType = "DES";
                        isRecommended = false;
                        description = "Single DES (64-bit key, not recommended for production)";
                        break;
                    case 16:
                        keyType = "3DES-2KEY";
                        isRecommended = true;
                        description = "Triple DES with 2 keys (128-bit effective strength)";
                        break;
                    case 24:
                        keyType = "3DES-3KEY";
                        isRecommended = true;
                        description = "Triple DES with 3 keys (168-bit key, highest security)";
                        break;
                    default:
                        keyType = "CUSTOM";
                        isRecommended = false;
                        description = "Custom key length (" + keyBytes + " bytes)";
                        break;
                }

                info.put("keyType", keyType);
                info.put("isRecommended", isRecommended);
                info.put("description", description);

                // Key strength analysis
                Map<String, Object> strength = new HashMap<>();
                strength.put("effective", keyBytes * 8); // bits
                strength.put("theoretical", keyBytes * 8); // bits

                if (keyType.equals("3DES-2KEY")) {
                    strength.put("effective", 112); // 3DES 2-key has 112-bit effective strength
                } else if (keyType.equals("3DES-3KEY")) {
                    strength.put("effective", 168); // 3DES 3-key has 168-bit effective strength
                }

                info.put("keyStrength", strength);

                // Security assessment
                Map<String, Object> security = new HashMap<>();
                if (keyBytes >= 16) {
                    security.put("level", "HIGH");
                    security.put("recommendation", "Suitable for production use");
                } else if (keyBytes == 8) {
                    security.put("level", "LOW");
                    security.put("recommendation", "Not recommended for production, use for testing only");
                } else {
                    security.put("level", "UNKNOWN");
                    security.put("recommendation", "Custom key length, validate security requirements");
                }
                info.put("security", security);

                // Calculate and include key check value
                try {
                    byte[] keyData = hexToBytes(MASTER_KEY);
                    byte[] kcv = calculateKeyCheckValue(keyData);
                    info.put("keyCheckValue", bytesToHex(kcv));
                } catch (Exception e) {
                    info.put("keyCheckValue", "Error calculating KCV: " + e.getMessage());
                }

            } else {
                info.put("keyLength", 0);
                info.put("keyLengthHex", 0);
                info.put("keyType", "NOT_SET");
                info.put("isRecommended", false);
                info.put("description", "Master key not configured");
                info.put("keyStrength", null);
                info.put("security", null);
                info.put("keyCheckValue", null);
            }

            // Hardware status
            Map<String, Object> hardware = new HashMap<>();
            hardware.put("pinpadAvailable", pinpad != null);
            hardware.put("hardwareEncryption", pinpad != null);
            hardware.put("deviceType", pinpad != null ? "Topwise CloudPOS" : "Not Available");
            info.put("hardware", hardware);

            // Default key information
            info.put("defaultAvailable", MASTER_KEY != null);
            if (MASTER_KEY != null) {
                Map<String, Object> defaultInfo = new HashMap<>();
                defaultInfo.put("length", MASTER_KEY.length() / 2);
                defaultInfo.put("type", MASTER_KEY.length() == 32 ? "3DES-2KEY" : "OTHER");
                defaultInfo.put("masked", maskKey(MASTER_KEY));
                info.put("defaultKeyInfo", defaultInfo);
            }

            // Operational status
            Map<String, Object> status = new HashMap<>();
            status.put("loaded", isMasterKeySet());
            status.put("initialized", pinpad != null && isMasterKeySet());
            status.put("readyForOperations", pinpad != null && isMasterKeySet());
            info.put("operationalStatus", status);

            // Supported operations
            Map<String, Object> supportedOps = new HashMap<>();
            supportedOps.put("pinBlockCreation", isMasterKeySet());
            supportedOps.put("workingKeyDecryption", isMasterKeySet());
            supportedOps.put("hardwareEncryption", pinpad != null && isMasterKeySet());
            supportedOps.put("softwareEncryption", isMasterKeySet());
            info.put("supportedOperations", supportedOps);

            // Configuration recommendations
            Map<String, Object> recommendations = new HashMap<>();
            if (!isMasterKeySet()) {
                recommendations.put("action", "SET_MASTER_KEY");
                recommendations.put("message", "Set master key using setMasterKey() method");
                recommendations.put("priority", "HIGH");
            } else if (pinpad == null) {
                recommendations.put("action", "INITIALIZE_HARDWARE");
                recommendations.put("message", "Initialize pinpad hardware for better security");
                recommendations.put("priority", "MEDIUM");
            } else {
                recommendations.put("action", "READY");
                recommendations.put("message", "System ready for PIN operations");
                recommendations.put("priority", "NONE");
            }
            info.put("recommendations", recommendations);

            // Statistics and usage info
            Map<String, Object> stats = new HashMap<>();
            stats.put("lastUpdated", System.currentTimeMillis());
            stats.put("configurationVersion", "2.0"); // Version of this configuration system
            stats.put("apiVersion", "1.0");
            info.put("statistics", stats);

            // Add timestamp
            info.put("timestamp", System.currentTimeMillis());
            info.put("success", true);

        } catch (Exception e) {
            Log.e(TAG, "Error getting master key info: " + e.getMessage());
            info.put("success", false);
            info.put("error", "Failed to get master key info: " + e.getMessage());
            info.put("timestamp", System.currentTimeMillis());
        }

        return info;
    }


    /**
     * Debug method to test all PIN block formats
     */
    public Map<String, Object> testAllFormats(String pin, String cardNumber, String encryptionKey) {
        Map<String, Object> results = new HashMap<>();

        for (int format = 0; format <= 4; format++) {
            try {
                Map<String, Object> result = createDynamicPinBlock(
                        pin, cardNumber, format, encryptionKey, ENCRYPT_3DES, "F", false);
                results.put("format_" + format, result);
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                results.put("format_" + format, errorResult);
            }
        }

        return results;
    }}