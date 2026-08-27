package jp.jig.glasses.sample.kmp.ui

import android.content.Context

/** 同梱データの寸法。ここから間引いて小さいコマを作る */
const val BAD_APPLE_WIDTH = 320
const val BAD_APPLE_HEIGHT = 240

private const val BAD_APPLE_PIXELS = BAD_APPLE_WIDTH * BAD_APPLE_HEIGHT
private const val PACKED_FRAME_BYTES = BAD_APPLE_PIXELS / 8
private const val BAD_APPLE_ASSET = "badapple320.bin"

/** 1画素1bitに詰めたコマの列。1コマ $PACKED_FRAME_BYTES バイトで並んでいる */
fun loadBadAppleFrames(context: Context): List<ByteArray> {
    val bytes = context.assets.open(BAD_APPLE_ASSET).use { it.readBytes() }
    return (0 until bytes.size / PACKED_FRAME_BYTES).map { index ->
        bytes.copyOfRange(index * PACKED_FRAME_BYTES, (index + 1) * PACKED_FRAME_BYTES)
    }
}

/** 1コマを SDK が受け取る1画素1バイトへ戻し、指定の寸法へ縮める */
fun badAppleFrame(packed: ByteArray, width: Int, height: Int): ByteArray {
    val source = unpackBits(packed, BAD_APPLE_PIXELS)
    if (width == BAD_APPLE_WIDTH && height == BAD_APPLE_HEIGHT) return source
    return shrink(source, BAD_APPLE_WIDTH, BAD_APPLE_HEIGHT, width, height)
}

/** 間引きだけの縮小。2値なので補間すると中間の階調が出て輪郭が甘くなる */
private fun shrink(source: ByteArray, srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int): ByteArray {
    val out = ByteArray(dstWidth * dstHeight)
    for (y in 0 until dstHeight) {
        val srcRow = y * srcHeight / dstHeight * srcWidth
        for (x in 0 until dstWidth) {
            out[y * dstWidth + x] = source[srcRow + x * srcWidth / dstWidth]
        }
    }
    return out
}
