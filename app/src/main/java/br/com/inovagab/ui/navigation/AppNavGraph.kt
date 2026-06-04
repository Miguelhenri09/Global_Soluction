package br.com.inovagab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.inovagab.domain.model.Project
import br.com.inovagab.ui.auth.AuthViewModel
import br.com.inovagab.ui.auth.LoginScreen
import br.com.inovagab.ui.leader.*
import br.com.inovagab.ui.manager.*
import br.com.inovagab.ui.operator.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Determine start destination
    val startDestination = if (authState.isLoggedIn && authState.user != null) {
        authViewModel.getHomeRoute(authState.user!!.role)
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── AUTH ────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authState,
                onLogin = { email, pass -> authViewModel.login(email, pass) }
            )
            // Observe login success and navigate
            if (authState.isLoggedIn && authState.user != null) {
                val route = authViewModel.getHomeRoute(authState.user!!.role)
                navController.navigate(route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }

        // ── OPERATOR ────────────────────────────────────────────────
        composable(Screen.OperatorHome.route) {
            val vm: OperatorViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            OperatorHomeScreen(
                uiState = state,
                onNavigateIdeas = { navController.navigate(Screen.OperatorIdeas.route) },
                onNavigateNewIdea = { navController.navigate(Screen.OperatorNewIdea.route) },
                onNavigateGuidelines = { navController.navigate(Screen.OperatorGuidelines.route) },
                onLogout = { authViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) } }
            )
        }

        composable(Screen.OperatorIdeas.route) {
            val vm: OperatorViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            OperatorIdeasScreen(
                ideas = state.ideas,
                onBack = { navController.popBackStack() },
                onNewIdea = { navController.navigate(Screen.OperatorNewIdea.route) }
            )
        }

        composable(Screen.OperatorNewIdea.route) {
            val vm: OperatorViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            OperatorNewIdeaScreen(
                isLoading = state.isLoading,
                message = state.message,
                onSubmit = { title, desc, cat -> vm.submitIdea(title, desc, cat) },
                onBack = { navController.popBackStack() },
                onMessageDismiss = { vm.clearMessage() }
            )
        }

        composable(Screen.OperatorGuidelines.route) {
            val vm: OperatorViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            OperatorGuidelinesScreen(
                guidelines = state.guidelines,
                onBack = { navController.popBackStack() }
            )
        }

        // ── MANAGER ─────────────────────────────────────────────────
        composable(Screen.ManagerHome.route) {
            val vm: ManagerViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            ManagerHomeScreen(
                uiState = state,
                onNavigateIdeas = { navController.navigate(Screen.ManagerIdeas.route) },
                onNavigateProjects = { navController.navigate(Screen.ManagerProjects.route) },
                onNavigateGuidelines = { navController.navigate(Screen.ManagerGuidelines.route) },
                onLogout = { authViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) } }
            )
        }

        composable(Screen.ManagerIdeas.route) {
            val vm: ManagerViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            ManagerIdeasScreen(
                uiState = state,
                onUpdateIdea = { id, status, comment, priority -> vm.updateIdeaStatus(id, status, comment, priority) },
                onCreateProject = { ideaId ->
                    navController.navigate(Screen.ManagerNewProject.createRoute(ideaId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ManagerProjects.route) {
            val vm: ManagerViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            ManagerProjectsScreen(
                projects = state.projects,
                onNewProject = { navController.navigate(Screen.ManagerNewProject.createRoute()) },
                onEditProject = { id -> navController.navigate(Screen.ManagerEditProject.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ManagerNewProject.route) { backStack ->
            val ideaId = backStack.arguments?.getString("ideaId") ?: ""
            val vm: ManagerViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            val originIdea = if (ideaId.isNotBlank()) vm.getIdeaById(ideaId) else null
            val baseProject = if (originIdea != null) {
                Project(title = originIdea.title, description = originIdea.description, originIdeaId = ideaId)
            } else null
            ManagerProjectFormScreen(
                project = baseProject,
                isLoading = state.isLoading,
                message = state.message,
                onSave = { project -> vm.saveProject(project) },
                onBack = { navController.popBackStack() },
                onMessageDismiss = { vm.clearMessage() }
            )
        }

        composable(Screen.ManagerEditProject.route) { backStack ->
            val projectId = backStack.arguments?.getString("projectId") ?: ""
            val vm: ManagerViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            val project = vm.getProjectById(projectId)
            ManagerProjectFormScreen(
                project = project,
                isLoading = state.isLoading,
                message = state.message,
                onSave = { p -> vm.saveProject(p) },
                onBack = { navController.popBackStack() },
                onMessageDismiss = { vm.clearMessage() }
            )
        }

        composable(Screen.ManagerGuidelines.route) {
            val vm: ManagerViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            OperatorGuidelinesScreen(
                guidelines = state.guidelines,
                onBack = { navController.popBackStack() }
            )
        }

        // ── LEADER ──────────────────────────────────────────────────
        composable(Screen.LeaderHome.route) {
            val vm: LeaderViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            val dashboard by vm.dashboardData.collectAsState()
            LeaderHomeScreen(
                uiState = state,
                dashboard = dashboard,
                onNavigateProjects = { navController.navigate(Screen.LeaderProjects.route) },
                onNavigateDashboard = { navController.navigate(Screen.LeaderDashboard.route) },
                onNavigateGuidelines = { navController.navigate(Screen.LeaderGuidelines.route) },
                onLogout = { authViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) } }
            )
        }

        composable(Screen.LeaderProjects.route) {
            val vm: LeaderViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            LeaderProjectsScreen(
                projects = state.projects,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LeaderDashboard.route) {
            val vm: LeaderViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            val dashboard by vm.dashboardData.collectAsState()
            LeaderDashboardScreen(
                dashboard = dashboard,
                projects = state.projects,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LeaderGuidelines.route) {
            val vm: LeaderViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            LeaderGuidelinesScreen(
                guidelines = state.guidelines,
                onNewGuideline = { navController.navigate(Screen.LeaderManageGuideline.createRoute()) },
                onEditGuideline = { id -> navController.navigate(Screen.LeaderManageGuideline.createRoute(id)) },
                onDeleteGuideline = { id -> vm.deleteGuideline(id) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LeaderManageGuideline.route) { backStack ->
            val guidelineId = backStack.arguments?.getString("guidelineId") ?: "new"
            val vm: LeaderViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()
            val guideline = if (guidelineId != "new") vm.getGuidelineById(guidelineId) else null
            LeaderGuidelineFormScreen(
                guideline = guideline,
                currentUserId = state.user?.id ?: "",
                isLoading = state.isLoading,
                message = state.message,
                onSave = { g -> vm.saveGuideline(g) },
                onBack = { navController.popBackStack() },
                onMessageDismiss = { vm.clearMessage() }
            )
        }
    }
}
