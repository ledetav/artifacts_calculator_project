package com.nokaori.genshinaibuilder.presentation.ui.characters.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase
import com.nokaori.genshinaibuilder.presentation.ui.common.components.SingleSelectToggleButtonGroup

@Composable
fun OwnershipFilter(
    selectedOption: FilterCharactersUseCase.OwnershipFilter,
    onOptionSelected: (FilterCharactersUseCase.OwnershipFilter) -> Unit
) {
    SingleSelectToggleButtonGroup(
        title = stringResource(R.string.filter_ownership), // ЗАМЕТКА: Добавить строку
        items = FilterCharactersUseCase.OwnershipFilter.entries,
        selectedItem = selectedOption,
        onItemSelect = onOptionSelected
    ) { option, isSelected ->
        // ЗАМЕТКА
        // Простая текстовая кнопка внутри группы
        // Здесь лучше использовать SegmentedButton или просто TextButton с выделением
        // Для простоты используем наш Text
        Text(
            text = option.name, // Заменить на маппер в строки ресурсов
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.padding(8.dp)
        )
    }
}