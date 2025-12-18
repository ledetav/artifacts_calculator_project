package com.nokaori.genshinaibuilder.presentation.ui.characters.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.presentation.ui.characters.details.components.*
import com.nokaori.genshinaibuilder.presentation.ui.theme.getElementColor
import com.nokaori.genshinaibuilder.presentation.viewmodel.CharacterDetailsViewModel
import kotlinx.coroutines.launch
import com.nokaori.genshinaibuilder.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val character by viewModel.character.collectAsStateWithLifecycle()
    val userCharacter by viewModel.userCharacter.collectAsStateWithLifecycle()
    val equippedWeapon by viewModel.equippedWeapon.collectAsStateWithLifecycle()
    val talents by viewModel.talents.collectAsStateWithLifecycle()
    val constellations by viewModel.constellations.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scrollState = rememberScrollState()

    if (character == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Text(
                        text = character?.name ?: "",
                        modifier = Modifier.offset(x = (-8).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    if (userCharacter == null) {
                        IconButton(onClick = { viewModel.toggleOwnership() }) {
                            Icon(
                                Icons.Default.Add, 
                                contentDescription = stringResource(R.string.cd_add_to_collection)
                            )
                        }
                    } else {
                        IconButton(onClick = { /* Open Edit Dialog */ }) {
                            Icon(
                                Icons.Default.Edit, 
                                contentDescription = stringResource(R.string.cd_edit_character)
                            )
                        }
                        IconButton(onClick = { viewModel.toggleOwnership() }) {
                            Icon(
                                Icons.Default.Delete, 
                                contentDescription = stringResource(R.string.cd_delete_character)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            CharacterInfoSection(character!!, userCharacter)

            HorizontalDivider()

            if (userCharacter != null) {
                EquipmentSection(
                    equippedWeapon = equippedWeapon.firstOrNull(),
                    onAddWeaponClick = { /* TODO */ },
                    onAddArtifactClick = { /* TODO */ }
                )
                HorizontalDivider()
            }

            TabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(selected = pagerState.currentPage == 0, onClick = { scope.launch { pagerState.animateScrollToPage(0) } }, text = { Text("Talents") })
                Tab(selected = pagerState.currentPage == 1, onClick = { scope.launch { pagerState.animateScrollToPage(1) } }, text = { Text("Constellations") })
            }

            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top
            ) { page ->
                val elementColor = getElementColor(character!!.element)

                when(page) {
                    0 -> TalentsList(
                        talents, 
                        if(userCharacter != null) listOf(1,1,1) else null,
                        elementColor = elementColor
                    )
                    1 -> ConstellationsList(
                        constellations, 
                        userCharacter?.constellation ?: 0, 
                        elementColor = elementColor
                        )
                }
            }
        }
    }
}