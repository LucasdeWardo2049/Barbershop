package com.pdm.barbershop.ui.feature.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PaymentMethod {
    PIX, CARD
}

enum class PaymentStep {
    SELECTION,
    PIX_DETAILS,
    CARD_DETAILS
}

data class PaymentUiState(
    val selectedMethod: PaymentMethod = PaymentMethod.PIX,
    val currentStep: PaymentStep = PaymentStep.SELECTION,
    
    // Dados do Cartão
    val cardHolderName: String = "",
    val cardNumber: String = "",
    val cardCvv: String = "",
    val cardExpiry: String = "",
    val isCredit: Boolean = true, // true = Crédito, false = Débito

    val isProcessing: Boolean = false,
    val paymentSuccess: Boolean = false
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    fun selectMethod(method: PaymentMethod) {
        _uiState.update { it.copy(selectedMethod = method) }
    }

    fun onAdvanceClick() {
        val nextStep = when (_uiState.value.selectedMethod) {
            PaymentMethod.PIX -> PaymentStep.PIX_DETAILS
            PaymentMethod.CARD -> PaymentStep.CARD_DETAILS
        }
        _uiState.update { it.copy(currentStep = nextStep) }
    }

    fun onBackStep() {
        _uiState.update { it.copy(currentStep = PaymentStep.SELECTION) }
    }

    // Inputs do Cartão
    fun onCardNameChange(value: String) { _uiState.update { it.copy(cardHolderName = value) } }
    fun onCardNumberChange(value: String) { _uiState.update { it.copy(cardNumber = value) } }
    fun onCardCvvChange(value: String) { _uiState.update { it.copy(cardCvv = value) } }
    fun onCardExpiryChange(value: String) { _uiState.update { it.copy(cardExpiry = value) } }
    fun onCardTypeChange(isCredit: Boolean) { _uiState.update { it.copy(isCredit = isCredit) } }

    fun processPayment() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            delay(2000) // Simula processamento
            
            // Salvar notificação
            notificationRepository.addNotification(
                title = "Pagamento Confirmado",
                message = "Seu pagamento de R$ 39,90 foi realizado com sucesso via ${if(_uiState.value.selectedMethod == PaymentMethod.PIX) "Pix" else "Cartão"}."
            )

            _uiState.update { it.copy(isProcessing = false, paymentSuccess = true) }
        }
    }
}
