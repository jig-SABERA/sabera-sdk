package jp.jig.glasses.sample.kmp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import app.jigglass.glass.PlatformContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SampleAssets {
    private var context: Context? = null

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    internal fun read(name: String): ByteArray =
        checkNotNull(context) { "Call SampleAssets.initialize(context) before reading sample assets" }
            .assets.open(name).use { it.readBytes() }
}

@Composable
actual fun samplePlatformContext(): PlatformContext = LocalContext.current

actual fun readSampleAsset(name: String): ByteArray = SampleAssets.read(name)

@Composable
actual fun rememberImagePicker(maxSize: Int, onResult: (Result<GrayscaleImage>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    val currentMaxSize by rememberUpdatedState(maxSize)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) {
            currentOnResult(Result.failure(IllegalStateException("Image selection cancelled")))
            return@rememberLauncherForActivityResult
        }
        val requestedSize = currentMaxSize
        scope.launch {
            val result = try {
                Result.success(withContext(Dispatchers.IO) {
                    val bitmap = context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    } ?: error("Could not decode the selected image")
                    try {
                        bitmap.toGlassGrayscale(requestedSize)
                    } finally {
                        bitmap.recycle()
                    }
                })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
            currentOnResult(result)
        }
    }
    return remember(launcher) {
        {
            try {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } catch (e: Exception) {
                currentOnResult(Result.failure(e))
            }
        }
    }
}

@Composable
actual fun rememberVideoPicker(invert: Boolean, onResult: (Result<List<String>>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    val currentInvert by rememberUpdatedState(invert)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) {
            currentOnResult(Result.failure(IllegalStateException("Video selection cancelled")))
            return@rememberLauncherForActivityResult
        }
        val requestedInvert = currentInvert
        scope.launch {
            val result = try {
                Result.success(decodeAsciiFrames(context, uri, requestedInvert))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
            currentOnResult(result)
        }
    }
    return remember(launcher) {
        {
            try {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            } catch (e: Exception) {
                currentOnResult(Result.failure(e))
            }
        }
    }
}

actual fun GrayscaleImage.toImageBitmap(): ImageBitmap {
    val colors = IntArray(width * height) { index ->
        val value = pixels[index].toInt() and 0xFF
        Color.rgb(value, value, value)
    }
    return Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
}

private fun Bitmap.toGlassGrayscale(maxSize: Int): GrayscaleImage {
    require(maxSize > 0) { "Image size must be positive" }
    val scale = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height, 1f)
    val scaledWidth = (width * scale).toInt().coerceAtLeast(1)
    val scaledHeight = (height * scale).toInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, true)
    try {
        val colors = IntArray(scaledWidth * scaledHeight)
        scaled.getPixels(colors, 0, scaledWidth, 0, 0, scaledWidth, scaledHeight)
        val pixels = ByteArray(colors.size) { index ->
            val color = colors[index]
            val luminance = 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)
            luminance.toInt().coerceIn(0, 255).toByte()
        }
        return GrayscaleImage(scaledWidth, scaledHeight, pixels)
    } finally {
        if (scaled !== this) scaled.recycle()
    }
}

private suspend fun decodeAsciiFrames(context: Context, uri: Uri, invert: Boolean): List<String> =
    withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: error("Could not read the video duration")
            require(durationMs > 0) { "The selected video is empty" }
            val count = (durationMs / 100L).coerceIn(1L, 200L).toInt()
            val ramp = " .:-=+*#%@"
            val frames = (0 until count).mapNotNull { index ->
                currentCoroutineContext().ensureActive()
                val frame = retriever.getScaledFrameAtTime(
                    index * 100_000L,
                    MediaMetadataRetriever.OPTION_CLOSEST,
                    20,
                    8,
                ) ?: return@mapNotNull null
                try {
                    // The retriever preserves aspect ratio, but the glass needs a fixed character grid.
                    val scaled = Bitmap.createScaledBitmap(frame, 20, 8, true)
                    try {
                        (0 until 8).joinToString("\n") { y ->
                            buildString {
                                for (x in 0 until 20) {
                                    val color = scaled.getPixel(x, y)
                                    val luminance = 0.299 * Color.red(color) +
                                        0.587 * Color.green(color) + 0.114 * Color.blue(color)
                                    val level = luminance.toInt().coerceIn(0, 255)
                                        .let { if (invert) 255 - it else it }
                                    append(ramp[level * (ramp.length - 1) / 255])
                                }
                            }.trimEnd()
                        }
                    } finally {
                        if (scaled !== frame) scaled.recycle()
                    }
                } finally {
                    frame.recycle()
                }
            }
            check(frames.isNotEmpty()) { "Could not decode any video frames" }
            frames
        } finally {
            retriever.release()
        }
    }

actual fun renderLauncherIcon(glyph: String, focused: Boolean, size: Int): GrayscaleImage {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    try {
        val canvas = Canvas(bitmap)
        val ink = if (focused) Color.BLACK else Color.WHITE
        canvas.drawColor(if (focused) Color.WHITE else Color.BLACK)
        val border = Paint().apply {
            color = ink
            style = Paint.Style.STROKE
            strokeWidth = if (focused) 8f else 3f
            isAntiAlias = false
        }
        val inset = border.strokeWidth / 2
        canvas.drawRect(inset, inset, size - inset, size - inset, border)
        val text = Paint().apply {
            color = ink
            textSize = size * 0.55f
            textAlign = Paint.Align.CENTER
            isAntiAlias = false
        }
        canvas.drawText(glyph, size / 2f, size / 2f - (text.descent() + text.ascent()) / 2f, text)
        val colors = IntArray(size * size)
        bitmap.getPixels(colors, 0, size, 0, 0, size, size)
        val pixels = ByteArray(colors.size) { index ->
            if (Color.red(colors[index]) >= 128) 0xFF.toByte() else 0x00
        }
        return GrayscaleImage(size, size, pixels)
    } finally {
        bitmap.recycle()
    }
}
