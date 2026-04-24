package pl.mikoch.asystentsocjalny.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.core.data.RecentItemsStore
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.DocumentType
import pl.mikoch.asystentsocjalny.core.model.RecentItem
import pl.mikoch.asystentsocjalny.core.model.RecentItemKind
import pl.mikoch.asystentsocjalny.features.benefits.BenefitDetailScreen
import pl.mikoch.asystentsocjalny.features.benefits.BenefitsScreen
import pl.mikoch.asystentsocjalny.features.cases.CaseDocumentsScreen
import pl.mikoch.asystentsocjalny.features.cases.CaseListScreen
import pl.mikoch.asystentsocjalny.features.cases.CaseListViewModel
import pl.mikoch.asystentsocjalny.features.common.EmptyStateMessage
import pl.mikoch.asystentsocjalny.features.home.HomeScreen
import pl.mikoch.asystentsocjalny.features.notes.NotesScreen
import pl.mikoch.asystentsocjalny.features.procedures.ProcedureDetailScreen
import pl.mikoch.asystentsocjalny.features.procedures.ProceduresScreen
import pl.mikoch.asystentsocjalny.features.changelog.ChangelogScreen
import pl.mikoch.asystentsocjalny.features.contacts.QuickContactsScreen
import pl.mikoch.asystentsocjalny.features.settings.SettingsScreen
import pl.mikoch.asystentsocjalny.features.urgent.CaseSummaryScreen
import pl.mikoch.asystentsocjalny.features.urgent.NotePreviewScreen
import pl.mikoch.asystentsocjalny.features.urgent.UrgentDetailScreen
import pl.mikoch.asystentsocjalny.features.urgent.UrgentFocusScreen
import pl.mikoch.asystentsocjalny.features.urgent.UrgentListScreen
import pl.mikoch.asystentsocjalny.features.urgent.UrgentViewModel
import pl.mikoch.asystentsocjalny.features.urgent.model.toUi
import java.util.UUID

@Composable
fun AsystentNavHost() {
    val navController = rememberNavController()
    val repository = KnowledgeRepository(LocalContext.current)
    val procedures = repository.loadProcedures()
    val benefits = repository.loadBenefits()
    val urgentViewModel: UrgentViewModel = hiltViewModel()
    val caseListViewModel: CaseListViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext
    val recentItemsStore = remember(appContext) { RecentItemsStore(appContext) }

    // Skróty z launchera (App Shortcuts) — pojedyncze zdarzenie konsumowane raz.
    LaunchedEffect(Unit) {
        ShortcutRouter.events.collect { destination ->
            when (destination) {
                ShortcutDestination.UrgentList ->
                    navController.navigate(Screen.UrgentList.route) {
                        launchSingleTop = true
                    }
                is ShortcutDestination.UrgentScenario ->
                    navController.navigate(Screen.UrgentDetail.createRoute(destination.scenarioId)) {
                        launchSingleTop = true
                    }
                ShortcutDestination.NewNote ->
                    navController.navigate(Screen.Notes.route) {
                        launchSingleTop = true
                    }
                ShortcutDestination.Contacts ->
                    navController.navigate(Screen.QuickContacts.route) {
                        launchSingleTop = true
                    }
            }
            ShortcutRouter.reset()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 6 },
                animationSpec = tween(220)
            ) + fadeIn(animationSpec = tween(220))
        },
        exitTransition = { fadeOut(animationSpec = tween(180)) },
        popEnterTransition = { fadeIn(animationSpec = tween(180)) },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it / 6 },
                animationSpec = tween(220)
            ) + fadeOut(animationSpec = tween(220))
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onOpenProcedures = { navController.navigate(Screen.Procedures.route) },
                onOpenBenefits = { navController.navigate(Screen.Benefits.route) },
                onOpenNotes = { navController.navigate(Screen.Notes.route) },
                onOpenUrgent = { navController.navigate(Screen.UrgentList.route) },
                onOpenCases = { navController.navigate(Screen.CaseList.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenContacts = { navController.navigate(Screen.QuickContacts.route) },
                onOpenRecent = { item ->
                    val route = when (item.kind) {
                        pl.mikoch.asystentsocjalny.core.model.RecentItemKind.PROCEDURE ->
                            Screen.ProcedureDetail.createRoute(item.id)
                        pl.mikoch.asystentsocjalny.core.model.RecentItemKind.BENEFIT ->
                            Screen.BenefitDetail.createRoute(item.id)
                        pl.mikoch.asystentsocjalny.core.model.RecentItemKind.URGENT_SCENARIO ->
                            Screen.UrgentDetail.createRoute(item.id)
                    }
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.Procedures.route) {
            ProceduresScreen(
                procedures = procedures,
                onOpenDetail = { id ->
                    navController.navigate(Screen.ProcedureDetail.createRoute(id))
                }
            )
        }
        composable(Screen.Benefits.route) {
            BenefitsScreen(
                benefits = benefits,
                onOpenDetail = { id ->
                    navController.navigate(Screen.BenefitDetail.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.BenefitDetail.route,
            arguments = listOf(navArgument("benefitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("benefitId").orEmpty()
            val benefit = benefits.firstOrNull { it.id == id }
            if (benefit != null) {
                LaunchedEffect(benefit.id) {
                    recentItemsStore.recordOpen(
                        RecentItem(
                            kind = RecentItemKind.BENEFIT,
                            id = benefit.id,
                            title = benefit.name,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                BenefitDetailScreen(benefit = benefit)
            } else {
                EmptyStateMessage(
                    title = "Nie znaleziono świadczenia",
                    subtitle = "Dane lokalne mogą być niekompletne."
                )
            }
        }
        composable(Screen.Notes.route) {
            NotesScreen(
                procedures = procedures,
                onCreateCase = { procedureId, procedureTitle, noteText ->
                    val caseId = caseListViewModel.createCase(procedureId, procedureTitle)
                    val documentStore = CaseDocumentStore(appContext)
                    val doc = CaseDocument(
                        documentId = UUID.randomUUID().toString(),
                        caseId = caseId,
                        type = DocumentType.NOTE_DRAFT,
                        title = "Notatka – $procedureTitle",
                        fileName = "notatka_${procedureId}.txt",
                        textContent = noteText,
                        filePath = "",
                        createdAt = System.currentTimeMillis()
                    )
                    coroutineScope.launch { documentStore.save(doc) }
                    navController.navigate(Screen.CaseList.route)
                }
            )
        }
        composable(
            route = Screen.ProcedureDetail.route,
            arguments = listOf(navArgument("procedureId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("procedureId").orEmpty()
            val procedure = procedures.firstOrNull { it.id == id }
            if (procedure != null) {
                LaunchedEffect(procedure.id) {
                    recentItemsStore.recordOpen(
                        RecentItem(
                            kind = RecentItemKind.PROCEDURE,
                            id = procedure.id,
                            title = procedure.title,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                ProcedureDetailScreen(procedure = procedure)
            } else {
                EmptyStateMessage(
                    title = "Nie znaleziono procedury",
                    subtitle = "Dane lokalne mogą być niekompletne."
                )
            }
        }
        composable(Screen.UrgentList.route) {
            UrgentListScreen(
                scenarios = urgentViewModel.scenarios,
                onOpenDetail = { id ->
                    navController.navigate(Screen.UrgentDetail.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.UrgentDetail.route,
            arguments = listOf(
                navArgument("scenarioId") { type = NavType.StringType },
                navArgument("caseId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("scenarioId").orEmpty()
            val caseId = backStackEntry.arguments?.getString("caseId")
            val scenario = urgentViewModel.scenarioById(id)
            if (scenario != null) {
                LaunchedEffect(scenario.id) {
                    recentItemsStore.recordOpen(
                        RecentItem(
                            kind = RecentItemKind.URGENT_SCENARIO,
                            id = scenario.id,
                            title = scenario.title,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                UrgentDetailScreen(
                    scenario = scenario,
                    viewModel = urgentViewModel,
                    onNavigateToPreview = {
                        navController.navigate(Screen.NotePreview.route)
                    },
                    onNavigateToSummary = {
                        navController.navigate(Screen.CaseSummary.route)
                    },
                    onNavigateToFocus = {
                        navController.navigate(Screen.UrgentFocus.createRoute(id))
                    },
                    caseId = caseId
                )
            } else {
                EmptyStateMessage(
                    title = "Nie znaleziono scenariusza",
                    subtitle = "Dane lokalne mogą być niekompletne."
                )
            }
        }
        composable(Screen.NotePreview.route) {
            NotePreviewScreen(
                viewModel = urgentViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CaseSummary.route) {
            val scenario = urgentViewModel.currentScenario
            if (scenario != null) {
                CaseSummaryScreen(
                    scenario = scenario,
                    viewModel = urgentViewModel,
                    onBack = { navController.popBackStack() },
                    onOpenDocuments = {
                        val caseId = urgentViewModel.activeCaseId
                        if (caseId != null) {
                            navController.navigate(Screen.CaseDocuments.createRoute(caseId))
                        }
                    }
                )
            } else {
                EmptyStateMessage(
                    title = "Brak aktywnej sprawy",
                    subtitle = "Wróć i wybierz scenariusz."
                )
            }
        }
        composable(Screen.CaseList.route) {
            CaseListScreen(
                viewModel = caseListViewModel,
                onOpenCase = { caseId, scenarioId ->
                    navController.navigate(Screen.UrgentDetail.createRoute(scenarioId, caseId))
                },
                onNewCase = {
                    navController.navigate(Screen.ScenarioPicker.route)
                },
                onOpenDocuments = { caseId ->
                    navController.navigate(Screen.CaseDocuments.createRoute(caseId))
                }
            )
        }
        composable(Screen.ScenarioPicker.route) {
            UrgentListScreen(
                scenarios = urgentViewModel.scenarios,
                onOpenDetail = { scenarioId ->
                    val scenario = urgentViewModel.scenarioById(scenarioId)
                    if (scenario != null) {
                        val caseId = caseListViewModel.createCase(scenarioId, scenario.title)
                        navController.navigate(Screen.UrgentDetail.createRoute(scenarioId, caseId)) {
                            popUpTo(Screen.CaseList.route)
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.CaseDocuments.route,
            arguments = listOf(navArgument("caseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId").orEmpty()
            CaseDocumentsScreen(
                caseId = caseId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onSaved = {},
                onOpenChangelog = { navController.navigate(Screen.Changelog.route) }
            )
        }
        composable(Screen.QuickContacts.route) {
            QuickContactsScreen()
        }
        composable(Screen.Changelog.route) {
            ChangelogScreen()
        }
        composable(
            route = Screen.UrgentFocus.route,
            arguments = listOf(navArgument("scenarioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("scenarioId").orEmpty()
            val scenario = urgentViewModel.scenarioById(id)
            if (scenario != null) {
                UrgentFocusScreen(
                    scenario = scenario,
                    viewModel = urgentViewModel,
                    onExit = { navController.popBackStack() }
                )
            } else {
                EmptyStateMessage(
                    title = "Nie znaleziono scenariusza",
                    subtitle = "Dane lokalne mogą być niekompletne."
                )
            }
        }
    }
}
