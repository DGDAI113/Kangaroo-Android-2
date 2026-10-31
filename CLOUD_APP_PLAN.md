# Kangaroo Cloud — Chromebook-first implementation plan

## Product direction

The primary client is an installable Progressive Web App (PWA) optimized for ChromeOS. The existing Android scaffold remains in the repository as a secondary experiment, not the primary product UI.

The Chromebook is the control plane. Long-running Kangaroo calculations execute on AWS workers, not in the browser.

## Target architecture (AWS us-east-2)

- **Frontend:** static PWA hosted in Amazon S3 and delivered through CloudFront.
- **API:** API Gateway + Lambda for authenticated control-plane requests.
- **Job orchestration:** AWS Batch job queue and managed compute environment for long-running CPU solver jobs. Select EC2-backed compute after measuring CPU/memory needs; do not assume Lambda is suitable for the solver.
- **Run metadata:** DynamoDB tables for jobs, status, worker heartbeats, and event summaries.
- **Checkpoint/config storage:** private S3 bucket with encryption, lifecycle rules, and short-lived presigned URLs.
- **Secrets:** AWS Secrets Manager for server-side credentials. MotherDuck credentials must never be shipped to the PWA or committed to Git.
- **Analytics:** server-side integration with MotherDuck. Expose allowlisted, parameterized analytics endpoints rather than arbitrary SQL execution.
- **Deployment:** GitHub Actions using OIDC and a narrowly scoped IAM role; no long-lived AWS keys in GitHub secrets.

## Initial PWA screens

1. Dashboard — active/completed/failed jobs and worker health.
2. New Search — validate configuration and display the solver's documented limits.
3. Job Detail — status, elapsed time, worker metrics, event log, and checkpoint actions.
4. History — searchable run records and outcomes.
5. Analytics — performance summaries and approved reports.
6. Settings — API connection, display preferences, and account/session controls.

The interface must clearly distinguish queued, running, paused, completed, failed, and cancelled jobs. It must never display simulated progress as real solver progress.

## API contract (initial proposal)

- `POST /api/runs` — validate and enqueue a job; return a run ID.
- `GET /api/runs` — list the authenticated user's runs.
- `GET /api/runs/{runId}` — read status and summary.
- `POST /api/runs/{runId}/actions` — request pause/resume/cancel where supported.
- `GET /api/runs/{runId}/events` — retrieve event summaries.
- `POST /api/runs/{runId}/checkpoint-upload` — issue a constrained upload URL.
- `POST /api/analytics/report` — run one of the server-defined analytics reports.

All endpoints require authentication and ownership checks. Validate input sizes and formats, enforce per-user quotas, and record audit events.

## Solver integration gates

The upstream repository describes a SECP256K1 interval solver and documents a 125-bit interval limit. The Android JNI bridge currently in this repository is only a status stub; it is not an operational solver.

Before enabling cloud job submission:
1. Build the upstream CPU solver in a reproducible Linux environment.
2. Audit and adapt architecture-specific code and compiler flags.
3. Define a worker wrapper with structured stdout/stderr, exit codes, timeouts, and cancellation.
4. Test known small intervals with known expected answers.
5. Test checkpoint creation, recovery, malformed inputs, and worker interruption.
6. Verify resource limits and benchmark CPU/memory use.
7. Only then connect the worker to AWS Batch.

Do not claim a search is running or completed unless worker telemetry and solver output confirm it.

## Delivery phases

### Phase 1 — PWA shell
Create responsive Chromebook UI, install manifest, service worker, navigation, and local demo fixtures clearly labeled as demo data.

### Phase 2 — API and identity
Add authenticated API, per-user authorization, request validation, rate limits, and audit logging.

### Phase 3 — solver worker
Containerize and test the actual CPU solver; integrate AWS Batch only after the solver gates pass.

### Phase 4 — persistence and recovery
Add DynamoDB run state and encrypted S3 checkpoint/config storage.

### Phase 5 — MotherDuck analytics
Add server-side secret handling and a small allowlist of parameterized reports.

### Phase 6 — release hardening
Add budgets/alerts, quotas, monitoring, backups, dependency scanning, and a documented rollback procedure.

## Cost and safety controls

No AWS resources should be created until the account owner approves the proposed services, region, and spending limit. Start with a small CPU-only worker configuration, explicit concurrency limits, job timeouts, and AWS Budgets alerts. Never upload private keys, wallet secrets, or unrelated sensitive files. Store only the minimum job configuration and result metadata needed for the requested puzzle workflow.
