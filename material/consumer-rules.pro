# Keep AIDL interfaces
-keep interface com.android.material.aidl.** { *; }

# Keep classes used by reflection
-keep class com.android.material.reflected.** { *; }

# Keep annotations
-keep @interface com.android.material.annotations.**

# Keep custom views
-keep class com.android.material.ui.** extends android.view.View { *; }
