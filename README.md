# CityFix

<p align="center">
  <b>UI preview</b> — snapshots taken from the submitted group build
  <br/><br/>
  <img src="docs/screenshots/home.png" width="30%" alt="Main list (citizen)" />
  &nbsp;
  <img src="docs/screenshots/new_report.png" width="30%" alt="New report" />
  &nbsp;
  <img src="docs/screenshots/report_detail.png" width="30%" alt="Report detail" />
</p>

---

Android app (Java) for **citizen and worker** urban maintenance reports: create
issues with **photos and a map pin**, **Firestore**-backed report lists (with
pagination, filters, and a boolean search), **favourites**, in-app
**notifications**, and an **LLM Q\&A** screen.

Group **G04**, **COMP6442** Semester 1, 2025, Australian National University.

> **This repository is a portfolio mirror of our coursework project.** The
> upstream lived on the ANU GitLab instance. Team member lines are given in
> Javadoc in the source; this README does not specify individual marks or roles
> for grading.

## More screenshots

### Login

<p align="center">
  <img src="docs/screenshots/login.png" width="32%" alt="Login" />
  <img src="docs/screenshots/login_error.png" width="32%" alt="Login with validation error" />
</p>

### Report lists and favourites

<p align="center">
  <img src="docs/screenshots/home.png" width="32%" alt="Home / main list" />
  <img src="docs/screenshots/reports.png" width="32%" alt="Report list with filters" />
  <img src="docs/screenshots/marked_reports.png" width="32%" alt="Marked / favourite reports" />
</p>

### Submit a report and track its status

<p align="center">
  <img src="docs/screenshots/new_report.png" width="32%" alt="New report" />
  <img src="docs/screenshots/report_detail.png" width="32%" alt="Report detail (citizen)" />
  <img src="docs/screenshots/report_detail_worker.png" width="32%" alt="Report detail (worker)" />
</p>

### Profile

<p align="center">
  <img src="docs/screenshots/profile.png" width="32%" alt="Profile" />
</p>

## Build

- **JDK 17** + **Android Studio** (recommended).
- `minSdk` is **33** (Android 13) — use a compatible device or emulator.
- **Secrets:** copy `secrets.properties.example` to `secrets.properties` in
  the project **root** (the folder that contains `settings.gradle`), and set:
  - `HUGGINGFACE_TOKEN` / `GEMINI_API_KEY` — for optional LLM features
  - `MAPS_API_KEY` — for Google Maps (same key type as the Cloud Console
    *Maps SDK for Android*)
- `google-services.json` must match your Firebase project (as for any Android
  + Firestore / Realtime DB app). For a new checkout you usually download it
  from the Firebase console.

## Run

Open the project in Android Studio, select the `app` run configuration, deploy
to a device with Google Play services.

## License

The About screen in the app references the Apache 2.0 text as written in the
course. For this GitHub mirror, treat the app source as team-authored unless a
per-file header says otherwise; if you need a single SPDX license for the
repo, open an issue and we can align the team on Apache-2.0 or MIT for the
portfolio copy.

**Rotate any API keys** that have ever been committed in plain text before
this migration.
