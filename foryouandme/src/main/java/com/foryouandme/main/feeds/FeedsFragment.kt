package com.foryouandme.main.feeds

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foryouandme.R
import com.foryouandme.core.arch.android.getFactory
import com.foryouandme.core.arch.android.viewModelFactory
import com.foryouandme.core.entity.configuration.Configuration
import com.foryouandme.core.entity.configuration.HEXGradient
import com.foryouandme.core.entity.user.User
import com.foryouandme.core.ext.*
import com.foryouandme.main.MainSectionFragment
import com.foryouandme.main.items.*
import com.giacomoparisi.recyclerdroid.core.DroidAdapter
import com.giacomoparisi.recyclerdroid.core.DroidItem
import com.giacomoparisi.recyclerdroid.core.decoration.LinearMarginItemDecoration
import kotlinx.android.synthetic.main.feeds.*
import java.text.MessageFormat


class FeedsFragment : MainSectionFragment<FeedsViewModel>(R.layout.feeds) {

    override val viewModel: FeedsViewModel by lazy {
        viewModelFactory(
            this,
            getFactory {
                FeedsViewModel(
                    navigator,
                    injector.feedModule(),
                    injector.taskModule(),
                    injector.authModule()
                )
            }
        )
    }

    private val adapter: DroidAdapter by lazy {
        DroidAdapter(
            TaskActivityViewHolder.factory {
                startCoroutineAsync { viewModel.executeTasks(rootNavController(), it) }
            },
            FeedRewardViewHolder.factory { },
            FeedEducationalViewHolder.factory { },
            FeedAlertViewHolder.factory { },
            DateViewHolder.factory(),
            QuickActivitiesViewHolder.factory(),
            FeedHeaderViewHolder.factory(),
            FeedEmptyViewHolder.factory()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateLiveData()
            .observeEvent(name()) { state ->
                when (state) {
                    is FeedsStateUpdate.Initialization -> {
                        applyFeeds(state.feeds)
                        configuration { applyUser(it, state.user) }
                    }
                }
            }

        viewModel.loadingLiveData()
            .observeEvent(name()) {
                when (it.task) {
                    FeedsLoading.Initialization ->
                        loading.setVisibility(it.active, viewModel.isInitialized())
                    FeedsLoading.QuickActivityUpload ->
                        loading.setVisibility(it.active, true)
                }
            }

        viewModel.errorLiveData()
            .observeEvent(name()) { errorPayload ->
                when (errorPayload.cause) {
                    FeedsError.Initialization ->
                        error.setError(errorPayload.error) {
                            configuration {
                                viewModel.initialize(rootNavController(), it)
                            }
                        }
                    FeedsError.QuickActivityUpload ->
                        errorToast(errorPayload.error)
                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

    }

    override fun onResume() {
        super.onResume()

        configuration {

            applyConfiguration(it)
            viewModel.initialize(rootNavController(), it)

        }
    }

    private suspend fun applyConfiguration(configuration: Configuration): Unit =
        evalOnMain {

            setStatusBar(configuration.theme.primaryColorStart.color())

            root.setBackgroundColor(configuration.theme.secondaryColor.color())

            header.background =
                HEXGradient.from(
                    configuration.theme.primaryColorStart,
                    configuration.theme.primaryColorEnd
                ).drawable()

            title.setTextColor(configuration.theme.secondaryTextColor.color())

            header_text.setTextColor(configuration.theme.secondaryColor.color())
            header_text.alpha = 0.5f

            logo.setImageResource(imageConfiguration.logoStudySecondary())
            logo.setOnClickListener {
                startCoroutineAsync {
                    viewModel.aboutYouPage(rootNavController())
                }
            }

            swipe_refresh.setOnRefreshListener {
                startCoroutineAsync {
                    viewModel.initialize(rootNavController(), configuration)
                    swipe_refresh.isRefreshing = false
                }
            }

        }

    private suspend fun applyUser(configuration: Configuration, user: User?): Unit =
        evalOnMain {

            val pregnancyMonths = viewModel.getPregnancyMonths(user)
            val pregnancyTrimester = pregnancyMonths?.let { "${(it / 3) + 1}nd" }

            header_text.text =
                pregnancyTrimester?.let {
                    MessageFormat.format(configuration.text.tab.feedTitle, it)
                }
            header_text.isVisible = pregnancyTrimester != null && pregnancyMonths >= 0

            val pregnancyWeeks = viewModel.getPregnancyWeeks(user)

            title.text =
                pregnancyWeeks?.let {
                    MessageFormat.format(configuration.text.tab.feedSubTitle, it)
                }
            title.isVisible = pregnancyWeeks != null && pregnancyWeeks >= 0

        }

    private fun applyFeeds(feeds: List<DroidItem<Any>>): Unit {

        adapter.submitList(feeds)

    }

    private fun setupList(): Unit {

        recycler_view.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycler_view.adapter = adapter
        recycler_view.invalidateItemDecorations()
        recycler_view.addItemDecoration(
            LinearMarginItemDecoration(
                topMargin = {
                    when {
                        it.isOfType<FeedEmptyItem>() -> 50.dpToPx()
                        else -> 0.dpToPx()
                    }
                },
                startMargin = {
                    when {
                        it.isOfType<FeedHeaderItem>() -> 0
                        it.isOfType<QuickActivitiesItem>() -> 0
                        else -> 20.dpToPx()
                    }
                },
                endMargin = {
                    when {
                        it.isOfType<FeedHeaderItem>() -> 0
                        it.isOfType<QuickActivitiesItem>() -> 0
                        else -> 20.dpToPx()
                    }
                },
                bottomMargin = {
                    when {
                        it.isOfType<FeedEmptyItem>() -> 20.dpToPx()
                        else -> 0.dpToPx()
                    }
                }
            )
        )

    }
}