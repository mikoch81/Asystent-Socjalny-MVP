package pl.mikoch.asystentsocjalny.features.changelog

internal data class ChangelogEntry(
    val version: String,
    val date: String,
    val title: String,
    val highlights: List<String>
)

internal val CHANGELOG: List<ChangelogEntry> = listOf(
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
