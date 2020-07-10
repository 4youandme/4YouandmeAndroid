package org.fouryouandme.auth.wearable.welcome

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import arrow.core.Option
import arrow.core.extensions.fx
import arrow.core.toOption
import kotlinx.android.synthetic.main.wearable.*
import kotlinx.android.synthetic.main.wearable_welcome.*
import org.fouryouandme.R
import org.fouryouandme.auth.wearable.WearableError
import org.fouryouandme.auth.wearable.WearableLoading
import org.fouryouandme.auth.wearable.WearableStateUpdate
import org.fouryouandme.auth.wearable.WearableViewModel
import org.fouryouandme.core.arch.android.BaseFragment
import org.fouryouandme.core.arch.android.getFactory
import org.fouryouandme.core.arch.android.viewModelFactory
import org.fouryouandme.core.entity.configuration.Configuration
import org.fouryouandme.core.entity.wearable.Wearable
import org.fouryouandme.core.ext.IORuntime
import org.fouryouandme.core.ext.navigator
import org.fouryouandme.core.ext.removeBackButton
import org.fouryouandme.core.ext.setStatusBar

class WearableWelcomeFragment : BaseFragment<WearableViewModel>(R.layout.wearable_welcome) {

    override val viewModel: WearableViewModel by lazy {
        viewModelFactory(
            requireParentFragment(),
            getFactory {
                WearableViewModel(
                    navigator,
                    IORuntime
                )
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateLiveData()
            .observeEvent(WearableWelcomeFragment::class.java.simpleName) {
                when (it) {
                    is WearableStateUpdate.Initialization ->
                        applyData(it.configuration, it.wearable)
                }
            }

        viewModel.loadingLiveData()
            .observeEvent(WearableWelcomeFragment::class.java.simpleName) {
                when (it.task) {
                    WearableLoading.Initialization ->
                        loading.setVisibility(it.active, false)
                }
            }

        viewModel.errorLiveData()
            .observeEvent(WearableWelcomeFragment::class.java.simpleName) {
                when (it.cause) {
                    WearableError.Initialization ->
                        error.setError(it.error) { viewModel.initialize(rootNavController()) }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        Option.fx { !viewModel.state().configuration to !viewModel.state().wearable }
            .fold({ viewModel.initialize(rootNavController()) }, { applyData(it.first, it.second) })
    }

    private fun setupView(): Unit {

        requireParentFragment().requireParentFragment().toolbar
            .toOption()
            .map { it.removeBackButton() }
    }

    private fun applyData(configuration: Configuration, wearable: Wearable): Unit {

        setStatusBar(configuration.theme.secondaryColor.color())

        root.setBackgroundColor(configuration.theme.secondaryColor.color())

        page.applyData(
            configuration,
            wearable.welcomePage,
            { viewModel.nextPage(findNavController(), it, true) },
            { viewModel.handleSpecialLink(it) },
            { url, nextPage ->
                viewModel.login(findNavController(), url, nextPage, true)
            }
        )

    }
}