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
import com.nokaori.genshinaibuilder.presentation.util.YattaAssets
import coil3.compose.rememberAsyncImagePainter

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

        val iconUrl = YattaAssets.getElementIconUrl(element)
        val painter = rememberAsyncImagePainter(iconUrl)

        IconToggleButton(
            onClick = { onElementSelected(element) },
            isSelected = isSelected,
            painter = painter,
            contentDescription = element.name,
            activeColor = elementColor,
            inactiveContentColor = elementColor
        )
    }
}