package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Recipe
import com.example.ui.components.HeroBannerSkeleton
import com.example.ui.components.RecipeCard
import com.example.ui.components.RecipeCarouselSkeletonRow
import com.example.ui.components.tvFocusable
import com.example.ui.viewmodel.RecipeUiState

@Composable
fun DiscoverScreen(
    state: RecipeUiState,
    savedRecipeIds: Set<String>,
    onRecipeClick: (Recipe) -> Unit,
    onStartCooking: (Recipe) -> Unit,
    onCategorySelect: (String) -> Unit,
    onBookmarkToggle: (Recipe) -> Unit,
    onSurpriseMe: () -> Unit,
    onSearchClick: (() -> Unit)? = null,
    onOpenFilterScreen: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val heroRecipe = state.featuredRecipes.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header matching "Professional Polish" header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title and Profile Avatar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.kitchen_open_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                            .tvFocusable("btn_user_avatar", shape = CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = stringResource(R.string.profile_content_description),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Polished Pill Search Bar
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    ),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .tvFocusable(
                            tag = "search_bar_pill",
                            shape = RoundedCornerShape(28.dp),
                            onClick = onSearchClick
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.nav_search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Category & Dietary Preference Filter System
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            text = stringResource(R.string.browse_category_diet_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.browse_category_diet_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (onOpenFilterScreen != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .wrapContentSize()
                                .defaultMinSize(minHeight = 36.dp)
                                .tvFocusable(
                                    tag = "btn_open_full_filter",
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = onOpenFilterScreen
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
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
                                    text = stringResource(R.string.all_filters),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.categories.forEach { category ->
                        val isSelected = category.strCategory.equals(state.selectedCategory, ignoreCase = true)
                        val categoryIcon = when (category.strCategory.lowercase()) {
                            "breakfast" -> Icons.Default.Restaurant
                            "vegan" -> Icons.Default.Spa
                            "quick meals" -> Icons.Default.Bolt
                            "vegetarian" -> Icons.Default.Spa
                            "pasta" -> Icons.Default.LocalDining
                            "seafood" -> Icons.Default.SetMeal
                            "dessert" -> Icons.Default.Cake
                            else -> Icons.Default.Restaurant
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(category.strCategory) },
                            leadingIcon = {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    text = category.strCategory,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.tvFocusable(
                                tag = "chip_cat_${category.strCategory}",
                                shape = RoundedCornerShape(12.dp),
                                onClick = { onCategorySelect(category.strCategory) }
                            )
                        )
                    }
                }
            }
        }

        // Hero Spotlight Card with 32.dp (rounded-[2rem])
        if (heroRecipe != null) {
            item {
                FeaturedHeroBanner(
                    recipe = heroRecipe,
                    onViewRecipe = { onRecipeClick(heroRecipe) },
                    onStartCooking = { onStartCooking(heroRecipe) },
                    onSurpriseMe = onSurpriseMe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        } else {
            item {
                HeroBannerSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        // Section 1: Selected Category Showcase
        item {
            RecipeCarouselSection(
                title = stringResource(R.string.popular_in_category, state.selectedCategory),
                subtitle = stringResource(R.string.popular_in_category_subtitle),
                recipes = state.categoryRecipes,
                savedIds = savedRecipeIds,
                isLoading = state.isCategoryLoading,
                onRecipeClick = onRecipeClick,
                onBookmarkToggle = onBookmarkToggle
            )
        }

        // Section 2: Quick Weeknight Dinners (<= 25 mins)
        if (state.quickRecipes.isNotEmpty() || state.isLoading) {
            item {
                RecipeCarouselSection(
                    title = stringResource(R.string.quick_easy_dinners_title),
                    subtitle = stringResource(R.string.quick_easy_dinners_subtitle),
                    recipes = state.quickRecipes,
                    savedIds = savedRecipeIds,
                    isLoading = state.isLoading,
                    onRecipeClick = onRecipeClick,
                    onBookmarkToggle = onBookmarkToggle
                )
            }
        }

        // Section 3: Healthy & Fresh
        if (state.healthyRecipes.isNotEmpty() || state.isLoading) {
            item {
                RecipeCarouselSection(
                    title = stringResource(R.string.healthy_fresh_title),
                    subtitle = stringResource(R.string.healthy_fresh_subtitle),
                    recipes = state.healthyRecipes,
                    savedIds = savedRecipeIds,
                    isLoading = state.isLoading,
                    onRecipeClick = onRecipeClick,
                    onBookmarkToggle = onBookmarkToggle
                )
            }
        }

        // Section 4: All Featured Spotlight
        item {
            RecipeCarouselSection(
                title = stringResource(R.string.recent_finds_title),
                subtitle = stringResource(R.string.recent_finds_subtitle),
                recipes = state.featuredRecipes,
                savedIds = savedRecipeIds,
                isLoading = state.isLoading,
                onRecipeClick = onRecipeClick,
                onBookmarkToggle = onBookmarkToggle
            )
        }
    }
}

@Composable
fun FeaturedHeroBanner(
    recipe: Recipe,
    onViewRecipe: () -> Unit,
    onStartCooking: () -> Unit,
    onSurpriseMe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .tvFocusable("hero_spotlight_card", shape = RoundedCornerShape(32.dp), onClick = onViewRecipe),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1B20)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Hero Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Cinematic Gradient from black/80 via black/20 to transparent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x33000000),
                                Color(0x661D1B20),
                                Color(0xEE1D1B20)
                            )
                        )
                    )
            )

            // Top-left Frosted Glass Tag: "Daily Special"
            Surface(
                color = Color(0x4DFFFFFF),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4DFFFFFF)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.daily_special_tag),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            // Content at Bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        lineHeight = 30.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Meta row with schedule and bar_chart
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.prep_time_minutes_format, recipe.prepTimeMinutes),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = recipe.difficulty,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "•",
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Text(
                        text = recipe.category,
                        color = Color(0xFFD0BCFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onStartCooking,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 44.dp)
                            .tvFocusable("btn_hero_start_cooking", shape = RoundedCornerShape(12.dp), onClick = onStartCooking)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_start_cooking),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    OutlinedButton(
                        onClick = onSurpriseMe,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 44.dp)
                            .tvFocusable("btn_hero_surprise", shape = RoundedCornerShape(12.dp), onClick = onSurpriseMe)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_surprise_dish),
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCarouselSection(
    title: String,
    subtitle: String,
    recipes: List<Recipe>,
    savedIds: Set<String>,
    onRecipeClick: (Recipe) -> Unit,
    onBookmarkToggle: (Recipe) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            RecipeCarouselSkeletonRow(itemCount = 4)
        } else if (recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_recipes_in_section),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        isBookmarked = savedIds.contains(recipe.id),
                        onClick = { onRecipeClick(recipe) },
                        onBookmarkToggle = { onBookmarkToggle(recipe) }
                    )
                }
            }
        }
    }
}
