# Demo Smoke Checklist

Quick checklist to verify the app before a demo session.

## 1. Uruchomienie
- [ ] Aplikacja uruchamia się bez crash-a
- [ ] Ekran główny wyświetla 4 karty (Sytuacje pilne, Procedury, Świadczenia, Notatki)
- [ ] Ekran główny jest przewijalny

## 2. Sytuacje pilne
- [ ] Lista scenariuszy ładuje się poprawnie (3 pozycje)
- [ ] Otwarcie szczegółów scenariusza — wyświetla tytuł, opis, checklistę
- [ ] Zaznaczenie kroków aktualizuje pasek postępu
- [ ] Kroki krytyczne wyróżnione kolorem i etykietą „⚠ Krok krytyczny"
- [ ] Sekcja „Co dalej" widoczna pod checklistą
- [ ] Wpisanie danych w pola tekstowe (miejsce, opis, uwagi)
- [ ] „Generuj notatkę" → przejście do podglądu notatki
- [ ] „Kopiuj do schowka" działa na ekranie podglądu
- [ ] Powrót do szczegółów zachowuje stan (draft)
- [ ] „Podsumowanie sprawy" → ekran z podsumowaniem statusu
- [ ] „Wyczyść zapis roboczy" resetuje formularz

## 3. Procedury
- [ ] Lista procedur z kolorowym oznaczeniem priorytetu
- [ ] Szczegóły procedury — sekcje: co zrobić, kogo powiadomić, dokumenty, podstawa prawna
- [ ] Treść czytelna, numerowane kroki

## 4. Świadczenia
- [ ] Lista świadczeń wyświetla się poprawnie
- [ ] Kliknięcie otwiera szczegóły z warunkami i dokumentami
- [ ] Sekcja „Uwagi" widoczna na dole

## 5. Notatki
- [ ] Dropdown z procedurami działa
- [ ] „Generuj szkic" tworzy treść notatki
- [ ] Wygenerowany tekst jest zaznaczalny

## 6. Ogólne UI
- [ ] Dolny padding na wszystkich ekranach (treść nie ucinana przy nawigacji gestami)
- [ ] Brak angielskich etykiet w interfejsie
- [ ] Puste stany widoczne gdy brak danych (np. usunięcie pliku JSON)
- [ ] Tekst czytelny — brak przeładowanych ekranów
