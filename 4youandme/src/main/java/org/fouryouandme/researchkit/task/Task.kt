package org.fouryouandme.researchkit.task

import arrow.core.some
import com.squareup.moshi.Moshi
import org.fouryouandme.core.arch.deps.ImageConfiguration
import org.fouryouandme.core.entity.configuration.Configuration
import org.fouryouandme.researchkit.recorder.config.DeviceMotionRecorderConfig
import org.fouryouandme.researchkit.step.Step

sealed class Task(
    val identifier: String,
    private val configuration: Configuration,
    private val imageConfiguration: ImageConfiguration
) {

    val steps: List<Step> by lazy { buildSteps(configuration, imageConfiguration) }

    protected open fun buildSteps(
        configuration: Configuration,
        imageConfiguration: ImageConfiguration
    ): List<Step> = emptyList()

    companion object {

        fun byType(
            type: ETaskType,
            configuration: Configuration,
            imageConfiguration: ImageConfiguration,
            moshi: Moshi
        ): Task =
            when (type) {
                ETaskType.WALK -> GaitTask(configuration, imageConfiguration, moshi)
                ETaskType.GAIT -> GaitTask(configuration, imageConfiguration, moshi)
                ETaskType.VIDEO_DIARY -> GaitTask(configuration, imageConfiguration, moshi)
            }

    }

    class GaitTask(
        configuration: Configuration,
        imageConfiguration: ImageConfiguration,
        private val moshi: Moshi
    ) : Task("reaction_time", configuration, imageConfiguration) {

        override fun buildSteps(
            configuration: Configuration,
            imageConfiguration: ImageConfiguration
        ): List<Step> =
            listOf(
                // TODO: Remove mock
                Step.IntroductionStep(
                    "introduction_1",
                    configuration,
                    "Gait and Balance",
                    "Find a place where you can safely walk back and forth in a straight line. Try to walk continuously by turning at the ends of your path, as if you are walking around a cone.\\n\\nNext you will be instructed to turn around in a full circle, then stand still with your arms at your sides and your feet about shoulder-width apart.\n\nTap Get Started when you are ready to begin.\\nThen place your device in a pocket or bag and follow the audio instructions.",
                    imageConfiguration.pocket(),
                    "Next"
                ),
                Step.CountDownStep(
                    "countdown_1",
                    configuration,
                    "Gait and Balance",
                    5
                ),
                Step.ActiveStep(
                    identifier = "active_step_walk",
                    configuration = configuration,
                    title = "Gait and Balance",
                    description = "Walk back and forth in a straight line for 30 seconds. Walk as you would normally.",
                    duration = 30,
                    recorderConfigurations = listOf(
                        //PedometerRecorderConfig(moshi),
                        //AccelerometerRecorderConfig(moshi, 100.toDouble()),
                        DeviceMotionRecorderConfig(moshi, 100.toDouble()),
                        //LocationRecorderConfig(moshi)
                    ),
                    spokenInstruction = "Walk back and forth in a straight line for 30 seconds. Walk as you would normally.".some(),
                    shouldPlaySoundOnFinish = true,
                    shouldVibrateOnFinish = true

                ),
                Step.ActiveStep(
                    identifier = "active_step_turn",
                    configuration = configuration,
                    title = "Gait and Balance",
                    description = "Turn in a full circle and then stand still for 30 seconds",
                    duration = 30,
                    recorderConfigurations = listOf(
                        //PedometerRecorderConfig(moshi),
                        //AccelerometerRecorderConfig(moshi, 100.toDouble()),
                        //DeviceMotionRecorderConfig(moshi, 100.toDouble()),
                        //LocationRecorderConfig(moshi)
                    ),
                    spokenInstruction = "Turn in a full circle and then stand still for 30 seconds".some(),
                    shouldPlaySoundOnFinish = true,
                    shouldVibrateOnFinish = true

                ),
                Step.IntroductionStep(
                    "introduction_completed",
                    configuration,
                    "Gait and Balance",
                    "You have completed the activity.",
                    imageConfiguration.pocket(),
                    "Next",
                    true
                ),
            )

    }

}