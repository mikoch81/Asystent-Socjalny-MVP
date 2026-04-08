# Asystent-Socjalny-MVP

Starter repo pod demo aplikacji Android dla pracowników MOPS/GOPS.

## Cel repo
To repo jest przygotowane pod pracę z agentami w Copilocie / Claude Opus 4.6. Zawiera:

- gotowy zestaw dokumentów produktowych,
- instrukcje dla agentów,
- prompt files do implementacji,
- backlog issue do wrzucenia do GitHuba,
- prosty, działający szkielet aplikacji Android w Kotlin + Jetpack Compose,
- przykładową lokalną bazę wiedzy w `assets/knowledge`.

## Założenia
- aplikacja jest **asystentem proceduralnym**, nie autonomicznym doradcą decyzyjnym,
- MVP działa **offline-first**,
- brak zewnętrznych wywołań AI z poziomu aplikacji mobilnej,
- dane wrażliwe mają być minimalizowane.

## Stack startowy
- Kotlin
- Jetpack Compose
- Navigation Compose
- Material 3
- ViewModel
- lokalna wiedza z `assets`

## Co jest gotowe
1. Ekran główny z 4 wejściami.
2. Lista procedur pilnych.
3. Szczegół procedury z checklistą.
4. Ekran świadczeń / form pomocy.
5. Generator szkicu notatki.
6. Przykładowe dane domenowe.
7. Dokumenty i prompty dla agentów.

## Szybki start lokalnie
1. Skopiuj zawartość tego pakietu do katalogu:
   `C:\Users\Michał\Asystent MOPS`
2. Otwórz katalog w Android Studio / IntelliJ.
3. Poczekaj aż Gradle pobierze zależności.
4. Uruchom aplikację na emulatorze lub telefonie Android.

## Szybki start z Git
```powershell
cd "C:\Users\Michał"
git clone https://github.com/mikoch81/Asystent-Socjalny-MVP.git "Asystent MOPS"
```

Następnie wklej pliki startera do repo i wykonaj:

```powershell
cd "C:\Users\Michał\Asystent MOPS"
git checkout -b feature/bootstrap-starter
git add .
git commit -m "bootstrap starter for Asystent Socjalny MVP"
git push -u origin feature/bootstrap-starter
```

## Jak pracować z agentami
1. Zacznij od `docs/PRD.md`, `docs/ARCHITECTURE.md`, `docs/DOMAIN_RULES.md`.
2. Dodaj `copilot-instructions.md` do kontekstu.
3. Uruchamiaj agentów na małych taskach z katalogu `issues/`.
4. Do implementacji używaj prompt files z `.github/prompts/`.

## Pierwsze zadania polecane do odpalenia
- `issues/01-bootstrap-ui-shell.md`
- `issues/02-knowledge-model-and-loader.md`
- `issues/03-urgent-procedure-list.md`
- `issues/04-procedure-detail-and-checklist.md`

## Uwaga
Ten starter jest przygotowany tak, żeby możliwie szybko wejść w pracę agentową i pokazać demo. Pełne bezpieczeństwo produkcyjne, integracje OPS i silnik świadczeń są poza pierwszym krokiem.
