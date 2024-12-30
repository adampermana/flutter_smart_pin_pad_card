package com.adpstore.flutter_smart_pin_pad_cards;

import androidx.annotation.NonNull;
import android.os.RemoteException;
import android.util.Log;
import android.content.Context;
import com.topwise.cloudpos.aidl.emv.AidlCheckCardListener;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.magcard.TrackData;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import java.util.HashMap;
import java.util.Map;

public class FlutterSmartPinPadCardsPlugin implements FlutterPlugin, MethodCallHandler {
  private static final String TAG = "SmartPinPadCards";
  private MethodChannel channel;
  private AidlEmvL2 aidlEmvL2;
  private Context context;
  private final static int TIME_OUT = 60 * 1000;
  private Result pendingResult;
  private boolean isCardReading = false;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_smart_pin_pad_cards");
    channel.setMethodCallHandler(this);

    // Initialize device service
    boolean bindResult = DeviceServiceManager.getInstance().bindDeviceService(context);
    Log.d(TAG, "Device service bind result: " + bindResult);

    // Initialize EMV service
    initializeEmvService();
  }

  private void initializeEmvService() {
    try {
      aidlEmvL2 = DeviceServiceManager.getInstance().getEmvL2();
      if (aidlEmvL2 == null) {
        Log.e(TAG, "EMV service initialization failed");
      } else {
        Log.d(TAG, "EMV service initialized successfully");
      }
    } catch (Exception e) {
      Log.e(TAG, "Error initializing EMV service: " + e.getMessage());
    }
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "startCardReading":
        if (isCardReading) {
          result.error("ALREADY_READING", "Card reader is already active", null);
          return;
        }
        startCardReading(result);
        break;
      case "stopCardReading":
        stopCardReading(result);
        break;
      default:
        result.notImplemented();
    }
  }

  private void startCardReading(final Result result) {
    if (aidlEmvL2 == null) {
      initializeEmvService();
      if (aidlEmvL2 == null) {
        result.error("START_ERROR", "Card reader hardware not available", null);
        return;
      }
    }

    try {
      isCardReading = true;
      pendingResult = result;

      aidlEmvL2.checkCard(true, false, false, TIME_OUT, new AidlCheckCardListener.Stub() {
        @Override
        public void onFindMagCard(TrackData trackData) throws RemoteException {
          Map<String, String> cardData = new HashMap<>();
          cardData.put("firstTrack", trackData.getFirstTrackData());
          cardData.put("secondTrack", trackData.getSecondTrackData());
          cardData.put("thirdTrack", trackData.getThirdTrackData());
          cardData.put("cardNumber", trackData.getCardno());
          cardData.put("expiryDate", trackData.getExpiryDate());
          cardData.put("serviceCode", trackData.getServiceCode());

          isCardReading = false;
          if (pendingResult != null) {
            pendingResult.success(cardData);
            pendingResult = null;
          }
        }

        @Override
        public void onSwipeCardFail() throws RemoteException {
          sendError("SWIPE_FAILED", "Failed to read card");
        }

        @Override
        public void onFindICCard() throws RemoteException {
          // Not implemented in this version
        }

        @Override
        public void onFindRFCard() throws RemoteException {
          // Not implemented in this version
        }

        @Override
        public void onTimeout() throws RemoteException {
          sendError("TIMEOUT", "Card reading timed out");
        }

        @Override
        public void onCanceled() throws RemoteException {
          sendError("CANCELLED", "Card reading was cancelled");
        }

        @Override
        public void onError(int error) throws RemoteException {
          sendError("READER_ERROR", "Card reader error: " + error);
        }
      });
    } catch (RemoteException e) {
      Log.e(TAG, "Remote exception during card reading: " + e.getMessage());
      result.error("START_ERROR", "Failed to start card reading: " + e.getMessage(), null);
      isCardReading = false;
    }
  }

  private void stopCardReading(Result result) {
    if (aidlEmvL2 != null && isCardReading) {
      try {
        aidlEmvL2.cancelCheckCard();
        isCardReading = false;
        result.success(null);
      } catch (RemoteException e) {
        result.error("STOP_ERROR", "Failed to stop card reader: " + e.getMessage(), null);
      }
    } else {
      result.success(null);
    }
  }

  private void sendError(String code, String message) {
    isCardReading = false;
    if (pendingResult != null) {
      pendingResult.error(code, message, null);
      pendingResult = null;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
    if (isCardReading && aidlEmvL2 != null) {
      try {
        aidlEmvL2.cancelCheckCard();
      } catch (RemoteException ignored) {
      }
    }
    DeviceServiceManager.getInstance().unBindDeviceService();
    channel.setMethodCallHandler(null);
    aidlEmvL2 = null;
    context = null;
  }
}