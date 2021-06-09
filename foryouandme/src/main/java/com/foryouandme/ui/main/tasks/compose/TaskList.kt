package com.foryouandme.ui.main.tasks.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foryouandme.core.arch.LazyData
import com.foryouandme.entity.activity.QuickActivityAnswer
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.ui.compose.ForYouAndMeTheme
import com.foryouandme.ui.compose.error.Error
import com.foryouandme.ui.compose.error.ErrorItem
import com.foryouandme.ui.compose.loading.Loading
import com.foryouandme.ui.main.compose.DateItem
import com.foryouandme.ui.main.compose.FeedItem
import com.foryouandme.ui.main.compose.QuickActivitiesItem
import com.foryouandme.ui.main.compose.TaskActivityItem
import com.foryouandme.ui.main.tasks.TasksState

@Composable
fun TaskList(
    state: TasksState,
    configuration: Configuration,
    onFirstPageRetry: () -> Unit = {},
    onNextPageRetry: () -> Unit = {},
    onFeedScrollPositionChange: (Int) -> Unit = {},
    onAnswerSelected: (FeedItem.QuickActivityItem, QuickActivityAnswer) -> Unit = { _, _ -> },
    onSubmit: (FeedItem.QuickActivityItem) -> Unit = {},
    onStartClicked: (FeedItem.TaskActivityItem) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {

        val feeds = state.feeds.currentOrPrevious()

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            if (feeds != null)
                itemsIndexed(feeds) { index, item ->
                    onFeedScrollPositionChange(index)
                    when (item) {
                        is FeedItem.TaskActivityItem ->
                            TaskActivityItem(
                                item = item,
                                configuration = configuration,
                                onStartClicked = onStartClicked
                            )
                        is FeedItem.QuickActivitiesItem ->
                            QuickActivitiesItem(
                                item = item,
                                configuration = configuration,
                                onAnswerSelected = onAnswerSelected,
                                onSubmit = onSubmit
                            )
                        is FeedItem.DateItem ->
                            DateItem(item = item, configuration = configuration)
                        else -> Unit
                    }
                }

            when (state.feeds) {
                is LazyData.Loading ->
                    item {
                        Loading(
                            backgroundColor = Color.Transparent,
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        )
                    }
                is LazyData.Error ->
                    item {
                        ErrorItem(
                            error = state.feeds.error,
                            configuration = configuration,
                            retry = onNextPageRetry
                        )
                    }
                else -> {
                }
            }
        }
        when (state.firstPage) {
            is LazyData.Error ->
                Error(
                    error = state.firstPage.error,
                    configuration = configuration,
                    modifier = Modifier.fillMaxSize(),
                    retry = onFirstPageRetry
                )
            is LazyData.Loading ->
                Loading(
                    configuration = configuration,
                    modifier = Modifier.fillMaxSize()
                )
            else -> {

            }
        }
    }
}

@Preview
@Composable
private fun TasksListPreview() {
    ForYouAndMeTheme {
        TaskList(
            state = TasksState.mock(),
            configuration = Configuration.mock()
        )
    }
}