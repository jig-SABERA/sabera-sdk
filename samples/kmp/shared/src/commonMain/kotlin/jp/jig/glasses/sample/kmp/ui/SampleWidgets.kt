package jp.jig.glasses.sample.kmp.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 画面をまたいで使う小さな部品。見た目を揃えるためだけのもの */

@Composable
internal fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(8.dp))
}

@Composable
internal fun CommandButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Text(label)
    }
}

@Composable
internal fun SendableTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    Button(
        onClick = onSend,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("$label を送信")
    }
    Spacer(Modifier.height(12.dp))
}
