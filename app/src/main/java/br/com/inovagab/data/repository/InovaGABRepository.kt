package br.com.inovagab.data.repository

import br.com.inovagab.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InovaGABRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    // ── AUTH ────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        val uid = auth.currentUser?.uid ?: error("UID null")
        getUser(uid) ?: error("User not found")
    }

    suspend fun logout() { auth.signOut() }

    fun currentUserId() = auth.currentUser?.uid

    suspend fun getUser(uid: String): User? {
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(UserFirestore::class.java)?.toDomain(uid)
    }

    suspend fun getCurrentUser(): User? {
        val uid = currentUserId() ?: return null
        return getUser(uid)
    }

    // ── USERS (leader CRUD) ─────────────────────────────────────────

    suspend fun createUser(email: String, password: String, name: String, role: UserRole, department: String): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("UID null")
        val data = mapOf(
            "name" to name,
            "email" to email,
            "role" to role.name,
            "department" to department
        )
        db.collection("users").document(uid).set(data).await()
    }

    // ── GUIDELINES ──────────────────────────────────────────────────

    fun getGuidelinesFlow(): Flow<List<StrategicGuideline>> = callbackFlow {
        val listener = db.collection("guidelines")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(GuidelineFirestore::class.java)?.toDomain(doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveGuideline(guideline: StrategicGuideline): Result<Unit> = runCatching {
        val data = mapOf(
            "title" to guideline.title,
            "description" to guideline.description,
            "category" to guideline.category,
            "year" to guideline.year,
            "createdBy" to guideline.createdBy,
            "createdAt" to guideline.createdAt
        )
        if (guideline.id.isBlank()) {
            db.collection("guidelines").add(data).await()
        } else {
            db.collection("guidelines").document(guideline.id).set(data).await()
        }
    }

    suspend fun deleteGuideline(id: String): Result<Unit> = runCatching {
        db.collection("guidelines").document(id).delete().await()
    }

    // ── IDEAS ───────────────────────────────────────────────────────

    fun getIdeasFlow(authorId: String? = null): Flow<List<Idea>> = callbackFlow {
        val query = if (authorId != null) {
            db.collection("ideas").whereEqualTo("authorId", authorId)
        } else {
            db.collection("ideas").orderBy("createdAt", Query.Direction.DESCENDING)
        }
        val listener = query.addSnapshotListener { snap, _ ->
            val list = snap?.documents?.mapNotNull { doc ->
                doc.toObject(IdeaFirestore::class.java)?.toDomain(doc.id)
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun submitIdea(idea: Idea): Result<Unit> = runCatching {
        val data = mapOf(
            "title" to idea.title,
            "description" to idea.description,
            "category" to idea.category,
            "authorId" to idea.authorId,
            "authorName" to idea.authorName,
            "department" to idea.department,
            "status" to idea.status.name,
            "priority" to idea.priority,
            "createdAt" to idea.createdAt,
            "updatedAt" to idea.updatedAt,
            "managerComment" to idea.managerComment
        )
        db.collection("ideas").add(data).await()
    }

    suspend fun updateIdeaStatus(ideaId: String, status: IdeaStatus, comment: String = "", priority: Int = 0): Result<Unit> = runCatching {
        db.collection("ideas").document(ideaId).update(
            mapOf(
                "status" to status.name,
                "managerComment" to comment,
                "priority" to priority,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    // ── PROJECTS ────────────────────────────────────────────────────

    fun getProjectsFlow(): Flow<List<Project>> = callbackFlow {
        val listener = db.collection("projects")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(ProjectFirestore::class.java)?.toDomain(doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getProjectsByManagerFlow(managerId: String): Flow<List<Project>> = callbackFlow {
        val listener = db.collection("projects")
            .whereEqualTo("managerId", managerId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(ProjectFirestore::class.java)?.toDomain(doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveProject(project: Project): Result<Unit> = runCatching {
        val data = mapOf(
            "title" to project.title,
            "description" to project.description,
            "managerId" to project.managerId,
            "managerName" to project.managerName,
            "originIdeaId" to project.originIdeaId,
            "status" to project.status.name,
            "stage" to project.stage,
            "investment" to project.investment,
            "expectedReturn" to project.expectedReturn,
            "actualReturn" to project.actualReturn,
            "productivityGain" to project.productivityGain,
            "costReduction" to project.costReduction,
            "startDate" to project.startDate,
            "endDate" to project.endDate,
            "progress" to project.progress,
            "notes" to project.notes,
            "createdAt" to project.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        if (project.id.isBlank()) {
            db.collection("projects").add(data).await()
        } else {
            db.collection("projects").document(project.id).update(data).await()
        }
    }
}

// ── Firestore DTOs ───────────────────────────────────────────────────

data class UserFirestore(
    val name: String = "",
    val email: String = "",
    val role: String = "OPERATOR",
    val department: String = ""
) {
    fun toDomain(id: String) = User(
        id = id, name = name, email = email,
        role = UserRole.valueOf(role),
        department = department
    )
}

data class GuidelineFirestore(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val year: Int = 2025,
    val createdBy: String = "",
    val createdAt: Long = 0L
) {
    fun toDomain(id: String) = StrategicGuideline(
        id = id, title = title, description = description,
        category = category, year = year, createdBy = createdBy, createdAt = createdAt
    )
}

data class IdeaFirestore(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val department: String = "",
    val status: String = "PENDING",
    val priority: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val managerComment: String = ""
) {
    fun toDomain(id: String) = Idea(
        id = id, title = title, description = description, category = category,
        authorId = authorId, authorName = authorName, department = department,
        status = IdeaStatus.valueOf(status), priority = priority,
        createdAt = createdAt, updatedAt = updatedAt, managerComment = managerComment
    )
}

data class ProjectFirestore(
    val title: String = "",
    val description: String = "",
    val managerId: String = "",
    val managerName: String = "",
    val originIdeaId: String = "",
    val status: String = "PLANNING",
    val stage: String = "",
    val investment: Double = 0.0,
    val expectedReturn: Double = 0.0,
    val actualReturn: Double = 0.0,
    val productivityGain: Double = 0.0,
    val costReduction: Double = 0.0,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val progress: Int = 0,
    val notes: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    fun toDomain(id: String) = Project(
        id = id, title = title, description = description,
        managerId = managerId, managerName = managerName, originIdeaId = originIdeaId,
        status = ProjectStatus.valueOf(status), stage = stage,
        investment = investment, expectedReturn = expectedReturn, actualReturn = actualReturn,
        productivityGain = productivityGain, costReduction = costReduction,
        startDate = startDate, endDate = endDate, progress = progress,
        notes = notes, createdAt = createdAt, updatedAt = updatedAt
    )
}
