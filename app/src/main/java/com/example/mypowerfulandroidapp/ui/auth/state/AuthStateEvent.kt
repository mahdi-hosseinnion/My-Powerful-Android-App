package com.example.mypowerfulandroidapp.ui.auth.state

sealed class AuthStateEvent {
    data class loginAttemptEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent()

    data class registrationAttempEvent(
        val email: String,
        val username: String,
        val password: String,
        val password_confirm: String
    ):AuthStateEvent()
    class checkPreviousAuthEvent:AuthStateEvent()

}