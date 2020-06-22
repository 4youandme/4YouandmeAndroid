package org.fouryouandme.auth.consent.user

import arrow.core.None
import arrow.core.Option
import org.fouryouandme.core.arch.navigation.NavigationAction
import org.fouryouandme.core.entity.configuration.Configuration

data class ConsentUserState(
    val configuration: Option<Configuration> = None,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
)

sealed class ConsentUserStateUpdate {

    data class Initialization(
        val configuration: Configuration
    ) : ConsentUserStateUpdate()

    data class FirstName(val firstName: String) : ConsentUserStateUpdate()

    data class LastName(val lastName: String) : ConsentUserStateUpdate()

    data class Email(val email: String) : ConsentUserStateUpdate()

}

sealed class ConsentUserLoading {

    object Initialization : ConsentUserLoading()

}

sealed class ConsentUserError {

    object Initialization : ConsentUserError()

}

/* --- navigation --- */

object ConsentUserNameToConsentUserEmail : NavigationAction
object ConsentUserEmailToConsentUserEmailValidationCode : NavigationAction