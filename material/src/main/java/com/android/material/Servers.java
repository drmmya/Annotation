package com.android.material;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.*;

public class Servers {
    public interface Callback {
        void result(List<JSONObject> free, List<JSONObject> premium);
    }

    public static void getServers(Activity a, Callback cb) {
        new Thread(() -> {
            List<JSONObject> free = new ArrayList<>(), premium = new ArrayList<>();
            try {
                HttpURLConnection c = (HttpURLConnection) new URL("https://yeasinatoz.com/library/sr.php").openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder(); String l;
                while ((l = r.readLine()) != null) sb.append(l);
                JSONArray arr = new JSONArray(sb.toString());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    if ("1".equals(o.optString("isFree", "0"))) free.add(o); else premium.add(o);
                }
            } catch (Exception e) { /* ignore for now */ }
            new Handler(Looper.getMainLooper()).post(() -> cb.result(free, premium));
        }).start();
    }
}
