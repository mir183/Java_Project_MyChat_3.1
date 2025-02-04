# MyChat Application

## Introduction

MyChat is a real-time chat application for Android, developed using **Java** and **Firebase**. It allows users to register, log in, and chat with others instantly. Additional features include profile management, and user presence status, making it a complete messaging solution.

---

## Features

- **User Registration & Authentication** – Secure user registration and login using Firebase Authentication.
- **Real-Time Messaging** – Send and receive messages instantly with Firebase Realtime Database.
- **Profile Management** – Users can update their profile picture and status.
- **User Presence Status** – Displays online/offline status for users.
- **Modern UI** – Clean, user-friendly, and responsive interface.

---

## Getting Started

### Prerequisites

Ensure you have the following tools installed on your system:

- [Android Studio](https://developer.android.com/studio)
- Java Development Kit (JDK 8 or later)
- A Firebase account

---

### Setup & Installation

#### Step 1: Clone the Repository
```bash
git clone https://github.com/mir183/Java_Project_MyChat_3.1.git
```

#### Step 2: Open in Android Studio
1. Launch Android Studio.
2. Select **Open an Existing Project** and navigate to the cloned repository.

#### Step 3: Configure Firebase
1. Log in to [Firebase Console](https://console.firebase.google.com/).
2. Create a new Firebase project.
3. Enable the following Firebase services:
   - **Authentication**
   - **Realtime Database**
   - **Storage**
4. Download the `google-services.json` file and place it in the `app/` directory.

#### Step 4: Build & Run
1. Connect your Android device or start an emulator.
2. In Android Studio, click **Run** (▶) to build and deploy the application.

---

## Project Structure

```
MyChat/
├── app/
│   ├── src/
│   │   ├── main/java/com/example/mychat/  # Java source code
│   │   ├── main/res/layout/               # XML UI layouts
│   │   ├── main/res/drawable/             # Icons and images
│   │   ├── main/res/values/               # Values (strings, colors, styles)
│   │   ├── main/res/anim/                 # Animations
│   │   ├── main/res/font/                 # Fonts
│   ├── build.gradle                       # Gradle configuration for the app module
│   ├── google-services.json               # Firebase configuration file
├── build.gradle                           # Top-level Gradle configuration
├── settings.gradle                        # Gradle settings
├── README.md                              # Documentation
└── .gitignore                             # Git ignore file
```

---

## Dependencies

The project uses the following dependencies:

```gradle
dependencies {
    implementation libs.appcompat
    implementation libs.material
    implementation libs.activity
    implementation libs.constraintlayout
    implementation libs.firebase.database
    implementation libs.firebase.auth
    implementation libs.firebase.storage
    implementation libs.firebase.inappmessaging
    testImplementation libs.junit
    androidTestImplementation libs.ext.junit
    androidTestImplementation libs.espresso.core
    implementation 'com.intuit.sdp:sdp-android:1.1.1'
    implementation 'de.hdodenhof:circleimageview:3.1.0'
    implementation 'com.squareup.picasso:picasso:2.8'
}
```

---

## Contribution

Contributions are welcome! Here's how you can contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes.
4. Submit a pull request with a detailed description of your changes.

---

## Contact

For any queries or feedback, feel free to reach out:

📧 **Email**: ahmedemon183@gmail.com

---

Happy coding! 🚀
