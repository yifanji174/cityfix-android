# Screenshots for README (GitHub)

The committed **PNG** files in this folder are **auto-generated placeholders** (dark background + label) so the README renders immediately. **Replace them** with real device screenshots for a portfolio-quality look: same filenames, then `git add`, `git commit`, `git push`.

## Where to get the real images

1. **Group GitLab:** `Images/page example/`  
2. **Monorepo:** `items/media/screenshots/` (names in `items/report.md`)

## Filenames (must match)

| File | Screen |
|------|--------|
| `Screenshot_login.png` | Login (citizen / worker) |
| `Screenshot_reportlist_citizen.png` | Main list — citizen |
| `Screenshot_reportlist_worker.png` | Main list — worker |
| `Screenshot_search.png` | Search |
| `Screenshot_search_filter.png` | Filters (1) |
| `Screenshot_search_filter2.png` | Filters (2) |
| `Screenshot_newReport.png` | New report |
| `Screenshot_reportdetail_citizen.png` | Report detail — citizen |
| `Screenshot_reportdetail_worker.png` | Report detail — worker |
| `Screenshot_profile_citizen.png` | Profile — citizen |
| `Screenshot_profile_worker.png` | Profile — worker |
| `Screenshot_notification.png` | Notifications |

After replacing:

```bash
git add docs/screenshots/*.png
git commit -m "Replace README screenshots with real app captures"
git push origin main
```
