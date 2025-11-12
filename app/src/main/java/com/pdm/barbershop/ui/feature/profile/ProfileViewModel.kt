package com.pdm.barbershop.ui.feature.profile

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.domain.repository.TokenRepository
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImageUri: Uri? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = userRepository.currentUser
        .map { user ->
            ProfileUiState(
                userName = user?.name ?: "",
                userEmail = user?.email ?: "",
                profileImageUri = user?.avatarUrl?.toUri()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState()
        )

    fun onProfileImageChanged(uri: Uri?) {
        // TODO: Implementar upload da imagem para o servidor futuramente
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.clearUser()
            tokenRepository.clearToken()
        }
    }
}
