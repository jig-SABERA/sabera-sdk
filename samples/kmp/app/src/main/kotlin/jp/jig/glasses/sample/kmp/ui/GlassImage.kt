package jp.jig.glasses.sample.kmp.ui

import android.graphics.Bitmap
import android.graphics.Color

/** グラスに送れる形に整えた画像。1画素1バイトのグレースケール */
data class GrayscaleImage(
    val width: Int,
    val height: Int,
    val pixels: ByteArray,
) {
    /** プレビュー表示用に Bitmap へ戻す */
    fun toBitmap(): Bitmap {
        val colors = IntArray(width * height) { index ->
            val value = pixels[index].toInt() and 0xFF
            Color.rgb(value, value, value)
        }
        return Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888)
    }
}

/** グラス側のバッファ上限。これを超えるサイズはファームが弾いて何も表示されない */
const val GLASS_IMAGE_MAX_SIZE = 196

/**
 * グラスに送れるサイズ・形式へ整える。
 * 縦横比は保ったまま [GLASS_IMAGE_MAX_SIZE] に収まるよう縮小し、輝度だけを取り出す。
 */
fun Bitmap.toGlassGrayscale(maxSize: Int = GLASS_IMAGE_MAX_SIZE): GrayscaleImage {
    val scale = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height, 1f)
    val scaledWidth = (width * scale).toInt().coerceAtLeast(1)
    val scaledHeight = (height * scale).toInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, true)

    val colors = IntArray(scaledWidth * scaledHeight)
    scaled.getPixels(colors, 0, scaledWidth, 0, 0, scaledWidth, scaledHeight)

    val pixels = ByteArray(colors.size) { index ->
        val color = colors[index]
        val luminance = 0.299 * Color.red(color) +
            0.587 * Color.green(color) +
            0.114 * Color.blue(color)
        luminance.toInt().coerceIn(0, 255).toByte()
    }
    return GrayscaleImage(scaledWidth, scaledHeight, pixels)
}

/** 画像を選ばずに送信を試せるテストパターン。左右のグラデーションに市松模様を重ねる */
fun testPatternImage(size: Int = GLASS_IMAGE_MAX_SIZE): GrayscaleImage {
    val block = size / 8
    val pixels = ByteArray(size * size) { index ->
        val x = index % size
        val y = index / size
        val gradient = x * 255 / (size - 1)
        val inverted = (x / block + y / block) % 2 == 0
        (if (inverted) gradient else 255 - gradient).toByte()
    }
    return GrayscaleImage(size, size, pixels)
}

/** 並べたときにどの画像がどこに出たか分かるよう、縞の太さを変えたパターン */
fun stripePatternImage(size: Int, stripeWidth: Int): GrayscaleImage {
    val pixels = ByteArray(size * size) { index ->
        val x = index % size
        val y = index / size
        // 外周を枠にして、画像の境目を見えるようにする
        val onBorder = x < 2 || y < 2 || x >= size - 2 || y >= size - 2
        when {
            onBorder -> 0xFF.toByte()
            (x / stripeWidth) % 2 == 0 -> 0xC0.toByte()
            else -> 0x20.toByte()
        }
    }
    return GrayscaleImage(size, size, pixels)
}
