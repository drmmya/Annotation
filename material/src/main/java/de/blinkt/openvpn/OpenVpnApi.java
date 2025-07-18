package de.blinkt.openvpn;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.material.R;

import java.io.IOException;
import java.io.StringReader;

import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;

public class OpenVpnApi {

    private static final String  TAG = "OpenVpnApi";
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static void startVpn(Context context, String inlineConfig, String sCountry, String userName, String pw) throws RemoteException {
        if (TextUtils.isEmpty(inlineConfig)) throw new RemoteException("config is empty");
        startVpnInternal(context, inlineConfig, sCountry, decrypt(userName), decrypt(pw));
    }

    static String decrypt(String encryptedStr) {
        StringBuilder sb = new StringBuilder(encryptedStr);
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < sb.length(); ++i) {
            if ((i + 1) % 2 == 0) {
                str.append(sb.charAt(i));
            }
        }

        return str.reverse().toString();
    }

    static void startVpnInternal(Context context, String inlineConfig, String sCountry, String userName, String pw) throws RemoteException {
        ConfigParser cp = new ConfigParser();
        try {
            cp.parseConfig(new StringReader(inlineConfig));
            VpnProfile vp = cp.convertProfile();// Analysis.ovpn
            Log.d(TAG, "startVpnInternal: =============="+cp+"\n" +
                    vp);
            vp.mName = sCountry;
            if (vp.checkProfile(context) != R.string.no_error_found){
                throw new RemoteException(context.getString(vp.checkProfile(context)));
            }
            vp.mProfileCreator = context.getPackageName();
            vp.mUsername = userName;
            vp.mPassword = pw;
            ProfileManager.setTemporaryProfile(context, vp);
            VPNLaunchHelper.startOpenVpn(vp, context);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            throw new RemoteException(e.getMessage());
        }
    }
}
