package com.example.my_rouge_mobile.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.my_rouge_mobile.ui.components.FramedPanel
import com.example.my_rouge_mobile.ui.components.GameButton
import com.example.my_rouge_mobile.ui.components.PanelText
import com.example.my_rouge_mobile.ui.theme.GameColors

private const val MAX_NAME_LENGTH = 8
private const val DEFAULT_NAME = "Unknown Hero"

/** Экран создания персонажа (бывший StartWindow). */
@Composable
fun StartScreen(onConfirm: (String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    val submit = { onConfirm(name.ifBlank { DEFAULT_NAME }) }

    FramedPanel(title = "=== СОЗДАНИЕ ПЕРСОНАЖА ===") {
        PanelText("Скажи нам свое имя, путник, иначе ты навсегда останешься безымянным героем.")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            // как в старом коде: только буквы/цифры, не больше 8 символов
            onValueChange = { name = it.filter(Char::isLetterOrDigit).take(MAX_NAME_LENGTH) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = GameColors.GreenBright,
                textAlign = TextAlign.Center
            ),
            placeholder = {
                Text(DEFAULT_NAME, color = GameColors.Gold.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            },
            supportingText = {
                Text("${name.length}/$MAX_NAME_LENGTH", color = GameColors.Gold)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GameColors.GoldBright,
                unfocusedBorderColor = GameColors.Gold,
                cursorColor = GameColors.GreenBright
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        GameButton(text = "В ПУТЬ", onClick = submit)
    }
}