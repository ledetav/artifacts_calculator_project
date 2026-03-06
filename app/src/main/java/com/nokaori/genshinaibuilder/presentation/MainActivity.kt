package com.nokaori.genshinaibuilder.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.ArtifactScreen
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.components.AddArtifactSelectionSheet
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.EditorArtifactScreen
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.scanner.ArtifactScannerScreen
import com.nokaori.genshinaibuilder.presentation.ui.characters.CharacterScreen
import com.nokaori.genshinaibuilder.presentation.ui.characters.details.CharacterDetailsScreen
import com.nokaori.genshinaibuilder.presentation.ui.common.components.AppDrawer
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MainTopAppBar
import com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.EncyclopediaScreen
import com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.details.ArtifactSetDetailsScreen
import com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.details.WeaponDetailsScreen
import com.nokaori.genshinaibuilder.presentation.ui.navigation.NavigationItem
import com.nokaori.genshinaibuilder.presentation.ui.settings.GestureSettingsScreen
import com.nokaori.genshinaibuilder.presentation.ui.settings.SettingsScreen
import com.nokaori.genshinaibuilder.presentation.ui.theme.GenshinAIBuilderTheme
import com.nokaori.genshinaibuilder.presentation.ui.weapons.WeaponScreen
import com.nokaori.genshinaibuilder.presentation.util.sensor.rememberTiltSensor
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.CharacterViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.EncyclopediaViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.GestureSettingsViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.SettingsViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.ThemeViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.WeaponViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hilt автоматически создает ViewModels
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val encyclopediaViewModel: EncyclopediaViewModel = hiltViewModel()
    val artifactViewModel: ArtifactViewModel = hiltViewModel()
    val weaponViewModel: WeaponViewModel = hiltViewModel()
    val characterViewModel: CharacterViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    var showAddArtifactSheet by remember { mutableStateOf(false) }



    val allNavItems = listOf(
        NavigationItem.Encyclopedia,
        NavigationItem.Characters,
        NavigationItem.Artifacts,
        NavigationItem.Weapons,
        NavigationItem.Builds,
        NavigationItem.Settings
    )

    val topLevelRoutes = allNavItems.map { it.route }

    val isTopLevelDestination = currentRoute in topLevelRoutes

    val currentNavItem = allNavItems.find { it.route == currentRoute }

    rememberTiltSensor(
        currentRoute = currentRoute,
        topLevelRoutes = topLevelRoutes,
        onSwipeLeft = { 
            val currentIndex = topLevelRoutes.indexOf(currentRoute)
            if (currentIndex < topLevelRoutes.size - 1) {
                val nextRoute = topLevelRoutes[currentIndex + 1]
                navController.navigate(nextRoute) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        onSwipeRight = { 
            val currentIndex = topLevelRoutes.indexOf(currentRoute)
            if (currentIndex > 0) {
                val prevRoute = topLevelRoutes[currentIndex - 1]
                navController.navigate(prevRoute) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )

    GenshinAIBuilderTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                        !isDarkTheme
                    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                        !isDarkTheme
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
                if (showAddArtifactSheet) {
                    AddArtifactSelectionSheet(
                        onDismissRequest = { showAddArtifactSheet = false },
                        onManualEntrySelected = {
                            showAddArtifactSheet = false
                            navController.navigate("artifact/editor/null")
                        },
                        onImageSelected = { uri ->
                            showAddArtifactSheet = false
                            val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
                            navController.navigate("artifact/scanner/$encodedUri")
                        }
                    )
                }

                Scaffold(
                    topBar = {
                        if (isTopLevelDestination) {
                            MainTopAppBar(
                                title = stringResource(
                                    id = currentNavItem?.titleResId ?: R.string.app_name
                                ),
                                onNavigationIconClick = {
                                    scope.launch { drawerState.open() }
                                },
                                actions = {
                                    if (currentRoute == NavigationItem.Artifacts.route) {
                                        IconButton(onClick = {
                                            showAddArtifactSheet = true 
                                        }) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = stringResource(R.string.artifact_add_button)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavigationItem.Encyclopedia.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavigationItem.Encyclopedia.route) {
                            EncyclopediaScreen(
                                encyclopediaViewModel = encyclopediaViewModel,
                                onArtifactSetClick = { setId ->
                                    navController.navigate("encyclopedia/artifact/$setId")
                                },
                                onWeaponClick = { weaponId ->
                                    navController.navigate("encyclopedia/weapon/$weaponId")
                                }
                            )
                        }

                        composable(NavigationItem.Characters.route) {
                            CharacterScreen(
                                characterViewModel = characterViewModel,
                                onCharacterClick = { id -> navController.navigate("character/$id") }
                            )
                        }

                        composable(NavigationItem.Artifacts.route) {
                            ArtifactScreen(
                                artifactViewModel = artifactViewModel,
                                onArtifactClick = { artifactId ->
                                    navController.navigate("artifact/editor/$artifactId")
                                }
                            )
                        }

                        composable(NavigationItem.Weapons.route) {
                            WeaponScreen(weaponViewModel = weaponViewModel)
                        }

                        composable(NavigationItem.Builds.route) {
                            Surface(modifier = Modifier.fillMaxSize()) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(stringResource(R.string.builds_coming_soon))
                                }
                            }
                        }

                        composable(NavigationItem.Settings.route) {
                            SettingsScreen(
                                settingsViewModel = settingsViewModel,
                                onNavigateToGestures = {
                                    navController.navigate("gesture_settings_route")
                                }
                            )
                        }

                        composable("gesture_settings_route") {
                            val gestureSettingsViewModel: GestureSettingsViewModel = hiltViewModel()
                            GestureSettingsScreen(
                                viewModel = gestureSettingsViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "character/{characterId}",
                            arguments = listOf(
                                navArgument("characterId") { type = NavType.IntType }
                            )
                        ) {
                            CharacterDetailsScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "encyclopedia/artifact/{setId}",
                            arguments = listOf(navArgument("setId") { type = NavType.IntType })
                        ) {
                            ArtifactSetDetailsScreen(onBackClick = { navController.popBackStack() })
                        }

                        composable(
                            route = "encyclopedia/weapon/{weaponId}",
                            arguments = listOf(navArgument("weaponId") { type = NavType.IntType })
                        ) {
                            WeaponDetailsScreen(onBackClick = { navController.popBackStack() })
                        }

                        composable(
                            route = "artifact/editor/{artifactId}",
                            arguments = listOf(
                                navArgument("artifactId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val savedStateHandle = backStackEntry.savedStateHandle
                            val scannedData = savedStateHandle.get<ParsedArtifactData>("scanned_artifact_data")
                            
                            EditorArtifactScreen(
                                onBackClick = { navController.popBackStack() },
                                artifactId = backStackEntry.arguments?.getString("artifactId"),
                                scannedData = scannedData
                            )
                            
                            if (scannedData != null) {
                                savedStateHandle.remove<ParsedArtifactData>("scanned_artifact_data")
                            }
                        }

                        composable(
                            route = "artifact/scanner/{imageUri}",
                            arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val imageUri = backStackEntry.arguments?.getString("imageUri")
                            
                            ArtifactScannerScreen(
                                imageUriString = imageUri,
                                onScanComplete = { parsedData ->
                                    navController.navigate("artifact/editor/null") {
                                        popUpTo("artifact/scanner/{imageUri}") { inclusive = true }
                                    }
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("scanned_artifact_data", parsedData)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}