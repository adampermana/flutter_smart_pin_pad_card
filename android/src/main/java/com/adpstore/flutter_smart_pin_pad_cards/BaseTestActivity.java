package com.adpstore.flutter_smart_pin_pad_cards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.List;

public class BaseTestActivity extends Activity {
    private static final String TAG = "BaseTestActivity";
    private static final int SHOW_MSG = 0;

    protected LinearLayout messageLayout;
    protected ScrollView scrollView;
    protected LinearLayout inputLayout;
    protected ProgressDialog progressDialog;

    protected EditText moneyInput;
    protected EditText orderInput;
    protected EditText passwordInput;
    protected EditText nameInput;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String message1 = bundle.getString("msg1");
            String message2 = bundle.getString("msg2");
            int color = bundle.getInt("color");
            updateView(message1, message2, color);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create base layout
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        // Initialize message layout
        messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.VERTICAL);

        // Initialize scroll view
        scrollView = new ScrollView(this);
        scrollView.addView(messageLayout);

        // Add scroll view to root layout
        rootLayout.addView(scrollView);

        setContentView(rootLayout);
    }

    protected void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    protected void updateView(final String msg1, final String msg2, final int color) {
        LinearLayout messageItem = new LinearLayout(this);
        messageItem.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView1 = new TextView(this);
        TextView textView2 = new TextView(this);

        textView1.setText(msg1);
        textView2.setText(msg2);

        textView1.setTextColor(Color.BLACK);
        textView2.setTextColor(color);

        textView1.setTextSize(20);
        textView2.setTextSize(20);

        messageItem.addView(textView1);
        messageItem.addView(textView2);

        messageLayout.addView(messageItem);

        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    protected void showMessage(final String msg1, final String msg2, final int color) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("msg1", msg1);
        bundle.putString("msg2", msg2);
        bundle.putInt("color", color);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    protected void showMessage(final String msg1, final int color) {
        showMessage(msg1, "", color);
    }

    protected void showMessage(String message) {
        showMessage(message, Color.BLUE);
    }

    protected void dismissProgressDialog(String message) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            showMessage(message, Color.BLUE);
        }
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected AlertDialog.Builder createInputDialog(String title, String hint, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);

        moneyInput = new EditText(this);
        moneyInput.setHint(hint);
        inputLayout.addView(moneyInput);

        builder.setTitle(title)
                .setView(inputLayout)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null);

        return builder;
    }

    protected AlertDialog.Builder createDoubleInputDialog(String title, String hint1, String hint2,
                                                          DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);

        moneyInput = new EditText(this);
        moneyInput.setHint(hint1);

        orderInput = new EditText(this);
        orderInput.setHint(hint2);

        inputLayout.addView(moneyInput);
        inputLayout.addView(orderInput);

        builder.setTitle(title)
                .setView(inputLayout)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null);

        return builder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    protected static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        ResolveInfo serviceInfo = resolveInfo.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName,
                serviceInfo.serviceInfo.name);

        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}