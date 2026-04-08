package pl.mikoch.asystentsocjalny.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.features.benefits.BenefitsScreen
import pl.mikoch.asystentsocjalny.features.home.HomeScreen
import pl.mikoch.asystentsocjalny.features.notes.NotesScreen
import pl.mikoch.asystentsocjalny.features.procedures.ProcedureDetailScreen
import pl.mikoch.asystentsocjalny.features.procedures.ProceduresScreen
import pl.mikoch.asystentsocjalny.features.urgent.NotePreviewScreen
import pl.mikoch.asystentsocjalny.features.urgent.UrgentDetailScreen
import pl.mikoch.asystentsocjalny.features.urgent.UrgentListScreen
import pl.mikoch.asystentsocjalny.features.urgent.UrgentViewModel
import pl.mikoch.asystentsocjalny.features.urgent.model.toUi

@Composable
fun AsystentNavHost() {
    val navController = rememberNavController()
    val repository = KnowledgeRepository(LocalContext.current)
    val procedures = repository.loadProcedures()
    val benefits = repository.loadBenefits()
    val urgentViewModel: UrgentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onOpenProcedures = { navController.navigate(Screen.Procedures.route) },
                onOpenBenefits = { navController.navigate(Screen.Benefits.route) },
                onOpenNotes = { navController.navigate(Screen.Notes.route) },
                onOpenUrgent = { navController.navigate(Screen.UrgentList.route) }
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
            BenefitsScreen(benefits = benefits)
        }
        composable(Screen.Notes.route) {
            NotesScreen(procedures = procedures)
        }
        composable(
            route = Screen.ProcedureDetail.route,
            arguments = listOf(navArgument("procedureId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("procedureId").orEmpty()
            val procedure = procedures.firstOrNull { it.id == id }
            if (procedure != null) {
                ProcedureDetailScreen(procedure = procedure)
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
            arguments = listOf(navArgument("scenarioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("scenarioId").orEmpty()
            val scenario = urgentViewModel.scenarioById(id)
            if (scenario != null) {
                UrgentDetailScreen(
                    scenario = scenario,
                    viewModel = urgentViewModel,
                    onNavigateToPreview = {
                        navController.navigate(Screen.NotePreview.route)
                    }
                )
            }
        }
        composable(Screen.NotePreview.route) {
            NotePreviewScreen(
                noteText = urgentViewModel.generatedNoteText.value,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
