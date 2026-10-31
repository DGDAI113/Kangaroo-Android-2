# Kangaroo Cloud Web App

Chromebook-first Progressive Web App frontend.

## Current status

This is a frontend shell only. It includes responsive navigation, dashboard, configuration form, job/history views, analytics placeholder, settings placeholder, and PWA manifest/service worker.

It does **not** yet authenticate users, submit cloud jobs, run the C++ solver, or connect to AWS/MotherDuck. UI placeholders intentionally show offline/not-connected states; no fake job progress is generated.

## Local preview

Serve this directory with any static HTTP server, for example:

```bash
python3 -m http.server 8080 --directory web
```

Open `http://localhost:8080`.

## Next

Implement authenticated API integration, then AWS worker orchestration only after the upstream CPU solver is built and validated with known-answer tests.
