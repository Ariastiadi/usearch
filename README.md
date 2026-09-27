<p align="center">
  <img src="app/src/main/res/drawable-nodpi/ic_usearch_foreground.png" width="120" alt="u search icon" />
</p>

<h1 align="center">u search</h1>

<p align="center">
  A tiny, private app search widget for Android.<br/>
  Tap. Type. Open.
</p>

---

## What it does

u search puts a small search bar on your home screen. Tap it, type a few letters, and the app you want appears right above the bar. Press Enter (or tap the result) to open it. That's it.

It works with any launcher. You don't have to replace your home screen, and you can put the widget on every page.

## Features

- **Home screen widget.** A slim 4×1 search bar you can resize and place on any page.
- **Fast, forgiving search.** Finds apps by name, by the start of any word (`maps` → Google Maps), by initials (`gm` → Google Maps) and by letters in order (`wtsp` → WhatsApp).
- **Search bar at the bottom,** right where your thumb is, with the keyboard already open.
- **Nothing-inspired look.** Black, grey and white with a single red dot.
- **Tiny.** The whole app is about 140 KB.

## Privacy

u search is built to know as little as possible:

- **No permissions at all.** No internet, no contacts, no location, no storage.
- It can only see which apps have an icon in your app drawer. That's all it needs to search them.
- Nothing is saved, logged or sent anywhere.
- The keyboard is asked not to learn from what you type in the search bar.
- No ads, no trackers, no analytics. The code is open for anyone to check.

## Install

Pick one:

- **Komi Store:** [open u search in Komi Store](https://github-store.org/app?repo=Ariastiadi/usearch)
- **Obtainium:** add `https://github.com/Ariastiadi/usearch`
- **Manually:** download the latest `u-search-vX.Y.Z.apk` from [Releases](https://github.com/Ariastiadi/usearch/releases/latest)

Updates install over the old version without uninstalling.

### Add the widget

1. Long-press an empty spot on your home screen.
2. Choose **Widgets**.
3. Find **u search** and drag it onto the page.

Repeat on any other page where you want a search bar.

## Verify the APK

Every release is signed with the same key. The SHA-256 fingerprint of the signing certificate is:

```
A6:A3:AE:49:BC:CF:DF:F0:E8:0D:95:76:61:C1:C9:95:58:84:C2:D0:09:17:40:0E:52:86:BC:85:C2:73:E7:8D
```

## Requirements

Android 8.0 (API 26) or newer.

## Build from source

The app is plain Kotlin with no third-party libraries. GitHub Actions builds it on every commit (see `.github/workflows/build.yml`). To build locally with Gradle 9.5 and JDK 17:

```
gradle :app:assembleRelease
```

Without the signing secrets the release APK is signed with the debug key.

## License

u search is free software under the [GNU General Public License v3.0](LICENSE).
