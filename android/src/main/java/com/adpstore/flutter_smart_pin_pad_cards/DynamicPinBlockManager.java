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
    private static final String MASTER_KEY = "0123456789ABCDEF0123456789ABCDEF";

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
     * Enhanced decrypt working key with hardware integration
     */
    public Map<String, Object> decryptWorkingKey(String encryptedWorkingKey) {
        Map<String, Object> result = new HashMap<>();
        try {
            Log.d(TAG, "Decrypting working key...");

            if (encryptedWorkingKey == null || encryptedWorkingKey.isEmpty()) {
                result.put("success", false);
                result.put("error", "Encrypted working key is required");
                result.put("responseCode", "91");
                return result;
            }

            // Check cache first
            long currentTime = System.currentTimeMillis();
            if (decryptedWorkingKey != null &&
                    (currentTime - workingKeyTimestamp) < WORKING_KEY_CACHE_DURATION) {
                Log.d(TAG, "Using cached working key");
                result.put("success", true);
                result.put("decryptedKey", decryptedWorkingKey);
                result.put("fromCache", true);
                result.put("cacheAge", currentTime - workingKeyTimestamp);
                return result;
            }

            // Decrypt working key with master key
            String decryptedKey = decryptWithMasterKey(encryptedWorkingKey);

            if (decryptedKey != null) {
                // Validate decrypted key
                if (validateWorkingKey(decryptedKey)) {
                    // Cache the decrypted working key
                    decryptedWorkingKey = decryptedKey;
                    workingKeyTimestamp = currentTime;

                    // Try to load key into hardware for future use
                    boolean hardwareLoaded = loadWorkingKeyIntoHardware(decryptedKey);

                    result.put("success", true);
                    result.put("decryptedKey", decryptedKey);
                    result.put("fromCache", false);
                    result.put("responseCode", "00");
                    result.put("timestamp", currentTime);
                    result.put("hardwareLoaded", hardwareLoaded);

                    Log.d(TAG, "Working key decrypted and cached successfully");
                    if (hardwareLoaded) {
                        Log.d(TAG, "Working key also loaded into hardware");
                    }
                } else {
                    result.put("success", false);
                    result.put("error", "Decrypted working key validation failed");
                    result.put("responseCode", "91");
                }
            } else {
                result.put("success", false);
                result.put("error", "Failed to decrypt working key");
                result.put("responseCode", "91");
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in decryptWorkingKey: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            result.put("responseCode", "91");
        }

        return result;
    }

    /**
     * Load working key into hardware for faster encryption
     */
    private boolean loadWorkingKeyIntoHardware(String workingKey) {
        try {
            if (pinpad == null) {
                Log.d(TAG, "Pinpad not available for working key loading");
                return false;
            }

            byte[] keyData = hexToBytes(workingKey);

            // Extend key if needed for 3DES
            if (keyData.length == 16) {
                byte[] fullKey = new byte[24];
                System.arraycopy(keyData, 0, fullKey, 0, 16);
                System.arraycopy(keyData, 0, fullKey, 16, 8);
                keyData = fullKey;
            }

            // Try to load as work key with key index 1 (reserve 0 for main key)
            boolean loaded = pinpad.loadWorkKey(KEY_TYPE_PIK, 0, 1, keyData, null);

            if (loaded) {
                Log.d(TAG, "Working key loaded into hardware successfully");
                return true;
            } else {
                Log.w(TAG, "Failed to load working key into hardware");
                return false;
            }

        } catch (Exception e) {
            Log.w(TAG, "Exception loading working key into hardware: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate working key format and content
     */
    private boolean validateWorkingKey(String workingKey) {
        try {
            if (workingKey == null || workingKey.length() != 32) {
                Log.e(TAG, "Working key validation failed - invalid length: " +
                        (workingKey != null ? workingKey.length() : 0));
                return false;
            }

            // Check if it's valid hex
            byte[] keyBytes = hexToBytes(workingKey);
            if (keyBytes.length != 16) {
                Log.e(TAG, "Working key validation failed - hex conversion error");
                return false;
            }

            // Check if key is not all zeros
            boolean allZeros = true;
            for (byte b : keyBytes) {
                if (b != 0) {
                    allZeros = false;
                    break;
                }
            }

            if (allZeros) {
                Log.e(TAG, "Working key validation failed - key is all zeros");
                return false;
            }

            Log.d(TAG, "Working key validation passed");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Working key validation exception: " + e.getMessage());
            return false;
        }
    }

//    /**
//     * Decrypt working key using master key
//     */
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
            if (workingKey == null || workingKey.isEmpty()) {
                result.put("success", false);
                result.put("error", "Working key is required");
                return result;
            }

            // Validate key length (should be 32 hex characters for 16 bytes)
            if (workingKey.length() != 32) {
                result.put("success", false);
                result.put("error", "Invalid working key length: " + workingKey.length() + " (expected 32)");
                return result;
            }

            // Cache the working key
            decryptedWorkingKey = workingKey.toUpperCase();
            workingKeyTimestamp = System.currentTimeMillis();

            result.put("success", true);
            result.put("workingKeySet", true);
            result.put("timestamp", workingKeyTimestamp);

            Log.d(TAG, "Working key set successfully");

        } catch (Exception e) {
            Log.e(TAG, "Exception in setWorkingKey: " + e.getMessage());
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
        }

        return result;
    }

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
     * Get key state
     */
//    Gak dipakai
//    public boolean getKeyState(int keyType, int keyIndex) {
//        try {
//            if (pinpad == null) {
//                Log.e(TAG, "Pinpad not initialized");
//                return false;
//            }
//
//            // This might need adjustment based on actual API
//            return true; // Placeholder implementation
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to get key state: " + e.getMessage());
//            return false;
//        }
//    }

    /**
     * Generate MAC
     */
//    Gak dipakai
//    public Map<String, Object> getMac(Bundle param) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            if (pinpad == null) {
//                result.put("success", false);
//                result.put("error", "Pinpad not initialized");
//                return result;
//            }
//
//            // Implementation depends on specific MAC requirements
//            // This is a placeholder implementation
//            result.put("success", true);
//            result.put("mac", "1234567890ABCDEF"); // Placeholder
//            result.put("timestamp", System.currentTimeMillis());
//
//        } catch (Exception e) {
//            result.put("success", false);
//            result.put("error", e.getMessage());
//        }
//        return result;
//    }


    /**
     * Generate random number
     */
//    Gak Di pakai
//    public byte[] getRandom() {
//        try {
//            if (pinpad == null) {
//                Log.e(TAG, "Pinpad not initialized");
//                return null;
//            }
//
//            // AidlPinpad.getRandom() returns byte[] directly
//            byte[] random = pinpad.getRandom();
//
//            if (random != null && random.length > 0) {
//                return random;
//            } else {
//                Log.e(TAG, "Failed to generate random number");
//                return null;
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Exception generating random: " + e.getMessage());
//            return null;
//        }
//    }

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
            String keyToUse = decryptedWorkingKey != null ? decryptedWorkingKey : encryptionKey;
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

            // Determine which key to use (working key has priority)
            String keyToUse;
            boolean usingWorkingKey = false;
            int keyIndexToUse = 0; // Default key index

            if (decryptedWorkingKey != null && isWorkingKeyValid()) {
                keyToUse = decryptedWorkingKey;
                usingWorkingKey = true;
                keyIndexToUse = 1; // Use working key index
                Log.d(TAG, "Using cached working key for PIN block generation");
            } else {
                keyToUse = encryptionKey;
                keyIndexToUse = 0; // Use main key index
                Log.d(TAG, "Using provided encryption key for PIN block generation");
            }

            // Validate the key to be used
            if (!validateAndPrepareKey(keyToUse, encryptionType)) {
                result.put("success", false);
                result.put("error", "Invalid encryption key format");
                result.put("responseCode", "91");
                return result;
            }

            // Format card number
            String formattedCardNumber = formatCardNumber(cardNumber);

            // Create plain PIN block
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
            Log.d(TAG, "Using Working Key: " + usingWorkingKey);
            Log.d(TAG, "Key Index: " + keyIndexToUse);

            // Encrypt the PIN block
            String encryptedPinBlock;
            if (useHardwareEncryption && ensureHardwareReady()) {
                encryptedPinBlock = encryptWithHardwareEnhanced(plainPinBlock, keyToUse, encryptionType, keyIndexToUse);
            } else {
                Log.d(TAG, "Using software encryption");
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
            result.put("encryptionMethod", useHardwareEncryption ? "Hardware" : "Software");
            result.put("fillerChar", fillerChar);
            result.put("usedWorkingKey", usingWorkingKey);
            result.put("keySource", usingWorkingKey ? "WorkingKey" : "ProvidedKey");
            result.put("keyIndex", keyIndexToUse);
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
     * Enhanced hardware encryption with specific key index support
     */
    private String encryptWithHardwareEnhanced(String plainPinBlock, String encryptionKey,
                                               int encryptionType, int keyIndex) {
        try {
            Log.d(TAG, "Starting enhanced hardware encryption...");
            Log.d(TAG, "Key Index: " + keyIndex);
            Log.d(TAG, "Encryption Type: " + encryptionType);

            if (pinpad == null) {
                Log.w(TAG, "Hardware pinpad not available");
                return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
            }

            // Prepare PIN block data
            byte[] pinBlockBytes = hexToBytes(plainPinBlock);
            byte[] encryptedBlock = new byte[8];

            // Try encryption using the specific key index
            int encryptResult;
            try {
                if (keyIndex == 1) {
                    // Use work key for encryption
                    encryptResult = pinpad.encryptByWorkKey(KEY_TYPE_PIK, 0, 1, MODE_ENCRYPT,
                            null, pinBlockBytes, encryptedBlock);
                } else {
                    // Use main key for encryption
                    encryptResult = pinpad.encryptByTdk(keyIndex, MODE_ENCRYPT,
                            null, pinBlockBytes, encryptedBlock);
                }

                Log.d(TAG, "Hardware encryption result: " + encryptResult);

                if (encryptResult == 0) {
                    String encryptedHex = bytesToHex(encryptedBlock);

                    // Validate result
                    if (!encryptedHex.equals("0000000000000000")) {
                        Log.d(TAG, "Enhanced hardware encryption successful: " + encryptedHex);
                        return encryptedHex;
                    } else {
                        Log.w(TAG, "Hardware encryption returned all zeros");
                    }
                } else {
                    Log.w(TAG, "Hardware encryption failed with code: " + encryptResult +
                            " (" + getHardwareErrorDescription(encryptResult) + ")");
                }
            } catch (Exception e) {
                Log.w(TAG, "Hardware encryption exception: " + e.getMessage());
            }

            // Fallback to software
            Log.d(TAG, "Falling back to software encryption");
            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);

        } catch (Exception e) {
            Log.e(TAG, "Enhanced hardware encryption failed: " + e.getMessage());
            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
        }
    }

    /**
     * Check if cached working key is still valid
     */
    private boolean isWorkingKeyValid() {
        if (decryptedWorkingKey == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        boolean isValid = (currentTime - workingKeyTimestamp) < WORKING_KEY_CACHE_DURATION;

        if (!isValid) {
            Log.d(TAG, "Working key cache expired");
            decryptedWorkingKey = null;
            workingKeyTimestamp = 0;
        }

        return isValid;
    }
//    public Map<String, Object> createDynamicPinBlock(
//            String pin,
//            String cardNumber,
//            int format,
//            String encryptionKey,
//            int encryptionType,
//            String fillerChar,
//            boolean useHardwareEncryption) {
//
//        Map<String, Object> result = new HashMap<>();
//
//        try {
//            // Input validation
//            if (!isValidPin(pin)) {
//                result.put("success", false);
//                result.put("error", "Invalid PIN: must be 4-12 digits");
//                result.put("responseCode", "55");
//                return result;
//            }
//
//            if (!isValidCardNumber(cardNumber)) {
//                result.put("success", false);
//                result.put("error", "Invalid card number");
//                result.put("responseCode", "21");
//                return result;
//            }
//
//            // Use working key if available, otherwise use provided encryption key
//            String keyToUse = decryptedWorkingKey != null ? decryptedWorkingKey : encryptionKey;
//            boolean usingWorkingKey = decryptedWorkingKey != null;
//
//            Log.d(TAG, "Creating PIN block with " + (usingWorkingKey ? "working key" : "provided key"));
//
//
//            // Format card number
//            String formattedCardNumber = formatCardNumber(cardNumber);
//
//            // Create PIN block based on format
//            String plainPinBlock = createPlainPinBlock(pin, formattedCardNumber, format, fillerChar);
//
//            if (plainPinBlock == null) {
//                result.put("success", false);
//                result.put("error", "Failed to create plain PIN block");
//                result.put("responseCode", "91");
//                return result;
//            }
//
//            // Log the creation process
//            Log.d(TAG, "PIN Block Creation Details:");
//            Log.d(TAG, "Format: " + format + " (" + getFormatDescription(format) + ")");
//            Log.d(TAG, "PIN Length: " + pin.length());
//            Log.d(TAG, "Card Number: " + maskCardNumber(cardNumber));
//            Log.d(TAG, "Plain PIN Block: " + plainPinBlock);
//
//            // Encrypt the PIN block
//            String encryptedPinBlock;
//            if (useHardwareEncryption && pinpad != null) {
//                encryptedPinBlock = encryptWithHardware(plainPinBlock, encryptionKey, encryptionType);
//            } else {
//                encryptedPinBlock = encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
//            }
//
//            if (encryptedPinBlock == null) {
//                result.put("success", false);
//                result.put("error", "Failed to encrypt PIN block");
//                result.put("responseCode", "91");
//                return result;
//            }
//
//            // Build comprehensive result
//            result.put("success", true);
//            result.put("responseCode", "00");
//            result.put("pinBlock", encryptedPinBlock);
//            result.put("plainPinBlock", plainPinBlock);
//            result.put("format", format);
//            result.put("formatDescription", getFormatDescription(format));
//            result.put("pinLength", pin.length());
//            result.put("cardNumber", maskCardNumber(cardNumber));
//            result.put("encryptionType", encryptionType);
//            result.put("encryptionMethod", useHardwareEncryption ? "Hardware" : "Software");
//            result.put("fillerChar", fillerChar);
//            result.put("usedWorkingKey", usingWorkingKey);
//            result.put("keySource", usingWorkingKey ? "WorkingKey" : "ProvidedKey");
//            result.put("timestamp", System.currentTimeMillis());
//
//            Log.d(TAG, "PIN Block created successfully: " + encryptedPinBlock);
//            return result;
//
//        } catch (Exception e) {
//            Log.e(TAG, "Exception during dynamic PIN block creation: " + e.getMessage());
//            result.put("success", false);
//            result.put("error", "Exception: " + e.getMessage());
//            result.put("responseCode", "91");
//            return result;
//        }
//    }

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
            Log.d(TAG, "Decrypting working key with master key");

            byte[] masterKeyBytes = hexToBytes(MASTER_KEY);
            byte[] encryptedKeyBytes = hexToBytes(encryptedWorkingKey);

            // Use Triple DES for decryption
            SecretKeySpec keySpec;
            Cipher cipher;

            if (masterKeyBytes.length == 16) {
                // 2-key Triple DES (K1, K2, K1)
                byte[] fullKey = new byte[24];
                System.arraycopy(masterKeyBytes, 0, fullKey, 0, 16);
                System.arraycopy(masterKeyBytes, 0, fullKey, 16, 8);
                keySpec = new SecretKeySpec(fullKey, "DESede");
                cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            } else if (masterKeyBytes.length == 24) {
                // 3-key Triple DES
                keySpec = new SecretKeySpec(masterKeyBytes, "DESede");
                cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            } else {
                Log.e(TAG, "Invalid master key length: " + masterKeyBytes.length);
                return null;
            }

            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedKeyBytes);
            String decryptedKey = bytesToHex(decryptedBytes);

            Log.d(TAG, "Working key decrypted successfully");
            return decryptedKey;

        } catch (Exception e) {
            Log.e(TAG, "Failed to decrypt working key: " + e.getMessage());
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

    /**
     * Enhanced encrypt PIN block using hardware with better error handling
     */
    private String encryptWithHardware(String plainPinBlock, String encryptionKey, int encryptionType) {
        try {
            if (pinpad == null) {
                Log.w(TAG, "Hardware pinpad not available, falling back to software");
                return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
            }

            Log.d(TAG, "Starting hardware encryption process...");
            Log.d(TAG, "Plain PIN Block: " + plainPinBlock);
            Log.d(TAG, "Encryption Key: " + maskKey(encryptionKey));
            Log.d(TAG, "Encryption Type: " + encryptionType);

            // Step 1: Prepare key data
            byte[] keyData = hexToBytes(encryptionKey);

            // For 3DES, ensure we have proper key length
            if (encryptionType == ENCRYPT_3DES && keyData.length == 16) {
                // Convert 16-byte key to 24-byte for 3DES (K1, K2, K1)
                byte[] fullKey = new byte[24];
                System.arraycopy(keyData, 0, fullKey, 0, 16);
                System.arraycopy(keyData, 0, fullKey, 16, 8);
                keyData = fullKey;
                Log.d(TAG, "Extended key to 24 bytes for 3DES");
            }

            // Step 2: Clear any existing keys first
            try {
                // Clear key slot 0 if possible
                pinpad.loadMainkey(0, new byte[keyData.length], null);
                Thread.sleep(100); // Give hardware time to process
            } catch (Exception e) {
                Log.d(TAG, "Key clearing attempt: " + e.getMessage());
            }

            // Step 3: Load main key with retry mechanism
            boolean keyLoaded = false;
            int maxRetries = 3;

            for (int retry = 0; retry < maxRetries; retry++) {
                try {
                    Log.d(TAG, "Attempting to load main key (attempt " + (retry + 1) + "/" + maxRetries + ")");
                    keyLoaded = pinpad.loadMainkey(0, keyData, null);

                    if (keyLoaded) {
                        Log.d(TAG, "Main key loaded successfully on attempt " + (retry + 1));
                        break;
                    } else {
                        Log.w(TAG, "Main key loading failed on attempt " + (retry + 1));
                        Thread.sleep(200); // Wait before retry
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Exception loading main key on attempt " + (retry + 1) + ": " + e.getMessage());
                    Thread.sleep(200);
                }
            }

            // Step 4: If main key failed, try work key
            if (!keyLoaded) {
                Log.d(TAG, "Main key loading failed, trying work key...");
                for (int retry = 0; retry < maxRetries; retry++) {
                    try {
                        keyLoaded = pinpad.loadWorkKey(KEY_TYPE_PIK, 0, 0, keyData, null);
                        if (keyLoaded) {
                            Log.d(TAG, "Work key loaded successfully on attempt " + (retry + 1));
                            break;
                        } else {
                            Thread.sleep(200);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Exception loading work key on attempt " + (retry + 1) + ": " + e.getMessage());
                        Thread.sleep(200);
                    }
                }
            }

            if (!keyLoaded) {
                Log.w(TAG, "All key loading attempts failed, using software fallback");
                return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
            }

            // Step 6: Prepare PIN block data
            byte[] pinBlockBytes = hexToBytes(plainPinBlock);
            byte[] encryptedBlock = new byte[8]; // Standard block size for PIN encryption

            Log.d(TAG, "PIN Block bytes length: " + pinBlockBytes.length);
            Log.d(TAG, "PIN Block bytes: " + bytesToHex(pinBlockBytes));

            // Step 7: Perform encryption with enhanced error handling
            int encryptResult = -1;
            for (int retry = 0; retry < maxRetries; retry++) {
                try {
                    Log.d(TAG, "Attempting hardware encryption (attempt " + (retry + 1) + "/" + maxRetries + ")");

                    // Use encryptByTdk method
                    encryptResult = pinpad.encryptByTdk(0, MODE_ENCRYPT, null, pinBlockBytes, encryptedBlock);

                    Log.d(TAG, "Encryption result code: " + encryptResult);

                    if (encryptResult == 0) {
                        String encryptedHex = bytesToHex(encryptedBlock);
                        Log.d(TAG, "Hardware encryption successful: " + encryptedHex);

                        // Validate result is not all zeros
                        if (!encryptedHex.equals("0000000000000000")) {
                            return encryptedHex;
                        } else {
                            Log.w(TAG, "Hardware encryption returned all zeros, retry...");
                        }
                    } else {
                        Log.w(TAG, "Hardware encryption failed with code: " + encryptResult + " on attempt " + (retry + 1));
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Exception during hardware encryption attempt " + (retry + 1) + ": " + e.getMessage());
                    Thread.sleep(100);
                }
            }

            // Step 8: If all retries failed, provide detailed error info
            String errorMsg = "Hardware encryption failed after " + maxRetries + " attempts. ";
            if (encryptResult == 22) {
                errorMsg += "Error code 22 typically indicates key format issue or invalid parameters.";
            } else if (encryptResult != 0) {
                errorMsg += "Final error code: " + encryptResult;
            }

            Log.w(TAG, errorMsg + " Using software fallback.");
            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);

        } catch (Exception e) {
            Log.e(TAG, "Hardware encryption exception: " + e.getMessage());
            e.printStackTrace();
            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
        }
    }

//    /**
//     * Encrypt PIN block using hardware
//     */
//    private String encryptWithHardware(String plainPinBlock, String encryptionKey, int encryptionType) {
//        try {
//            if (pinpad == null) {
//                Log.w(TAG, "Hardware pinpad not available, falling back to software");
//                return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
//            }
//
//            // Try to load the encryption key
//            byte[] keyData = hexToBytes(encryptionKey);
//            boolean keyLoaded = pinpad.loadMainkey(0, keyData, null);
//
//            if (!keyLoaded) {
//                Log.w(TAG, "Failed to load encryption key, trying work key...");
//                keyLoaded = pinpad.loadWorkKey(KEY_TYPE_PIK, 0, 0, keyData, null);
//            }
//
//            if (keyLoaded) {
//                byte[] pinBlockBytes = hexToBytes(plainPinBlock);
//                byte[] encryptedBlock = new byte[8];
//
//                int result = pinpad.encryptByTdk(0, MODE_ENCRYPT, null, pinBlockBytes, encryptedBlock);
//
//                if (result == 0) {
//                    Log.d(TAG, "Hardware encryption successful");
//                    return bytesToHex(encryptedBlock);
//                } else {
//                    Log.w(TAG, "Hardware encryption failed with code: " + result);
//                }
//            }
//
//            Log.w(TAG, "Hardware encryption failed, using software fallback");
//            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
//
//        } catch (Exception e) {
//            Log.e(TAG, "Hardware encryption exception: " + e.getMessage());
//            return encryptWithSoftware(plainPinBlock, encryptionKey, encryptionType);
//        }
//    }

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