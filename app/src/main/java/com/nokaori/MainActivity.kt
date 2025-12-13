package com.nokaori.genshinaibuilder.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.ArtifactScreen
import com.nokaori.genshinaibuilder.presentation.ui.characters.CharacterScreen
import com.nokaori.genshinaibuilder.presentation.ui.common.components.AppDrawer
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MainTopAppBar
import com.nokaori.genshinaibuilder.presentation.ui.navigation.NavigationItem
import com.nokaori.genshinaibuilder.presentation.ui.settings.SettingsScreen
import com.nokaori.genshinaibuilder.presentation.ui.theme.GenshinAIBuilderTheme
import com.nokaori.genshinaibuilder.presentation.ui.weapons.WeaponScreen
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.CharacterViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.SettingsViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.ThemeViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.ViewModelFactory
import com.nokaori.genshinaibuilder.presentation.viewmodel.WeaponViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // --- INJECTION: Получаем ViewModel'и через Фабрику ---
    // Factory сама достанет зависимости из Application класса
    
    val themeViewModel: ThemeViewModel = viewModel(factory = ViewModelFactory.Factory)
    
    // Эти VM можно создавать здесь или внутри composable блоков навигации.
    // Создадим здесь, чтобы передать в Screens.
    val artifactViewModel: ArtifactViewModel = viewModel(factory = ViewModelFactory.Factory)
    val weaponViewModel: WeaponViewModel = viewModel(factory = ViewModelFactory.Factory)
    val characterViewModel: CharacterViewModel = viewModel(factory = ViewModelFactory.Factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = ViewModelFactory.Factory)

    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    val allNavItems = listOf(
        NavigationItem.Encyclopedia,
        NavigationItem.Characters,
        NavigationItem.Artifacts,
        NavigationItem.Weapons,
        NavigationItem.Builds,
        NavigationItem.Settings
    )
    val currentNavItem = allNavItems.find { it.route == currentRoute }

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
                            currentItemRoute = currentRoute,
                            onItemClick = { item ->
                                scope.launch { drawerState.close() }
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
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
                                // Кнопки "+" временно скрыты, пока нет экранов ввода
                                // (чтобы не вызывать краш при пустой базе)
                                /*
                                if (currentRoute == NavigationItem.Artifacts.route) {
                                    IconButton(onClick = { /* TODO: Open Add Artifact Screen */ }) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    }
                                }
                                */
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        // Теперь стартуем с Энциклопедии (или с Персонажей, как тебе удобнее)
                        startDestination = NavigationItem.Encyclopedia.route, 
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Энциклопедия (Табы)
                        composable(NavigationItem.Encyclopedia.route) {
                            // Тут ViewModel пока не нужна, или создадим позже EncyclopediaViewModel
                            com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.EncyclopediaScreen()
                        }

                        // 2. Персонажи (Список)
                        composable(NavigationItem.Characters.route) {
                            CharacterScreen(characterViewModel = characterViewModel)
                        }

                        // 3. Инвентарь: Артефакты
                        composable(NavigationItem.Artifacts.route) {
                            ArtifactScreen(artifactViewModel = artifactViewModel)
                        }

                        // 4. Инвентарь: Оружие
                        composable(NavigationItem.Weapons.route) {
                            WeaponScreen(weaponViewModel = weaponViewModel)
                        }

                        // 5. Билды
                        composable(NavigationItem.Builds.route) {
                            Surface(modifier = Modifier.fillMaxSize()) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Builds Screen (Coming Soon)")
                                }
                            }
                        }

                        // 6. Настройки
                        composable(NavigationItem.Settings.route) {
                            SettingsScreen(settingsViewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}