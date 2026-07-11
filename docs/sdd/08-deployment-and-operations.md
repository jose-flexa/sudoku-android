# 08 - Deployment and Operations

## Environments
| Environment | Purpose | Deployment Method |
| --- | --- | --- |
| Dev | Day-to-day feature development | Local debug build from Android Studio/Gradle |
| Test | QA and regression verification | Signed internal build distributed via Firebase App Distribution |
| Prod | Public release | Google Play internal -> closed -> production tracks |

## CI/CD Outline
1. Build: Gradle assemble for debug/release variants.
2. Test: unit + integration + UI test jobs.
3. Package: sign AAB/APK with release keystore in secure CI secrets.
4. Deploy: publish to Play Console track.

## Rollback Plan
- Trigger conditions:
	- Crash spike after release.
	- Critical gameplay regression (cannot start or complete game).
- Rollback steps:
	1. Halt staged rollout.
	2. Promote previous stable version in Play Console.
	3. Create hotfix branch and patch issue.

## Monitoring and Alerting
- Key metrics:
	- Crash-free users/sessions.
	- ANR rate.
	- Startup time p95.
	- Session length and completion rate.
- Log aggregation:
	- Crashlytics for crash and non-fatal analytics.
- Alert ownership:
	- Tech Lead + on-call engineer during release window.

## Runbooks
- Incident response:
	- Reproduce issue, collect logs, classify severity, patch or rollback.
- Common operational tasks:
	- Rotate signing keys/cert monitoring.
	- Update dependencies monthly.
	- Validate targetSdk upgrade readiness.
