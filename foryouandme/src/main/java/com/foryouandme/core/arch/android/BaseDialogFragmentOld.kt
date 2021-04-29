package com.foryouandme.core.arch.android

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.foryouandme.core.activity.FYAMActivity
import com.foryouandme.core.activity.FYAMState
import com.foryouandme.core.activity.FYAMViewModel
import com.foryouandme.core.arch.livedata.Event
import com.foryouandme.core.arch.livedata.EventObserver
import com.foryouandme.core.arch.navigation.RootNavController
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.core.ext.startCoroutineAsync

abstract class BaseDialogFragmentOld<T : BaseViewModel<*, *, *, *>> : DialogFragment() {

    protected abstract val viewModel: T

    protected val fyamViewModel: FYAMViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.activityActions()
            .observeEvent { it(requireActivity()) }
    }

    fun rootNavController(): RootNavController =
        RootNavController(requireActivity().supportFragmentManager.fragments[0].findNavController())

    fun <A> LiveData<Event<A>>.observeEvent(handle: (A) -> Unit): Unit =
        observe(this@BaseDialogFragmentOld, EventObserver { handle(it) })

    fun name(): String = this.javaClass.simpleName

    fun fyamActivity(): FYAMActivity = requireActivity() as FYAMActivity

    fun configuration(block: suspend (Configuration) -> Unit): Unit =
        startCoroutineAsync {

            val configuration = fyamViewModel.state.configuration

            block(configuration!!)

        }

    fun fyamState(block: suspend (FYAMState) -> Unit): Unit =
        startCoroutineAsync { block(fyamViewModel.state) }

}