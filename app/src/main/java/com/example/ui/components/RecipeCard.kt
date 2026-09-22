package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Recipe

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false,
    onBookmarkToggle: (() -> Unit)? = null,
    cardWidth: Dp = 260.dp,
    cardHeight: Dp = 210.dp
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .tvFocusable(
                tag = "recipe_card_${recipe.id}",
                shape = RoundedCornerShape(20.dp),
                focusedScale = 1.06f,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedBorderWidth = 3.5.dp,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                unfocusedBorderWidth = 1.dp,
                onClick = onClick,
                onFocusChanged = { isFocused = it }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFocused) 12.dp else 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Recipe Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Bottom Gradient for pristine legibility across any bright or dark recipe photo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.35f to Color(0x33000000),
                            0.62f to Color(0xCC121212),
                            1.0f to Color(0xFA121212)
                        )
                    )
            )

            // TV Focus Indicator Badge (Only shown on TV devices)
            val context = LocalContext.current
            val isTv = LocalIsTvDevice.current || context.isTvDevice()
            if (isTv && isFocused) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = stringResource(R.string.select_to_cook),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Top Row: Category Chip & Bookmark (Professional Polish Glass/Pill Style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = recipe.category.uppercase(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (onBookmarkToggle != null) {
                    val bookmarkScale by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = if (isBookmarked) 1.25f else 1.0f,
                        animationSpec = androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                        ),
                        label = "bookmarkScale"
                    )
                    Surface(
                        color = Color(0x991D1B20),
                        shape = CircleShape,
                        modifier = Modifier.size(34.dp)
                    ) {
                        IconButton(
                            onClick = onBookmarkToggle,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isBookmarked) stringResource(R.string.remove_bookmark_desc) else stringResource(R.string.add_to_cookbook_desc),
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primaryContainer else Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .scale(bookmarkScale)
                            )
                        }
                    }
                }
            }

            // Bottom Info (Title and Meta)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = stringResource(R.string.prep_time_desc),
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.prep_time_minutes_format, recipe.prepTimeMinutes),
                        color = Color(0xFFE6E1E5),
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )

                    Text(
                        text = " • ",
                        color = Color(0xFFCAC4D0),
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )

                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = stringResource(R.string.difficulty_desc),
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = recipe.difficulty,
                        color = Color(0xFFE6E1E5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )

                    Text(
                        text = " • ",
                        color = Color(0xFFCAC4D0),
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )

                    Text(
                        text = recipe.area,
                        color = Color(0xFFCAC4D0),
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
