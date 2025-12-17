# 🆘 MayDay SOS – Emergency Alert Android App

MayDay SOS is an Android application designed to help users quickly send emergency alerts to predefined contacts along with their live location. The app focuses on reliability, simplicity, and real-world safety scenarios.

---

## 📱 Features

- One-tap **SOS button** to trigger emergency alerts
- Send SOS messages to **multiple emergency contacts**
- **Dynamic contact management**
  - Add or remove contacts
  - Prevents sending if any contact field is empty
- Automatically attaches **live GPS location**
  - Latitude & longitude
  - Clickable Google Maps link
- **Guaranteed message delivery**
  - Location timeout and fallback handling
  - Message sent even if GPS is unavailable
- **Multipart SMS support**
  - Handles long messages with location and URLs
- Emergency **Call button** for instant dialing
- Clean, responsive **Jetpack Compose UI**
- Vertical scrolling support for smaller screens

---

## 🛠️ Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose (Material 3)  
- **Location Services:** Google Fused Location Provider  
- **Android APIs:** SmsManager, Runtime Permissions  
- **Architecture:** Single-activity Compose architecture  

---

## 🔐 Permissions Used

The app requires the following permissions:

- `SEND_SMS` – To send emergency SMS messages  
- `ACCESS_FINE_LOCATION` – To fetch precise GPS location  
- `ACCESS_COARSE_LOCATION` – Location fallback support  

> All permissions are requested at runtime following Android best practices.

---

## ⚙️ How the App Works

1. User enters one or more emergency contact numbers  
2. User taps the **SOS** button  
3. App validates that all contact fields are filled  
4. App attempts to fetch the current GPS location  
   - Uses timeout-based fallback to avoid blocking  
5. SOS message is composed with location details  
6. Message is sent as **multipart SMS** to all contacts  

If location is unavailable, the message is still sent with a fallback note.

---

## 🧪 Testing Notes

- SMS functionality **must be tested on a real Android device**
- Android emulators do not reliably support outgoing SMS
- GPS accuracy improves when device location services are enabled

---

## 🚀 Future Enhancements

- SMS delivery and read confirmation
- Background / lock-screen SOS trigger
- Auto-call emergency contact if SMS fails
- Persistent contact storage using DataStore
- Emergency vibration and sound alerts

---

## 👨‍💻 Developer Notes

- Built with real-world edge cases in mind (GPS delay, long SMS, permission handling)
- Designed as a **production-quality learning project**
- Suitable for academic submission, portfolio, and internship evaluation

---

## 📄 License

This project is for educational and personal use.  
You are free to modify and extend it as needed.

---

⭐ If you find this project useful, feel free to star the repository!
