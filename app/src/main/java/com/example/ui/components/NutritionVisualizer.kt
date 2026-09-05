package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NutritionInfo

@Composable
fun NutritionVisualizer(
    nutrition: NutritionInfo,
    servings: Int = 4,
    modifier: Modifier = Modifier
) {
    var showTotalRecipe by remember { mutableStateOf(false) }

    val activeNutrition = if (showTotalRecipe) {
        nutrition.scaleFor(servings.toFloat())
    } else {
        nutrition
    }

    // Colors matching high-contrast Professional Polish guidelines
    val proteinColor = Color(0xFF2E7D32) // Forest Emerald
    val proteinBg = Color(0xFFE8F5E9)
    val carbsColor = Color(0xFFE65100)   // Deep Amber / Gold
    val carbsBg = Color(0xFFFFF3E0)
    val fatColor = Color(0xFFC2185B)     // Rich Rose / Coral
    val fatBg = Color(0xFFFCE4EC)

    val dailyCalorieTarget = 2000
    val dailyCaloriePercent = ((activeNutrition.calories.toFloat() / dailyCalorieTarget) * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                RoundedCornerShape(22.dp)
            )
            .tvFocusable("nutrition_visualizer_card", shape = RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Title & Scope Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.nutritional_breakdown_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (showTotalRecipe) stringResource(R.string.total_dish_servings_format, servings) else stringResource(R.string.estimated_per_serving),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Per-serving vs Whole dish Chip Toggle
                FilterChip(
                    selected = showTotalRecipe,
                    onClick = { showTotalRecipe = !showTotalRecipe },
                    label = {
                        Text(
                            text = if (showTotalRecipe) stringResource(R.string.whole_dish) else stringResource(R.string.per_serving),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = showTotalRecipe,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.tvFocusable("btn_toggle_nutrition_scope", shape = RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Calorie Metric & Daily Value Indicator
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            AnimatedContent(
                                targetState = activeNutrition.calories,
                                label = "calAnim"
                            ) { cal ->
                                Text(
                                    text = stringResource(R.string.calories_kcal_format, cal),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = if (showTotalRecipe) stringResource(R.string.entire_recipe_energy) else stringResource(R.string.energy_per_serving),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Daily Value Pill
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.daily_value_format, dailyCaloriePercent),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Macro Ratio Distribution Stacked Bar
            Text(
                text = stringResource(R.string.macronutrient_ratio_title),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stacked Bar
            val protRatio = activeNutrition.proteinRatio
            val carbRatio = activeNutrition.carbsRatio
            val fatRatio = activeNutrition.fatRatio

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.matchParentSize()) {
                    if (protRatio > 0.01f) {
                        Box(
                            modifier = Modifier
                                .weight(protRatio)
                                .fillMaxHeight()
                                .background(proteinColor)
                        )
                    }
                    if (carbRatio > 0.01f) {
                        Box(
                            modifier = Modifier
                                .weight(carbRatio)
                                .fillMaxHeight()
                                .background(carbsColor)
                        )
                    }
                    if (fatRatio > 0.01f) {
                        Box(
                            modifier = Modifier
                                .weight(fatRatio)
                                .fillMaxHeight()
                                .background(fatColor)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Macro Cards: Protein, Carbs, Fat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MacroCard(
                    name = stringResource(R.string.macro_protein),
                    grams = activeNutrition.proteinGrams,
                    calories = activeNutrition.proteinCalories,
                    percent = (protRatio * 100).toInt(),
                    accentColor = proteinColor,
                    bgColor = proteinBg,
                    modifier = Modifier.weight(1f)
                )

                MacroCard(
                    name = stringResource(R.string.macro_carbs),
                    grams = activeNutrition.carbsGrams,
                    calories = activeNutrition.carbsCalories,
                    percent = (carbRatio * 100).toInt(),
                    accentColor = carbsColor,
                    bgColor = carbsBg,
                    modifier = Modifier.weight(1f)
                )

                MacroCard(
                    name = stringResource(R.string.macro_fat),
                    grams = activeNutrition.fatGrams,
                    calories = activeNutrition.fatCalories,
                    percent = (fatRatio * 100).toInt(),
                    accentColor = fatColor,
                    bgColor = fatBg,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Micronutrient Badges: Fiber, Sodium, Net Carbs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MicroNutrientPill(
                    label = stringResource(R.string.dietary_fiber),
                    value = stringResource(R.string.grams_format, activeNutrition.fiberGrams),
                    modifier = Modifier.weight(1f)
                )
                MicroNutrientPill(
                    label = stringResource(R.string.net_carbs),
                    value = stringResource(R.string.grams_format, (activeNutrition.carbsGrams - activeNutrition.fiberGrams).coerceAtLeast(0)),
                    modifier = Modifier.weight(1f)
                )
                MicroNutrientPill(
                    label = stringResource(R.string.sodium),
                    value = stringResource(R.string.milligrams_format, activeNutrition.sodiumMg),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MacroCard(
    name: String,
    grams: Int,
    calories: Int,
    percent: Int,
    accentColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.grams_format, grams),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.macro_percent_cal_format, percent, calories),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF49454F)
            )
        }
    }
}

@Composable
private fun MicroNutrientPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
