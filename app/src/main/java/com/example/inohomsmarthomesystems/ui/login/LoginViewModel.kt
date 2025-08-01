package com.example.inohomsmarthomesystems.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inohomsmarthomesystems.data.remote.WebSocketService
import com.example.inohomsmarthomesystems.data.model.AuthenticationResponse
import com.example.inohomsmarthomesystems.utils.state.ConnectionState
import com.example.inohomsmarthomesystems.utils.state.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val webSocketService: WebSocketService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UIState<String>>(UIState.Idle)
    val uiState: StateFlow<UIState<String>> = _uiState
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    init {
        viewModelScope.launch {
            webSocketService.connectionState.collectLatest { state ->
                _connectionState.value = state
                handleConnectionStateChange(state)
            }
        }
        
        viewModelScope.launch {
            webSocketService.authenticationResponse.collectLatest { response ->
                handleAuthenticationResponse(response)
            }
        }
    }

    fun onAccountsButtonClicked() {
        _uiState.value = UIState.Loading
        
        // Timeout kontrolü başlat
        startConnectionTimeout()
        
        // WebSocket bağlantısını başlat
        webSocketService.connect()
    }

    private fun handleConnectionStateChange(state: ConnectionState) {
        when (state) {
            ConnectionState.CONNECTED -> {
                webSocketService.sendAuthenticationRequest("demo", "123456")
            }
            ConnectionState.ERROR -> {
                _uiState.value = UIState.Error("WebSocket bağlantı hatası")
            }
            ConnectionState.DISCONNECTED -> {
                if (_uiState.value is UIState.Loading) {
                    _uiState.value = UIState.Error("Bağlantı kesildi")
                }
            }
        }
    }

    private fun startConnectionTimeout() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(10000)
            if (_uiState.value is UIState.Loading) {
                _uiState.value = UIState.Error("Bağlantı zaman aşımı")
            }
        }
    }

    private fun handleAuthenticationResponse(response: AuthenticationResponse?) {
        response?.let { authResponse ->
            if (authResponse.error == null && authResponse.method == "OnAuthenticated") {
                _uiState.value = UIState.Success(authResponse.params.firstOrNull() ?: "")
            } else {
                _uiState.value = UIState.Error("Authentication başarısız: ${authResponse.error}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketService.disconnect()
    }
} 