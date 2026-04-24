# Play Release Notes PL - v0.9.2 (Internal testing)

## Wariant do pola "Co nowego" (krotki)

[DEMO] v0.9.2
- domkniecie DI w nawigacji (stabilniejszy zapis notatki),
- gotowy pakiet Play Console: Data Safety, App Content, listing PL,
- nowy runbook internal testing i checklista demo-ready,
- polityka prywatnosci PL gotowa do podpiecia.

## Wariant rozszerzony (dla zespolu/testerow)

Wydanie porzadkuje gotowosc do publikacji demo na Google Play (Internal testing).
Najwazniejsze zmiany:
- stabilizacja przeplywu nawigacji przez domkniecie DI (Hiltowy AsystentNavHostViewModel),
- komplet dokumentow pod Play Console (Data Safety, App Content, listing PL),
- runbook publikacji AAB i rolloutu na Internal testing,
- checklista demo-ready,
- polityka prywatnosci PL gotowa do podania jako publiczny URL.

## Known issues (na teraz)

- Znany warning Kotlin (KT-73255) nie blokuje budowania aplikacji.
- W tym srodowisku pelne :app:testDebugUnitTest moze sporadycznie nie wystartowac przez problem z Gradle worker.

## Powiazane pliki

- docs/PLAY_INTERNAL_TESTING_RUNBOOK.md
- docs/DEMO_READY_PLAY_CHECKLIST.md
- docs/PLAY_DATA_SAFETY_TEMPLATE.md
- docs/PLAY_APP_CONTENT_TEMPLATE.md
- docs/PRIVACY_POLICY_PL.md
