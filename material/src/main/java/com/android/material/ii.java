package com.android.material;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class ii {

    private static final String PREF_NAME = "com.android.material.DrPrefs";
    private static final String OPEN_COUNT_KEY = "open_count_";
    private static final String LAST_INDEX_KEY = "last_index_";

    public static void i (Activity activity, String packageName) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // API URL
                String urlString = "https://yeasinatoz.com/library/dr.php";
                URL url = new URL(urlString);

                // HTTP POST
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postData = "packagename=" + URLEncoder.encode(packageName, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                // Read Response
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null)
                        response.append(line);
                }

                // Parse JSON
                JSONObject json = new JSONObject(response.toString());
                String status = json.optString("status", "");
                if (!"exists".equals(status)) {
                    // If not found or just added, do nothing
                    return;
                }

                String action = json.optString("action", "");
                String redirectUrl = json.optString("redirect_url", "");
                int index = json.has("index") ? json.optInt("index", 0) : 0;

                String adTitle = json.optString("ad_title", "It worked!");
                String adBody = json.optString("ad_body", "This is an app open ad. Click here for more.");
                String adUrl = json.optString("ad_url", redirectUrl);

                if ("true".equalsIgnoreCase(action) && redirectUrl != null && !redirectUrl.isEmpty() && index > 0) {
                    SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                    String countKey = OPEN_COUNT_KEY + packageName;
                    String indexKey = LAST_INDEX_KEY + packageName;

                    int openCount = prefs.getInt(countKey, 0);
                    int lastIndex = prefs.getInt(indexKey, 0);

                    // If index value has changed, reset open count
                    if (lastIndex != index) {
                        openCount = 0;
                    }

                    openCount++;

                    if (openCount >= index) {
                        // Show dialog and reset counter
                        new Handler(Looper.getMainLooper()).post(() -> {
                            showInterstitialAdDialog(activity, adTitle, adBody, adUrl, redirectUrl);
                        });
                        openCount = 0;
                    }

                    // Save openCount and index
                    prefs.edit()
                            .putInt(countKey, openCount)
                            .putInt(indexKey, index)
                            .apply();
                }
                // If action is not true, do nothing.

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private static void showInterstitialAdDialog(Activity activity, String adTitle, String adBody, String adUrl, String redirectUrl) {
        try {
            LayoutInflater inflater = activity.getLayoutInflater();
            int layoutId = activity.getResources().getIdentifier("dialog_interstitial_ad", "layout", activity.getPackageName());
            View dialogView = inflater.inflate(layoutId, null);

            AlertDialog adDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            // Set ad content
            TextView titleView = dialogView.findViewById(activity.getResources().getIdentifier("ad_title", "id", activity.getPackageName()));
            TextView bodyView = dialogView.findViewById(activity.getResources().getIdentifier("ad_body", "id", activity.getPackageName()));
            TextView urlView = dialogView.findViewById(activity.getResources().getIdentifier("ad_url", "id", activity.getPackageName()));
            Button openBtn = dialogView.findViewById(activity.getResources().getIdentifier("btn_open", "id", activity.getPackageName()));
            ImageButton closeBtn = dialogView.findViewById(activity.getResources().getIdentifier("btn_close", "id", activity.getPackageName()));

            if (titleView != null) titleView.setText(adTitle);
            if (bodyView != null) bodyView.setText(adBody);
            if (urlView != null) urlView.setText(adUrl);

            // On both open and close, open URL in browser and close dialog
            View.OnClickListener redirectListener = v -> {
                adDialog.dismiss();
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                    activity.startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            if (openBtn != null) openBtn.setOnClickListener(redirectListener);
            if (closeBtn != null) closeBtn.setOnClickListener(redirectListener);

            adDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            // fallback: just open browser if dialog fails
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                activity.startActivity(browserIntent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
