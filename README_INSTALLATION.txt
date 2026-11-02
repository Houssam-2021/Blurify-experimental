# Blurify v1.3 — Dual Control Center Test
This package is built from public Houssam-2021/Blur main (commit fbcbdc3).
Blufify ORIGINAL style remains unchanged and is selected by default.
The second HYPEROS style embeds Petal V3 as a WebView inside Blurify's existing AccessibilityService overlay.

Settings > Control center appearance > Original / HyperOS.
Right-side pull-down opens the selected Control Center.
Left-side pull-down opens Blurify Notifications.
In HyperOS mode, swiping RIGHT in the panel opens Notifications.
In Notifications, swiping LEFT returns to HyperOS.

Functional controls: brightness (Modify system settings permission), media volume,
flashlight (if available), rotation, DND (permission), sound mode, media transport.
Restricted toggles such as mobile data and airplane mode open system settings.
The status values are read from Android rather than fabricated.

Debug test app PACKAGE: com.miui.volumebar.petaltest
Version: 1.3-petaltest. Minimum API 28 / Android 9; Center requires Android 12+.
The test app is side-by-side with the existing signed Blurify and will NOT
update it. Do not enable both Blurify Accessibility Services at once.

Compiled with JDK 21, Android Gradle Plugin 8.9.1, Gradle 8.11.1,
Android SDK 36. APK signed with the debug key and signature verified.
Browser JS syntax and native bridge entrypoints checked.
No emulator or physical-device execution was possible here.
To publish production update, use the original release signing key
and remove the debug applicationIdSuffix before making a release APK.

NO CHANGES WERE PUSHED TO GITHUB. Connector write authorization is blocked.
