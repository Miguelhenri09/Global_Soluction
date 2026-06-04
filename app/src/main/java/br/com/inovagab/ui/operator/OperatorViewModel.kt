package br.com.inovagab.ui.operator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagab.data.repository.InovaGABRepository
import br.com.inovagab.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OperatorUiState(
    val ideas: List<Idea> = emptyList(),
    val guidelines: List<StrategicGuideline> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val user: User? = null
)

@HiltViewModel
class OperatorViewModel @Inject constructor(
    private val repository: InovaGABRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperatorUiState())
    val uiState = _uiState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            _uiState.update { it.copy(user = user) }
            user?.let { u ->
                repository.getIdeasFlow(u.id).collect { ideas ->
                    _uiState.update { it.copy(ideas = ideas) }
                }
            }
        }
        viewModelScope.launch {
            repository.getGuidelinesFlow().collect { guidelines ->
                _uiState.update { it.copy(guidelines = guidelines) }
            }
        }
    }

    fun submitIdea(title: String, description: String, category: String) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val idea = Idea(
                title = title, description = description, category = category,
                authorId = user.id, authorName = user.name, department = user.department
            )
            repository.submitIdea(idea)
                .onSuccess { _uiState.update { it.copy(isLoading = false, message = "Ideia enviada com sucesso!") } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, message = e.message) } }
        }
    }

    fun clearMessage() { _uiState.update { it.copy(message = null) } }
}
