package br.com.inovagab.domain.model

enum class UserRole { OPERATOR, MANAGER, LEADER }

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.OPERATOR,
    val department: String = ""
)

data class Idea(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val department: String = "",
    val status: IdeaStatus = IdeaStatus.PENDING,
    val priority: Int = 0,         // 0=normal, 1=high, 2=urgent
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val managerComment: String = ""
)

enum class IdeaStatus {
    PENDING, UNDER_REVIEW, APPROVED, REJECTED, CONVERTED_TO_PROJECT
}

data class StrategicGuideline(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val year: Int = 2025,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Project(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val managerId: String = "",
    val managerName: String = "",
    val originIdeaId: String = "",
    val status: ProjectStatus = ProjectStatus.PLANNING,
    val stage: String = "",
    val investment: Double = 0.0,
    val expectedReturn: Double = 0.0,
    val actualReturn: Double = 0.0,
    val productivityGain: Double = 0.0,   // percentage
    val costReduction: Double = 0.0,       // R$
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = 0L,
    val progress: Int = 0,                 // 0-100
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class ProjectStatus {
    PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED
}
