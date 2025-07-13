# Keep AIDL interface and generated stub classes
-keep interface de.blinkt.openvpn.** { *; }
-keep class de.blinkt.openvpn.** extends android.os.Binder { *; }
-keep class de.blinkt.openvpn.** implements android.os.IInterface { *; }
