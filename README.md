




World Clock with timezones is a mighty little app that adds fun to timekeeping. Ditch boring digital clock faces and instead use intricate mechanical watchfaces.
<p><a href="https://play.google.com/store/apps/details?id=com.coroutines.clockwithtimezone">World Clock With Timezone on Google Play</a></p>
<p></p>
<p>Cool AGSL effects thanks to https://github.com/JumpingKeyCaps</p>

<table style="width:100%">
  <tr>
    <th>Watch Selection</th>
    <th>Watch Detail</th> 
    <th>List of Watches</th> 
  </tr>
  <tr>
    <td style="width:33%"><img src="https://github.com/dmitrish/SwissTime/blob/main/art/screenshot1.png"/></td>
    <td style="width:33%"><img src="https://github.com/dmitrish/SwissTime/blob/main/art/Screenshot_20250706_194254_Timezone%20Clock.jpg"/></td> 
    <td style="width:33%"><img src="https://github.com/dmitrish/SwissTime/blob/main/art/Screenshot_20250709_165100_Timezone%20Clock.jpg"/></td>
  
  </tr>
  
</table>
<p></p>

<table>
  <tr>
    <td><img src="https://github.com/dmitrish/SwissTime/blob/main/art/worldclock.gif"></td>
   <td><img src="https://github.com/dmitrish/SwissTime/blob/main/art/shader.gif"></td>
  </tr>
</table>

<table style="width:100%">
  <tr>
    <th>Live Wallpaper Home Screen</th>
    <th>Live Wallpaper Lock Screen</th>
  </tr>
  <tr>
    <td>
      <img src="https://github.com/dmitrish/SwissTime/blob/main/art/hm.gif"/>
    </td>
    <td>
        <img src="https://github.com/dmitrish/SwissTime/blob/main/art/lockedscreen.gif" />
    </td>
  </tr>
</table>

<p></p>
<table style="width:100%">
  <tr>
    <th>Round Watch</th>
    <th>Square Watch</th> 
    <th>Round Watch</th>
  </tr>
  <tr>
    <td style="width:33%"><img src="https://github.com/dmitrish/SwissTime/blob/main/art/roundwatch.gif"/></td>
    <td style="width:33%"><img src="https://github.com/dmitrish/SwissTime/blob/main/art/watchsquare.gif"></td> 
   <td style="width:33%"><img src="https://github.com/dmitrish/SwissTime/blob/main/art/chronomaguswatch.gif"></td>
  
  </tr>
  
</table>
<p></p>




---

## Baseline Profile: Where to find guidance and how to run it

If you're looking for where the Baseline Profile guidance is provided, here are the essentials directly in this repository:

- How to run from Android Studio
  - Connect a device/emulator (API 33+ recommended; rooted API 28+ also works).
  - In Run/Debug configurations, choose "Generate Baseline Profile" or right‑click baselineprofile/src/main/java/com/coroutines/baselineprofile/BaselineProfileGenerator.kt and Run.
  - The generator starts the app, waits for the Welcome screen to settle ("Tap to zoom" visible), collects the profile, and copies it into the app module.

- How to run from the command line
  - ./gradlew :app:generateReleaseBaselineProfile
  - The baselineprofile module is configured to use connected devices (see baselineprofile/build.gradle.kts).

- What gets generated (and where)
  - Baseline profile: app/src/main/baselineProfiles/baseline-prof.txt
  - Startup profile (used for dex layout): app/src/main/startupProfiles/startup-prof.txt

- How it gets into your AAB/APK
  - The Android Gradle plugin packages baseline-prof.txt into assets/dexopt/baseline.prof.
  - androidx.profileinstaller installs it on first launch; Google Play also uses it from the AAB to precompile hot code paths at install time.

- Verify in your bundle
  - Build: ./gradlew :app:bundleRelease
  - Inspect: unzip app/build/outputs/bundle/release/app-release.aab and look for base/assets/dexopt/baseline.prof

- Troubleshooting
  - If the generator times out waiting for UI, ensure you see the "Tap to zoom" text on the Welcome screen or switch device locale to English.
  - Re-run with logs: ./gradlew :app:generateReleaseBaselineProfile --info
  - Make sure a device is connected and unlocked.
More sources in this repo:
- baselineprofile/src/main/java/com/coroutines/baselineprofile/BaselineProfileGenerator.kt (generator + UI wait logic)
- baselineprofile/src/main/java/com/coroutines/baselineprofile/StartupBenchmarks.kt (compare startup with/without profiles)
