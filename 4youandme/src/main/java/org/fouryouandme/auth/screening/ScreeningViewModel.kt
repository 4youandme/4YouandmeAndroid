package org.fouryouandme.auth.screening

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.fx.ForIO
import arrow.fx.coroutines.parSequence
import org.fouryouandme.auth.AuthNavController
import org.fouryouandme.auth.screening.questions.ScreeningQuestionItem
import org.fouryouandme.auth.screening.questions.toItem
import org.fouryouandme.core.arch.android.BaseViewModel
import org.fouryouandme.core.arch.deps.Runtime
import org.fouryouandme.core.arch.deps.modules.AnswerModule
import org.fouryouandme.core.arch.deps.modules.ScreeningModule
import org.fouryouandme.core.arch.deps.modules.nullToError
import org.fouryouandme.core.arch.error.FourYouAndMeError
import org.fouryouandme.core.arch.error.handleAuthError
import org.fouryouandme.core.arch.navigation.AnywhereToWeb
import org.fouryouandme.core.arch.navigation.AnywhereToWelcome
import org.fouryouandme.core.arch.navigation.Navigator
import org.fouryouandme.core.arch.navigation.RootNavController
import org.fouryouandme.core.cases.common.AnswerUseCase.sendAnswer
import org.fouryouandme.core.cases.screening.ScreeningUseCase.getScreening
import org.fouryouandme.core.entity.configuration.Configuration
import org.fouryouandme.core.ext.countAndAccumulate
import org.fouryouandme.core.ext.startCoroutineAsync

class ScreeningViewModel(
    navigator: Navigator,
    runtime: Runtime<ForIO>,
    private val screeningModule: ScreeningModule,
    private val answerModule: AnswerModule
) : BaseViewModel<
        ForIO,
        ScreeningState,
        ScreeningStateUpdate,
        ScreeningError,
        ScreeningLoading>
    (navigator = navigator, runtime = runtime) {

    /* --- initialize --- */

    suspend fun initialize(
        navController: RootNavController,
        configuration: Configuration
    ): Either<FourYouAndMeError, ScreeningState> {

        showLoadingFx(ScreeningLoading.Initialization)

        val state =
            screeningModule.getScreening()
                .nullToError()
                .handleAuthError(navController, navigator)
                .fold(
                    {
                        setErrorFx(it, ScreeningError.Initialization)
                        it.left()
                    },
                    { screening ->

                        val state =
                            ScreeningState(
                                screening,
                                screening.questions.map { it.toItem(configuration) }
                            )

                        setStateFx(state)
                        { ScreeningStateUpdate.Initialization(it.screening) }

                        state.right()

                    }
                )

        hideLoadingFx(ScreeningLoading.Initialization)

        return state

    }


    /* --- validation --- */

    suspend fun validate(
        rootNavController: RootNavController,
        screeningNavController: ScreeningNavController
    ): Unit {

        val correctAnswers =
            state().questions.map { item ->
                val answer =
                    when (item.answer) {
                        item.question.answers1.id -> item.question.answers1
                        item.question.answers2.id -> item.question.answers2
                        else -> null
                    }

                val request =
                    suspend {
                        answerModule.sendAnswer(
                            item.question.id,
                            answer?.text.orEmpty(),
                            answer?.id.orEmpty()
                        ).handleAuthError(rootNavController, navigator)
                    }

                (answer?.correct ?: false) toT request

            }.countAndAccumulate()

        //correctAnswers.b.parSequence()  // await execution
        startCoroutineAsync { correctAnswers.b.parSequence() }

        if (correctAnswers.a >= state().screening.minimumAnswer)
            navigator.navigateTo(
                screeningNavController,
                ScreeningQuestionsToScreeningSuccess
            )
        else
            navigator.navigateTo(
                screeningNavController,
                ScreeningQuestionsToScreeningFailure
            )

    }


    /* --- state update --- */

    suspend fun answer(item: ScreeningQuestionItem): Unit {

        val questions =
            state().questions.map { if (it.question.id == item.question.id) item else it }

        setStateFx(state().copy(questions = questions))
        { ScreeningStateUpdate.Questions(questions) }

    }


    /* --- navigation --- */

    suspend fun back(
        screeningNavController: ScreeningNavController,
        authNavController: AuthNavController,
        rootNavController: RootNavController
    ): Unit {

        if (navigator.back(screeningNavController).not())
            if (navigator.back(authNavController).not())
                navigator.back(rootNavController)

    }


    suspend fun questions(
        screeningNavController: ScreeningNavController,
        fromWelcome: Boolean
    ): Unit =
        navigator.navigateTo(
            screeningNavController,
            if (fromWelcome) ScreeningWelcomeToScreeningQuestions
            else ScreeningPageToScreeningQuestions
        )

    suspend fun page(
        screeningNavController: ScreeningNavController,
        id: String,
        fromWelcome: Boolean
    ): Unit =
        navigator.navigateTo(
            screeningNavController,
            if (fromWelcome) ScreeningWelcomeToScreeningPage(id)
            else ScreeningPageToScreeningPage(id)
        )

    suspend fun consentInfo(authNavController: AuthNavController): Unit =
        navigator.navigateTo(authNavController, ScreeningToConsentInfo)

    suspend fun abort(authNavController: AuthNavController): Unit =
        navigator.navigateTo(authNavController, AnywhereToWelcome)

    suspend fun web(navController: RootNavController, url: String): Unit =
        navigator.navigateTo(navController, AnywhereToWeb(url))

    suspend fun retryFromWelcome(screeningNavController: ScreeningNavController): Unit {

        // reset old answers
        val questions =
            state().questions.map { it.copy(answer = null) }

        setStateFx(state().copy(questions = questions))
        { ScreeningStateUpdate.Questions(questions) }

        navigator.navigateTo(
            screeningNavController,
            ScreeningFailureToScreeningWelcome
        )

    }
}