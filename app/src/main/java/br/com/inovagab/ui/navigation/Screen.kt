package br.com.inovagab.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object OperatorHome : Screen("operator_home")
    object OperatorIdeas : Screen("operator_ideas")
    object OperatorNewIdea : Screen("operator_new_idea")
    object OperatorGuidelines : Screen("operator_guidelines")
    object ManagerHome : Screen("manager_home")
    object ManagerIdeas : Screen("manager_ideas")
    object ManagerProjects : Screen("manager_projects")
    object ManagerNewProject : Screen("manager_new_project/{ideaId}") {
        fun createRoute(ideaId: String = "") = "manager_new_project/$ideaId"
    }
    object ManagerEditProject : Screen("manager_edit_project/{projectId}") {
        fun createRoute(projectId: String) = "manager_edit_project/$projectId"
    }
    object ManagerGuidelines : Screen("manager_guidelines")
    object LeaderHome : Screen("leader_home")
    object LeaderProjects : Screen("leader_projects")
    object LeaderDashboard : Screen("leader_dashboard")
    object LeaderGuidelines : Screen("leader_guidelines")
    object LeaderManageGuideline : Screen("leader_manage_guideline/{guidelineId}") {
        fun createRoute(id: String = "new") = "leader_manage_guideline/$id"
    }
}
