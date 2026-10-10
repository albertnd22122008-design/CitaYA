package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.TextDark

@Composable
fun CitaYaLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.width * 0.13f
        val w = this.size.width
        val h = this.size.height

        // Outer Orange Arc ("C" and Checkmark)
        drawArc(
            color = SunsetOrange,
            startAngle = 140f,
            sweepAngle = 260f,
            useCenter = false,
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size = Size(w * 0.76f, h * 0.76f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Orange Checkmark stroke extending out
        val checkPathOrange = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.38f, h * 0.52f)
            lineTo(w * 0.52f, h * 0.66f)
            lineTo(w * 0.88f, h * 0.22f)
        }
        drawPath(
            path = checkPathOrange,
            color = SunsetOrange,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Intertwined Cobalt Blue Loop ("Y" bottom support)
        drawArc(
            color = CobaltBlue,
            startAngle = 20f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.24f, h * 0.38f),
            size = Size(w * 0.56f, h * 0.52f),
            style = Stroke(width = strokeWidth * 0.95f, cap = StrokeCap.Round)
        )

        // Blue inner diagonal
        drawLine(
            color = CobaltBlue,
            start = Offset(w * 0.40f, h * 0.42f),
            end = Offset(w * 0.56f, h * 0.64f),
            strokeWidth = strokeWidth * 0.95f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun CitaYaBrandHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 26.dp,
    textColor: Color = TextDark,
    fontSize: Int = 22
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CitaYaLogoMark(size = logoSize)
        Text(
            text = "citaya",
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = (-0.5).sp
        )
    }
}
