package com.example.my_rouge_mobile.ui.render

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import kotlin.math.ceil
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Окно просмотра: какая часть карты видна на экране.
 * Размер клетки фиксирован (в пикселях), число видимых клеток зависит от размера Canvas,
 * поэтому одинаково работает в портретном и альбомном режиме.
 */
class Camera(
    val left: Int,
    val top: Int,
    val tilePx: Float,
    val cols: Int,
    val rows: Int
) {
    fun screenX(x: Int): Float = (x - left) * tilePx
    fun screenY(y: Int): Float = (y - top) * tilePx
    fun isVisible(x: Int, y: Int): Boolean =
        x >= left && x < left + cols && y >= top && y < top + rows

    companion object {
        /**
         * Камера, центрированная на игроке и не выходящая за границы карты.
         *
         * insets* - зоны экрана, закрытые кнопками управления (в пикселях). Камера может заехать за
         * край карты ровно на эту величину, чтобы край карты оказывался рядом с кнопками,
         * а не под ними. Без отступов (0) поведение обычное: карта упирается в край экрана.
         */


        fun centeredOn(
            playerX: Int, playerY: Int,
            mapWidth: Int, mapHeight: Int,
            canvasWidthPx: Float, canvasHeightPx: Float,
            tilePx: Float,
            insetLeftPx: Float = 0f, insetTopPx: Float = 0f,
            insetRightPx: Float = 0f, insetBottomPx: Float = 0f
        ): Camera {
            val tile = tilePx.coerceAtLeast(1f)
            val cols = ceil(canvasWidthPx / tile).toInt().coerceAtLeast(1)
            val rows = ceil(canvasHeightPx / tile).toInt().coerceAtLeast(1)

            val padLeft = ceil(insetLeftPx / tile).toInt()
            val padTop = ceil(insetTopPx / tile).toInt()
            val padRight = ceil(insetRightPx / tile).toInt()
            val padBottom = ceil(insetBottomPx / tile).toInt()

            // 1. Вычисляем "идеальную" позицию камеры, чтобы игрок был ровно по центру
            val idealLeft = playerX - cols / 2
            val idealTop = playerY - rows / 2

            // 2. Вычисляем границы, насколько камера может сдвинуться за край карты
            // Если карта больше экрана, камера может уехать в минус на размер кнопок.
            // Если карта меньше экрана, мы центрируем её.
            val minLeft = -padLeft
            val maxLeft = maxOf(minLeft, mapWidth - cols + padRight)

            val minTop = -padTop
            val maxTop = maxOf(minTop, mapHeight - rows + padBottom)

            // 3. Применяем ограничения
            val left = if (mapWidth <= cols) {
                // Если карта уже экрана, просто центрируем её
                (mapWidth - cols) / 2
            } else {
                idealLeft.coerceIn(minLeft, maxLeft)
            }

            val top = if (mapHeight <= rows) {
                (mapHeight - rows) / 2
            } else {
                idealTop.coerceIn(minTop, maxTop)
            }

            return Camera(left, top, tile, cols, rows)
        }

    }
}

/** Всё, что нужно рендерерам за один кадр. */
class RenderContext(
    val cam: Camera,
    val measurer: TextMeasurer,
    val baseStyle: TextStyle,
//    val playerImage: ImageBitmap? = null
)