# CamScanner

A lightweight, minimal Android application for capturing photos via the camera and converting them into PDF documents. Built with modern Android technologies including Jetpack Compose, CameraX, and Coroutines.

## 🚀 Features

*   **Real-time Camera Preview:** Powered by CameraX for a smooth, lifecycle-aware viewfinder.
*   **Photo Capture:** Quick capture button with state management to prevent double-clicks.
*   **PDF Generation:** Simple one-tap conversion of captured images into a PDF file.
*   **Dynamic Filters:** Includes a Grayscale toggle for document-style processing.
*   **Torch Control:** Toggle the device flashlight directly from the UI.
*   **Scoped Storage:** Uses `DocumentPicker` to allow users to save PDFs securely to their preferred location.

## 🛠️ Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Camera:** AndroidX CameraX (Core, View, and Lifecycle)
*   **Architecture:** MVVM (ViewModel + StateFlow)
*   **Image Loading:** Coil 3
*   **Async Processing:** Kotlin Coroutines
*   **Permissions:** Accompanist Permissions

## 🏗️ Prerequisites

*   Android Studio Ladybug (or newer)
*   Android SDK 24+ (Android 7.0 or higher)
*   Kotlin 2.1.0+

    
