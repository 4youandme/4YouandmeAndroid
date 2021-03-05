package com.foryouandme.core.arch.android

import android.content.ServiceConnection
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.foryouandme.core.arch.error.ErrorMessenger
import com.foryouandme.core.arch.error.ErrorView
import com.foryouandme.core.arch.navigation.Navigator
import com.foryouandme.core.arch.navigation.RootNavController
import com.foryouandme.core.ext.catchToNull
import com.foryouandme.core.ext.launchSafe
import com.foryouandme.entity.configuration.Configuration
import javax.inject.Inject

abstract class BaseFragment : Fragment {

    @Inject
    protected lateinit var errorMessenger: ErrorMessenger

    @Inject
    lateinit var navigator: Navigator

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    val name: String
        get() = javaClass.simpleName

    /* --- navigation --- */

    fun rootNavController(): RootNavController =
        RootNavController(
            requireActivity().supportFragmentManager.fragments[0].findNavController()
        )


    fun name(): String = javaClass.simpleName

    /* --- error --- */

    fun errorToast(throwable: Throwable, configuration: Configuration?) {

        val message = errorMessenger.getMessage(throwable, configuration)

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }

    protected fun ErrorView.setError(
        throwable: Throwable,
        configuration: Configuration?,
        retry: () -> Unit = {}
    ) {

        lifecycleScope.launchSafe {

            setError(
                errorMessenger.getTitle(),
                errorMessenger.getMessage(throwable, configuration),
                retry
            )


        }

    }

    /* --- service --- */

    fun unbindService(serviceConnection: ServiceConnection) {
        catchToNull { requireActivity().applicationContext.unbindService(serviceConnection) }
    }

}