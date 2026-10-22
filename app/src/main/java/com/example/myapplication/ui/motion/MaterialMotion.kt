package com.example.myapplication.ui.motion
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
object MaterialMotion {
    private val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0f, 1.0f)
    private val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    private val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    private const val DURATION_ENTER_MS = 400
    private const val DURATION_EXIT_MS = 400
    private const val DURATION_FADE_IN_MS = 200
    private const val DURATION_FADE_OUT_MS = 100
    private const val FADE_THROUGH_ENTER_DURATION = 300
    private const val FADE_THROUGH_ENTER_DELAY = 150
    private const val FADE_THROUGH_EXIT_DURATION = 150
    fun sharedAxisXEnter(forward: Boolean): EnterTransition {
        return fadeIn(
            animationSpec = tween(
                durationMillis = DURATION_ENTER_MS / 2,  
                delayMillis = DURATION_EXIT_MS / 2,      
                easing = LinearEasing
            )
        )
    }
    fun sharedAxisXExit(forward: Boolean): ExitTransition {
        return fadeOut(
            animationSpec = tween(
                durationMillis = DURATION_EXIT_MS / 2,  
                easing = LinearEasing
            )
        )
    }
    fun sharedAxisYEnter(forward: Boolean): EnterTransition {
        return slideInVertically(
            initialOffsetY = { fullHeight -> if (forward) fullHeight / 3 else -fullHeight / 3 },
            animationSpec = tween(
                durationMillis = DURATION_ENTER_MS,
                easing = EmphasizedDecelerateEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = DURATION_FADE_IN_MS,
                delayMillis = DURATION_FADE_OUT_MS,
                easing = LinearEasing
            )
        )
    }
    fun sharedAxisYExit(forward: Boolean): ExitTransition {
        return slideOutVertically(
            targetOffsetY = { fullHeight -> if (forward) -fullHeight / 3 else fullHeight / 3 },
            animationSpec = tween(
                durationMillis = DURATION_EXIT_MS,
                easing = EmphasizedAccelerateEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = DURATION_FADE_OUT_MS,
                easing = LinearEasing
            )
        )
    }
    fun sharedAxisZEnter(forward: Boolean): EnterTransition {
        return scaleIn(
            initialScale = if (forward) 0.9f else 1.1f,
            animationSpec = tween(
                durationMillis = DURATION_ENTER_MS,
                easing = EmphasizedDecelerateEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = DURATION_FADE_IN_MS,
                delayMillis = DURATION_FADE_OUT_MS,
                easing = LinearEasing
            )
        )
    }
    fun sharedAxisZExit(forward: Boolean): ExitTransition {
        return scaleOut(
            targetScale = if (forward) 1.1f else 0.9f,
            animationSpec = tween(
                durationMillis = DURATION_EXIT_MS,
                easing = EmphasizedAccelerateEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = DURATION_FADE_OUT_MS,
                easing = LinearEasing
            )
        )
    }
    val fadeThroughEnter: EnterTransition = fadeIn(
        animationSpec = tween(
            durationMillis = FADE_THROUGH_ENTER_DURATION,
            delayMillis = FADE_THROUGH_ENTER_DELAY,
            easing = EmphasizedDecelerateEasing
        )
    )
    val fadeThroughExit: ExitTransition = fadeOut(
        animationSpec = tween(
            durationMillis = FADE_THROUGH_EXIT_DURATION,
            easing = EmphasizedAccelerateEasing
        )
    )
    val containerTransformEnter: EnterTransition = scaleIn(
        initialScale = 0.9f,
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MS,
            easing = EmphasizedDecelerateEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = DURATION_FADE_IN_MS,
            delayMillis = DURATION_FADE_OUT_MS,
            easing = LinearEasing
        )
    )
    val containerTransformExit: ExitTransition = scaleOut(
        targetScale = 0.9f,
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MS,
            easing = EmphasizedAccelerateEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = DURATION_FADE_OUT_MS,
            easing = LinearEasing
        )
    )
    val fadeEnter: EnterTransition = fadeIn(
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MS,
            easing = LinearEasing
        )
    )
    val fadeExit: ExitTransition = fadeOut(
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MS,
            easing = LinearEasing
        )
    )
}