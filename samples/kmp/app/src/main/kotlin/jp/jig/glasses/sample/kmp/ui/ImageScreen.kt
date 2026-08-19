package jp.jig.glasses.sample.kmp.ui

import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassClient

private const val TAG = "ImageScreen"

/** 画像をグラスの画像表示ページに送る画面。技適マークの表示に使われているのと同じ画面 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(client: GlassClient, onBack: () -> Unit) {
    val context = LocalContext.current
    val commandManager = remember(client) { client.createCommandManager() }

    var image by remember { mutableStateOf<GrayscaleImage?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            val bitmap = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
            if (bitmap == null) {
                error = "画像を読み込めませんでした"
                return@rememberLauncherForActivityResult
            }
            image = bitmap.toGlassGrayscale()
        } catch (e: Throwable) {
            Log.e(TAG, "failed to load image", e)
            error = e.message
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("画像を送る") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "グラスに送れるのは ${GLASS_IMAGE_MAX_SIZE}x$GLASS_IMAGE_MAX_SIZE まで。" +
                    "選んだ画像は縦横比を保って縮小し、輝度だけを送る。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("画像を選ぶ")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { image = testPatternImage() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("テストパターンを使う")
            }

            val current = image
            if (current != null) {
                Spacer(Modifier.height(24.dp))
                Image(
                    bitmap = remember(current) { current.toBitmap().asImageBitmap() },
                    contentDescription = "グラスに送る画像のプレビュー",
                    modifier = Modifier.size(196.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${current.width}x${current.height} / ${current.pixels.size} bytes",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        Log.d(TAG, "sendImage: ${current.width}x${current.height}")
                        try {
                            commandManager.enterImageDisplayPage()
                            commandManager.sendImage(
                                width = current.width,
                                height = current.height,
                                grayscale = current.pixels,
                            )
                        } catch (e: Throwable) {
                            Log.e(TAG, "sendImage failed", e)
                            error = e.message
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("グラスに送る")
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    commandManager.enterHomePage()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("戻る（グラスは Home へ）")
            }

            error?.let { message ->
                Spacer(Modifier.height(16.dp))
                Text(message, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { error = null }) {
                    Text("エラーを消す")
                }
            }
        }
    }
}
