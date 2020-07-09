package org.fouryouandme.auth.wearable


import arrow.core.None
import arrow.core.Option
import org.fouryouandme.core.arch.navigation.NavigationAction
import org.fouryouandme.core.entity.configuration.Configuration
import org.fouryouandme.core.entity.wearable.Wearable

data class WearableState(
    val configuration: Option<Configuration> = None,
    val wearable: Option<Wearable> = None
)

sealed class WearableStateUpdate {

    data class Initialization(
        val configuration: Configuration,
        val wearable: Wearable
    ) : WearableStateUpdate()

}

sealed class WearableLoading {

    object Initialization : WearableLoading()

}

sealed class WearableError {

    object Initialization : WearableError()

}


/* --- special action --- */

sealed class App(val packageName: String) {

    object Oura : App("com.ouraring.oura")
    object Fitbit : App("com.fitbit.FitbitMobile")

}

sealed class SpecialLinkAction {
    data class OpenApp(val app: App) : SpecialLinkAction()
    data class Download(val app: App) : SpecialLinkAction()
}

/* --- navigation --- */

data class WearableWelcomeToWearablePage(val pageId: String) : NavigationAction
data class WearableWelcomeToWearableLogin(val url: String) : NavigationAction
data class WearablePageToWearablePage(val pageId: String) : NavigationAction
data class WearablePageToWearableLogin(val url: String) : NavigationAction

