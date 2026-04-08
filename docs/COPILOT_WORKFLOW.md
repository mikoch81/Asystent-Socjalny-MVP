# COPILOT_WORKFLOW

## Zalecany workflow
1. Otwórz repo w VS Code / Android Studio.
2. Dodaj do kontekstu:
   - `README.md`
   - `docs/PRD.md`
   - `docs/ARCHITECTURE.md`
   - `docs/DOMAIN_RULES.md`
   - `.github/copilot-instructions.md`
3. Uruchom prompt `plan-feature.prompt.md`.
4. Daj agentowi mały task z katalogu `issues/`.
5. Po każdej zmianie rób review diffa i dopiero wtedy merge.

## Pierwszy prompt do Copilota
`Przeanalizuj repo, przeczytaj PRD, ARCHITECTURE i DOMAIN_RULES. Przygotuj plan rozwoju demo na 3 iteracje po 2-3 taski każda, bez zmiany obecnego scope MVP.`

## Drugi prompt
`Zaimplementuj issue 07-search-procedures.md. Najpierw przedstaw plan, potem wykonaj zmiany, a na końcu pokaż listę zmodyfikowanych plików i ryzyka.`

## Trzeci prompt
`Przejrzyj projekt pod kątem gotowości demo dla interesariusza z urzędu. Wskaż brakujące elementy UX, ryzyka domenowe i 5 szybkich poprawek o najwyższym wpływie.`
