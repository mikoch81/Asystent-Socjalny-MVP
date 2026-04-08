# AGENTS.md

## Mission
Build an Android demo app for MOPS/GOPS workers.
The app is an offline-first procedural assistant and must never pretend to replace a legal advisor, supervisor or administrative decision-maker.

## Product boundaries
- Support procedural work.
- Show step-by-step actions.
- Show sources and legal references.
- Generate note drafts and follow-up checklists.
- Do not implement autonomous legal decisions.
- Do not add public-cloud AI calls from inside the app.

## Working style
- Prefer small, reviewable commits.
- Prefer deterministic code over “smart” code.
- Keep UI text concise and readable under stress.
- Optimize for demo reliability first, architecture second, polish third.

## Code style
- Kotlin only.
- Jetpack Compose for UI.
- Immutable UI state.
- Business logic outside Composables.
- Keep functions short and easy to test.

## Security and privacy
- No analytics.
- No sensitive data in logs.
- No secrets in repo.
- Default flows should work without personal data.

## Domain response format
Wherever the UI renders a procedural answer, prefer this structure:
1. Co zrobić teraz
2. Kogo powiadomić
3. Czego nie pominąć
4. Podstawa prawna / źródło
5. Czy wymagana konsultacja
6. Jakie dokumenty przygotować

## Definition of done
A task is done when:
- code builds,
- changed files are summarized,
- at least basic tests exist where meaningful,
- scope matches issue,
- no hidden “future cleanup” is required to make the feature understandable.
