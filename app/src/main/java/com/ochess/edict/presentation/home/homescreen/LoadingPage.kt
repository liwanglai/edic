package com.ochess.edict.presentation.home.homescreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.math.MathUtils.lerp
import kotlin.math.withSign


/**
 * 摘自： https://juejin.cn/post/7322664566717349940
 */

@Composable
@Preview
fun LoadingPage(d: Double= 0.4) {
    val maxRadius =  15f
    val minRadius =  10f
    val startDistance =  0f
    val endDistance =  60f
    val openMiniThreshold =  0.25f

    val scrollPercent:Float =d.toFloat()
    val height =100.dp
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        //绘制大球
        drawCircle(
            Color.Gray,
            if (scrollPercent <= openMiniThreshold / 3) lerp(
                0f,
                maxRadius,
                scrollPercent / (openMiniThreshold / 3)
            ) else lerp(
                maxRadius,
                minRadius,
                (scrollPercent.minus(openMiniThreshold / 3)) / ((openMiniThreshold.minus(
                    openMiniThreshold / 3
                )))
            )
        )
        //绘制两个小球
        if (scrollPercent > openMiniThreshold / 3) {
            repeat(2) {
                translate(
                    left = lerp(
                        startDistance,
                        endDistance,
                         (scrollPercent.minus(openMiniThreshold / 3)) / ((openMiniThreshold.minus(
                            openMiniThreshold / 3
                        )))
                    ).withSign((if (it == 0) 1 else -1))
                ) {
                    drawCircle(Color.Gray, minRadius)
                }
            }
        }
    }
}
