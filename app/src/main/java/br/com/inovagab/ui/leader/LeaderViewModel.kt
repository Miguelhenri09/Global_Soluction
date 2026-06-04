package br.com.inovagab.ui.leader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagab.data.repository.InovaGABRepository
import br.com.inovagab.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderUiState(
    val projects: List<Project> = emptyList(),
    val guidelines: List<StrategicGuideline> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val user: User? = null
)

data class DashboardData(
    val totalProjects: Int = 0,
    val completedProjects: Int = 0,
    val totalInvestment: Double = 0.0,
    val totalReturn: Double = 0.0,
    val totalCostReduction: Double = 0.0,
    val avgProductivityGain: Double = 0.0,
    val roi: Double = 0.0,
    val inProgressCount: Int = 0,
    val planningCount: Int = 0,
)

@HiltViewModel
class LeaderViewModel @Inject constructor(
    private val repository: InovaGABRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderUiState())
    val uiState = _uiState.asStateFlow()

    val dashboardData: StateFlow<DashboardData> = _uiState.map { state ->
        val projects = state.projects
        val totalInv = projects.sumOf { it.investment }
        val totalReturn = projects.sumOf { it.actualReturn }
        val roi = if (totalInv > 0) ((totalReturn - totalInv) / totalInv) * 100 else 0.0
        DashboardData(
            totalProjects = projects.size,
            completedProjects = projects.count { it.status == ProjectStatus.COMPLETED },
            totalInvestment = totalInv,
            totalReturn = totalReturn,
            totalCostReduction = projects.sumOf { it.costReduction },
            avgProductivityGain = if (projects.isEmpty()) 0.0 else projects.sumOf { it.productivityGain } / projects.size,
            roi = roi,
            inProgressCount = projects.count { it.status == ProjectStatus.IN_PROGRESS },
            planningCount = projects.count { it.status == ProjectStatus.PLANNING },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DashboardData())

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            _uiState.update { it.copy(user = user) }
        }
        viewModelScope.launch {
            repository.getProjectsFlow().collect { projects ->
                _uiState.update { it.copy(projects = projects) }
            }
        }
        viewModelScope.launch {
            repository.getGuidelinesFlow().collect { g ->
                _uiState.update { it.copy(guidelines = g) }
            }
        }
    }

    fun saveGuideline(guideline: StrategicGuideline) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.saveGuideline(guideline)
                .onSuccess { _uiState.update { it.copy(isLoading = false, message = "Diretriz salva!") } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, message = e.message) } }
        }
    }

    fun deleteGuideline(id: String) {
        viewModelScope.launch {
            repository.deleteGuideline(id)
                .onSuccess { _uiState.update { it.copy(message = "Diretriz excluída.") } }
                .onFailure { e -> _uiState.update { it.copy(message = e.message) } }
        }
    }

    fun clearMessage() { _uiState.update { it.copy(message = null) } }

    fun getGuidelineById(id: String) = _uiState.value.guidelines.find { it.id == id }
}
