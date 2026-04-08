# Repo instructions for GitHub Copilot

## Project purpose
This repository contains an Android demo app for MOPS/GOPS workers.
The app is an offline-first procedural helper and not an autonomous legal advisor.

## Stack
- Kotlin
- Jetpack Compose
- Navigation Compose
- Material 3

## Rules
- Keep code deterministic.
- Prefer explicit models over generic maps.
- Keep business logic out of composables.
- Keep screens simple and readable in stressful situations.
- Do not add public-cloud AI calls from inside the app.
- Do not log sensitive content.

## Output expectations
When implementing a feature:
1. inspect existing modules,
2. produce a short implementation plan,
3. keep changes scoped,
4. summarize changed files,
5. mention risks or follow-up tasks.

## Domain formatting
When rendering procedures, structure content around:
- co zrobić teraz,
- kogo powiadomić,
- czego nie pominąć,
- podstawa prawna / źródło,
- czy wymagana konsultacja,
- jakie dokumenty przygotować.
