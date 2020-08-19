package com.example.mypowerfulandroidapp.ui.auth.state

import com.example.mypowerfulandroidapp.models.AuthToken

data class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var login_fields: LoginFields? = LoginFields(),
    var authToken: AuthToken? =null
)

data class RegistrationFields(
    var registration_email: String? = null,
    var registration_username: String? = null,
    var registration_password: String? = null,
    var registration_password_confirm: String? = null
) {
    class RegistrationError {
        companion object {
            fun mustFillAllFields(): String {
                return "every fields must fill"
            }

            fun passwordDoNotMatch(): String {
                return "password do not match"
            }

            fun none(): String {
                return "none"
            }
        }

    }

    fun isValidForRegistration(): String {
        if (registration_email.isNullOrEmpty()
            || registration_password.isNullOrEmpty()
            || registration_password_confirm.isNullOrEmpty()
            || registration_username.isNullOrEmpty()
        ) {
            return RegistrationError.mustFillAllFields()
        }
        if (!registration_password.equals(registration_password_confirm)) {
            return RegistrationError.passwordDoNotMatch()
        }
        return RegistrationError.none()
    }
}

data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
) {
    class LoginError {
        companion object {
            fun mustFillAllFields(): String {
                return "every fields must fill"
            }

            fun none(): String {
                return "none"
            }
        }
    }

    fun isValidForLogin(): String {
        if (login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()
        ) {
            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }
}