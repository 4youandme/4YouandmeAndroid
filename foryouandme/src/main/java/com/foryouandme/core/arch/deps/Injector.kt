package com.foryouandme.core.arch.deps

import android.content.SharedPreferences
import com.foryouandme.core.arch.deps.modules.*
import com.foryouandme.core.arch.navigation.Navigator
import com.foryouandme.data.repository.usersettings.network.UserSettingsApi
import com.foryouandme.core.data.api.consent.informed.ConsentInfoApi
import com.foryouandme.core.data.api.optins.OptInsApi
import com.foryouandme.data.repository.yourdata.network.YourDataApi
import com.foryouandme.data.datasource.Environment
import com.foryouandme.data.repository.auth.answer.network.AuthAnswerApi
import com.foryouandme.data.repository.auth.consent.network.ConsentApi
import com.foryouandme.data.repository.auth.integration.network.IntegrationApi
import com.foryouandme.data.repository.auth.network.AuthApi
import com.foryouandme.data.repository.auth.screening.network.ScreeningApi
import com.foryouandme.data.repository.configuration.network.ConfigurationApi
import com.foryouandme.data.repository.consent.user.network.ConsentUserApi
import com.foryouandme.data.repository.feed.network.FeedApi
import com.foryouandme.data.repository.study.network.StudyInfoApi
import com.foryouandme.data.repository.survey.network.SurveyApi
import com.foryouandme.data.repository.task.network.TaskApi
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.moshi.Moshi

interface Injector {

    /* --- navigation --- */

    val navigator: Navigator

    /* --- cache --- */

    val prefs: SharedPreferences

    /* --- environment --- */

    val environment: Environment

    /* --- image configuration --- */

    val imageConfiguration: ImageConfiguration

    /* --- video configuration --- */

    val videoConfiguration: VideoConfiguration

    /* --- json --- */

    val moshi: Moshi

    /* --- analytics --- */

    val firebaseAnalytics: FirebaseAnalytics

    /* --- api --- */

    val configurationApi: ConfigurationApi
    val authApi: AuthApi
    val screeningApi: ScreeningApi
    val consentInfoApi: ConsentInfoApi
    val consentApi: ConsentApi
    val consentUserApi: ConsentUserApi
    val optInsApi: OptInsApi
    val integrationApi: IntegrationApi
    val taskApi: TaskApi
    val answerApi: AuthAnswerApi
    val feedApi: FeedApi
    val yourDataApi: YourDataApi
    val studyInfoApi: StudyInfoApi
    val surveyApi: SurveyApi
    val userSettingsApi: UserSettingsApi


    /* --- modules --- */

    fun errorModule(): ErrorModule = ErrorModule(moshi)

    fun permissionModule(): PermissionModule

    fun analyticsModule(): AnalyticsModule = AnalyticsModule(firebaseAnalytics)

    fun configurationModule(): ConfigurationModule =
        ConfigurationModule(
            configurationApi,
            prefs,
            moshi,
            environment,
            errorModule()
        )

    fun authModule(): AuthModule =
        AuthModule(
            authApi,
            prefs,
            moshi,
            environment,
            errorModule(),
            configurationModule(),
            analyticsModule()
        )

    fun optInModule(): OptInModule =
        OptInModule(
            optInsApi,
            environment,
            errorModule(),
            authModule()
        )

    fun consentUserModule(): ConsentUserModule =
        ConsentUserModule(
            consentUserApi,
            environment,
            errorModule(),
            authModule()
        )

    fun consentInfoModule(): ConsentInfoModule =
        ConsentInfoModule(
            consentInfoApi,
            environment,
            errorModule(),
            authModule()
        )

    fun answerModule(): AnswerModule =
        AnswerModule(
            answerApi,
            environment,
            errorModule(),
            authModule()
        )

    fun taskModule(): TaskModule =
        TaskModule(
            taskApi,
            moshi,
            environment,
            errorModule(),
            authModule()
        )

    fun consentReviewModule(): ConsentReviewModule =
        ConsentReviewModule(
            consentApi,
            errorModule(),
            authModule(),
            environment
        )

    fun feedModule(): FeedModule =
        FeedModule(
            feedApi,
            moshi,
            environment,
            errorModule(),
            authModule()
        )

    fun yourDataModule(): YourDataModule =
        YourDataModule(
            yourDataApi,
            environment,
            errorModule(),
            authModule()
        )

    fun studyInfoModule(): StudyInfoModule =
        StudyInfoModule(
            studyInfoApi,
            environment,
            errorModule(),
            authModule()
        )

    fun surveyModule(): SurveyModule =
        SurveyModule(
            surveyApi,
            environment,
            errorModule(),
            authModule()
        )
}