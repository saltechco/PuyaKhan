# PuyaKhan (پویا خوان)  ![Android](https://img.shields.io/static/v1?label=Platform&message=Android&color=green)  ![Kotlin](https://img.shields.io/static/v1?label=Kotlin&message=1.9.10&color=purple)

###### Made By SalTech

Nowadays, with payment and banking services moving online, visiting an ATM is often unnecessary. In Iran, all online payments require a One-Time Password (OTP).

**PuyaKhan** is an Android application designed to collect and manage all the one-time passwords sent to your phone via SMS. This app intelligently detects OTP messages, making it easier for you to access and use them.

-----

## Key Features 🚀

PuyaKhan is designed to simplify the process of using OTPs by offering a variety of features:

  * **Intelligent OTP Detection:** The app automatically analyzes incoming SMS messages to extract one-time passwords.
  * **Complete Information Extraction:** In addition to the code itself, it extracts and displays the **bank's name** and the **transaction amount**.
  * **Quick & Versatile Access Methods:** You can customize how you access new OTPs:
      * **Auto-Copy:** The OTP is automatically copied to your clipboard upon arrival.
      * **Notification:** A rich notification displays the full transaction details with a quick-copy button.
      * **Floating Window:** A small, floating window appears on your screen, allowing you to access the OTP without leaving your current app.
  * **OTP History:** The main screen shows a list of recent OTPs along with their expiration timers.
  * **Modern UI:** Built with **Jetpack Compose** following the latest **Material Design 3** guidelines.
  * **Optimized Performance:** The app is optimized to run smoothly on a wide range of devices, including those with low RAM.
  * **Privacy-Focused:** PuyaKhan does not require internet access, ensuring that your information is never sent online.

-----

## Screenshots 📸

![Main Screen](saltechco/puyakhan/PuyaKhan-dev/screenshots/Screenshot\_20250731\_164120.png)  ![Floating Window](saltechco/puyakhan/PuyaKhan-dev/screenshots/Screenshot\_20250731\_174728.png)  ![Notification](saltechco/puyakhan/PuyaKhan-dev/screenshots/Screenshot\_20250731\_180819.png)  ![Settings](saltechco/puyakhan/PuyaKhan-dev/screenshots/Screenshot\_20250731\_172019.png) 

-----

## Technical Details 🛠️

This project showcases modern Android development practices and technologies:

  * **UI:** The entire user interface is built with **Jetpack Compose**, enabling a declarative and modern UI layer.
  * **Architecture:** The app follows the **MVVM (Model-View-ViewModel)** architecture, ensuring a clean separation of concerns and a scalable codebase.
  * **Data Persistence & Security:**
      * **Proto DataStore** is used for asynchronously storing the list of OTP codes in a structured and efficient way.
      * All stored data is encrypted using the **Android Keystore** system, providing hardware-backed security for sensitive information.
  * **Background Processing:**
      * A **Foreground Service** is implemented to keep the app alive in the background, ensuring it can listen for new SMS messages even on devices with aggressive battery optimization (like Xiaomi).
      * A **BroadcastReceiver** is used to listen for the `SMS_RECEIVED_ACTION` and `BOOT_COMPLETED` intents, allowing the app to process OTPs as soon as they arrive and to restart its service after the device reboots.
  * **Advanced OTP Parsing:** The core logic for extracting codes from SMS messages uses a sophisticated algorithm based on regular expressions and contextual keyword analysis. It identifies positive keywords (like "رمز", "code") and negative keywords (like "تخفیف", "order") to accurately distinguish OTPs from other numerical data in messages.

-----

## License 📜 

This project is licensed under the SIL Open Font License 1.1 for fonts and the Apache License 2.0 for dependencies.
