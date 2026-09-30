# Archive.org -> Android App

Aapki archive.org ki website ko Android App me convert kar diya gaya hai!

## 📁 Project me kya hai?
- **Advanced WebView App** - Download, Share, Back Button, Pull-to-Refresh, Offline Support
- **Play Store Ready** - 21+ Android versions supported
- **Full Source Code** - Android Studio me open karke APK bana sakte hain

## 🔧 Apna Link Kaise Dalein?

1.  `app/src/main/java/com/archiveapp/app/MainActivity.kt` file kholein
2.  Line 22 par ye milega:
    ```kotlin
    private val WEBSITE_URL = "https://archive.org/details/@your_username"
    ```
3.  Isko apne collection ka link se replace kar dein:
    ```kotlin
    private val WEBSITE_URL = "https://archive.org/details/@aapka_username"
    ```
4.  `app/src/main/res/values/strings.xml` me app ka naam badal dein:
    ```xml
    <string name="app_name">Aapke App Ka Naam</string>
    ```

## 📱 APK Kaise Banayein? (2 Tarike)

### Tarika 1: Android Studio se (Best)
1.  Android Studio download karein: https://developer.android.com/studio
2.  `ArchiveApp` folder ko Android Studio me Open karein
3.  Top menu me `Build > Build APK(s)` par click karein
4.  APK yahan milega: `app/build/outputs/apk/debug/app-debug.apk`

### Tarika 2: Online APK Builder se (Bina Android Studio ke)
1.  https://www.appsgeyser.com ya https://appmaker.xyz par jayein
2.  Website URL daalein aur APK download kar lein
3.  Lekin isme Download/Share jaise advanced features nahi honge

### Tarika 3: Main Aapke Liye Bana Du
Aap mujhe apna **exact archive.org ka link** aur **App ka naam + Icon** de dijiye, main yahin par APK build karke de dunga.

## ✨ Features jo is App me hain

✅ **WebView** - Aapki website app ki tarah khulegi
✅ **Download Manager** - Archive se PDF, MP3, Video direct download
✅ **Back Button** - Pichhle page par jayega, app band nahi hoga
✅ **Pull to Refresh** - Neeche kheench kar refresh
✅ **Share Button** - Kisi bhi page ko WhatsApp/FB par share
✅ **Offline Page** - Internet na hone par sundar error page
✅ **Zoom Support** - Books padhne ke liye zoom kar sakte hain
✅ **Fast Cache** - Dusri baar website tezi se khulegi

## 🚀 Play Store Par Dalna Hai?

Is project se aap direct AAB (Android App Bundle) bana sakte hain:
`Build > Generate Signed Bundle / APK > Android App Bundle`

Play Store ko chahiye:
- App Icon (512x512) - `app-icon.png` ready hai
- Feature Graphic
- Privacy Policy (archive.org ka content hai to mention karein)

## ❓ Help Chahiye?

Mujhe bas ye bhejiye:
1.  Aapki archive.org collection ka sahi link
2.  App ka final naam kya rakhna hai?
3.  Koi khaas icon hai to bhejiye

Main 2 minute me final APK ready kar dunga!
