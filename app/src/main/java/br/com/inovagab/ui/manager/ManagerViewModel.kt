package br.com.inovagab.ui.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagab.data.repository.InovaGABRepository
import br.com.inovagab.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerUiState(
    val ideas: List<Idea> = emptyList(),
    val projects: List<Project> = emptyList(),
    val guidelines: List<StrategicGuideline> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val user: User? = null
)

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val repository: InovaGABRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerUiState())
    val uiState = _uiState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            _uiState.update { it.copy(user = user) }
            user?.let { u ->
                repository.getProjectsByManagerFlow(u.id).collect { projects ->
                    _uiState.update { it.copy(projects = projects) }
                }
            }
        }
        viewModelScope.launch {
            repository.getIdeasFlow().collect { ideas ->
                _uiState.update { it.copy(ideas = ideas) }
            }
        }
        viewModelScope.launch {
            repository.getGuidelinesFlow().collect { g ->
                _uiState.update { it.copy(guidelines = g) }
            }
        }
    }

    fun updateIdeaStatus(ideaId: String, status: IdeaStatus, comment: String, priority: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.updateIdeaStatus(ideaId, status, comment, priority)
                .onSuccess { _uiState.update { it.copy(isLoading = false, message = "Ideia atualizada!") } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, message = e.message) } }
        }
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            val user = _uiState.value.user ?: return@launch
            _uiState.update { it.copy(isLoading = true) }
            val withManager = project.copy(managerId = user.id, managerName = user.name)
            repository.saveProject(withManager)
                .onSuccess { _uiState.update { it.copy(isLoading = false, message = "Projeto salvo!") } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, message = e.message) } }
        }
    }

    fun clearMessage() { _uiState.update { it.copy(message = null) } }

    fun getProjectById(id: String) = _uiState.value.projects.find { it.id == id }
    fun getIdeaById(id: String) = _uiState.value.ideas.find { it.id == id }
}
