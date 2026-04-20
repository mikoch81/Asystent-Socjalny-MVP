package pl.mikoch.asystentsocjalny.core.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Procedures : Screen("procedures")
    data object Benefits : Screen("benefits")
    data object BenefitDetail : Screen("benefitDetail/{benefitId}") {
        fun createRoute(benefitId: String): String = "benefitDetail/$benefitId"
    }
    data object Notes : Screen("notes")
    data object ProcedureDetail : Screen("procedureDetail/{procedureId}") {
        fun createRoute(procedureId: String): String = "procedureDetail/$procedureId"
    }
    data object UrgentList : Screen("urgentList")
    data object UrgentDetail : Screen("urgentDetail/{scenarioId}?caseId={caseId}") {
        fun createRoute(scenarioId: String, caseId: String? = null): String =
            if (caseId != null) "urgentDetail/$scenarioId?caseId=$caseId"
            else "urgentDetail/$scenarioId"
    }
    data object NotePreview : Screen("notePreview")
    data object CaseSummary : Screen("caseSummary")
    data object CaseList : Screen("caseList")
    data object ScenarioPicker : Screen("scenarioPicker")
    data object CaseDocuments : Screen("caseDocuments/{caseId}") {
        fun createRoute(caseId: String): String = "caseDocuments/$caseId"
    }
    data object Settings : Screen("settings")
    data object QuickContacts : Screen("quickContacts")
}
