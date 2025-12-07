package com.nokaori.genshinaibuilder.presentation.ui.characters.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase
import com.nokaori.genshinaibuilder.presentation.ui.characters.data.CharacterFilterState
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseDialog

@Composable
fun CharacterFilterDialog(
    filterState: CharacterFilterState,
    areFiltersChanged: Boolean,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onElementSelected: (Element) -> Unit,
    onOwnershipFilterChanged: (FilterCharactersUseCase.OwnershipFilter) -> Unit
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.filter_dialog_title),
        content = {
            Column {
                ElementFilter(
                    selectedElements = filterState.selectedElements,
                    onElementSelected = onElementSelected
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OwnershipFilter(
                    selectedOption = filterState.ownershipFilter,
                    onOptionSelected = onOwnershipFilterChanged
                )
            }
        },
        actions = {
            TextButton(onClick = onReset) {
                Text(stringResource(R.string.reset))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onApply, enabled = areFiltersChanged) {
                Text(stringResource(R.string.apply))
            }
        }
    )
}