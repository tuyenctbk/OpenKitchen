package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.model.CategoryDto
import com.example.data.model.Recipe
import com.example.data.remote.SpoonacularApi
import com.example.data.remote.TheMealDbApi
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeUiState(
    val featuredRecipes: List<Recipe> = emptyList(),
    val quickRecipes: List<Recipe> = emptyList(),
    val healthyRecipes: List<Recipe> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val selectedCategory: String = "Pasta",
    val categoryRecipes: List<Recipe> = emptyList(),
    val activeRecipe: Recipe? = null,
    val activeServings: Int = 4,
    val activeCookStepIndex: Int = 0,
    val keepScreenOn: Boolean = true,
    val searchQuery: String = "",
    val searchResults: List<Recipe> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val isCategoryLoading: Boolean = false,
    val userNotesDraft: String = "",
    // Category-based filter screen state
    val filterCategory: String = "Breakfast",
    val filterMaxPrepMinutes: Int? = null,
    val filterDiet: String? = null,
    val filterSearchQuery: String = "",
    val filterRecipes: List<Recipe> = emptyList(),
    val isFilterLoading: Boolean = false
)

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    private val spoonacularApiKey: String = runCatching {
        // Safe check for Spoonacular API key if configured in BuildConfig or environment
        val field = BuildConfig::class.java.getField("SPOONACULAR_API_KEY")
        field.get(null) as? String ?: ""
    }.getOrDefault("")

    init {
        val db = AppDatabase.getInstance(application)
        val api = TheMealDbApi.create()
        val spoonApi = SpoonacularApi.create()
        repository = RecipeRepository(api, spoonApi, db.recipeDao(), db.favoriteDao())
    }

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    val savedRecipes: StateFlow<List<Recipe>> = repository.savedRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteRecipes: StateFlow<List<Recipe>> = repository.favoriteRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadInitialData()
        loadFilteredRecipes("Breakfast", null, null, "")
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val categories = repository.getCategories()
            val featured = repository.getFeaturedRecipes()

            val quick = featured.filter { it.prepTimeMinutes <= 25 }
            val healthy = featured.filter {
                it.category.equals("Vegetarian", true) ||
                it.category.equals("Seafood", true) ||
                it.tags.any { tag -> tag.contains("Veg", true) || tag.contains("Healthy", true) }
            }

            _uiState.update {
                it.copy(
                    categories = categories,
                    featuredRecipes = featured,
                    quickRecipes = if (quick.isNotEmpty()) quick else featured.take(3),
                    healthyRecipes = if (healthy.isNotEmpty()) healthy else featured.takeLast(3),
                    isLoading = false
                )
            }

            // Load initial category dishes
            selectCategory("Breakfast")
        }
    }

    fun selectCategory(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedCategory = category, isCategoryLoading = true) }
            val recipes = repository.getCategoryFilteredRecipes(
                category = category,
                spoonacularApiKey = spoonacularApiKey
            )
            _uiState.update { it.copy(categoryRecipes = recipes, isCategoryLoading = false) }
        }
    }

    fun openRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val fullRecipe = repository.getRecipeById(recipe.id) ?: recipe
            _uiState.update {
                it.copy(
                    activeRecipe = fullRecipe,
                    activeServings = fullRecipe.baseServings,
                    activeCookStepIndex = 0,
                    userNotesDraft = fullRecipe.userNotes.orEmpty()
                )
            }
        }
    }

    fun openRecipeById(id: String) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(id)
            if (recipe != null) {
                openRecipe(recipe)
            }
        }
    }

    fun clearActiveRecipe() {
        _uiState.update { it.copy(activeRecipe = null) }
    }

    fun updateServings(newServings: Int) {
        if (newServings in 1..16) {
            _uiState.update { it.copy(activeServings = newServings) }
        }
    }

    fun setCookStep(index: Int) {
        val totalSteps = _uiState.value.activeRecipe?.steps?.size ?: 1
        if (index in 0 until totalSteps) {
            _uiState.update { it.copy(activeCookStepIndex = index) }
        }
    }

    fun nextCookStep() {
        val current = _uiState.value.activeCookStepIndex
        val total = _uiState.value.activeRecipe?.steps?.size ?: 1
        if (current < total - 1) {
            _uiState.update { it.copy(activeCookStepIndex = current + 1) }
        }
    }

    fun prevCookStep() {
        val current = _uiState.value.activeCookStepIndex
        if (current > 0) {
            _uiState.update { it.copy(activeCookStepIndex = current - 1) }
        }
    }

    fun toggleKeepScreenOn() {
        _uiState.update { it.copy(keepScreenOn = !it.keepScreenOn) }
    }

    fun toggleBookmark(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleSave(recipe, _uiState.value.userNotesDraft)
        }
    }

    fun updateNotesDraft(notes: String) {
        _uiState.update { it.copy(userNotesDraft = notes) }
    }

    fun saveNotes() {
        val recipe = _uiState.value.activeRecipe ?: return
        val notes = _uiState.value.userNotesDraft
        viewModelScope.launch {
            repository.updateNotes(recipe.id, notes)
            // also update activeRecipe local instance
            _uiState.update {
                it.copy(activeRecipe = recipe.copy(userNotes = notes.ifBlank { null }))
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            val results = repository.searchRecipes(query)
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    fun loadRandomRecipe() {
        viewModelScope.launch {
            val random = repository.getRandomRecipe()
            openRecipe(random)
        }
    }

    // Category-based filter actions (Breakfast, Vegan, Quick Meals, etc.)
    fun setFilterCategory(category: String) {
        _uiState.update { it.copy(filterCategory = category) }
        loadFilteredRecipes(
            category = category,
            maxPrep = _uiState.value.filterMaxPrepMinutes,
            diet = _uiState.value.filterDiet,
            query = _uiState.value.filterSearchQuery
        )
    }

    fun setFilterMaxPrepMinutes(minutes: Int?) {
        _uiState.update { it.copy(filterMaxPrepMinutes = minutes) }
        loadFilteredRecipes(
            category = _uiState.value.filterCategory,
            maxPrep = minutes,
            diet = _uiState.value.filterDiet,
            query = _uiState.value.filterSearchQuery
        )
    }

    fun setFilterDiet(diet: String?) {
        _uiState.update { it.copy(filterDiet = diet) }
        loadFilteredRecipes(
            category = _uiState.value.filterCategory,
            maxPrep = _uiState.value.filterMaxPrepMinutes,
            diet = diet,
            query = _uiState.value.filterSearchQuery
        )
    }

    fun setFilterSearchQuery(query: String) {
        _uiState.update { it.copy(filterSearchQuery = query) }
        loadFilteredRecipes(
            category = _uiState.value.filterCategory,
            maxPrep = _uiState.value.filterMaxPrepMinutes,
            diet = _uiState.value.filterDiet,
            query = query
        )
    }

    fun loadFilteredRecipes(
        category: String,
        maxPrep: Int?,
        diet: String?,
        query: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFilterLoading = true) }
            val results = repository.getCategoryFilteredRecipes(
                category = category,
                maxReadyTime = maxPrep,
                diet = diet,
                query = query.ifBlank { null },
                spoonacularApiKey = spoonacularApiKey
            )
            _uiState.update {
                it.copy(
                    filterRecipes = results,
                    isFilterLoading = false
                )
            }
        }
    }
}
