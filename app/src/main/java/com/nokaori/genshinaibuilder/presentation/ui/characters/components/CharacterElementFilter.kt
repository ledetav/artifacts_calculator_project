package com.nokaori.genshinaibuilder.presentation.ui.characters.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.presentation.ui.common.components.IconToggleButton
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MultiSelectToggleButtonGroup
import com.nokaori.genshinaibuilder.presentation.ui.theme.getElementColor

@Composable
fun ElementFilter(
    selectedElements: Set<Element>,
    onElementSelected: (Element) -> Unit
) {
    MultiSelectToggleButtonGroup(
        title = stringResource(R.string.filter_element),
        items = Element.entries,
        selectedItems = selectedElements,
        onItemClick = onElementSelected
    ) { element, isSelected ->
        val elementColor = getElementColor(element)

        IconToggleButton(
            onClick = { onElementSelected(element) },
            isSelected = isSelected,
            icon = Icons.Default.Circle,
            contentDescription = element.name,
            
            activeColor = elementColor,
            inactiveContentColor = elementColor
        )
    }
}