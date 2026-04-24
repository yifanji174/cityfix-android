# CityFix

<!-- 顶部为界面预览图；可替换 <code>docs/screenshots/*.png</code> 为真机截图后重新 <code>git push</code> -->
<p align="center">
  <b>UI preview</b> — <a href="docs/screenshots/README.md">file names &amp; how to replace</a>
  <br/><br/>
  <img src="docs/screenshots/Screenshot_reportlist_citizen.png" width="30%" alt="Main list (citizen)" />
  &nbsp;
  <img src="docs/screenshots/Screenshot_newReport.png" width="30%" alt="New report" />
  &nbsp;
  <img src="docs/screenshots/Screenshot_search.png" width="30%" alt="Search" />
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

> Source for images: GitLab
> [`Images/page example`](https://gitlab.comp.anu.edu.au/u7951193/gp-25s1/-/tree/f216a469a8951423bc324be4c468883eb34e3096/Images/page%20example)
> or `items/media/screenshots/` in the monorepo — filenames must match
> [`docs/screenshots/README.md`](docs/screenshots/README.md).

### Login and main list (citizen / worker)

<p align="center">
  <img src="docs/screenshots/Screenshot_login.png" width="32%" alt="Login" />
  <img src="docs/screenshots/Screenshot_reportlist_citizen.png" width="32%" alt="Report list — citizen" />
  <img src="docs/screenshots/Screenshot_reportlist_worker.png" width="32%" alt="Report list — worker" />
</p>

### Search, filters, and new report

<p align="center">
  <img src="docs/screenshots/Screenshot_search.png" width="32%" alt="Search" />
  <img src="docs/screenshots/Screenshot_search_filter.png" width="32%" alt="Filters" />
  <img src="docs/screenshots/Screenshot_newReport.png" width="32%" alt="New report" />
</p>

### Report detail, profile, notifications

<p align="center">
  <img src="docs/screenshots/Screenshot_reportdetail_citizen.png" width="32%" alt="Report detail — citizen" />
  <img src="docs/screenshots/Screenshot_reportdetail_worker.png" width="32%" alt="Report detail — worker" />
  <img src="docs/screenshots/Screenshot_profile_citizen.png" width="32%" alt="Profile — citizen" />
</p>

<p align="center">
  <img src="docs/screenshots/Screenshot_profile_worker.png" width="32%" alt="Profile — worker" />
  <img src="docs/screenshots/Screenshot_notification.png" width="32%" alt="Notifications" />
  <img src="docs/screenshots/Screenshot_search_filter2.png" width="32%" alt="Filters (variant)" />
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
