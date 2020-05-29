package org.fouryouandme.core.arch.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.fouryouandme.core.arch.livedata.Event
import org.fouryouandme.core.arch.livedata.EventObserver


abstract class BaseFragment<T : BaseViewModel<*, *, *, *, *>> : Fragment {

    protected abstract val viewModel: T

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.activityActions()
            .observeEvent { it(requireActivity()) }
    }

    fun rootNavController(): NavController =
        requireActivity().supportFragmentManager.fragments[0].findNavController()

    fun <A> LiveData<Event<A>>.observeEvent(handle: (A) -> Unit): Unit =
        observe(this@BaseFragment, EventObserver { handle(it) })
}