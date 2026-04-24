# Asystent-Socjalny-MVP

## Product name

Mobile Social Shield

Demo aplikacji Android dla pracownikow MOPS/GOPS.
Aplikacja jest asystentem proceduralnym offline-first i nie podejmuje decyzji prawnych za uzytkownika.

## Status projektu

Repo nie jest juz "starterem". Aktualny MVP zawiera:

- baze wiedzy zintegrowana pod MOPS Zgierz,
- procedury pogrupowane w 8 kategoriach,
- katalog swiadczen i form pomocy z mapowaniem do procedur,
- generator szkicu notatki,
- lokalny parser JSON i modele domenowe,
- oznaczenia statusu prawnego tresci (wymaga walidacji).

## Zalozenia produktowe

- aplikacja wspiera prace proceduralna, a nie autonomiczne decyzje,
- dziala offline-first,
- nie wykonuje public-cloud AI calls z poziomu aplikacji mobilnej,
- nie zapisuje danych wrazliwych w logach,
- dane w repo to dane demonstracyjne.

## Stack

- Kotlin
- Jetpack Compose
- Navigation Compose
- Material 3
- lokalna wiedza z `app/src/main/assets/knowledge`

## Zakres MVP

1. Ekran glowny i nawigacja po modulach.
2. Lista i szczegol procedur.
3. Lista i szczegol swiadczen/form pomocy.
4. Powiazania procedura <-> swiadczenie.
5. Sekcje kontaktowe MOPS w procedurach.
6. Generator szkicu notatki.
7. Oznaczenia statusu prawnego tresci:
   - `legalValidationStatus`
   - `legalUpdatedAt`
   - `legalReviewDueAt`

## Dane wiedzy i walidacja prawna

Pliki danych:

- `app/src/main/assets/knowledge/procedures.json`
- `app/src/main/assets/knowledge/benefits.json`

Kazdy rekord procedury i swiadczenia zawiera status prawny. Dodatkowo oba zbiory maja sekcje `metadata` z informacja o jurysdykcji i dacie snapshotu.

Wazne: tresci prawne w MVP sa oznaczone jako "Wymaga walidacji" i musza byc zweryfikowane przez kierownika MOPS oraz eksperta prawnego przed uzyciem operacyjnym.

## Uruchomienie lokalne

1. Otworz repo w Android Studio.
2. Poczekaj na synchronizacje Gradle.
3. Uruchom aplikacje na emulatorze lub urzadzeniu Android.

Przydatne komendy:

```powershell
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:assembleDebug
```

## Testy

Testy parsera sa w `app/src/test/java/pl/mikoch/asystentsocjalny/core/data`.

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Jesli testy lokalnie nie startuja z powodow srodowiskowych Gradle worker, traktuj `compileDebugKotlin` i `assembleDebug` jako minimalny gate techniczny dla zmian.

## Dokumentacja

- `docs/PRD.md`
- `docs/ARCHITECTURE.md`
- `docs/DOMAIN_RULES.md`
- `docs/DEMO_SMOKE_CHECKLIST.md`
- `docs/DEMO_READY_PLAY_CHECKLIST.md`
- `docs/PLAY_INTERNAL_TESTING_RUNBOOK.md`

## Bezpieczenstwo i granice odpowiedzialnosci

- brak analytics,
- brak sekretow w repo,
- brak danych osobowych w sample data,
- aplikacja nie zastępuje doradcy prawnego, przelozonego ani decyzji administracyjnej.
