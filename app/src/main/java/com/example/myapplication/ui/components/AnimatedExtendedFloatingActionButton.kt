package com.example.myapplication.ui.components
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
@Stable
private class FabAnimationState(expanded: Boolean) {
    val width = Animatable(if (expanded) 168f else 56f)
    val textAlpha = Animatable(if (expanded) 1f else 0f)
    val textWidth = Animatable(if (expanded) 120f else 0f)
    suspend fun animateTo(
        expanded: Boolean,
        springSpec: androidx.compose.animation.core.AnimationSpec<Float>
    ) {
        kotlinx.coroutines.coroutineScope {
            launch { width.animateTo(if (expanded) 168f else 56f, springSpec) }
            launch { textAlpha.animateTo(if (expanded) 1f else 0f, springSpec) }
            launch { textWidth.animateTo(if (expanded) 120f else 0f, springSpec) }
        }
    }
}
private val FabSpringSpec = spring<Float>(
    stiffness = Spring.StiffnessMediumLow,
    dampingRatio = Spring.DampingRatioMediumBouncy
)
private fun Modifier.animatedWidth(animatable: Animatable<Float, *>) = this.then(
    Modifier.layout { measurable, constraints ->
        val widthPx = animatable.value.dp.roundToPx().coerceAtLeast(0)
        val targetConstraints = constraints.constrain(
            Constraints(
                minWidth = widthPx,
                maxWidth = widthPx,
                minHeight = constraints.minHeight,
                maxHeight = constraints.maxHeight
            )
        )
        val placeable = measurable.measure(targetConstraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
)
@Composable
fun AnimatedExtendedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation()
) {
    val animationState = remember { FabAnimationState(expanded) }
    LaunchedEffect(expanded) {
        animationState.animateTo(expanded, FabSpringSpec)
    }
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .animatedWidth(animationState.width) 
            .height(56.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                Box(
                    modifier = Modifier
                        .animatedWidth(animationState.textWidth)
                        .height(24.dp)
                        .graphicsLayer {
                            alpha = animationState.textAlpha.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    text()
                }
            }
        }
    }
}