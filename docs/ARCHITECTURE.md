# ARCHITECTURE

## 1. Założenie
Demo ma być proste, czytelne i stabilne. Priorytetem jest pokazanie workflow, a nie pełnej produkcyjnej złożoności.

## 2. Warstwy
### UI
- Jetpack Compose
- Navigation Compose
- Ekrany:
  - Home
  - Procedures
  - Procedure Detail
  - Benefits
  - Notes

### Domain
- modele procedur,
- modele benefitów,
- generator szkicu notatki,
- logika mapowania danych z plików assets.

### Data
- lokalne pliki JSON w `app/src/main/assets/knowledge`
- loader czytający dane z assets

## 3. Dlaczego bez Room/Hilt w pierwszym commicie
Pierwszy bootstrap ma:
- szybko się uruchomić,
- być prosty dla agentów,
- mieć minimalne ryzyko konfiguracji.

Room i Hilt można dołożyć jako osobne taski po uruchomieniu szkieletu.

## 4. Kolejne kroki po bootstrapie
1. Dodać ViewModel dla ekranów.
2. Dodać Room dla cache / historii.
3. Dodać Hilt dla DI.
4. Dodać wyszukiwarkę pełnotekstową.
5. Dodać wersjonowanie bazy wiedzy.
