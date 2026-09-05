package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.tvFocusable
import com.example.ui.screens.CategoryFilterScreen
import com.example.ui.screens.CookModeScreen
import com.example.ui.screens.CookbookScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.viewmodel.RecipeViewModel

enum class AppDestination {
    DISCOVER,
    FILTER,
    SEARCH,
    COOKBOOK
}

@Composable
fun MainApp(viewModel: RecipeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val savedRecipes by viewModel.savedRecipes.collectAsStateWithLifecycle()
    val savedRecipeIds = remember(savedRecipes) { savedRecipes.map { it.id }.toSet() }

    var currentDestination by remember { mutableStateOf(AppDestination.DISCOVER) }
    var isInCookMode by remember { mutableStateOf(false) }

    // Back handling: CookMode -> Detail -> Destination
    BackHandler(enabled = isInCookMode || state.activeRecipe != null) {
        if (isInCookMode) {
            isInCookMode = false
        } else if (state.activeRecipe != null) {
            viewModel.clearActiveRecipe()
        }
    }

    if (isInCookMode && state.activeRecipe != null) {
        CookModeScreen(
            recipe = state.activeRecipe!!,
            currentStepIndex = state.activeCookStepIndex,
            keepScreenOn = state.keepScreenOn,
            servingMultiplier = state.activeServings.toFloat() / state.activeRecipe!!.baseServings.toFloat(),
            onStepChange = { viewModel.setCookStep(it) },
            onNextStep = { viewModel.nextCookStep() },
            onPrevStep = { viewModel.prevCookStep() },
            onToggleKeepScreenOn = { viewModel.toggleKeepScreenOn() },
            onExitCookMode = { isInCookMode = false }
        )
        return
    }

    if (state.activeRecipe != null) {
        val recipe = state.activeRecipe!!
        RecipeDetailScreen(
            recipe = recipe,
            currentServings = state.activeServings,
            isBookmarked = savedRecipeIds.contains(recipe.id),
            userNotesDraft = state.userNotesDraft,
            onServingsChange = { viewModel.updateServings(it) },
            onBookmarkToggle = { viewModel.toggleBookmark(recipe) },
            onStartCooking = { isInCookMode = true },
            onNotesDraftChange = { viewModel.updateNotesDraft(it) },
            onSaveNotes = { viewModel.saveNotes() },
            onBack = { viewModel.clearActiveRecipe() }
        )
        return
    }

    // Adaptive Container for TV / Tablet / Mobile
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp // Android TV and tablets

        if (isWideScreen) {
            // TV / Large Tablet Layout with Side Navigation Rail in Professional Polish theme
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    header = {
                        Column(
                            modifier = Modifier.padding(vertical = 20.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = "Kitchen Open",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "OPEN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    NavigationRailItem(
                        selected = currentDestination == AppDestination.DISCOVER,
                        onClick = { currentDestination = AppDestination.DISCOVER },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination == AppDestination.DISCOVER) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = stringResource(R.string.nav_home)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_home)) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.tvFocusable("rail_nav_discover", shape = RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.FILTER,
                        onClick = { currentDestination = AppDestination.FILTER },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination == AppDestination.FILTER) Icons.Filled.FilterAlt else Icons.Outlined.FilterAlt,
                                contentDescription = stringResource(R.string.nav_filter)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_filter_short)) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.tvFocusable("rail_nav_filter", shape = RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.SEARCH,
                        onClick = { currentDestination = AppDestination.SEARCH },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination == AppDestination.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.nav_search)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_search)) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.tvFocusable("rail_nav_search", shape = RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.COOKBOOK,
                        onClick = { currentDestination = AppDestination.COOKBOOK },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination == AppDestination.COOKBOOK) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                contentDescription = stringResource(R.string.nav_cookbook)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_cookbook)) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.tvFocusable("rail_nav_cookbook", shape = RoundedCornerShape(12.dp))
                    )
                }

                // Main screen area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentDestination) {
                        AppDestination.DISCOVER -> {
                            DiscoverScreen(
                                state = state,
                                savedRecipeIds = savedRecipeIds,
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onStartCooking = {
                                    viewModel.openRecipe(it)
                                    isInCookMode = true
                                },
                                onCategorySelect = { viewModel.selectCategory(it) },
                                onOpenFilterScreen = {
                                    viewModel.setFilterCategory(state.selectedCategory)
                                    currentDestination = AppDestination.FILTER
                                },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) },
                                onSurpriseMe = { viewModel.loadRandomRecipe() },
                                onSearchClick = { currentDestination = AppDestination.SEARCH }
                            )
                        }
                        AppDestination.FILTER -> {
                            CategoryFilterScreen(
                                selectedCategory = state.filterCategory,
                                selectedMaxTime = state.filterMaxPrepMinutes,
                                selectedDiet = state.filterDiet,
                                searchQuery = state.filterSearchQuery,
                                recipes = state.filterRecipes,
                                savedIds = savedRecipeIds,
                                isLoading = state.isFilterLoading,
                                onCategorySelected = { viewModel.setFilterCategory(it) },
                                onMaxTimeSelected = { viewModel.setFilterMaxPrepMinutes(it) },
                                onDietSelected = { viewModel.setFilterDiet(it) },
                                onSearchQueryChanged = { viewModel.setFilterSearchQuery(it) },
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) }
                            )
                        }
                        AppDestination.SEARCH -> {
                            SearchScreen(
                                searchQuery = state.searchQuery,
                                searchResults = state.searchResults,
                                isSearching = state.isSearching,
                                savedIds = savedRecipeIds,
                                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) }
                            )
                        }
                        AppDestination.COOKBOOK -> {
                            CookbookScreen(
                                savedRecipes = savedRecipes,
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onStartCooking = {
                                    viewModel.openRecipe(it)
                                    isInCookMode = true
                                },
                                onRemoveRecipe = { viewModel.toggleBookmark(it) },
                                onExploreClick = { currentDestination = AppDestination.DISCOVER }
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile Layout with Bottom Navigation Bar matching Design HTML
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        tonalElevation = 0.dp,
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        )
                    ) {
                        NavigationBarItem(
                            selected = currentDestination == AppDestination.DISCOVER,
                            onClick = { currentDestination = AppDestination.DISCOVER },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.DISCOVER) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = stringResource(R.string.nav_home)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.nav_home),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.tvFocusable("nav_discover")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.FILTER,
                            onClick = { currentDestination = AppDestination.FILTER },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.FILTER) Icons.Filled.FilterAlt else Icons.Outlined.FilterAlt,
                                    contentDescription = stringResource(R.string.nav_filter)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.nav_filter_short),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.tvFocusable("nav_filter")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.SEARCH,
                            onClick = { currentDestination = AppDestination.SEARCH },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = stringResource(R.string.nav_search)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.nav_search),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.tvFocusable("nav_search")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.COOKBOOK,
                            onClick = { currentDestination = AppDestination.COOKBOOK },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.COOKBOOK) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                    contentDescription = stringResource(R.string.nav_cookbook)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.nav_cookbook),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.tvFocusable("nav_cookbook")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentDestination) {
                        AppDestination.DISCOVER -> {
                            DiscoverScreen(
                                state = state,
                                savedRecipeIds = savedRecipeIds,
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onStartCooking = {
                                    viewModel.openRecipe(it)
                                    isInCookMode = true
                                },
                                onCategorySelect = { viewModel.selectCategory(it) },
                                onOpenFilterScreen = {
                                    viewModel.setFilterCategory(state.selectedCategory)
                                    currentDestination = AppDestination.FILTER
                                },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) },
                                onSurpriseMe = { viewModel.loadRandomRecipe() },
                                onSearchClick = { currentDestination = AppDestination.SEARCH }
                            )
                        }
                        AppDestination.FILTER -> {
                            CategoryFilterScreen(
                                selectedCategory = state.filterCategory,
                                selectedMaxTime = state.filterMaxPrepMinutes,
                                selectedDiet = state.filterDiet,
                                searchQuery = state.filterSearchQuery,
                                recipes = state.filterRecipes,
                                savedIds = savedRecipeIds,
                                isLoading = state.isFilterLoading,
                                onCategorySelected = { viewModel.setFilterCategory(it) },
                                onMaxTimeSelected = { viewModel.setFilterMaxPrepMinutes(it) },
                                onDietSelected = { viewModel.setFilterDiet(it) },
                                onSearchQueryChanged = { viewModel.setFilterSearchQuery(it) },
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) }
                            )
                        }
                        AppDestination.SEARCH -> {
                            SearchScreen(
                                searchQuery = state.searchQuery,
                                searchResults = state.searchResults,
                                isSearching = state.isSearching,
                                savedIds = savedRecipeIds,
                                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) }
                            )
                        }
                        AppDestination.COOKBOOK -> {
                            CookbookScreen(
                                savedRecipes = savedRecipes,
                                onRecipeClick = { viewModel.openRecipe(it) },
                                onStartCooking = {
                                    viewModel.openRecipe(it)
                                    isInCookMode = true
                                },
                                onRemoveRecipe = { viewModel.toggleBookmark(it) },
                                onExploreClick = { currentDestination = AppDestination.DISCOVER }
                            )
                        }
                    }
                }
            }
        }
    }
}
