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


## Deploy the frontend on Cloudflare Pages

This repository is configured as a static site; no build step is required.

1. Sign in to Cloudflare and open **Workers & Pages → Create application → Pages → Connect to Git**.
2. Connect the GitHub account that owns `DGDAI113/Kangaroo-Android-2` and select this repository.
3. Use these build settings:
   - **Production branch:** `master`
   - **Root directory (advanced):** `web`
   - **Build command:** leave blank
   - **Build output directory:** `.`
4. Save and deploy. Cloudflare will publish the contents of `web/` and provide a `*.pages.dev` address.
5. After deployment, open the Pages URL over HTTPS and test the dashboard, navigation, and PWA service-worker registration.

Future commits to `master` will trigger deployments automatically. Preview deployments are created for supported branch or pull-request updates.

### Important deployment boundary

Cloudflare Pages hosts only the static browser frontend. It does not compile or run the long-lived C++ Kangaroo solver. Do not add AWS or MotherDuck credentials to Pages environment variables for browser use; the future API must keep secrets server-side. No cloud worker, API, authentication, or solver execution is configured by this deployment.

Cloudflare's free static hosting is appropriate for this frontend. The solver compute will need a separately approved service and may incur charges.
