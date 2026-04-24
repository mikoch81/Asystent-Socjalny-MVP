# Demo Ready Play Checklist

Checklist prowadzi od "dziala lokalnie" do "mozna pokazac i wrzucic na Google Play (Internal testing)".

## 1. Jakosc i stabilnosc
- [ ] `assembleDebug` i `assembleRelease` przechodza bez bledow
- [ ] Brak nowych warningow krytycznych po zmianie (poza znanymi, udokumentowanymi)
- [ ] Smoke test z `docs/DEMO_SMOKE_CHECKLIST.md` przechodzi w 100%
- [ ] Sciezka awaryjna dziala offline (procedury, swiadczenia, pilne)
- [ ] Brak recznego tworzenia store/repository poza DI (Hilt)

## 2. Dane i walidacja tresci
- [ ] `legalValidationStatus`, `legalUpdatedAt`, `legalReviewDueAt` uzupelnione w rekordach demo
- [ ] Pole `validatedBy` uzupelniane tylko po realnej walidacji przez osobe odpowiedzialna
- [ ] Widoczna informacja o wersji bazy wiedzy i zrodle (bundled/external)
- [ ] Sprawdzona instrukcja OTA dla podmiany plikow knowledge
- [ ] Brak danych osobowych i wrazliwych w plikach demo

## 3. Zgodnosc z granicami produktu
- [ ] Komunikaty UI nie sugeruja autonomicznych decyzji prawnych
- [ ] Widoczne eskalacje w krytycznych flow (kogo powiadomic / konsultacja)
- [ ] Jezyk interfejsu jest jasny, polski i terenowy
- [ ] Brak public-cloud AI calls z aplikacji mobilnej

## 4. Techniczna gotowosc do Play
- [ ] Podpis release skonfigurowany i zweryfikowany
- [ ] Wygenerowany `app-release.aab`
- [ ] `versionCode` zwiekszony wzgledem ostatniego wydania
- [ ] Ikona aplikacji i nazwa produkcyjna spójne
- [ ] Przetestowane na min. 2 wersjach Android + 1 fizycznym urzadzeniu

## 5. Play Console (internal testing)
- [ ] Uzupelnione "App content" (wymagane deklaracje)
- [ ] Uzupelnione "Data safety" zgodnie z faktycznym przetwarzaniem danych
- [ ] Podany URL do polityki prywatnosci
- [ ] Listing sklepu uzupelniony (opis, screenshoty, ikonka, feature graphic)
- [ ] Build wrzucony na Internal testing i zainstalowany przez testera

## 6. Gate demo-ready (przed pokazem)
- [ ] 0 blockerow P0/P1
- [ ] Scenariusz demo (5-10 min) przechodzi bez restartu aplikacji
- [ ] Changelog i release notes gotowe i zrozumiale dla odbiorcy nietechnicznego
- [ ] Wlasciciel merytoryczny akceptuje tresci demo
