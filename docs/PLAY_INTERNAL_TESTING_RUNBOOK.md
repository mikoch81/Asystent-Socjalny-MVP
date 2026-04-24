# Play Internal Testing Runbook

Cel: szybkie i powtarzalne wrzucenie builda demo na Google Play (track: Internal testing).

## 1. Build lokalny

Wymaganie: podpis release jest poprawnie skonfigurowany w `local.properties`.

```powershell
.\gradlew.bat bundleRelease
```

Artefakt:
- `app/build/outputs/bundle/release/app-release.aab`

## 2. Kontrola przed uploadem

- Sprawdz `versionCode` i `versionName` w `app/build.gradle.kts`
- Sprawdz changelog w aplikacji (`features/changelog/Changelog.kt`)
- Przejdz `docs/DEMO_SMOKE_CHECKLIST.md`
- Przejdz `docs/DEMO_READY_PLAY_CHECKLIST.md`

## 3. Upload do Play Console (Internal testing)

1. Wejdz do Google Play Console -> aplikacja.
2. Wejdz w `Testing` -> `Internal testing`.
3. `Create new release` / `Edit release`.
4. Wgraj `app-release.aab`.
5. Uzupelnij notatki wydania (PL, krotko i rzeczowo).
6. Zapisz i `Review release`.
7. `Start rollout to Internal testing`.

## 4. Weryfikacja po rollout

- Build widoczny na tracku Internal testing
- Tester dostal link i moze zainstalowac build
- Instalacja i uruchomienie bez crasha
- Scenariusz demo (5-10 min) przechodzi bez resetu danych

## 5. Szablon notatek wydania (Internal)

```text
[DEMO] vX.Y.Z
- najwazniejsza zmiana 1
- najwazniejsza zmiana 2
- najwazniejsza zmiana 3

Known issues:
- ...
```

## 6. Najczestsze problemy

- `versionCode already used`: zwieksz `versionCode` i zbuduj ponownie.
- Brak mozliwosci uploadu `.apk`: Internal testing wymaga `.aab`.
- Problem z podpisem: sprawdz `RELEASE_STORE_*` i alias klucza.
