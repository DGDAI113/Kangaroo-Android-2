# Kangaroo Cloud

**Chromebook-first, web-only interface** for controlling cloud-hosted Pollard kangaroo search jobs for SECP256K1 interval ECDLP research.

The primary product is an installable Progressive Web App (PWA) in `web/`. There is no Android application target; the previous Android scaffold has been retired.

## Frontend

See [web/README.md](web/README.md). The PWA currently provides a responsive dashboard, search configuration screen, job monitor, history, analytics placeholder, and settings placeholder.

**Current limitation:** this is a frontend shell, not a working solver service. Cloud authentication, API, worker orchestration, and MotherDuck reporting are not connected. The interface intentionally does not fabricate job status or progress.

## Solver source and limits

This repository contains the original C++ solver source. Its README documents a maximum 125-bit interval width. The CPU engine must be built, ported as needed, and validated with known-answer tests before any cloud job is allowed to run. See [CLOUD_APP_PLAN.md](CLOUD_APP_PLAN.md).

## Architecture

- Chromebook: installable PWA
- AWS us-east-2: API, authentication, job queue/orchestration, CPU workers, run metadata, private checkpoint storage
- MotherDuck: server-side analytics only; credentials never go in the browser
- GitHub Actions: OIDC deployment role with least privilege; no long-lived AWS keys

No paid AWS resources are created by this repository. Deployment requires explicit budget and resource approval.

## Development preview

```bash
python3 -m http.server 8080 --directory web
```

Then open `http://localhost:8080`.
