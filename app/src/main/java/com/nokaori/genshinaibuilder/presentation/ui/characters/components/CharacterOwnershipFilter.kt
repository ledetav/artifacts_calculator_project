package com.nokaori.genshinaibuilder.presentation.ui.characters.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase
import com.nokaori.genshinaibuilder.presentation.ui.common.components.SingleSelectToggleButtonGroup
import com.nokaori.genshinaibuilder.presentation.ui.common.components.TextToggleButton
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName

@Composable
fun OwnershipFilter(
    selectedOption: FilterCharactersUseCase.OwnershipFilter,
    onOptionSelected: (FilterCharactersUseCase.OwnershipFilter) -> Unit
) {
    SingleSelectToggleButtonGroup(
        title = stringResource(R.string.filter_ownership),
        items = FilterCharactersUseCase.OwnershipFilter.entries,
        selectedItem = selectedOption,
        onItemSelect = onOptionSelected
    ) { option, isSelected ->
        TextToggleButton(
            text = option.toDisplayName(),
            isSelected = isSelected,
            onClick = { onOptionSelected(option) }
        )
    }
}