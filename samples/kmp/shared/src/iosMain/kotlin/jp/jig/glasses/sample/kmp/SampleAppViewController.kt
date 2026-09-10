package jp.jig.glasses.sample.kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import app.jigglass.glass.getGlassManager
import jp.jig.glasses.sample.kmp.ui.GlassesApp
import jp.jig.glasses.sample.kmp.ui.samplePlatformContext

fun SampleAppViewController() = ComposeUIViewController {
    val context = samplePlatformContext()
    MaterialTheme { GlassesApp(getGlassManager(context)) }
}
