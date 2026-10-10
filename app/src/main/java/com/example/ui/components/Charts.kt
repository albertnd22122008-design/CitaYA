package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

/**
 * Line Chart for Dashboard Cash Flow matching Screen 2 in the user's reference image
 */
@Composable
fun CashFlowLineChart(
    modifier: Modifier = Modifier,
    dataPoints: List<Float> = listOf(0.15f, 0.22f, 0.18f, 0.45f, 0.88f, 0.52f, 0.35f, 0.40f),
    labels: List<String> = listOf("feb", "mar", "abr", "jun", "dic")
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800),
        label = "lineChartAnim"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            val w = size.width
            val h = size.height
            val paddingBottom = 20f

            // Background horizontal grid lines
            val gridColor = Color(0xFFF0EFEA)
            drawLine(gridColor, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 1.5f)
            drawLine(gridColor, Offset(0f, h * 0.55f), Offset(w, h * 0.55f), strokeWidth = 1.5f)
            drawLine(gridColor, Offset(0f, h - paddingBottom), Offset(w, h - paddingBottom), strokeWidth = 2f)

            if (dataPoints.size < 2) return@Canvas

            val stepX = w / (dataPoints.size - 1)
            val path = Path()
            val fillPath = Path()

            val points = dataPoints.mapIndexed { index, value ->
                val x = index * stepX
                val y = (h - paddingBottom) - (value * (h - paddingBottom - 15f) * animatedProgress)
                Offset(x, y)
            }

            path.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, h - paddingBottom)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX = (p0.x + p1.x) / 2f
                path.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }

            fillPath.lineTo(points.last().x, h - paddingBottom)
            fillPath.close()

            // Draw Area Gradient Fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CobaltBlue.copy(alpha = 0.35f),
                        CobaltBlue.copy(alpha = 0.02f)
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Draw Smooth Line Stroke
            drawPath(
                path = path,
                color = CobaltBlue,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Peak indicator dot
            val peakPoint = points[4] // June peak
            drawCircle(
                color = CobaltBlue,
                radius = 5.dp.toPx(),
                center = peakPoint
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = peakPoint
            )
        }

        // X-Axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Semi-circular radial occupancy gauge matching Screen 4 in the user's reference image
 */
@Composable
fun OccupancyRadialGauge(
    percentage: Int = 73,
    modifier: Modifier = Modifier
) {
    val animatedPercent by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 900),
        label = "gaugeAnim"
    )

    Box(
        modifier = modifier.size(100.dp, 68.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(100.dp, 68.dp)) {
            val strokeWidth = 11.dp.toPx()
            val diameter = size.width - strokeWidth
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Track Background Arc (180 degrees)
            drawArc(
                color = Color(0xFFE6E7EE),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress Arc in Cobalt Blue
            drawArc(
                color = CobaltBlue,
                startAngle = 180f,
                sweepAngle = 180f * animatedPercent,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$percentage%",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

/**
 * Appointment Trend Bar Chart matching Screen 4 in user's reference image
 */
@Composable
fun AppointmentsBarChart(
    modifier: Modifier = Modifier,
    values: List<Float> = listOf(0.2f, 0.65f, 0.45f, 0.8f, 0.6f, 0.95f, 0.4f, 0.85f, 0.3f, 0.15f, 0.1f)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 750),
        label = "barChartAnim"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        val w = size.width
        val h = size.height
        val barCount = values.size
        val barWidth = 13.dp.toPx()
        val spacing = (w - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

        values.forEachIndexed { index, fraction ->
            val barHeight = (fraction * h * 0.9f * animatedProgress).coerceAtLeast(4f)
            val x = index * (barWidth + spacing)
            val y = h - barHeight

            drawRoundRect(
                color = SunsetOrange,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )
        }
    }
}
