package jp.jig.glasses.sample.kmp.ui

/** グラスに送れる形に整えた画像。1画素1バイトのグレースケール */
data class GrayscaleImage(
    val width: Int,
    val height: Int,
    val pixels: ByteArray,
)

/** グラス側のバッファ上限。これを超えるサイズはファームが弾いて何も表示されない */
const val GLASS_IMAGE_MAX_SIZE = 196

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

/** 1bitずつ詰めた画像を、SDKが受け取る1画素1バイトへ戻す。MSBが左端 */
fun unpackBits(packed: ByteArray, pixelCount: Int): ByteArray {
    val pixels = ByteArray(pixelCount)
    pixels.indices.forEach { index ->
        val bit = (packed[index / 8].toInt() shr (7 - index % 8)) and 1
        pixels[index] = if (bit == 1) 0xFF.toByte() else 0
    }
    return pixels
}
