package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryDarkTeal
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SurfaceBackground

/**
 * Bindora Savings Progress Bar component.
 *
 * Styled explicitly with Bindora's brand colors:
 * - Primary Dark Teal (#064E46)
 * - Primary Green (#16A34A)
 * - Accent Green (#65C466)
 *
 * Features:
 * - Smooth spring/tween gradient animation
 * - Milestone indicator ticks (25%, 50%, 75%)
 * - Rounded pill design with subtle mint track background
 * - Accessible content description and testTag
 */
@Composable
fun BindoraGoalProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    showMilestones: Boolean = true,
    animate: Boolean = true,
    testTag: String = "bindora_goal_progress_bar"
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = if (animate) {
            tween(durationMillis = 650, easing = FastOutSlowInEasing)
        } else {
            tween(durationMillis = 0)
        },
        label = "bindora_goal_progress_anim"
    )

    // The signature Bindora teal-to-green gradient
    val bindoraGradient = Brush.horizontalGradient(
        colors = listOf(
            PrimaryDarkTeal, // #064E46
            PrimaryGreen,    // #16A34A
            AccentGreen      // #65C466
        )
    )

    val percentInt = (clampedProgress * 100).toInt()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics { contentDescription = "Savings goal progress: $percentInt percent" }
            .testTag(testTag)
    ) {
        val fullWidth = maxWidth

        // 1. Background Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(percent = 50))
                .background(SoftMint.copy(alpha = 0.55f))
                .border(
                    width = 0.75.dp,
                    color = SubtleBorder,
                    shape = RoundedCornerShape(percent = 50)
                )
        )

        // 2. Animated Gradient Progress Fill
        if (animatedProgress > 0.001f) {
            Box(
                modifier = Modifier
                    .width(fullWidth * animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(percent = 50))
                    .background(bindoraGradient)
            )
        }

        // 3. Milestone Markers (25%, 50%, 75%)
        if (showMilestones && height >= 8.dp) {
            listOf(0.25f, 0.50f, 0.75f).forEach { markerRatio ->
                val isReached = animatedProgress >= markerRatio
                val markerX = (fullWidth * markerRatio) - 1.dp

                Box(
                    modifier = Modifier
                        .offset(x = markerX)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(
                            if (isReached) Color.White.copy(alpha = 0.85f)
                            else PrimaryDark.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

/**
 * Bindora Goal Progress Header widget displaying:
 * - Current saved vs Target in formatted ₹
 * - Percentage badge with Bindora accent
 * - BindoraGoalProgressBar
 * - Remaining amount & days countdown
 */
@Composable
fun BindoraGoalProgressCardHeader(
    savedAmount: Double,
    targetAmount: Double,
    remainingDays: Long,
    categoryName: String,
    modifier: Modifier = Modifier
) {
    val progress = if (targetAmount > 0) (savedAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val percent = if (targetAmount > 0) ((savedAmount / targetAmount) * 100).toInt() else 0
    val remainingAmount = (targetAmount - savedAmount).coerceAtLeast(0.0)
    val isCompleted = savedAmount >= targetAmount

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftMint)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = categoryName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                if (remainingDays > 0 && !isCompleted) {
                    Text(
                        text = "• $remainingDays days left",
                        fontSize = 11.sp,
                        color = SecondaryText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Percentage Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isCompleted) PrimaryGreen
                        else SoftMint
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (isCompleted) "100% COMPLETE 🎉" else "$percent% SAVED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color.White else PrimaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large Amount Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = formatRupee(savedAmount),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = "saved of ${formatRupee(targetAmount)} goal",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            if (!isCompleted) {
                Text(
                    text = "${formatRupee(remainingAmount)} to go",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrand
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // The Signature Bindora Progress Bar
        BindoraGoalProgressBar(
            progress = progress,
            height = 10.dp,
            showMilestones = true,
            modifier = Modifier.testTag("goal_card_progress_bar")
        )
    }
}
