<div align="center">

<img src="app/src/main/res/drawable-nodpi/ic_usearch_foreground.png" width="120" alt="u search icon" />

# u search

**Tap. Type. Open.**

A tiny, private app-search widget for Android.
No permissions. No internet. About 140 KB.

[![Latest release](https://img.shields.io/github/v/release/Ariastiadi/usearch?style=flat-square&color=D71921&label=release)](https://github.com/Ariastiadi/usearch/releases/latest)
[![License: GPL v3](https://img.shields.io/badge/license-GPL--3.0-000000?style=flat-square)](LICENSE)
![Android 8.0+](https://img.shields.io/badge/android-8.0%2B-000000?style=flat-square)
![Permissions: 0](https://img.shields.io/badge/permissions-0-D71921?style=flat-square)
![Size: ~140 KB](https://img.shields.io/badge/size-~140%20KB-000000?style=flat-square)

[Install](#install) · [Features](#features) · [Privacy](#privacy-and-security) · [Verify the APK](#verify-the-apk) · [Build](#build-from-source)

</div>

---

## What is it?

u search puts one slim search bar on your home screen. Tap it, type a few letters, and the matching apps appear right above the keyboard. Press Enter or tap an app to open it.

It is **not a launcher**. You keep the launcher you already use, and you can place the widget on every home-screen page.

It was built for people who want the speed of a search-first launcher without handing over a pile of permissions, a network connection, or a 30 MB install.

## Features

| | |
|---|---|
| **Home-screen widget** | A 4×1 search bar you can drop on any page. Works with any launcher. |
| **Fast, forgiving search** | Matches the start of a word (`maps`), initials (`gm` → Google Maps), or letters in order (`wtsp` → WhatsApp). |
| **Keyboard first** | The search bar sits at the bottom of the screen and the keyboard opens right away, so the results are in thumb reach. |
| **Nothing-inspired look** | Black, gray and white with a single red dot, set in a monospace font. |
| **Tiny** | Plain Kotlin, no third-party libraries. About 140 KB. |
| **Updates in place** | Every release is signed with the same key, so a new version installs over the old one. No uninstall needed. |

## Privacy and security

The goal is a security posture you would expect from a hardened OS: the app cannot leak what it never has.

| Check | Status |
|---|---|
| Permissions in the manifest | **None.** No internet, contacts, location or storage. |
| Seeing your installed apps | Only through a scoped `<queries>` declaration, not `QUERY_ALL_PACKAGES`. |
| Data stored, logged or sent | **None.** Nothing is recorded, saved or transmitted. |
| Analytics, ads, trackers | **None.** No SDKs at all. |
| Keyboard learning | The search field asks your keyboard not to learn from what you type (`IME_FLAG_NO_PERSONALIZED_LEARNING`). |
| Backups | Disabled (`allowBackup="false"`). |

Because the app has no internet permission, it cannot phone home even if it wanted to. You can check this yourself in the manifest or in Android's app info screen.

## Install

**Requires Android 8.0 (API 26) or newer.**

1. **Komi Store**: [github-store.org/app?repo=Ariastiadi/usearch](https://github-store.org/app?repo=Ariastiadi/usearch)
2. **Obtainium**: add `https://github.com/Ariastiadi/usearch` as a source.
3. **Manual**: download `u-search-<version>.apk` from the [Releases page](https://github.com/Ariastiadi/usearch/releases/latest) and open it.

### Add the widget

1. Long-press an empty spot on your home screen.
2. Open **Widgets**.
3. Find **u search** and drag it to the page you want.
4. Repeat on every page where you want a search bar.

## How search works

Type a few letters and u search ranks installed apps in this order:

1. **Exact name**: `camera` finds Camera.
2. **Start of a word**: `maps` finds Google Maps.
3. **Initials**: `gm` finds Google Maps, `yt` finds YouTube.
4. **Letters in order**: `wtsp` finds WhatsApp, `cmra` finds Camera.

Everything happens on the device, in memory, and is forgotten when the screen closes.

## Verify the APK

Every release APK is signed with the same release key. Before installing from outside a store, compare the certificate fingerprint:

```
A6:A3:AE:49:BC:CF:DF:F0:E8:0D:95:76:61:C1:C9:95:58:84:C2:D0:09:17:40:0E:52:86:BC:85:C2:73:E7:8D
```

Check it with the Android SDK build tools:

```
apksigner verify --print-certs u-search-<version>.apk
```

The `SHA-256 digest` line must match the fingerprint above.

## Build from source

You need JDK 17 and Gradle 9.5 (or the Gradle wrapper).

```
git clone https://github.com/Ariastiadi/usearch.git
cd usearch
gradle assembleRelease
```

Without signing secrets, the release build is signed with the debug key, which is fine for local testing. Such an APK will not match the fingerprint above, and it cannot update over an official release.

### Automated builds

GitHub Actions builds the app on every push to `main`. When a GitHub Release is published, the signed APK is attached to it automatically. The signing key lives in repository secrets, not in the code.

## FAQ

**Is this a launcher?**
No. It is a widget plus a small search screen. Your launcher stays as it is.

**Why does it need no permissions?**
It only lists the apps you already have and opens one when you tap it. It has no reason to touch the internet, your files or your contacts.

**Will it search my files, contacts or the web?**
No. Apps only, on purpose. That is what keeps it small and private.

**Do I need to uninstall before updating?**
No. Updates install over the old version because every release uses the same signing key.

## License

u search is free software, licensed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE) for the full text.

<div align="center">

Made by [Ariastiadi](https://github.com/Ariastiadi)

</div>
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
