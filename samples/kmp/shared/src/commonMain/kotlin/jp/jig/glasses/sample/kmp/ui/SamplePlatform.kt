package jp.jig.glasses.sample.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import app.jigglass.glass.PlatformContext

object SampleLog {
    var sink: (String, String) -> Unit = { _, _ -> }

    fun d(tag: String, message: String) {
        sink(tag, message)
    }

    fun e(tag: String, message: String, error: Throwable) {
        sink(tag, "$message\n${error.stackTraceToString()}")
    }
}

@Composable
expect fun rememberImagePicker(maxSize: Int, onResult: (Result<GrayscaleImage>) -> Unit): () -> Unit

/** Report picker dismissal as a CancellationException so the UI can clear its loading state. */
@Composable
expect fun rememberVideoPicker(invert: Boolean, onResult: (Result<List<String>>) -> Unit): () -> Unit

@Composable
expect fun samplePlatformContext(): PlatformContext

expect fun readSampleAsset(name: String): ByteArray

expect fun GrayscaleImage.toImageBitmap(): ImageBitmap

expect fun renderLauncherIcon(glyph: String, focused: Boolean, size: Int): GrayscaleImage
