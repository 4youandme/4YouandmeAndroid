package com.foryouandme.core.arch.android

import android.content.ServiceConnection
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.foryouandme.core.activity.FYAMActivity
import com.foryouandme.core.arch.error.handleAuthError
import com.foryouandme.core.arch.livedata.Event
import com.foryouandme.core.arch.livedata.EventObserver
import com.foryouandme.core.arch.navigation.RootNavController
import com.foryouandme.core.cases.CachePolicy
import com.foryouandme.core.cases.configuration.ConfigurationUseCase.getConfiguration
import com.foryouandme.core.ext.evalOnMain
import com.foryouandme.core.ext.injector
import com.foryouandme.core.ext.navigator
import com.foryouandme.core.ext.startCoroutineAsync
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.researchkit.task.TaskConfiguration

abstract class BaseFragmentOld<T : BaseViewModel<*, *, *, *>> : Fragment {

    protected abstract val viewModel: T

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.activityActions()
            .observeEvent { it(requireActivity()) }

    }

    fun rootNavController(): RootNavController =
        RootNavController(requireActivity().supportFragmentManager.fragments[0].findNavController())

    fun <A> LiveData<Event<A>>.observeEvent(handlerId: String? = null, handle: (A) -> Unit): Unit =
        observe(this@BaseFragmentOld, EventObserver(handlerId) { handle(it) })

    fun <A> LiveData<Event<A>>.observeEventPeek(handle: (A) -> Unit): Unit =
        observe(this@BaseFragmentOld, { handle(it.peekContent()) })

    fun unbindService(serviceConnection: ServiceConnection): Unit =
        startCoroutineAsync {
            Either.catch {
                evalOnMain { requireActivity().applicationContext.unbindService(serviceConnection) }
            }
        }

    fun name(): String = this.javaClass.simpleName

    fun fyamActivity(): FYAMActivity = requireActivity() as FYAMActivity

    fun configuration(block: suspend (Configuration) -> Unit): Unit =
        startCoroutineAsync {

            val configuration =
                injector.configurationModule().getConfiguration(CachePolicy.MemoryFirst)
                    .handleAuthError(rootNavController(), navigator)
                    .orNull()

            configuration?.let { block(it) }

        }

}