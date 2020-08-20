package com.example.mypowerfulandroidapp.ui.auth.state

sealed class AuthStateEvent {
    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent()

    data class RegistrationAttemptEvent(
        val email: String,
        val username: String,
        val password: String,
        val password_confirm: String
    ):AuthStateEvent()
    class CheckPreviousAuthEvent:AuthStateEvent()

}