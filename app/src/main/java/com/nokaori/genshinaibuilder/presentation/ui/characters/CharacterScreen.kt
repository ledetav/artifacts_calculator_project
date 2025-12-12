package com.nokaori.genshinaibuilder.presentation.ui.characters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.characters.components.CharacterFilterDialog
import com.nokaori.genshinaibuilder.presentation.ui.characters.components.CharacterItem
import com.nokaori.genshinaibuilder.presentation.viewmodel.CharacterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    characterViewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by characterViewModel.searchQuery.collectAsStateWithLifecycle()
    val characters by characterViewModel.characters.collectAsStateWithLifecycle()
    val isFilterDialogShown by characterViewModel.isFilterDialogShown.collectAsStateWithLifecycle()
    val areFiltersChanged by characterViewModel.areFiltersChanged.collectAsStateWithLifecycle()

    if (isFilterDialogShown) {
        val draftState by characterViewModel.draftFilterState.collectAsStateWithLifecycle()
        
        CharacterFilterDialog(
            filterState = draftState,
            areFiltersChanged = areFiltersChanged,
            onDismiss = characterViewModel::onFilterDialogDismiss,
            onApply = characterViewModel::onApplyFilters,
            onReset = characterViewModel::onResetFilters,
            onElementSelected = characterViewModel::onElementSelected,
            onOwnershipFilterChanged = characterViewModel::onOwnershipFilterChanged
        )
    }

    Column(modifier = modifier.padding(8.dp)) {
        // Поиск и кнопка фильтра
        OutlinedTextField(
            value = searchQuery,
            onValueChange = characterViewModel::onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            label = { Text(stringResource(R.string.character_search_placeholder)) }, // ЗАМЕТКА: Добавить строку
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = characterViewModel::onFilterIconClicked) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Плитка персонажей
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 105.dp), // Адаптивная ширина колонок
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(characters) { character ->
                CharacterItem(
                    character = character,
                    onClick = { characterViewModel.onCharacterClicked(character.id) }
                )
            }
        }
    }
}