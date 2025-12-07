package com.nokaori.genshinaibuilder.presentation.ui.characters.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.presentation.ui.common.components.IconToggleButton
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MultiSelectToggleButtonGroup

@Composable
fun ElementFilter(
    selectedElements: Set<Element>,
    onElementSelected: (Element) -> Unit
) {
    // ЗАМЕТКА: В идеале здесь нужны векторные иконки элементов. Пока используем цветные кружочки.
    MultiSelectToggleButtonGroup(
        title = stringResource(R.string.filter_element), // Добавить строку в strings.xml
        items = Element.entries,
        selectedItems = selectedElements,
        onItemClick = onElementSelected
    ) { element, isSelected ->
        // Кастомная кнопка с цветом элемента
        IconToggleButton(
            onClick = { onElementSelected(element) },
            isSelected = isSelected,
            icon = Icons.Default.Circle, // Заглушка
            contentDescription = element.name,
            // Здесь можно доработать IconToggleButton, чтобы он принимал tint
            // Или использовать свой Box
        )
    }
}