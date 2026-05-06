package com.example.fotochef.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    val currentUser: com.google.firebase.auth.FirebaseUser?
        get() = authRepository.currentUser

    fun signInWithEmail(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Por favor, rellena todos los campos")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithEmail(email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun signUpWithEmail(name: String, email: String, pass: String, repeatPass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Por favor, rellena todos los campos")
            return
        }
        if (pass != repeatPass) {
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }
        if (pass.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signUpWithEmail(email, pass)
            if (result.isSuccess) {
                // Here we could update the Firebase user profile with the name
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error al registrarse")
            }
        }
    }

    fun signInWithGoogle() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle()
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                // Ignoramos el error si el usuario simplemente cerró el popup de Google
                if (errorMsg.contains("Cancelado por el usuario")) {
                    _authState.value = AuthState.Idle
                } else {
                    _authState.value = AuthState.Error(errorMsg)
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
