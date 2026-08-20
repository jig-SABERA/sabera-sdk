package jp.jig.glasses.sample.kmp.audio

import kotlin.math.floor

/**
 * PCM16 リトルエンディアン・モノラルのサンプルレート変換。
 * グラスのマイクは 16kHz で流れてくるが、リアルタイム音声 API は 24kHz を要求することが多い。
 *
 * 直線補間で、チャンクをまたぐ位置と直前のサンプルを持ち越す。
 * 1チャンクごとに作り直すと継ぎ目でプツプツ鳴るので、ストリーム1本につき1インスタンスを使い回す。
 * スレッドセーフではない。
 */
class PcmResampler(
    private val inputRate: Int,
    private val outputRate: Int,
) {
    /** 出力1サンプルぶんの、入力サンプル単位での進み幅 */
    private val step = inputRate.toDouble() / outputRate.toDouble()

    /** 次に出力したい位置。今回のチャンクの先頭を 0 とした入力サンプル単位 */
    private var position = 0.0

    /** 前のチャンクの末尾サンプル。position が負のとき補間の左端に使う */
    private var previous: Short = 0

    fun reset() {
        position = 0.0
        previous = 0
    }

    fun resample(pcm: ByteArray): ByteArray {
        if (inputRate == outputRate) return pcm
        val inputCount = pcm.size / 2
        if (inputCount == 0) return ByteArray(0)

        fun sampleAt(index: Int): Short =
            if (index < 0) previous else pcm.readSample(index)

        val output = ArrayList<Short>(((inputCount / step) + 2).toInt())
        while (position < inputCount - 1) {
            val index = floor(position).toInt()
            val fraction = position - index
            val left = sampleAt(index).toDouble()
            val right = sampleAt(index + 1).toDouble()
            output.add((left + (right - left) * fraction).toInt().toShort())
            position += step
        }

        previous = pcm.readSample(inputCount - 1)
        position -= inputCount

        val bytes = ByteArray(output.size * 2)
        output.forEachIndexed { i, sample -> bytes.writeSample(i, sample) }
        return bytes
    }
}

private fun ByteArray.readSample(index: Int): Short {
    val offset = index * 2
    return (((this[offset + 1].toInt() shl 8) or (this[offset].toInt() and 0xFF)).toShort())
}

private fun ByteArray.writeSample(index: Int, sample: Short) {
    val offset = index * 2
    this[offset] = (sample.toInt() and 0xFF).toByte()
    this[offset + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
}
