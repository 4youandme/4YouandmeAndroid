package org.fouryouandme.researchkit.step.video

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.MediaController
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.VideoCapture
import androidx.camera.view.CameraView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arrow.core.Either
import kotlinx.android.synthetic.main.step_video_diary.*
import kotlinx.android.synthetic.main.task.*
import org.fouryouandme.R
import org.fouryouandme.core.arch.android.getFactory
import org.fouryouandme.core.arch.android.viewModelFactory
import org.fouryouandme.core.entity.configuration.background.roundTopBackground
import org.fouryouandme.core.entity.configuration.button.button
import org.fouryouandme.core.entity.configuration.progressbar.progressDrawable
import org.fouryouandme.core.ext.*
import org.fouryouandme.core.permission.Permission
import org.fouryouandme.core.permission.PermissionError
import org.fouryouandme.core.permission.requestMultiplePermission
import org.fouryouandme.researchkit.step.StepFragment
import java.io.File


class VideoStepFragment : StepFragment(R.layout.step_video_diary) {

    private val mediaController: MediaController by lazy { MediaController(requireContext()) }

    private val videoViewModel: VideoViewModel by lazy {
        viewModelFactory(
            this,
            getFactory {
                VideoViewModel(
                    navigator,
                    injector.taskModule()
                )
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getVideoDirectory().deleteRecursively()

        videoViewModel.stateLiveData()
            .observeEvent {

                when (it) {
                    is VideoStateUpdate.RecordTime -> bindRecordingHeader()
                    is VideoStateUpdate.Recording -> {
                        bindRecordingState(it.recordingState)
                        bindRecordingHeader()
                    }
                    is VideoStateUpdate.Flash ->
                        bindFlash(it.isFlashEnabled)
                    is VideoStateUpdate.Camera ->
                        bindCamera(it.isBackCameraToggled)
                }

            }

        videoViewModel.errorLiveData()
            .observeEvent {

                when (it.cause) {
                    VideoError.Recording ->
                        errorToast(it.error.message(requireContext()))
                    VideoError.Merge ->
                        errorToast(it.error.message(requireContext()))
                    VideoError.Upload ->
                        errorToast(it.error.message(requireContext()))
                }

            }

        videoViewModel.loadingLiveData()
            .observeEvent {
                when (it.task) {
                    VideoLoading.Merge -> loading.setVisibility(it.active)
                    VideoLoading.Upload -> loading.setVisibility(it.active)
                }
            }

        startCoroutineAsync {

            if (videoViewModel.isInitialized().not()) {

                val step =
                    viewModel.getStepByIndexAs<VideoStep>(indexArg())

                step?.let { videoViewModel.initialize(it) }

            }

            setupCamera(videoViewModel.state().step)
        }

    }

    @SuppressLint("MissingPermission")
    private suspend fun setupCamera(step: VideoStep): Unit {

        val permsission =
            requestMultiplePermission(requireContext(), Permission.Camera, Permission.RecordAudio)

        permsission.fold(
            {
                when (it) {
                    is PermissionError.PermissionDenied ->
                        handleMissingPermission(it.permission, step)
                    PermissionError.Unknown ->
                        viewModel.close(taskNavController())
                }
            },
            {
                /* All permission are granted */
                evalOnMain {

                    camera.isVisible = true
                    camera.bindToLifecycle(this)
                    camera.captureMode = CameraView.CaptureMode.VIDEO

                }

            }
        )
    }

    private suspend fun handleMissingPermission(
        permission: Permission,
        step: VideoStep
    ): Unit {

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
        }

    }

    private suspend fun showPermissionError(
        title: String,
        description: String,
        settings: String,
        cancel: String
    ): Unit {

        evalOnMain {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(settings) { _, _ ->
                    startCoroutineAsync {
                        videoViewModel.permissionSettings()
                        viewModel.close(taskNavController())
                    }
                }
                .setNegativeButton(cancel) { _, _ ->
                    startCoroutineAsync { viewModel.close(taskNavController()) }
                }
                .setCancelable(false)
                .show()
        }

    }

    override fun onResume() {
        super.onResume()

        startCoroutineAsync {

            if (videoViewModel.isInitialized().not()) {

                val step =
                    viewModel.getStepByIndexAs<VideoStep>(indexArg())

                step?.let { videoViewModel.initialize(it) }

            }

            evalOnMain { setupUI() }

        }

    }

    private fun setupUI(): Unit {

        val step = videoViewModel.state().step

        title.setTextColor(step.titleColor)

        camera_toggle.setImageResource(imageConfiguration.videoDiaryToggleCamera())
        camera_toggle.setOnClickListener {
            startCoroutineAsync { videoViewModel.toggleCamera() }
        }

        flash_toggle.setOnClickListener {
            startCoroutineAsync { videoViewModel.toggleFlash() }
        }

        record_info.background =
            roundTopBackground(step.infoBackgroundColor, 30)
        review_info.background =
            roundTopBackground(step.infoBackgroundColor, 30)

        close_recording.setImageResource(step.closeImage)
        close_recording.setOnClickListener { showCancelDialog() }
        close_review.setImageResource(step.closeImage)
        close_review.setOnClickListener { showCancelDialog() }

        recording_title.setTextColor(step.startRecordingDescriptionColor)

        recording_time.setTextColor(step.timeColor)
        review_time.setTextColor(step.reviewTimeColor)

        recording_time_image.setImageResource(step.timeImage)

        recording_progress.progressDrawable =
            progressDrawable(step.timeProgressBackgroundColor, step.timeProgressColor)
        recording_progress.max = 100
        recording_progress.progress = 50

        recording_info_title.text = step.infoTitle
        recording_info_title.setTextColor(step.infoTitleColor)

        recording_info_body.text = step.infoBody
        recording_info_body.setTextColor(step.infoBodyColor)

        review.background = button(step.buttonColor)
        review.setTextColor(step.buttonTextColor)
        review.setOnClickListener { startCoroutineAsync { review() } }
        review.text = step.reviewButton

        submit.background = button(step.buttonColor)
        submit.setTextColor(step.buttonTextColor)
        submit.setOnClickListener {
            startCoroutineAsync {
                videoViewModel.submit(viewModel.state().task.type, getVideoMergeFile())
            }
        }
        submit.text = step.submitButton

        bindRecordingState(videoViewModel.state().recordingState)
        bindRecordingHeader()
        bindFlash(videoViewModel.state().isFlashEnabled)
        bindCamera(videoViewModel.state().isBackCameraToggled)

        taskFragment().toolbar.apply { hide() }

    }

    private fun bindRecordingState(state: RecordingState): Unit {

        val step = videoViewModel.state().step

        when (state) {
            RecordingState.Recording -> {

                recording_pause.isVisible = true
                review_pause.isEnabled = false

                video_view.isVisible = false

                flash_toggle.isVisible = videoViewModel.state().isBackCameraToggled
                camera_toggle.isVisible = false

                recording_pause.setImageResource(step.pauseImage)

                recording_pause.setOnClickListener {

                    startCoroutineAsync { videoViewModel.pause() }
                    pause()

                }

                record_info.isVisible = false
                review_info.isVisible = false

            }
            is RecordingState.RecordingPause -> {

                recording_pause.isVisible = true
                review_pause.isEnabled = false

                video_view.isVisible = false

                flash_toggle.isVisible = videoViewModel.state().isBackCameraToggled
                camera_toggle.isVisible = true

                recording_pause.setImageResource(step.recordImage)

                recording_title.text = step.startRecordingDescription

                val currentRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state().recordTimeSeconds)
                val maxRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state().maxRecordTimeSeconds)

                val recordTimeLabel =
                    "$currentRecordTime/$maxRecordTime"

                recording_time.text = recordTimeLabel

                recording_progress.max = videoViewModel.state().maxRecordTimeSeconds.toInt()
                recording_progress.progress = videoViewModel.state().recordTimeSeconds.toInt()

                recording_pause.setOnClickListener {

                    val file = createVideoFile()
                    startCoroutineAsync { videoViewModel.record(file.absolutePath) }
                    record(file)

                }

                review.isEnabled = videoViewModel.state().recordTimeSeconds > 0

                record_info.isVisible = true
                review_info.isVisible = false

            }
            RecordingState.Merged -> {

                recording_pause.isVisible = false
                review_pause.isEnabled = false
                flash_toggle.isVisible = false
                camera_toggle.isVisible = false

                review_loading.setVisibility(isVisible = true, opaque = false)

                mediaController.setMediaPlayer(video_view)
                mediaController.setAnchorView(video_view)
                video_view.setMediaController(mediaController)
                video_view.isVisible = true
                video_view.setVideoPath(getVideoMergeFilePath())
                video_view.setOnPreparedListener {
                    review_loading.setVisibility(isVisible = false, opaque = false)
                    it.isLooping = true
                    startCoroutineAsync { videoViewModel.reviewPause() }

                }
            }
            RecordingState.ReviewPause -> {

                recording_pause.isVisible = false
                review_pause.isEnabled = true
                flash_toggle.isVisible = false
                camera_toggle.isVisible = false
                video_view.isVisible = true
                camera.isVisible = false

                recording_title.text = step.startRecordingDescription

                val currentRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state().recordTimeSeconds)
                val maxRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state().maxRecordTimeSeconds)
                recording_title.text = step.startRecordingDescription

                val recordTimeLabel =
                    "$currentRecordTime/$maxRecordTime"

                review_time.text = recordTimeLabel


                mediaController.hide()
                mediaController.isVisible = false

                review_pause.setImageResource(step.playImage)
                review_pause.setOnClickListener {
                    startCoroutineAsync {
                        video_view.start()
                        videoViewModel.reviewPlay()
                    }
                }

                record_info.isVisible = false
                review_info.isVisible = true

            }
            RecordingState.Review -> {

                recording_pause.isVisible = false
                review_pause.isEnabled = true
                flash_toggle.isVisible = false
                camera_toggle.isVisible = false
                video_view.isVisible = true
                camera.isVisible = false

                recording_title.text = step.startRecordingDescription

                mediaController.isVisible = true
                mediaController.show()

                review_pause.setImageResource(step.pauseImage)
                review_pause.setOnClickListener {
                    startCoroutineAsync {
                        video_view.pause()
                        videoViewModel.reviewPause()
                    }
                }

                record_info.isVisible = false
                review_info.isVisible = false

            }
            RecordingState.Uploaded -> startCoroutineAsync { next() }
        }

    }

    private fun bindRecordingHeader(): Unit {

        when (videoViewModel.state().recordingState) {
            RecordingState.Recording -> {

                val currentRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state().recordTimeSeconds)
                val maxRecordTime =
                    DateUtils.formatElapsedTime(videoViewModel.state().maxRecordTimeSeconds)

                val recordTimeLabel =
                    "$currentRecordTime/$maxRecordTime"

                title.text = recordTimeLabel

            }
            RecordingState.RecordingPause ->
                title.text = videoViewModel.state().step.title
            RecordingState.Review ->
                title.text = videoViewModel.state().step.title
        }
    }

    // TODO: check if flash is available
    private fun bindFlash(isFlashEnabled: Boolean): Unit {

        startCoroutine { Either.catch { camera.enableTorch(isFlashEnabled) } }

        flash_toggle.setImageResource(
            if (isFlashEnabled) videoViewModel.state().step.flashOnImage
            else videoViewModel.state().step.flashOffImage
        )

    }

    // TODO: check if camera lens is available
    private fun bindCamera(isBackCameraToggled: Boolean): Unit {

        startCoroutine {

            Either.catch {
                camera.cameraLensFacing =
                    if (isBackCameraToggled) CameraSelector.LENS_FACING_BACK
                    else CameraSelector.LENS_FACING_FRONT
            }

            // enable flash button only for back camera
            flash_toggle.isVisible = isBackCameraToggled
        }

    }

    private fun record(file: File): Unit {

        camera.startRecording(
            file,
            ContextCompat.getMainExecutor(requireContext()),
            object : VideoCapture.OnVideoSavedCallback {

                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {

                    startCoroutineAsync { videoViewModel.pause() }

                }

                override fun onError(
                    videoCaptureError: Int,
                    message: String,
                    cause: Throwable?
                ) {

                    startCoroutineAsync { videoViewModel.handleRecordError() }

                }

            }
        )

    }

    private fun pause(): Unit {

        camera.stopRecording()

    }

    private suspend fun review(): Unit {


        val mergeDirectory = File(getVideoMergeDirectoryPath())

        if (mergeDirectory.exists().not())
            mergeDirectory.mkdir()

        videoViewModel.merge(
            getVideoDirectoryPath(),
            mergeDirectory.absolutePath,
            getVideoMergeFileName()
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