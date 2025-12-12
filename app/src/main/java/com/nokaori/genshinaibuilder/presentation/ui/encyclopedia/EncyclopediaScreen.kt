package com.nokaori.genshinaibuilder.presentation.ui.encyclopedia

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import kotlinx.coroutines.launch

@Composable
fun EncyclopediaScreen() {
    val tabs = listOf(
        R.string.nav_artifact_sets,
        R.string.nav_weapons
    )

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, titleResId ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(stringResource(titleResId)) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> EncyclopediaArtifactsTab()
                1 -> EncyclopediaWeaponsTab()
            }
        }
    }
}

@Composable
fun EncyclopediaArtifactsTab() {
    // ЗАГЛУШКА: Сюда потом подключим список сетов из базы
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Список сетов артефактов")
    }
}

@Composable
fun EncyclopediaWeaponsTab() {
    // ЗАГЛУШКА: Сюда потом подключим список оружия из базы
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("База оружия")
    }
}