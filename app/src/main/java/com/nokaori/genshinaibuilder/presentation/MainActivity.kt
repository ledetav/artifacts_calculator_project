package com.nokaori.genshinaibuilder.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.data.repository.ArtifactRepositoryImpl
import com.nokaori.genshinaibuilder.data.repository.ThemeRepositoryImpl
import com.nokaori.genshinaibuilder.data.repository.WeaponRepositoryImpl
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.ArtifactScreen
import com.nokaori.genshinaibuilder.presentation.ui.common.components.AppDrawer
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MainTopAppBar
import com.nokaori.genshinaibuilder.presentation.ui.navigation.NavigationItem
import com.nokaori.genshinaibuilder.presentation.ui.theme.GenshinAIBuilderTheme
import com.nokaori.genshinaibuilder.presentation.ui.characters.CharacterScreen
import com.nokaori.genshinaibuilder.presentation.ui.weapons.WeaponScreen
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.CharacterViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.ThemeViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.ViewModelFactory
import com.nokaori.genshinaibuilder.presentation.viewmodel.WeaponViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.data.repository.CharacterRepositoryImpl
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // синглтоны в рамках Activity
    private val artifactRepository by lazy { ArtifactRepositoryImpl() }
    private val weaponRepository by lazy { WeaponRepositoryImpl() }
    private val characterRepository by lazy { CharacterRepositoryImpl() }
    
    // Используем applicationContext, чтобы избежать утечек памяти
    private val themeRepository by lazy { ThemeRepositoryImpl(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = ViewModelFactory(
            artifactRepository, 
            weaponRepository, 
            themeRepository,
            characterRepository
        )

        setContent {
            AppContent(factory = factory)
        }
    }
}

@Composable
fun AppContent(factory: ViewModelFactory) { 
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val themeViewModel: ThemeViewModel = viewModel(factory = factory)
    val artifactViewModel: ArtifactViewModel = viewModel(factory = factory)
    val weaponViewModel: WeaponViewModel = viewModel(factory = factory)
    val characterViewModel: CharacterViewModel = viewModel(factory = factory)

    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    val navigationItems = listOf(
        NavigationItem.Artifacts,
        NavigationItem.Weapons,
        NavigationItem.Characters,
        NavigationItem.Builds,
        NavigationItem.Settings
    )

    val currentNavItem = navigationItems.find { it.route == currentRoute }

    GenshinAIBuilderTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
                    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
                }
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { themeViewModel.toggleTheme() },
                                modifier = Modifier.alpha(0.7f)
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.Brightness7
                                    else Icons.Default.Brightness4,
                                    contentDescription = stringResource(R.string.theme_switch)
                                )
                            }
                        }

                        HorizontalDivider()

                        AppDrawer(
                            items = navigationItems,
                            currentItemRoute = currentRoute,
                            onItemClick = { item ->
                                scope.launch { drawerState.close() }
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        MainTopAppBar(
                            title = stringResource(id = currentNavItem?.titleResId ?: R.string.app_name),
                            onNavigationIconClick = {
                                scope.launch { drawerState.open() }
                            },
                            actions = {
                                if (currentRoute == NavigationItem.Artifacts.route) {
                                    IconButton(onClick = { artifactViewModel.addDefaultArtifact() }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(R.string.artifact_add_button)
                                        )
                                    }
                                }
                                if (currentRoute == NavigationItem.Weapons.route) {
                                    IconButton(onClick = { weaponViewModel.addDefaultWeapon() }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(R.string.weapon_add_button)
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavigationItem.Artifacts.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavigationItem.Artifacts.route) {
                            ArtifactScreen(artifactViewModel = artifactViewModel)
                        }

                        composable(NavigationItem.Weapons.route) {
                            WeaponScreen(weaponViewModel = weaponViewModel)
                        }

                        composable(NavigationItem.Characters.route) {
                            CharacterScreen(characterViewModel = characterViewModel)
                        }

                        composable(NavigationItem.Builds.route) {
                            Text("Builds", modifier = Modifier.padding(16.dp))
                        }

                        composable(NavigationItem.Settings.route) {
                            Text("Settings", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}