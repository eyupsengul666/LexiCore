# LexiCore: Turkish Word Finder

Turkish word finder for Android. Minimal, fast, open source.

## Download

<a href="https://play.google.com/store/apps/details?id=com.dunyadanuzak.lexicore" target="_blank">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80"/>
</a>

## About

LexiCore is a simple utility for finding Turkish words from given letters. Built with a modern Android stack, optimized for performance.

No analytics. No cloud sync. Your searches stay on your device.

## Technical Stack

- Language: Kotlin 2.4.20
- UI: Jetpack Compose
- Database: Room 2.8.5
- DI: Hilt 2.60.1
- Target SDK: 37 (Android 17)
- Build: Gradle 9.8.0 / AGP 9.4.1
- Java: 25

## Build

Requirements:
- Java 25 at `/usr/lib/jvm/java-25-openjdk`
- Android SDK Platform 37.2 and Build-Tools 37.0.0
- Signing keystore `lexicore-key.jks` in the project root (alias `lexicore`, same store and key password)

```bash
./build.sh
```

Output files will be in the `release/` directory.

## Privacy

- LexiCore's own code collects no personal data
- Ads are served by Google AdMob, which receives device data such as the IP address with each ad request
- Ad requests use child age treatment (COPPA), which stops the advertising ID from being sent, and G-rated ads only
- No analytics SDKs
- No network requests (except ads)
- Local-only storage, no cloud backup

See privacy policy: https://www.dunyadanuzak.com/privacy-lexicore.html

## License

This project is licensed under the GNU General Public License v3.0 or later (GPL-3.0-or-later).

See [LICENSE](LICENSE) for details.

## Author

EPSL666  
https://www.dunyadanuzak.com
