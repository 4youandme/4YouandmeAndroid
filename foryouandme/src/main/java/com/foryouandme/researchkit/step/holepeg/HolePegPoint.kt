package com.foryouandme.researchkit.step.holepeg

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.foryouandme.entity.task.holepeg.HolePegAttempt
import com.foryouandme.entity.task.holepeg.HolePegPointPosition
import com.foryouandme.entity.task.holepeg.HolePegSubStep
import com.foryouandme.entity.task.holepeg.HolePegTargetPosition
import java.util.*

@Composable
fun HolePegPoint(
    attempt: HolePegAttempt,
    pointColor: Color = Color.Black,
    targetColor: Color = Color.Black,
    pointSize: Dp = 90.dp,
    pointPadding: Dp = 30.dp,
    onDragStart: () -> Unit = { },
    onDrag: (Float) -> Unit = { }, // the distance of the drag in pixels
    onDragEnd: (Boolean) -> Unit = { }, // user has reach the target
) {

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val rememberKey = remember(
            attempt.id,
            attempt.step.start,
            attempt.step.target,
            attempt.errorCount,
            attempt.pegs.size,
            maxWidth,
            pointSize,
            pointPadding
        ) { UUID.randomUUID().toString() }

        var startOffset by remember(rememberKey) {
            mutableStateOf(
                getOffsetByPoint(
                    attempt.step.start,
                    maxWidth,
                    pointSize,
                    pointPadding
                )
            )
        }

        val targetOffset by remember(rememberKey) {
            mutableStateOf(
                getOffsetByTarget(
                    attempt.step.target,
                    maxWidth,
                    pointSize,
                    pointPadding
                )
            )
        }


        var alpha by remember { mutableStateOf(1f) }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .offset(startOffset.x, startOffset.y)

        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier =
                Modifier
                    .size((pointSize + 100.dp), (pointSize + 200.dp))
                    .pointerInput(rememberKey) {
                        detectGrabGestures(
                            maxDistanceBetweenFinger = pointSize * 3f,
                            onDragStart = {
                                onDragStart()
                                alpha = 0.5f
                            },
                            onDragEnd = {
                                onDragEnd(
                                    isTargetReached(
                                        startOffset,
                                        targetOffset,
                                        attempt.step.target,
                                        pointSize
                                    )
                                )
                                alpha = 1f
                            },
                            onDragCancel = {
                                onDragEnd(
                                    isTargetReached(
                                        startOffset,
                                        targetOffset,
                                        attempt.step.target,
                                        pointSize
                                    )
                                )
                                alpha = 1f
                            }
                        ) { change, dragAmount ->
                            change.consumeAllChanges()

                            val offsetX = startOffset.x + dragAmount.x.toDp()
                            val offsetY = startOffset.y + dragAmount.y.toDp()

                            val distance = getDistance(dragAmount)
                            onDrag(distance)

                            startOffset = DpOffset(offsetX, offsetY)

                            alpha =
                                if (
                                    isTargetReached(
                                        startOffset,
                                        targetOffset,
                                        attempt.step.target,
                                        pointSize
                                    )
                                ) 1f
                                else 0.5f
                        }
                    }
                    .alpha(alpha)

            ) {
                Box(
                    modifier = Modifier
                        .size(pointSize)
                        .clip(CircleShape)
                        .background(pointColor)
                )
            }
        }

        // Target Point
        HolePegTarget(attempt.step.target, targetColor, targetOffset, pointSize)

    }

}

@Preview
@Composable
private fun HolePegPointPreview() {
    HolePegPoint(
        attempt =
        HolePegAttempt(
            0,
            HolePegSubStep(
                HolePegPointPosition.End,
                HolePegTargetPosition.StartCenter,
            ),
            0
        )
    )
}