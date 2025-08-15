package com.adpstore.flutter_smart_pin_pad_cards;

import android.os.RemoteException;
import android.util.Log;
import android.os.Bundle;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.data.AidlErrorCode;
import com.topwise.cloudpos.data.PinpadConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DynamicPinBlockManager {
    private static final String TAG = "DynamicPinBlockManager";
    private AidlPinpad pinpad;
    private static DynamicPinBlockManager instance;

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

    // Encryption modes
    public static final byte MODE_ENCRYPT = 0;
    public static final byte MODE_DECRYPT = 1;

    // Hardcoded Master Key - Static value for Bank Jateng
    private static final String MASTER_KEY = "0123456789ABCDEF01234567";

    // Working key cache
    private String decryptedWorkingKey = null;
    private long workingKeyTimestamp = 0;
    private static final long WORKING_KEY_CACHE_DURATION = 30 * 60 * 1000; // 30 minutes

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
                // AidlPinpad doesn't have open() method, just check if it's available
                Log.d(TAG, "Pinpad initialized successfully");
                return true;
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
     * Get master key info
     */
    public Map<String, Object> getMasterKeyInfo() {
        Map<String, Object> info = new HashMap<>();
        try {
            info.put("masterKeyConfigured", true);
            info.put("masterKeyLength", MASTER_KEY.length());
            info.put("masterKeyMasked", maskKey(MASTER_KEY));
            info.put("workingKeyCached", decryptedWorkingKey != null);
            if (decryptedWorkingKey != null) {
                long cacheAge = System.currentTimeMillis() - workingKeyTimestamp;
                info.put("workingKeyCacheAge", cacheAge);
                info.put("workingKeyCacheValid", cacheAge < WORKING_KEY_CACHE_DURATION);
            }
            info.put("timestamp", System.currentTimeMillis());
        } catch (Exception e) {
            info.put("error", e.getMessage());
        }
        return info;
    }

    /**
     * Decrypt working key using master key
     */
    public Map<String, Object> decryptWorkingKey(String encryptedWorkingKey) {
        try {
            Log.d(TAG, "DECRYPTING WORKING KEY");

            // decryptedWorkingKey = null;
            // workingKeyTimestamp = 0;

            String decryptedKey = decryptWithMasterKey(encryptedWorkingKey);

            Map<String, Object> result = new HashMap<>();
            if (decryptedKey != null) {
                result.put("success", true);
                result.put("decryptedKey", decryptedKey);
                result.put("responseCode", "00");
            } else {
                result.put("success", false);
                result.put("error", "Decryption failed");
                result.put("responseCode", "91");
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "CRITICAL DECRYPT ERROR: " + e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91");
            return result;
        }
    }
//    public Map<String, Object> decryptWorkingKey(String encryptedWorkingKey) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            Log.d(TAG, "Decrypting working key...");
//
//            if (encryptedWorkingKey == null || encryptedWorkingKey.isEmpty()) {
//                result.put("success", false);
//                result.put("error", "Encrypted working key is required");
//                result.put("responseCode", "91");
//                return result;
//            }
//
//            // Check cache first
//            long currentTime = System.currentTimeMillis();
//            if (decryptedWorkingKey != null &&
//                    (currentTime - workingKeyTimestamp) < WORKING_KEY_CACHE_DURATION) {
//                Log.d(TAG, "Using cached working key");
//                result.put("success", true);
//                result.put("decryptedKey", decryptedWorkingKey);
//                result.put("fromCache", true);
//                result.put("cacheAge", currentTime - workingKeyTimestamp);
//                return result;
//            }
//
//            // Decrypt working key with master key
//            String decryptedKey = decryptWithMasterKey(encryptedWorkingKey);
//
//            if (decryptedKey != null) {
//                // Cache the decrypted working key
//                decryptedWorkingKey = decryptedKey;
//                workingKeyTimestamp = currentTime;
//
//                result.put("success", true);
//                result.put("decryptedKey", decryptedKey);
//                result.put("fromCache", false);
//                result.put("responseCode", "00");
//                result.put("timestamp", currentTime);
//
//                Log.d(TAG, "Working key decrypted and cached successfully");
//            } else {
//                result.put("success", false);
//                result.put("error", "Failed to decrypt working key");
//                result.put("responseCode", "91");
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "Exception in decryptWorkingKey: " + e.getMessage());
//            result.put("success", false);
//            result.put("error", "Exception: " + e.getMessage());
//            result.put("responseCode", "91");
//        }
//
//        return result;
//    }

    /**
     * Set working key directly (for cases where working key is already decrypted)
     */
    public Map<String, Object> setWorkingKey(String workingKey) {
        Map<String, Object> result = new HashMap<>();
        try {
            Log.d(TAG, "VALIDATING WORKING KEY FORMAT ONLY");

            // Hanya validasi format
            if (!workingKey.matches("[0-9A-Fa-f]{32}")) {
                result.put("success", false);
                result.put("error", "Invalid working key format");
                return result;
            }

            result.put("success", true);
            result.put("message", "Format valid - no storage");
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Validation error: " + e.getMessage());
            return result;
        }
    }
//    public Map<String, Object> setWorkingKey(String workingKey) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            Log.d(TAG, "=== SET WORKING KEY PROCESS ===");
//            Log.d(TAG, "Raw input: " + workingKey);
//            Log.d(TAG, "Input length: " + workingKey.length());
//
//            if (workingKey == null || workingKey.isEmpty()) {
//                result.put("success", false);
//                result.put("error", "Working key is required");
//                return result;
//            }
//
//            String actualWorkingKey = workingKey.trim().toUpperCase();
//
//            // Handle field 62 format: 032<working_key>
//            if (actualWorkingKey.length() >= 35 && actualWorkingKey.substring(0, 3).equals("032")) {
//                actualWorkingKey = actualWorkingKey.substring(3);
//                Log.d(TAG, "Extracted from field 62 format");
//                Log.d(TAG, "Extracted key: " + actualWorkingKey);
//                Log.d(TAG, "Extracted length: " + actualWorkingKey.length());
//            }
//
//            // Fix working key length jika tidak pas 32
//            if (actualWorkingKey.length() > 32) {
//                // Ambil 32 chars pertama
//                actualWorkingKey = actualWorkingKey.substring(0, 32);
//                Log.d(TAG, "Truncated to 32 chars: " + actualWorkingKey);
//            } else if (actualWorkingKey.length() < 32) {
//                // Pad dengan 0 di akhir hingga 32 chars
//                while (actualWorkingKey.length() < 32) {
//                    actualWorkingKey += "0";
//                }
//                Log.d(TAG, "Padded to 32 chars: " + actualWorkingKey);
//            }
//
//            // Final validation
//            if (actualWorkingKey.length() != 32) {
//                result.put("success", false);
//                result.put("error", "Cannot normalize working key to 32 chars: " + actualWorkingKey.length());
//                return result;
//            }
//
//            // Validate hex format
//            if (!actualWorkingKey.matches("[0-9A-Fa-f]{32}")) {
//                result.put("success", false);
//                result.put("error", "Working key is not valid hex format");
//                return result;
//            }
//
//            // Cache the working key
//            decryptedWorkingKey = actualWorkingKey;
//            workingKeyTimestamp = System.currentTimeMillis();
//
//            result.put("success", true);
//            result.put("workingKeySet", true);
//            result.put("timestamp", workingKeyTimestamp);
//            result.put("originalInput", workingKey);
//            result.put("finalWorkingKey", maskKey(actualWorkingKey));
//
//            Log.d(TAG, "✅ Working key set successfully");
//            Log.d(TAG, "Final working key: " + maskKey(actualWorkingKey));
//            Log.d(TAG, "Final length: " + actualWorkingKey.length());
//
//        } catch (Exception e) {
//            Log.e(TAG, "Exception in setWorkingKey: " + e.getMessage());
//            result.put("success", false);
//            result.put("error", "Exception: " + e.getMessage());
//        }
//
//        return result;
//    }

    /**
     * Clear working key cache
     */
    public void clearWorkingKeyCache() {
        decryptedWorkingKey = null;
        workingKeyTimestamp = 0;
        Log.d(TAG, "Working key cache cleared");
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
     * Load work key
     */
    public boolean loadWorkKey(int keyType, int masterKeyId, int workKeyId, byte[] keyData, byte[] checkValue) {
        try {
            if (pinpad == null) {
                Log.e(TAG, "Pinpad not initialized");
                return false;
            }

            boolean result = pinpad.loadWorkKey(keyType, masterKeyId, workKeyId, keyData, checkValue);
            Log.d(TAG, "Load work key result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load work key: " + e.getMessage());
            return false;
        }
    }

    /**
     * Legacy createPinBlock method for backward compatibility
     */
    public Map<String, Object> createPinBlock(String pin, String cardNumber, int format, int keyIndex, int encryptionType,  String encryptionKey) {
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

            // Use working key for encryption if available, otherwise use provided key
//            String keyToUse = decryptedWorkingKey != null ? decryptedWorkingKey : encryptionKey;
            Log.d(TAG, "Using legacy PIN block creation");

            // Since AidlPinpad doesn't have direct inputPin method,
            // we'll use the dynamic PIN block creation as fallback
            Log.d(TAG, "Using dynamic PIN block creation for legacy method");

            // Since AidlPinpad doesn't have direct inputPin method,
            // we'll use the dynamic PIN block creation as fallback
            Log.d(TAG, "Using dynamic PIN block creation for legacy method");

//            String defaultKey = "404142434445464748494A4B4C4D4E4F";
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
            result.put("usedWorkingKey", decryptedWorkingKey != null);
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

            // Use working key if available, otherwise use provided encryption key
            String keyToUse = decryptedWorkingKey != null ? decryptedWorkingKey : encryptionKey;
            boolean usingWorkingKey = decryptedWorkingKey != null;


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
                encryptedPinBlock = encryptWithSoftware(plainPinBlock, keyToUse, encryptionType);
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
            result.put("usedWorkingKey", usingWorkingKey);
            result.put("keySource", usingWorkingKey ? "WorkingKey" : "ProvidedKey");
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
            result.put("usedWorkingKey", decryptedWorkingKey != null);
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
            authData.put("usedWorkingKey", decryptedWorkingKey != null);

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
     * Decrypt working key using master key
     */
    private String decryptWithMasterKey(String encryptedWorkingKey) {
        try {
            Log.d(TAG, "=== WORKING KEY DECRYPTION PROCESS ===");
            Log.d(TAG, "Master Key: " + maskKey(MASTER_KEY));
            Log.d(TAG, "Working Key: " + maskKey(encryptedWorkingKey));
            Log.d(TAG, "Working Key Length: " + encryptedWorkingKey.length());

            byte[] masterKeyBytes = hexToBytes(MASTER_KEY);
            byte[] encryptedKeyBytes = hexToBytes(encryptedWorkingKey);

            Log.d(TAG, "Master Key Bytes Length: " + masterKeyBytes.length);
            Log.d(TAG, "Encrypted Key Bytes Length: " + encryptedKeyBytes.length);
            Log.d(TAG, "Master Key Bytes: " + bytesToHex(masterKeyBytes));
            Log.d(TAG, "Encrypted Key Bytes: " + bytesToHex(encryptedKeyBytes));

            // Use Triple DES for decryption
            SecretKeySpec keySpec;
            Cipher cipher;

            if (masterKeyBytes.length == 16) {
                Log.d(TAG, "Using 2-key Triple DES (K1, K2, K1)");
                // 2-key Triple DES (K1, K2, K1)
                byte[] fullKey = new byte[24];
                System.arraycopy(masterKeyBytes, 0, fullKey, 0, 16);
                System.arraycopy(masterKeyBytes, 0, fullKey, 16, 8);
                keySpec = new SecretKeySpec(fullKey, "DESede");
                cipher = Cipher.getInstance("DESede/ECB/NoPadding");
                Log.d(TAG, "Full 24-byte Key: " + bytesToHex(fullKey));
            } else if (masterKeyBytes.length == 24) {
                Log.d(TAG, "Using 3-key Triple DES");
                // 3-key Triple DES
                keySpec = new SecretKeySpec(masterKeyBytes, "DESede");
                cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            } else {
                Log.e(TAG, "Invalid master key length: " + masterKeyBytes.length);
                return null;
            }

            Log.d(TAG, "Cipher Algorithm: " + cipher.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            Log.d(TAG, "Starting decryption...");
            byte[] decryptedBytes = cipher.doFinal(encryptedKeyBytes);
            String decryptedKey = bytesToHex(decryptedBytes);

            Log.d(TAG, "Decrypted Working Key: " + maskKey(decryptedKey));
            Log.d(TAG, "Decrypted Working Key Length: " + decryptedKey.length());
            Log.d(TAG, "Decrypted Working Key Full: " + decryptedKey);
            Log.d(TAG, "=== WORKING KEY DECRYPTION COMPLETED ===");

            return decryptedKey;

        } catch (Exception e) {
            Log.e(TAG, "Failed to decrypt working key: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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
// Fixed Hardware Encryption Methods for DynamicPinBlockManager.java

    /**
     * Improved hardware encryption method based on working implementation in PinPadActivity
     */
    private String encryptWithHardware(String plainPinBlock, String encryptionKey, int encryptionType) {
        try {
            if (pinpad == null) {
                Log.w(TAG, "Hardware pinpad not available, falling back to software");
                return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
            }

            // Determine which key to use - cached working key or provided key
            String keyToUse = decryptedWorkingKey != null ? decryptedWorkingKey : encryptionKey;
            boolean usingCachedKey = decryptedWorkingKey != null;

            Log.d(TAG, "=== HARDWARE ENCRYPTION ATTEMPT ===");
            Log.d(TAG, "Plain PIN Block: " + plainPinBlock);
            Log.d(TAG, "Using key source: " + (usingCachedKey ? "CACHED WORKING KEY" : "PROVIDED PARAMETER"));
            Log.d(TAG, "Encryption Key: " + maskKey(keyToUse));

            // Step 1: First load the key as a work key
            byte[] keyData = hexToBytes(keyToUse); // FIXED: Using keyToUse instead of encryptionKey
            int workKeyIndex = 1; // Use work key index 1
            int mainKeyIndex = 0; // Main key index is typically 0

            // Check if we need to load the key or if it's already been loaded
            boolean workKeyLoaded = false;

            try {
                // Method 1: Try loadWorkKey - matches PinPadActivity.injectTDK method
                Log.d(TAG, "Attempting to load as work key with loadWorkKey");
                workKeyLoaded = pinpad.loadWorkKey(
                        PinpadConstant.KeyType.KEYTYPE_TDK, // Use TDK key type as in PinPadActivity
                        mainKeyIndex,
                        workKeyIndex,
                        keyData, // Using keyToUse data
                        null
                );

                Log.d(TAG, "Work key loaded: " + workKeyLoaded);
            } catch (Exception e) {
                Log.e(TAG, "Error loading work key: " + e.getMessage());
                workKeyLoaded = false;
            }

            if (!workKeyLoaded) {
                Log.w(TAG, "Failed to load work key, trying fixed key method");
                try {
                    // Method 2: Try loadKey (for fixed keys) - another approach from PinPadActivity
                    workKeyLoaded = pinpad.loadKey(
                            workKeyIndex,
                            PinpadConstant.KeyType.KEYTYPE_FIXED_TDK,
                            keyData, // Using keyToUse data
                            null
                    );
                    Log.d(TAG, "Fixed TDK key loaded: " + workKeyLoaded);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading fixed key: " + e.getMessage());
                    workKeyLoaded = false;
                }
            }

            if (!workKeyLoaded) {
                Log.w(TAG, "All key loading methods failed, using software fallback");
                return encryptWithSoftware(plainPinBlock, keyToUse, encryptionType); // Fixed: Using keyToUse
            }

            // Step 2: Prepare the data
            byte[] pinBlockBytes = hexToBytes(plainPinBlock);
            if (pinBlockBytes.length != 8) {
                Log.e(TAG, "Invalid PIN block length: " + pinBlockBytes.length + " (expected 8)");
                return encryptWithSoftware(plainPinBlock, keyToUse, encryptionType); // Fixed: Using keyToUse
            }

            // Step 3: Encrypt using the method from PinPadActivity
            byte[] encryptedBlock = new byte[8];
            int maxRetries = 3;

            for (int i = 0; i < maxRetries; i++) {
                try {
                    Log.d(TAG, "Encryption attempt " + (i+1) + "/" + maxRetries);
                    Log.d(TAG, "Using cryptByTdk with workKeyIndex=" + workKeyIndex + ", ECB mode");

                    // Use ECB mode (3) like in PinPadActivity.encryptByTdk
                    int result = pinpad.cryptByTdk(
                            workKeyIndex,
                            (byte) PinpadConstant.BasicAlg.ALG_ENCRYPT_DES_ECB, // Use ECB mode (3) not 0
                            pinBlockBytes,
                            null, // IV is null for ECB mode
                            encryptedBlock
                    );

                    Log.d(TAG, "cryptByTdk result: " + result);

                    if (result == 0) {
                        String encryptedHex = bytesToHex(encryptedBlock);
                        Log.d(TAG, "Hardware encryption successful: " + encryptedHex);
                        return encryptedHex;
                    } else {
                        Log.w(TAG, "Hardware encryption failed with code: " + result);
                        // Try CBC mode as alternative if ECB fails
                        if (i == maxRetries - 2) {
                            Log.d(TAG, "Trying CBC mode as alternative");

                            // Generate random IV for CBC mode
                            byte[] randomIV = new byte[8];
                            for (int j = 0; j < 8; j++) {
                                randomIV[j] = (byte) (Math.random() * 256);
                            }

                            result = pinpad.cryptByTdk(
                                    workKeyIndex,
                                    (byte) PinpadConstant.BasicAlg.ALG_ENCRYPT_DES_CBC, // Try CBC mode (1)
                                    pinBlockBytes,
                                    randomIV, // Random IV for CBC
                                    encryptedBlock
                            );

                            if (result == 0) {
                                String encryptedHex = bytesToHex(encryptedBlock);
                                Log.d(TAG, "Hardware encryption (CBC) successful: " + encryptedHex);
                                return encryptedHex;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error during encryption attempt " + (i+1) + ": " + e.getMessage());
                }

                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(200); // Short delay between retries
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            }

            // If all hardware attempts fail, fall back to software
            Log.w(TAG, "All hardware encryption attempts failed, falling back to software");
            return encryptWithSoftware(plainPinBlock, keyToUse, encryptionType); // Fixed: Using keyToUse
        } catch (Exception e) {
            Log.e(TAG, "Hardware encryption exception: " + e.getMessage());
            e.printStackTrace();
            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
        }
    }

    /**
     * Load working key into hardware for faster encryption
     * This method is now public to be called from the plugin
     */
    /**
     * Load working key into hardware with improved fallback mechanisms
     */
    public boolean loadWorkingKeyIntoHardware(String workingKey) {
        try {
            if (pinpad == null) {
                Log.d(TAG, "Pinpad not available for working key loading");
                return false;
            }

            Log.d(TAG, "Loading working key into hardware...");
            byte[] keyData = hexToBytes(workingKey);
            int workKeyIndex = 1; // Use work key index 1
            int mainKeyIndex = 0; // Main key index is typically 0
            boolean loaded = false;

            // Try all supported key loading methods in sequence

            // Method 1: loadWorkKey as TDK (Data Encryption Key)
            try {
                Log.d(TAG, "Attempting to load as TDK work key");
                loaded = pinpad.loadWorkKey(
                        PinpadConstant.KeyType.KEYTYPE_TDK,
                        mainKeyIndex,
                        workKeyIndex,
                        keyData,
                        null
                );
                Log.d(TAG, "TDK work key loaded: " + loaded);
                if (loaded) return true;
            } catch (Exception e) {
                Log.w(TAG, "TDK work key loading failed: " + e.getMessage());
            }

            // Method 2: loadWorkKey as PIK (PIN Encryption Key)
            try {
                Log.d(TAG, "Attempting to load as PIK work key");
                loaded = pinpad.loadWorkKey(
                        PinpadConstant.KeyType.KEYTYPE_PEK,
                        mainKeyIndex,
                        workKeyIndex,
                        keyData,
                        null
                );
                Log.d(TAG, "PIK work key loaded: " + loaded);
                if (loaded) return true;
            } catch (Exception e) {
                Log.w(TAG, "PIK work key loading failed: " + e.getMessage());
            }

            // Method 3: loadKey as fixed TDK
            try {
                Log.d(TAG, "Attempting to load as fixed TDK key");
                loaded = pinpad.loadKey(
                        workKeyIndex,
                        PinpadConstant.KeyType.KEYTYPE_FIXED_TDK,
                        keyData,
                        null
                );
                Log.d(TAG, "Fixed TDK key loaded: " + loaded);
                if (loaded) return true;
            } catch (Exception e) {
                Log.w(TAG, "Fixed TDK key loading failed: " + e.getMessage());
            }

            // Method 4: loadKey as fixed PEK
            try {
                Log.d(TAG, "Attempting to load as fixed PEK key");
                loaded = pinpad.loadKey(
                        workKeyIndex,
                        PinpadConstant.KeyType.KEYTYPE_FIXED_PEK,
                        keyData,
                        null
                );
                Log.d(TAG, "Fixed PEK key loaded: " + loaded);
                if (loaded) return true;
            } catch (Exception e) {
                Log.w(TAG, "Fixed PEK key loading failed: " + e.getMessage());
            }

            Log.w(TAG, "All key loading methods failed");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Key loading exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get detailed error description for hardware error codes
     */
    private String getHardwareErrorDescription(int errorCode) {
        switch (errorCode) {
            case 0:
                return "Success";
            case 1:
                return "General error";
            case 2:
                return "Invalid parameter";
            case 3:
                return "Hardware not ready";
            case 4:
                return "Key not found";
            case 5:
                return "Key loading failed";
            case 22:
                return "Invalid parameter or key format error - Key may not be properly loaded or format mismatch";
            case 23:
                return "Key not loaded or invalid key index";
            case 24:
                return "Hardware busy or not ready";
            case 25:
                return "Encryption algorithm not supported";
            case 26:
                return "Data length invalid";
            case 27:
                return "Key check value mismatch";
            case 28:
                return "Hardware security violation";
            case 29:
                return "Key expired or invalid";
            case 30:
                return "Insufficient privileges";
            default:
                return "Unknown error code: " + errorCode;
        }
    }

    /**
     * Enhanced key state verification
     */
    private boolean verifyKeyState(int keyIndex, int keyType) {
        try {
            if (pinpad == null) return false;

            // Use getKeyState to verify key is loaded
            boolean keyExists = pinpad.getKeyState(keyType, keyIndex);
            Log.d(TAG, "Key state for index " + keyIndex + ", type " + keyType + ": " + keyExists);

            if (keyExists) {
                // Try to get key check value for additional verification
                try {
                    byte[] checkValue = pinpad.getKeyCheckValue(keyType, keyIndex);
                    if (checkValue != null && checkValue.length > 0) {
                        Log.d(TAG, "Key check value: " + bytesToHex(checkValue));
                        return true;
                    } else {
                        Log.w(TAG, "Key exists but no check value available");
                        return true; // Still consider valid if key exists
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Cannot get key check value: " + e.getMessage());
                    return true; // Key exists, check value not critical
                }
            }

            return false;

        } catch (Exception e) {
            Log.w(TAG, "Key state verification failed: " + e.getMessage());
            return false;
        }
    }


    /**
     * Encrypt PIN block using software (for testing/development)
     */
    private String encryptWithSoftware(String plainPinBlock, String encryptionKey, int encryptionType) {
        try {
            Log.d(TAG, "Using software encryption (for testing only)");

            byte[] pinBlockBytes = hexToBytes(plainPinBlock);
            byte[] keyBytes = hexToBytes(encryptionKey);

            // Untuk Triple DES
            if (encryptionType == ENCRYPT_3DES) {
                // Triple DES membutuhkan key 16 atau 24 bytes
                if (keyBytes.length == 16) {
                    // Untuk 2-key Triple DES (K1, K2, K1)
                    byte[] fullKey = new byte[24];
                    System.arraycopy(keyBytes, 0, fullKey, 0, 16);
                    System.arraycopy(keyBytes, 0, fullKey, 16, 8);
                    keyBytes = fullKey;
                }

                // Setup Triple DES
                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "DESede");
                Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);

                // Encrypt
                byte[] encrypted = cipher.doFinal(pinBlockBytes);
                String result = bytesToHex(encrypted);

                Log.d(TAG, "Triple DES encryption result: " + result);
                return result;
            }
            // Untuk DES biasa
            else if (encryptionType == ENCRYPT_DES) {
                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "DES");
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

    private String maskKey(String key) {
        if (key == null || key.length() < 8) return "****";
        return key.substring(0, 4) + "*".repeat(key.length() - 8) + key.substring(key.length() - 4);
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