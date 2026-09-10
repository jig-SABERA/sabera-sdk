package jp.jig.glasses.sample.kmp.ui

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ImageFunctionsTest {
    @Test
    fun defaultTestPatternHasGlassDimensionsAndFullGrayscaleRange() {
        val image = testPatternImage()

        assertEquals(196, image.width)
        assertEquals(196, image.height)
        assertEquals(196 * 196, image.pixels.size)
        assertEquals(0, image.pixels[0].toInt() and 0xFF)
        assertEquals(255, image.pixels[195].toInt() and 0xFF)
    }

    @Test
    fun testPatternAlternatesGradientAcrossCheckerboardBlocks() {
        val image = testPatternImage(16)

        assertEquals(16, image.width)
        assertEquals(16, image.height)
        assertEquals(256, image.pixels.size)
        val firstRow = intArrayOf(0, 17, 221, 204, 68, 85, 153, 136, 136, 153, 85, 68, 204, 221, 17, 0)
        assertContentEquals(firstRow, image.pixels.take(16).map { it.toInt() and 0xFF }.toIntArray())
        assertContentEquals(firstRow, image.pixels.slice(16 until 32).map { it.toInt() and 0xFF }.toIntArray())
        assertContentEquals(
            intArrayOf(255, 238, 34, 51, 187, 170, 102, 119, 119, 102, 170, 187, 51, 34, 238, 255),
            image.pixels.slice(32 until 48).map { it.toInt() and 0xFF }.toIntArray(),
        )
    }

    @Test
    fun unpackBitsReadsMsbFirstAcrossBytesAndIgnoresPadding() {
        val pixels = unpackBits(byteArrayOf(0xA1.toByte(), 0x7F), pixelCount = 11)

        assertContentEquals(
            byteArrayOf(-1, 0, -1, 0, 0, 0, 0, -1, 0, -1, -1),
            pixels,
        )
    }

    @Test
    fun badAppleFrameDownsamplesPacked320By240UsingNearestSourcePixels() {
        val packed = ByteArray(320 * 240 / 8)
        // A 3x2 output samples columns 0, 106, 213 and rows 0, 120.
        packed[0] = 0xC0.toByte()
        packed[26] = 0x04
        packed[40] = 0xFF.toByte()
        packed[120 * 40 + 13] = 0x20

        val pixels = badAppleFrame(packed, width = 3, height = 2)

        assertContentEquals(byteArrayOf(-1, 0, -1, 0, -1, 0), pixels)
    }
}
