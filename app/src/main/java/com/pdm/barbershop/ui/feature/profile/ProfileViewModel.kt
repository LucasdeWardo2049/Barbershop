package com.pdm.barbershop.ui.feature.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.domain.model.toDomain
import com.pdm.barbershop.domain.repository.TokenRepository
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImageUri: Uri? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val apiService: ApiService, // Adicionado
    private val application: Application // Adicionado
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
        uri ?: return

        viewModelScope.launch {
            val user = userRepository.currentUser.first()
            user ?: return@launch

            try {
                val mimeType = application.contentResolver.getType(uri)
                val inputStream: InputStream? = application.contentResolver.openInputStream(uri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                fileBytes ?: return@launch

                val requestFile = fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile)

                val updatedUser = apiService.uploadAvatar(user.userId, body)

                userRepository.updateUser(updatedUser.toDomain())

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Falha no upload do avatar", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.clearUser()
            tokenRepository.clearToken()
        }
    }
}
