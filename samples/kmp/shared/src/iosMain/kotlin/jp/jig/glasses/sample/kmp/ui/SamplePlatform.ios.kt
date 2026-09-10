@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlin.experimental.ExperimentalObjCName::class)

package jp.jig.glasses.sample.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import app.jigglass.glass.PlatformContext
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontEdging
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.Rect
import org.jetbrains.skia.impl.use
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy
import kotlin.native.ObjCName

/** Complete every request exactly once, including cancellation via onError. */
@ObjCName("SampleMediaPicker", swiftName = "SampleMediaPicker", exact = true)
interface SampleMediaPicker {
    fun pickImage(maxSize: Int, onSuccess: (GrayscaleImage) -> Unit, onError: (String) -> Unit)

    fun pickVideo(invert: Boolean, onSuccess: (List<String>) -> Unit, onError: (String) -> Unit)
}

@ObjCName("SampleMediaPickerHolder", swiftName = "SampleMediaPickerHolder", exact = true)
object SampleMediaPickerHolder {
    var picker: SampleMediaPicker? = null
}

private object IosSamplePlatformContext : PlatformContext()

@Composable
actual fun samplePlatformContext(): PlatformContext = IosSamplePlatformContext

actual fun readSampleAsset(name: String): ByteArray {
    val path = NSBundle.mainBundle.pathForResource(name, ofType = null)
        ?: error("Sample asset not found: $name")
    val data = NSData.dataWithContentsOfFile(path) ?: error("Could not read sample asset: $name")
    require(data.length <= Int.MAX_VALUE.toULong()) { "Sample asset is too large: $name" }
    return ByteArray(data.length.toInt()).also { bytes ->
        if (bytes.isNotEmpty()) {
            bytes.usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
        }
    }
}

@Composable
actual fun rememberImagePicker(maxSize: Int, onResult: (Result<GrayscaleImage>) -> Unit): () -> Unit {
    val currentOnResult by rememberUpdatedState(onResult)
    val currentMaxSize by rememberUpdatedState(maxSize)
    val scope = rememberCoroutineScope()
    return remember(scope) {
        {
            val picker = SampleMediaPickerHolder.picker
            if (picker == null) {
                currentOnResult(Result.failure(IllegalStateException("SampleMediaPicker is not registered")))
            } else {
                picker.pickImage(
                    maxSize = currentMaxSize,
                    onSuccess = { image ->
                        scope.launch { currentOnResult(Result.success(image)) }
                        Unit
                    },
                    onError = { message ->
                        scope.launch {
                            val error = IllegalStateException(message.ifBlank { "Image selection failed or was cancelled" })
                            currentOnResult(Result.failure(error))
                        }
                        Unit
                    },
                )
            }
        }
    }
}

@Composable
actual fun rememberVideoPicker(invert: Boolean, onResult: (Result<List<String>>) -> Unit): () -> Unit {
    val currentOnResult by rememberUpdatedState(onResult)
    val currentInvert by rememberUpdatedState(invert)
    val scope = rememberCoroutineScope()
    return remember(scope) {
        {
            val picker = SampleMediaPickerHolder.picker
            if (picker == null) {
                currentOnResult(Result.failure(IllegalStateException("SampleMediaPicker is not registered")))
            } else {
                picker.pickVideo(
                    invert = currentInvert,
                    onSuccess = { frames ->
                        scope.launch {
                            currentOnResult(
                                if (frames.isEmpty()) {
                                    Result.failure(IllegalStateException("Could not decode any video frames"))
                                } else {
                                    Result.success(frames)
                                },
                            )
                        }
                        Unit
                    },
                    onError = { message ->
                        scope.launch {
                            val error = IllegalStateException(message.ifBlank { "Video selection failed or was cancelled" })
                            currentOnResult(Result.failure(error))
                        }
                        Unit
                    },
                )
            }
        }
    }
}

actual fun GrayscaleImage.toImageBitmap(): ImageBitmap {
    val rgba = ByteArray(width * height * 4) { index ->
        if (index % 4 == 3) 0xFF.toByte() else pixels[index / 4]
    }
    return Image.makeRaster(
        ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.OPAQUE),
        rgba,
        width * 4,
    ).toComposeImageBitmap()
}

actual fun renderLauncherIcon(glyph: String, focused: Boolean, size: Int): GrayscaleImage {
    val typeface = checkNotNull(
        FontMgr.default.matchFamilyStyleCharacter(null, FontStyle.NORMAL, arrayOf("ja"), glyph.first().code),
    ) { "No font available for launcher glyph: $glyph" }
    typeface.use {
        Font(typeface, size * 0.55f).use { font ->
            font.edging = FontEdging.ALIAS
            Bitmap().use { bitmap ->
                check(bitmap.allocN32Pixels(size, size)) { "Could not allocate launcher icon pixels" }
                Canvas(bitmap).use { canvas ->
                    val ink = if (focused) Color.BLACK else Color.WHITE
                    canvas.clear(if (focused) Color.WHITE else Color.BLACK)
                    Paint().use { paint ->
                        paint.color = ink
                        paint.isAntiAlias = false
                        paint.mode = PaintMode.STROKE
                        paint.strokeWidth = if (focused) 8f else 3f
                        val inset = paint.strokeWidth / 2
                        canvas.drawRect(Rect.makeLTRB(inset, inset, size - inset, size - inset), paint)
                        paint.mode = PaintMode.FILL
                        val x = (size - font.measureTextWidth(glyph)) / 2f
                        val y = size / 2f - (font.metrics.descent + font.metrics.ascent) / 2f
                        canvas.drawString(glyph, x, y, font, paint)
                    }
                }
                val pixels = ByteArray(size * size) { index ->
                    val red = (bitmap.getColor(index % size, index / size) shr 16) and 0xFF
                    if (red >= 128) 0xFF.toByte() else 0x00
                }
                return GrayscaleImage(size, size, pixels)
            }
        }
    }
}
