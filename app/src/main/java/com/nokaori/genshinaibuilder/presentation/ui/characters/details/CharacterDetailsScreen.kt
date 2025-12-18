package com.nokaori.genshinaibuilder.presentation.ui.characters.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.presentation.ui.characters.details.components.*
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MainTopAppBar
import com.nokaori.genshinaibuilder.presentation.viewmodel.CharacterDetailsViewModel
import kotlinx.coroutines.launch

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

    if (character == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (userCharacter == null) {
                        IconButton(onClick = { viewModel.toggleOwnership() }) {
                            Icon(Icons.Default.Add, contentDescription = "Add to collection")
                        }
                    } else {
                        IconButton(onClick = { /* Open Edit Dialog */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.toggleOwnership() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // 1. Инфо
            CharacterInfoSection(character!!, userCharacter)
            
            HorizontalDivider()

            // 2. Экипировка (только если есть)
            if (userCharacter != null) {
                EquipmentSection(
                    equippedWeapon = equippedWeapon.firstOrNull(),
                    onAddWeaponClick = { /* TODO */ },
                    onAddArtifactClick = { /* TODO */ }
                )
                HorizontalDivider()
            }

            // 3. Табы
            TabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(selected = pagerState.currentPage == 0, onClick = { scope.launch { pagerState.animateScrollToPage(0) } }, text = { Text("Talents") })
                Tab(selected = pagerState.currentPage == 1, onClick = { scope.launch { pagerState.animateScrollToPage(1) } }, text = { Text("Constellations") })
            }

            HorizontalPager(state = pagerState) { page ->
                when(page) {
                    0 -> TalentsList(talents, if(userCharacter != null) listOf(1,1,1) else null) // Заглушка уровней
                    1 -> ConstellationsList(constellations, userCharacter?.constellation ?: 0)
                }
            }
        }
    }
}