package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Recipe
import com.example.ui.components.RecipeCard
import com.example.ui.components.RecipeCardSkeleton
import com.example.ui.components.tvFocusable

data class CategoryFilterItem(
    val name: String,
    val icon: ImageVector,
    val labelRes: Int
)

val FILTER_CATEGORIES = listOf(
    CategoryFilterItem("Breakfast", Icons.Default.WbSunny, R.string.category_breakfast),
    CategoryFilterItem("Vegan", Icons.Default.Spa, R.string.category_vegan),
    CategoryFilterItem("Quick Meals", Icons.Default.ElectricBolt, R.string.category_quick_meals),
    CategoryFilterItem("Vegetarian", Icons.Default.FilterAlt, R.string.category_vegetarian),
    CategoryFilterItem("Pasta", Icons.Default.RestaurantMenu, R.string.category_pasta),
    CategoryFilterItem("Seafood", Icons.Default.RestaurantMenu, R.string.category_seafood),
    CategoryFilterItem("Dessert", Icons.Default.RestaurantMenu, R.string.category_dessert),
    CategoryFilterItem("Chicken", Icons.Default.RestaurantMenu, R.string.category_chicken)
)

data class TimeFilterItem(
    val minutes: Int?,
    val labelRes: Int
)

val TIME_FILTERS = listOf(
    TimeFilterItem(null, R.string.any_time),
    TimeFilterItem(15, R.string.max_time_15),
    TimeFilterItem(25, R.string.max_time_25),
    TimeFilterItem(35, R.string.max_time_35),
    TimeFilterItem(45, R.string.max_time_45)
)

@Composable
fun CategoryFilterScreen(
    selectedCategory: String,
    selectedMaxTime: Int?,
    selectedDiet: String?,
    searchQuery: String,
    recipes: List<Recipe>,
    savedIds: Set<String>,
    isLoading: Boolean,
    onCategorySelected: (String) -> Unit,
    onMaxTimeSelected: (Int?) -> Unit,
    onDietSelected: (String?) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onBookmarkToggle: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.category_filter_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.category_filter_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .wrapContentSize()
                        .defaultMinSize(minHeight = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.dishes_count, recipes.size),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // Search Bar for within filtered category
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = {
                Text(
                    text = stringResource(R.string.filter_in_category_placeholder, selectedCategory),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChanged("") },
                        modifier = Modifier.tvFocusable("btn_filter_clear_search", shape = CircleShape, onClick = { onSearchQueryChanged("") })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_content_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .tvFocusable("filter_search_input", shape = RoundedCornerShape(20.dp))
        )

        // Main Categories Carousel (Breakfast, Vegan, Quick Meals, etc.)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FILTER_CATEGORIES.forEach { item ->
                val isSelected = selectedCategory.equals(item.name, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(item.name) },
                    label = {
                        Text(
                            text = stringResource(item.labelRes),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = if (isSelected) null else FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        enabled = true,
                        selected = false
                    ),
                    modifier = Modifier.tvFocusable(
                        tag = "category_chip_${item.name}",
                        shape = RoundedCornerShape(14.dp),
                        onClick = { onCategorySelected(item.name) }
                    )
                )
            }
        }

        // Prep Time & Diet Sub-filters Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            TIME_FILTERS.forEach { filterItem ->
                val isSelected = selectedMaxTime == filterItem.minutes
                val labelText = stringResource(filterItem.labelRes)
                Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.secondary) else null,
                    modifier = Modifier
                        .wrapContentSize()
                        .defaultMinSize(minHeight = 32.dp)
                        .tvFocusable(
                            tag = "time_chip_${filterItem.minutes ?: "any"}",
                            shape = RoundedCornerShape(10.dp),
                            onClick = { onMaxTimeSelected(filterItem.minutes) }
                        )
                ) {
                    Text(
                        text = labelText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        // Recipes Grid or Shimmer Skeletons
        if (isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) {
                items(6) {
                    RecipeCardSkeleton(cardWidth = 260.dp, cardHeight = 210.dp)
                }
            }
        } else if (recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.no_recipes_found_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.no_recipes_found_filter_desc, selectedCategory),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        isBookmarked = savedIds.contains(recipe.id),
                        onClick = { onRecipeClick(recipe) },
                        onBookmarkToggle = { onBookmarkToggle(recipe) },
                        cardWidth = 260.dp,
                        cardHeight = 210.dp
                    )
                }
            }
        }
    }
}
