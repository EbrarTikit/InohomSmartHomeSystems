package com.example.inohomsmarthomesystems.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _navigateToOnBoarding = MutableStateFlow(false)
    val navigateToOnBoarding: StateFlow<Boolean> = _navigateToOnBoarding

    init {
        startSplashScreen()
    }

    private fun startSplashScreen() {
        viewModelScope.launch {
            delay(1500)
            _navigateToOnBoarding.value = true
        }
    }
}