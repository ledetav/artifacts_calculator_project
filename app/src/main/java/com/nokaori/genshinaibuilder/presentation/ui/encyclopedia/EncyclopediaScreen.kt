package com.nokaori.genshinaibuilder.presentation.ui.encyclopedia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.components.EncyclopediaArtifactsTab
import com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.components.EncyclopediaWeaponsTab
import com.nokaori.genshinaibuilder.presentation.viewmodel.EncyclopediaViewModel
import kotlinx.coroutines.launch
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

@Composable
fun EncyclopediaScreen(encyclopediaViewModel: EncyclopediaViewModel) {
    val artifactSets by encyclopediaViewModel.artifactSets.collectAsStateWithLifecycle()
    val weaponsPaged = encyclopediaViewModel.weaponsPaged.collectAsLazyPagingItems()

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
                0 -> EncyclopediaArtifactsTab(sets = artifactSets)
                1 -> EncyclopediaWeaponsTab(weaponsPaged)
            }
        }
    }
}