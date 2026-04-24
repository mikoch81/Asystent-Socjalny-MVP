# Play Data Safety (Wypelnione - stan obecny)

Sciezka w Play Console: App content -> Data safety.

## 1. Zalecione odpowiedzi dla aktualnego builda

- App collects data: NO
- App shares data with third parties: NO

Uzasadnienie techniczne (aktualny stan):
- brak uprawnienia `INTERNET` w `AndroidManifest.xml`,
- brak SDK analytics/crash reporting i brak backend sync,
- dane dzialaja lokalnie (offline-first, DataStore/pliki lokalne),
- brak public-cloud AI calls.

## 2. Kategorie danych

W obecnym buildzie: nie zaznaczaj zadnej kategorii jako collected/shared,
bo dane nie sa wysylane poza urzadzenie przez aplikacje.

## 3. Security practices

- Data is encrypted in transit: Not applicable (brak transmisji danych)
- You provide a way to request data deletion: Not applicable (brak danych po stronie serwera)
- Independently verified security review: opcjonalne

## 4. Kiedy zaktualizowac deklaracje

Zmien odpowiedzi natychmiast, jesli dodacie:
- jakikolwiek backend/API,
- analytics/crash telemetry,
- zdalna synchronizacje profilu/notatek,
- logowanie kontem.

## 5. Final check

- [ ] Odpowiedzi sa zgodne z realnym zachowaniem aplikacji
- [ ] Odpowiedzi sa zgodne z `docs/PRIVACY_POLICY_PL.md`
- [ ] Odpowiedzi sa zgodne z opisem aplikacji i listingiem
