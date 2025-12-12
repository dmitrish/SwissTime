# Swiss Time Watch - WearOS Companion App

This is the WearOS companion app for the Swiss Time Watch Android application. It allows users to view and interact with a collection of elegant watch faces on their WearOS smartwatch.

## Features

- Browse a collection of elegant watch faces
- View detailed watch faces in full screen
- Real-time updating watch hands synchronized with the current time
- Support for different time zones

## Implementation Details

The app is built using:

- Kotlin
- Jetpack Compose for WearOS
- WearOS navigation components
- Canvas-based watch face rendering

## Project Structure

- `MainActivity.kt` - Main entry point for the app, sets up navigation
- `model/WatchFace.kt` - Data model for watch faces
- `repository/WatchFaceRepository.kt` - Repository for managing watch faces
- `watchfaces/` - Directory containing watch face implementations

## Building and Running

1. Open the project in Android Studio
2. Select the wearOS module
3. Build and run on a WearOS device or emulator

## Adding New Watch Faces

To add a new watch face:

1. Create a new Kotlin file in the `watchfaces` directory
2. Implement the watch face using Compose Canvas
3. Add the watch face to the `watchFaces` list in `WatchFaceRepository.kt`

## Future Improvements

- Add more watch faces
- Implement complications
- Add settings for customizing watch faces
- Implement data synchronization with the phone app
