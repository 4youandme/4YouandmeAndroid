package com.foryouandme.researchkit.step.choosemany

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foryouandme.core.arch.flow.StateUpdateFlow
import com.foryouandme.core.ext.launchSafe
import com.foryouandme.researchkit.step.common.QuestionItem
import kotlinx.coroutines.flow.SharedFlow

class ChooseManyViewModel @ViewModelInject constructor(
    private val stateUpdateFlow: StateUpdateFlow<ChooseManyStepStateUpdate>
) : ViewModel() {

    /* --- state -- */

    var state: ChooseManyStepState = ChooseManyStepState()
        private set

    /* --- flow --- */

    val stateUpdates: SharedFlow<ChooseManyStepStateUpdate> = stateUpdateFlow.stateUpdates

    /* --- initialize --- */

    private suspend fun initialize(step: ChooseManyStep, answers: List<ChooseManyAnswer>) {

        val items =
            listOf(QuestionItem(step.questionId, step.question, step.questionColor, step.image))
                .plus(
                    answers.map {

                        ChooseManyAnswerItem(
                            it.id,
                            it.text,
                            false,
                            it.textColor,
                            it.buttonColor
                        )


                    }
                )

        state = state.copy(items = items)
        stateUpdateFlow.update(ChooseManyStepStateUpdate.Initialization(items))

    }

    /* --- answer --- */

    private suspend fun answer(answerId: String) {

        val items = state.items.map {
            if (it is ChooseManyAnswerItem) {
                if (it.id == answerId) it.copy(isSelected = it.isSelected.not())
                else it
            } else it
        }

        state = state.copy(items = items)
        stateUpdateFlow.update(ChooseManyStepStateUpdate.Answer(items))

    }

    fun getSelectedAnswers(): List<ChooseManyAnswerItem> =
        state.items
            .mapNotNull { it as? ChooseManyAnswerItem }
            .filter { it.isSelected }

    /* --- state event --- */

    fun execute(stateEvent: ChooseManyStepStateEvent) {

        when (stateEvent) {
            is ChooseManyStepStateEvent.Answer ->
                viewModelScope.launchSafe { answer(stateEvent.id) }
            is ChooseManyStepStateEvent.Initialize ->
                viewModelScope.launchSafe { initialize(stateEvent.step, stateEvent.answers) }
        }

    }

}