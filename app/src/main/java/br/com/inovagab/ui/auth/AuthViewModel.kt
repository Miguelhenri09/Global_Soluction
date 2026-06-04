package br.com.inovagab.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagab.data.repository.InovaGABRepository
import br.com.inovagab.domain.model.User
import br.com.inovagab.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: InovaGABRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init { checkSession() }

    private fun checkSession() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                _uiState.value = AuthUiState(user = user, isLoggedIn = true)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.login(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState(user = user, isLoggedIn = true)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState(error = e.message ?: "Erro ao fazer login")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun getHomeRoute(role: UserRole) = when (role) {
        UserRole.OPERATOR -> br.com.inovagab.ui.navigation.Screen.OperatorHome.route
        UserRole.MANAGER  -> br.com.inovagab.ui.navigation.Screen.ManagerHome.route
        UserRole.LEADER   -> br.com.inovagab.ui.navigation.Screen.LeaderHome.route
    }
}
