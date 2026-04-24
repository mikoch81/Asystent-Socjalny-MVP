package pl.mikoch.asystentsocjalny.features.changelog

internal data class ChangelogEntry(
    val version: String,
    val date: String,
    val title: String,
    val highlights: List<String>
)

internal val CHANGELOG: List<ChangelogEntry> = listOf(
    ChangelogEntry(
        version = "0.8.0",
        date = "2026-05",
        title = "UX w terenie: dostępność, skróty, tryb przy kliencie",
        highlights = listOf(
            "Dostępność: skala tekstu (S/M/L/XL) i tryb wysokiego kontrastu",
            "App Shortcuts: pilne, niebieska karta — dziecko, niebieska karta — przemoc, nowa notatka",
            "Tryb „przy kliencie\" — pełen ekran, jeden krok na raz, duże przyciski",
            "Home: sekcje „Przypięte\" i „Ostatnio użyte\" (offline, deterministycznie)",
            "Większe pola dotyku i opisy a11y na kluczowych przyciskach"
        )
    ),
    ChangelogEntry(
        version = "0.7.0",
        date = "2026-04",
        title = "Treść: 5 nowych scenariuszy pilnych",
        highlights = listOf(
            "Eksmisja w toku — rodzina z dziećmi (8 kroków, 5 krytycznych)",
            "Zgon w rodzinie — obecność dzieci lub osób zależnych",
            "Utrata mieszkania — pożar, zalanie, katastrofa",
            "Klient sygnalizuje myśli samobójcze (numery wsparcia + tryb nagły)",
            "Osoba zależna pozostawiona bez opieki (NK + sąd opiekuńczy)"
        )
    ),
    ChangelogEntry(
        version = "0.6.1",
        date = "2026-04",
        title = "Hilt domknięte: ViewModele, test DI, dokumentacja",
        highlights = listOf(
            "Settings, Home, Notes i CaseDocuments używają dedykowanych @HiltViewModel",
            "Wszystkie stores wstrzykiwane jako @Singleton — koniec z remember { Store(ctx) }",
            "Nowy AppModuleInjectionTest (Hilt instrumentation) pilnuje grafu DI",
            "docs/TOOLCHAIN.md z macierzą wersji AGP/Kotlin/KSP/Hilt"
        )
    ),
    ChangelogEntry(
        version = "0.6.0",
        date = "2026-04",
        title = "Hilt DI: refaktor architektury",
        highlights = listOf(
            "Wstrzykiwanie zależności przez Hilt 2.59.2 + KSP 2.3.6",
            "ViewModele migrowane na @HiltViewModel z konstruktorami @Inject",
            "Nowy moduł AppModule udostępnia singletony stores i repository",
            "Application i MainActivity oznaczone @HiltAndroidApp / @AndroidEntryPoint"
        )
    ),
    ChangelogEntry(
        version = "0.5.1",
        date = "2026-04",
        title = "Polish: motyw, animacje, lepsze empty states",
        highlights = listOf(
            "Markowy motyw kolorów (granat MOPS) z pełną obsługą dark mode",
            "Płynne animacje przejść między ekranami",
            "Empty states w wyszukiwaniu z ikoną i przyciskiem „Wyczyść filtry”",
            "Lepsze copy: brak wyników pokazuje wpisaną frazę"
        )
    ),
    ChangelogEntry(
        version = "0.5.0",
        date = "2026-04",
        title = "Polish demo flow + changelog",
        highlights = listOf(
            "Nowy ekran „Co nowego” dostępny z ustawień",
            "Drobne poprawki UX i czytelności demo"
        )
    ),
    ChangelogEntry(
        version = "0.4.0",
        date = "2026-04",
        title = "Testy + porządki",
        highlights = listOf(
            "11 nowych testów jednostkowych (profil, notatki, eksport ZIP)",
            "Naprawione wszystkie deprecation warnings",
            "Build kompiluje się bez ostrzeżeń"
        )
    ),
    ChangelogEntry(
        version = "0.3.0",
        date = "2026-04",
        title = "Wyszukiwanie, UX notatek, eksport ZIP",
        highlights = listOf(
            "Pasek wyszukiwania + filtry kategorii w Procedurach (27) i Świadczeniach (43)",
            "Pole „Miejsce zdarzenia” z autouzupełnianiem ostatniej wartości",
            "Licznik znaków + wymóg min. 50 znaków przed PDF",
            "Eksport sprawy do ZIP (case.json + README + notatki + PDF)"
        )
    ),
    ChangelogEntry(
        version = "0.2.0",
        date = "2026-04",
        title = "Profil pracownika + szybkie kontakty",
        highlights = listOf(
            "Ekran ustawień profilu (lokalnie, offline)",
            "Podpisywanie notatek i PDF danymi pracownika",
            "Klikalne kontakty (telefon, mapa) bez dodatkowych uprawnień",
            "Ekran szybkich kontaktów (numery alarmowe i wsparcia)"
        )
    ),
    ChangelogEntry(
        version = "0.1.0",
        date = "2026-04",
        title = "MVP",
        highlights = listOf(
            "27 procedur w 8 kategoriach",
            "43 świadczenia z dokumentami i warunkami",
            "Sytuacje pilne — interwencja krok po kroku",
            "Generator szkiców notatek",
            "Praca w pełni offline"
        )
    )
)
