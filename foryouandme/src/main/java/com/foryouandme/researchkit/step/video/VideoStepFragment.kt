package com.foryouandme.researchkit.step.video

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.MediaController
import androidx.annotation.experimental.UseExperimental
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraView
import androidx.camera.view.video.ExperimentalVideo
import androidx.camera.view.video.OnVideoSavedCallback
import androidx.camera.view.video.OutputFileResults
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foryouandme.R
import com.foryouandme.core.arch.flow.observeIn
import com.foryouandme.core.arch.navigation.permissionSettingsAction
import com.foryouandme.core.ext.*
import com.foryouandme.databinding.StepVideoDiaryBinding
import com.foryouandme.domain.usecase.permission.RequestPermissionsUseCase
import com.foryouandme.entity.configuration.background.roundTopBackground
import com.foryouandme.entity.configuration.button.button
import com.foryouandme.entity.configuration.progressbar.progressDrawable
import com.foryouandme.entity.permission.Permission
import com.foryouandme.entity.permission.PermissionResult
import com.foryouandme.researchkit.step.StepFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class VideoStepFragment : StepFragment(R.layout.step_video_diary) {

    @Inject
    lateinit var requestPermissionsUseCase: RequestPermissionsUseCase

    private val mediaController: MediaController by lazy { MediaController(requireContext()) }

    private val videoViewModel: VideoViewModel by viewModels()

    private val binding: StepVideoDiaryBinding?
        get() = view?.let { StepVideoDiaryBinding.bind(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getVideoDirectory().deleteRecursively()

        videoViewModel.stateUpdate
            .onEach {

                when (it) {
                    is VideoStateUpdate.RecordTime -> bindRecordingHeader()
                    is VideoStateUpdate.Recording -> {
                        bindRecordingState(videoViewModel.state.recordingState)
                        bindRecordingHeader()
                    }
                    is VideoStateUpdate.Flash ->
                        bindFlash(videoViewModel.state.isFlashEnabled)
                    is VideoStateUpdate.Camera ->
                        bindCamera(videoViewModel.state.isBackCameraToggled)
                }

            }
            .observeIn(this)

        videoViewModel.error
            .onEach {

                when (it.cause) {
                    VideoError.Recording ->
                        errorToast(it.error, null)
                    VideoError.Merge ->
                        errorToast(it.error, null)
                    VideoError.Upload ->
                        errorToast(it.error, null)
                }

            }
            .observeIn(this)

        videoViewModel.loading
            .onEach {
                when (it.task) {
                    VideoLoading.Merge -> binding?.loading?.setVisibility(it.active)
                    VideoLoading.Upload -> binding?.loading?.setVisibility(it.active)
                }
            }
            .observeIn(this)


        viewModel.getStepByIndexAs<VideoStep>(indexArg())?.let {
            lifecycleScope.launchSafe { setupCamera(it) }
        }

    }

    @UseExperimental(markerClass = ExperimentalVideo::class)
    @SuppressLint("MissingPermission")
    private suspend fun setupCamera(step: VideoStep) {

        val viewBinding = binding

        val permissions =
            requestPermissionsUseCase(listOf(Permission.Camera, Permission.RecordAudio))

        val deniedPermission = permissions.firstOrNull { it is PermissionResult.Denied }?.permission

        if (deniedPermission != null) handleMissingPermission(deniedPermission, step)
        else {

            /* All permission are granted */
            viewBinding?.camera?.isVisible = true
            viewBinding?.camera?.bindToLifecycle(this)
            viewBinding?.camera?.captureMode = CameraView.CaptureMode.VIDEO

        }

    }

    private fun handleMissingPermission(
        permission: Permission,
        step: VideoStep
    ) {

        when (permission) {
            Permission.Camera ->
                showPermissionError(
                    step.missingPermissionCamera,
                    step.missingPermissionCameraBody,
                    step.settings,
                    step.cancel
                )
            Permission.RecordAudio ->
                showPermissionError(
                    step.missingPermissionMic,
                    step.missingPermissionMicBody,
                    step.settings,
                    step.cancel
                )
            else -> Unit
        }

    }

    private fun showPermissionError(
        title: String,
        description: String,
        settings: String,
        cancel: String
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton(settings) { _, _ ->
                navigator.performAction(permissionSettingsAction())
                close()
            }
            .setNegativeButton(cancel) { _, _ -> close() }
            .setCancelable(false)
            .show()

    }

    override fun onResume() {
        super.onResume()

        setupUI()

    }

    private fun setupUI() {

        val step = viewModel.getStepByIndexAs<VideoStep>(indexArg())
        val task = viewModel.state.task
        val viewBinding = binding

        if (step != null && task != null && viewBinding != null) {

            viewBinding.title.setTextColor(step.titleColor)

            viewBinding.cameraToggle.setImageResource(imageConfiguration.videoDiaryToggleCamera())
            viewBinding.cameraToggle.setOnClickListener {
                videoViewModel.execute(VideoStateEvent.ToggleCamera)
            }

            viewBinding.flashToggle.setOnClickListener {
                videoViewModel.execute(VideoStateEvent.ToggleFlash)
            }

            viewBinding.recordInfo.background =
                roundTopBackground(step.infoBackgroundColor, 30)
            viewBinding.reviewInfo.background =
                roundTopBackground(step.infoBackgroundColor, 30)

            viewBinding.closeRecording.setImageResource(step.closeImage)
            viewBinding.closeRecording.setOnClickListener { showCancelDialog() }
            viewBinding.closeReview.setImageResource(step.closeImage)
            viewBinding.closeReview.setOnClickListener { showCancelDialog() }

            viewBinding.recordingTitle.setTextColor(step.startRecordingDescriptionColor)

            viewBinding.recordingTime.setTextColor(step.timeColor)
            viewBinding.reviewTime.setTextColor(step.reviewTimeColor)

            viewBinding.recordingTimeImage.setImageResource(step.timeImage)

            viewBinding.recordingProgress.progressDrawable =
                progressDrawable(step.timeProgressBackgroundColor, step.timeProgressColor)
            viewBinding.recordingProgress.max = 100
            viewBinding.recordingProgress.progress = 50

            viewBinding.recordingInfoTitle.text = step.infoTitle
            viewBinding.recordingInfoTitle.setTextColor(step.infoTitleColor)

            viewBinding.recordingInfoBody.text = step.infoBody
            viewBinding.recordingInfoBody.setTextColor(step.infoBodyColor)

            viewBinding.review.background = button(step.buttonColor)
            viewBinding.review.setTextColor(step.buttonTextColor)
            viewBinding.review.setOnClickListener { review() }
            viewBinding.review.text = step.reviewButton

            viewBinding.submit.background = button(step.buttonColor)
            viewBinding.submit.setTextColor(step.buttonTextColor)
            viewBinding.submit.setOnClickListener {
                videoViewModel.execute(VideoStateEvent.Submit(task.id, getVideoMergeFile()))
            }
            viewBinding.submit.text = step.submitButton

            bindRecordingState(videoViewModel.state.recordingState)
            bindRecordingHeader()
            bindFlash(videoViewModel.state.isFlashEnabled)
            bindCamera(videoViewModel.state.isBackCameraToggled)

            taskFragment().binding?.toolbar?.hide()

        }

    }

    private fun bindRecordingState(state: RecordingState) {

        val step = viewModel.getStepByIndexAs<VideoStep>(indexArg())
        val viewBinding = binding

        if (viewBinding != null && step != null) {

            when (state) {
                RecordingState.Recording -> {

                    viewBinding.recordingPause.isVisible = true
                    viewBinding.reviewPause.isEnabled = false

                    viewBinding.videoView.isVisible = false

                    viewBinding.flashToggle.isVisible = videoViewModel.state.isBackCameraToggled
                    viewBinding.cameraToggle.isVisible = false

                    viewBinding.recordingPause.setImageResource(step.pauseImage)

                    viewBinding.recordingPause.setOnClickListener {

                        videoViewModel.execute(VideoStateEvent.Pause)
                        pause()

                    }

                    viewBinding.recordInfo.isVisible = false
                    viewBinding.reviewInfo.isVisible = false

                }
                is RecordingState.RecordingPause -> {

                    viewBinding.recordingPause.isVisible = true
                    viewBinding.reviewPause.isEnabled = false

                    viewBinding.videoView.isVisible = false

                    viewBinding.flashToggle.isVisible = videoViewModel.state.isBackCameraToggled
                    viewBinding.cameraToggle.isVisible = true

                    viewBinding.recordingPause.setImageResource(step.recordImage)

                    viewBinding.recordingTitle.text = step.startRecordingDescription

                    val currentRecordTime =
                        DateUtils.formatElapsedTime(videoViewModel.state.recordTimeSeconds)
                    val maxRecordTime =
                        DateUtils.formatElapsedTime(videoViewModel.state.maxRecordTimeSeconds)

                    val recordTimeLabel =
                        "$currentRecordTime/$maxRecordTime"

                    viewBinding.recordingTime.text = recordTimeLabel

                    viewBinding.recordingProgress.max =
                        videoViewModel.state.maxRecordTimeSeconds.toInt()
                    viewBinding.recordingProgress.progress =
                        videoViewModel.state.recordTimeSeconds.toInt()

                    viewBinding.recordingPause.setOnClickListener {

                        val file = createVideoFile()
                        videoViewModel.execute(VideoStateEvent.Record(file.absolutePath))
                        record(file)

                    }

                    viewBinding.review.isEnabled = videoViewModel.state.recordTimeSeconds > 0

                    viewBinding.recordInfo.isVisible = true
                    viewBinding.reviewInfo.isVisible = false

                }
                RecordingState.Merged -> {

                    viewBinding.recordingPause.isVisible = false
                    viewBinding.reviewPause.isEnabled = false
                    viewBinding.flashToggle.isVisible = false
                    viewBinding.cameraToggle.isVisible = false

                    viewBinding?.reviewLoading.setVisibility(isVisible = true, opaque = false)

                    mediaController.setMediaPlayer(viewBinding.videoView)
                    mediaController.setAnchorView(viewBinding.videoView)
                    viewBinding.videoView.setMediaController(mediaController)
                    viewBinding.videoView.isVisible = true
                    viewBinding.videoView.setVideoPath(getVideoMergeFilePath())
                    viewBinding.videoView.setOnPreparedListener {
                        binding?.reviewLoading?.setVisibility(isVisible = false, opaque = false)
                        it.isLooping = true
                        videoViewModel.execute(VideoStateEvent.ReviewPause)

                    }
                }
                RecordingState.ReviewPause -> {

                    viewBinding.recordingPause.isVisible = false
                    viewBinding.reviewPause.isEnabled = true
                    viewBinding.flashToggle.isVisible = false
                    viewBinding.cameraToggle.isVisible = false
                    viewBinding.videoView.isVisible = true
                    viewBinding.camera.isVisible = false

                    viewBinding.recordingTitle.text = step.startRecordingDescription

                    val currentRecordTime =
                        DateUtils.formatElapsedTime(videoViewModel.state.recordTimeSeconds)
                    val maxRecordTime =
                        DateUtils.formatElapsedTime(videoViewModel.state.maxRecordTimeSeconds)
                    viewBinding.recordingTitle.text = step.startRecordingDescription

                    val recordTimeLabel =
                        "$currentRecordTime/$maxRecordTime"

                    viewBinding.reviewTime.text = recordTimeLabel


                    mediaController.hide()
                    mediaController.isVisible = false

                    viewBinding.reviewPause.setImageResource(step.playImage)
                    viewBinding.reviewPause.setOnClickListener {
                        binding?.videoView?.start()
                        videoViewModel.execute(VideoStateEvent.ReviewPlay)
                    }

                    viewBinding.recordInfo.isVisible = false
                    viewBinding.reviewInfo.isVisible = true

                }
                RecordingState.Review -> {

                    viewBinding.recordingPause.isVisible = false
                    viewBinding.reviewPause.isEnabled = true
                    viewBinding.flashToggle.isVisible = false
                    viewBinding.cameraToggle.isVisible = false
                    viewBinding.videoView.isVisible = true
                    viewBinding.camera.isVisible = false

                    viewBinding.recordingTitle.text = step.startRecordingDescription

                    mediaController.isVisible = true
                    mediaController.show()

                    viewBinding.reviewPause.setImageResource(step.pauseImage)
                    viewBinding.reviewPause.setOnClickListener {
                        binding?.videoView?.pause()
                        videoViewModel.execute(VideoStateEvent.ReviewPause)
                    }

                    viewBinding.recordInfo.isVisible = false
                    viewBinding.reviewInfo.isVisible = false

                }
                RecordingState.Uploaded -> next()
            }
        }
    }

    private fun bindRecordingHeader() {

        val step = viewModel.getStepByIndexAs<VideoStep>(indexArg())

        when (videoViewModel.state.recordingState) {
            RecordingState.Recording -> {

                val currentRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state.recordTimeSeconds)
                val maxRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state.maxRecordTimeSeconds)

                val recordTimeLabel =
                    "$currentRecordTime/$maxRecordTime"

                binding?.title?.text = recordTimeLabel

            }
            RecordingState.RecordingPause ->
                binding?.title?.text = step?.title.orEmpty()
            RecordingState.Review ->
                binding?.title?.text = step?.title.orEmpty()
        }
    }

    // TODO: check if flash is available
    private fun bindFlash(isFlashEnabled: Boolean) {

        val step = viewModel.getStepByIndexAs<VideoStep>(indexArg())
        val viewBinding = binding

        if (viewBinding != null && step != null) {
            catchToNull { viewBinding.camera.enableTorch(isFlashEnabled) }

            viewBinding.flashToggle.setImageResource(
                if (isFlashEnabled) step.flashOnImage
                else step.flashOffImage
            )
        }

    }

    // TODO: check if camera lens is available
    private fun bindCamera(isBackCameraToggled: Boolean) {

        val viewBinding = binding

        catchToNull {
            viewBinding?.camera?.cameraLensFacing =
                if (isBackCameraToggled) CameraSelector.LENS_FACING_BACK
                else CameraSelector.LENS_FACING_FRONT
        }

        // enable flash button only for back camera
        viewBinding?.flashToggle?.isVisible = isBackCameraToggled

    }

    @UseExperimental(markerClass = ExperimentalVideo::class)
    private fun record(file: File) {

        binding?.camera?.startRecording(
            file,
            ContextCompat.getMainExecutor(requireContext()),
            object : OnVideoSavedCallback {

                override fun onVideoSaved(outputFileResults: OutputFileResults) {
                    videoViewModel.execute(VideoStateEvent.Pause)
                }

                override fun onError(
                    videoCaptureError: Int,
                    message: String,
                    cause: Throwable?
                ) {

                    videoViewModel.execute(VideoStateEvent.HandleRecordError)

                }

            }
        )

    }

    @UseExperimental(markerClass = ExperimentalVideo::class)
    private fun pause() {

        binding?.camera?.stopRecording()

    }

    private fun review() {

        val mergeDirectory = File(getVideoMergeDirectoryPath())

        if (mergeDirectory.exists().not())
            mergeDirectory.mkdir()

        videoViewModel.execute(
            VideoStateEvent.Merge(
                getVideoDirectoryPath(),
                mergeDirectory.absolutePath,
                getVideoMergeFileName()
            )
        )
    }

    private fun createVideoFile(): File {

        val directory = getVideoDirectory()

        // crate also the video directory if not exist
        if (!directory.exists())
            directory.mkdir()

        return File(directory, "${System.currentTimeMillis()}.mp4")

    }

    private fun getVideoDirectory(): File = File(getVideoDirectoryPath())

    private fun getVideoDirectoryPath(): String =
        "${requireContext().applicationContext.filesDir.absolutePath}/video-diary"

    private fun getVideoMergeDirectoryPath(): String =
        "${getVideoDirectoryPath()}/merge"

    private fun getVideoMergeFileName(): String =
        "merged_video_diary.mp4"

    private fun getVideoMergeFilePath(): String =
        "${getVideoMergeDirectoryPath()}/${getVideoMergeFileName()}"

    private fun getVideoMergeFile(): File = File(getVideoMergeFilePath())

    override fun onDestroy() {

        File(getVideoDirectoryPath()).deleteRecursively()

        super.onDestroy()
    }
}