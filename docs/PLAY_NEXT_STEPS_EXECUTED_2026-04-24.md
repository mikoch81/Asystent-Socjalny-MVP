# Play Next Steps - wykonanie (2026-04-24)

Ten dokument zamyka "kolejne kroki" po v0.9.2 w zakresie, ktory da sie wykonac po stronie repo/lokalnego srodowiska.

## 1. Wykonane automatycznie w tym repo

- [x] Build gate debug: `:app:assembleDebug` -> SUCCESS
- [x] Build gate release: `:app:assembleRelease` -> SUCCESS
- [x] Artefakt Play: `:app:bundleRelease` -> SUCCESS
- [x] Weryfikacja manifestu: brak `INTERNET` i brak deklaracji runtime permissions
- [x] Weryfikacja zaleznosci: brak SDK analytics/crash telemetry
- [x] Przygotowany gotowy URL polityki prywatnosci w `docs/PLAY_APP_CONTENT_TEMPLATE.md`

## 2. Wynik kontroli zgodnosci (kod vs deklaracje Play)

Deklaracje z `docs/PLAY_DATA_SAFETY_TEMPLATE.md` i `docs/PLAY_APP_CONTENT_TEMPLATE.md` sa spojne z obecnym kodem aplikacji:

- App collects data: NO (brak transmisji danych do serwera)
- App shares data: NO
- Ads: NO
- Login required: NO

## 3. Kroki manualne do wykonania w Play Console

Tych krokow nie da sie wykonac z poziomu repo:

1. Wejdz w `App content` i wklej URL polityki prywatnosci.
2. Uzupelnij/zweryfikuj sekcje `Data safety` zgodnie z template.
3. Wgraj `app-release.aab` na `Testing -> Internal testing`.
4. Wpisz notatki wydania i uruchom rollout.
5. Potwierdz instalacje przez testera z linku internal testing.

## 4. Artefakty i referencje

- AAB: `app/build/outputs/bundle/release/app-release.aab`
- Runbook: `docs/PLAY_INTERNAL_TESTING_RUNBOOK.md`
- Data Safety: `docs/PLAY_DATA_SAFETY_TEMPLATE.md`
- App Content: `docs/PLAY_APP_CONTENT_TEMPLATE.md`
- Privacy policy: `docs/PRIVACY_POLICY_PL.md`
