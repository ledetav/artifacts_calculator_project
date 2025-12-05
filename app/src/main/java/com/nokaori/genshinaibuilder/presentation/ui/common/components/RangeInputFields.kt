package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun RangeInputFields(
    fromValue: String,
    toValue: String,
    fromLabel: String,
    toLabel: String,
    onFromChanged: (String) -> Unit,
    onToChanged: (String) -> Unit,
    onCommit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = fromValue,
            onValueChange = onFromChanged,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { if (!it.isFocused) onCommit() },
            label = { Text(fromLabel) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                onCommit()
                focusManager.clearFocus()
            })
        )

        Spacer(modifier = Modifier.width(16.dp))

        TextField(
            value = toValue,
            onValueChange = onToChanged,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { if (!it.isFocused) onCommit() },
            label = { Text(toLabel) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                onCommit()
                focusManager.clearFocus()
            })
        )
    }
}
