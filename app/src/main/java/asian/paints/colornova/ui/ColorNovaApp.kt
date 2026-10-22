package asian.paints.colornova.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import asian.paints.colornova.data.ColorCategory
import asian.paints.colornova.data.ColorItem
import asian.paints.colornova.data.ColorPage
import asian.paints.colornova.data.SampleColorRepository
import asian.paints.colornova.data.pageRange
import asian.paints.colornova.ui.theme.ColorNovaTheme
import asian.paints.colornova.ui.theme.DarkAppColors
import asian.paints.colornova.ui.theme.LightAppColors
import asian.paints.colornova.ui.theme.LocalAppColors
import asian.paints.colornova.ui.theme.NovaCoral
import asian.paints.colornova.ui.theme.NovaTeal
import asian.paints.colornova.ui.theme.NovaViolet
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorNovaApp() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        SampleColorRepository.loadFromAssets(context)
    }

    val prefs = remember { context.getSharedPreferences("colornova_prefs", Context.MODE_PRIVATE) }
    var isDarkTheme by remember {
        mutableStateOf(prefs.getBoolean("is_dark_theme", true))
    }

    val initialFavorites = remember {
        prefs.getStringSet("saved_favorites", null) ?: setOf("L146", "7670", "9616", "9645")
    }
    var favorites by remember { mutableStateOf(initialFavorites) }

    fun toggleFavorite(id: String) {
        val updated = favorites.toMutableSet()
        if (updated.contains(id)) updated.remove(id) else updated.add(id)
        favorites = updated
        prefs.edit().putStringSet("saved_favorites", updated).apply()
    }

    fun toggleTheme() {
        val newTheme = !isDarkTheme
        isDarkTheme = newTheme
        prefs.edit().putBoolean("is_dark_theme", newTheme).apply()
    }

    var isFavoritesView by remember { mutableStateOf(false) }
    var fullScreenColor by remember { mutableStateOf<ColorItem?>(null) }
    var selectedCategory by remember { mutableStateOf(ColorCategory.LIGHT) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedColorForDetail by remember { mutableStateOf<ColorItem?>(null) }
    var colorToShare by remember { mutableStateOf<ColorItem?>(null) }

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val pageListState = rememberLazyListState()

    // Map of colors grouped by page for instant swatch lookup in page gradient bar
    val colorsByPage = remember {
        SampleColorRepository.getAllColors().groupBy { it.page }
    }

    var glideJob by remember { mutableStateOf<Job?>(null) }

    // Search and clubbing per category
    val lightPages = remember(searchQuery) {
        SampleColorRepository.searchPages(searchQuery, ColorCategory.LIGHT)
    }
    val mediumPages = remember(searchQuery) {
        SampleColorRepository.searchPages(searchQuery, ColorCategory.MEDIUM)
    }
    val darkPages = remember(searchQuery) {
        SampleColorRepository.searchPages(searchQuery, ColorCategory.DARK)
    }
    val comboPages = remember(searchQuery) {
        SampleColorRepository.searchPages(searchQuery, ColorCategory.COMBINATION)
    }

    val pageCountsByCategory = remember(lightPages, mediumPages, darkPages, comboPages) {
        mapOf(
            ColorCategory.LIGHT to lightPages.size,
            ColorCategory.MEDIUM to mediumPages.size,
            ColorCategory.DARK to darkPages.size,
            ColorCategory.COMBINATION to comboPages.size
        )
    }

    val allCategories = remember {
        listOf(
            ColorCategory.LIGHT,
            ColorCategory.MEDIUM,
            ColorCategory.DARK,
            ColorCategory.COMBINATION
        )
    }

    // Hide tabs with empty results when searching:
    val visibleCategories = remember(searchQuery, pageCountsByCategory) {
        if (searchQuery.isBlank()) {
            allCategories
        } else {
            val nonEmpties = allCategories.filter { (pageCountsByCategory[it] ?: 0) > 0 }
            if (nonEmpties.isEmpty()) allCategories else nonEmpties
        }
    }

    // Dynamic Tab Switching: If searching and current tab has 0 results, switch to first tab with results
    LaunchedEffect(searchQuery, visibleCategories) {
        if (searchQuery.isNotBlank()) {
            val currentCount = pageCountsByCategory[selectedCategory] ?: 0
            if (currentCount == 0) {
                val firstWithResults = visibleCategories.firstOrNull { (pageCountsByCategory[it] ?: 0) > 0 }
                if (firstWithResults != null && firstWithResults != selectedCategory) {
                    selectedCategory = firstWithResults
                }
            }
        }
    }

    // Active pages for the selected tab
    val displayPages = when (selectedCategory) {
        ColorCategory.LIGHT -> lightPages
        ColorCategory.MEDIUM -> mediumPages
        ColorCategory.DARK -> darkPages
        ColorCategory.COMBINATION -> comboPages
    }

    // Track active page currently in view
    val currentVisiblePage by remember(displayPages) {
        derivedStateOf {
            val idx = pageListState.firstVisibleItemIndex
            displayPages.getOrNull(idx)?.pageNumber ?: selectedCategory.pageRange.first
        }
    }

    val appColors = if (isDarkTheme) DarkAppColors else LightAppColors

    ColorNovaTheme(darkTheme = isDarkTheme) {
        CompositionLocalProvider(LocalAppColors provides appColors) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = appColors.background,
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isFavoritesView) {
                        BackHandler { isFavoritesView = false }
                        FavoritesPageView(
                            favorites = favorites,
                            allColors = SampleColorRepository.getAllColors(),
                            onBack = { isFavoritesView = false },
                            onToggleFavorite = { toggleFavorite(it) },
                            onFullScreen = { fullScreenColor = it },
                            onColorClick = { selectedColorForDetail = it },
                            onCopyAll = {
                                val allFavs = SampleColorRepository.getAllColors().filter { it.id in favorites }
                                val hexList = allFavs.joinToString(", ") { "${it.name} (${it.hex})" }
                                clipboardManager.setText(AnnotatedString(hexList))
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Copied all ${allFavs.size} favorite shades")
                                }
                            },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { toggleTheme() }
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Top Action Bar with Search, Favorites button, Theme Toggle
                            TopActionBar(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                onClearQuery = { searchQuery = "" },
                                favoritesCount = favorites.size,
                                onOpenFavorites = { isFavoritesView = true },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { toggleTheme() }
                            )

                            // Category TabBar with dynamic hiding of empty tabs
                            CategoryTabBar(
                                categories = visibleCategories,
                                selectedCategory = selectedCategory,
                                categoryCounts = pageCountsByCategory,
                                isSearching = searchQuery.isNotBlank(),
                                onCategorySelected = { newCat ->
                                    selectedCategory = newCat
                                    coroutineScope.launch {
                                        pageListState.scrollToItem(0)
                                    }
                                }
                            )

                            // Page Scrubber / Gradient Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PageGradientBar(
                                    category = selectedCategory,
                                    activePage = currentVisiblePage,
                                    colorsByPage = colorsByPage,
                                    onPageGlided = { page ->
                                        if (searchQuery.isNotEmpty()) {
                                            searchQuery = ""
                                        }
                                        glideJob?.cancel()
                                        glideJob = coroutineScope.launch {
                                            val targetIndex = displayPages.indexOfFirst { it.pageNumber == page }
                                            if (targetIndex >= 0) {
                                                pageListState.scrollToItem(targetIndex)
                                            }
                                        }
                                    }
                                )
                            }

                            // Content Area: Page clubbing for Light, Medium, Dark, and Combos
                            if (displayPages.isEmpty()) {
                                EmptySearchResults(
                                    query = searchQuery,
                                    onClearQuery = { searchQuery = "" }
                                )
                            } else {
                                LazyColumn(
                                    state = pageListState,
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(displayPages, key = { "${it.category}_${it.pageNumber}" }) { page ->
                                        PageClubCard(
                                            page = page,
                                            favorites = favorites,
                                            searchQuery = searchQuery,
                                            onToggleFavorite = { toggleFavorite(it) },
                                            onFullScreenClick = { fullScreenColor = it },
                                            onColorClick = { selectedColorForDetail = it },
                                            onCopyPage = {
                                                val hexList = page.colors.joinToString(", ") { "${it.name} (${it.hex})" }
                                                clipboardManager.setText(AnnotatedString(hexList))
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Copied Page ${page.pageNumber} (${page.colors.size} shades)")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Full Screen Color Dialog (Available in both catalog and favorites view)
                    fullScreenColor?.let { color ->
                        FullScreenColorView(
                            color = color,
                            isFavorite = favorites.contains(color.id),
                            onToggleFavorite = { toggleFavorite(color.id) },
                            onDismiss = { fullScreenColor = null },
                            onShare = {
                                fullScreenColor = null
                                colorToShare = color
                            },
                            onCopy = { text, label ->
                                clipboardManager.setText(AnnotatedString(text))
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Copied $label: $text")
                                }
                            }
                        )
                    }

                    // Detail Bottom Sheet for Color
                    selectedColorForDetail?.let { color ->
                        ColorDetailSheet(
                            color = color,
                            onDismiss = { selectedColorForDetail = null },
                            onCopy = { text, label ->
                                clipboardManager.setText(AnnotatedString(text))
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Copied $label: $text")
                                }
                            },
                            onShare = {
                                colorToShare = color
                            }
                        )
                    }

                    // Share Dialog for Color
                    colorToShare?.let { color ->
                        ShareColorDialog(
                            color = color,
                            onDismiss = { colorToShare = null },
                            onShare = { userNote ->
                                ColorShareHelper.generateAndShare(context, color, userNote)
                                colorToShare = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopActionBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    favoritesCount: Int,
    onOpenFavorites: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search Bar Field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Search name, code, #hex, page...",
                    color = appColors.textTertiary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = appColors.accentTeal,
                    modifier = Modifier.size(19.dp)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = appColors.textTertiary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = appColors.surface,
                unfocusedContainerColor = appColors.surface,
                focusedBorderColor = appColors.accentTeal,
                unfocusedBorderColor = appColors.border,
                focusedTextColor = appColors.textPrimary,
                unfocusedTextColor = appColors.textPrimary,
                cursorColor = appColors.accentTeal
            )
        )

        // Saved Favorites Button with Count
        Box(
            modifier = Modifier
                .height(52.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(appColors.surface)
                .border(
                    1.dp,
                    if (favoritesCount > 0) appColors.accentCoral.copy(alpha = 0.5f) else appColors.border,
                    RoundedCornerShape(14.dp)
                )
                .clickable { onOpenFavorites() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (favoritesCount > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorites page",
                    tint = if (favoritesCount > 0) appColors.accentCoral else appColors.textTertiary,
                    modifier = Modifier.size(20.dp)
                )
                if (favoritesCount > 0) {
                    Text(
                        text = "$favoritesCount",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = appColors.accentCoral
                    )
                }
            }
        }

        // Theme Toggle Button (Light / Dark)
        Box(
            modifier = Modifier
                .height(52.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(appColors.surface)
                .border(1.dp, appColors.border, RoundedCornerShape(14.dp))
                .clickable { onToggleTheme() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme",
                tint = if (isDarkTheme) Color(0xFFFFD54F) else Color(0xFF6366F1),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun FavoritesPageView(
    favorites: Set<String>,
    allColors: List<ColorItem>,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onFullScreen: (ColorItem) -> Unit,
    onColorClick: (ColorItem) -> Unit,
    onCopyAll: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val appColors = LocalAppColors.current
    val clipboardManager = LocalClipboardManager.current
    var favSearchQuery by remember { mutableStateOf("") }
    var showCustomCombination by remember { mutableStateOf(false) }

    val favoriteColors = remember(favorites, allColors) {
        favorites.mapNotNull { id -> allColors.firstOrNull { it.id.equals(id, ignoreCase = true) } }
    }

    val filteredColors = remember(favSearchQuery, favoriteColors) {
        if (favSearchQuery.isBlank()) favoriteColors
        else favoriteColors.filter { it.matchesQuery(favSearchQuery) }
    }

    if (showCustomCombination) {
        BackHandler { showCustomCombination = false }
        CustomCombinationView(
            favoriteColors = favoriteColors,
            onBack = { showCustomCombination = false },
            onFullScreenColor = onFullScreen,
            onCopyCombo = { comboList ->
                val hexList = comboList.joinToString(", ") { "${it.name} (${it.hex})" }
                clipboardManager.setText(AnnotatedString(hexList))
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(appColors.surface)
                        .border(1.dp, appColors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Catalog",
                        tint = appColors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Favorites",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(appColors.accentCoral.copy(alpha = 0.16f))
                                .border(0.8.dp, appColors.accentCoral.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${favoriteColors.size} saved",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.accentCoral
                            )
                        }
                    }
                    Text(
                        text = "Saved permanently in your palette",
                        fontSize = 11.5.sp,
                        color = appColors.textTertiary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (favoriteColors.isNotEmpty()) {
                    // Custom Combination Button in Header
                    IconButton(
                        onClick = { showCustomCombination = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(appColors.accentCoral.copy(alpha = 0.16f))
                            .border(1.dp, appColors.accentCoral.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Custom Combination",
                            tint = appColors.accentCoral,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onCopyAll,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(appColors.surface)
                            .border(1.dp, appColors.border, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy all favorites",
                            tint = appColors.accentTeal,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(appColors.surface)
                        .border(1.dp, appColors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle theme",
                        tint = if (isDarkTheme) Color(0xFFFFD54F) else Color(0xFF6366F1),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        if (favoriteColors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(appColors.accentCoral.copy(alpha = 0.12f))
                            .border(1.2.dp, appColors.accentCoral.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = appColors.accentCoral,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favorite shades yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the heart icon on any color card across Light, Medium, Dark, or Combos to save your shades permanently.",
                        fontSize = 13.sp,
                        color = appColors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = appColors.accentTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Browse Color Fan Deck",
                            fontWeight = FontWeight.Bold,
                            color = if (appColors.isDark) Color(0xFF0D111D) else Color.White
                        )
                    }
                }
            }
        } else {
            // Custom Combination Hero Card Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clickable { showCustomCombination = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                border = BorderStroke(1.2.dp, appColors.accentCoral.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(appColors.accentCoral, appColors.accentTeal)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Custom Combination",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.textPrimary
                            )
                            Text(
                                text = "View up to 4 favorites joint seamlessly",
                                fontSize = 11.5.sp,
                                color = appColors.textTertiary
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Mini preview of favorite colors joint without gap
                        Row(
                            modifier = Modifier
                                .height(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(0.8.dp, if (appColors.isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        ) {
                            favoriteColors.take(4).forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .fillMaxHeight()
                                        .background(color.toComposeColor())
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = appColors.accentCoral,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            // Search inside favorites if more than 3 items
            if (favoriteColors.size > 3) {
                OutlinedTextField(
                    value = favSearchQuery,
                    onValueChange = { favSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    placeholder = {
                        Text(
                            text = "Filter saved shades by name, code...",
                            color = appColors.textTertiary,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = appColors.textTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (favSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { favSearchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = appColors.textTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = appColors.surface,
                        unfocusedContainerColor = appColors.surface,
                        focusedBorderColor = appColors.accentTeal,
                        unfocusedBorderColor = appColors.border,
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        cursorColor = appColors.accentTeal
                    )
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredColors, key = { it.id }) { color ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onColorClick(color) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = appColors.surface),
                        border = BorderStroke(1.dp, appColors.border)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            // Swatch Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(82.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color.toComposeColor())
                                    .border(
                                        0.8.dp,
                                        if (color.isLight()) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(12.dp)
                                    )
                            ) {
                                // Top-Left: Fullscreen view button
                                IconButton(
                                    onClick = { onFullScreen(color) },
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(3.dp)
                                        .size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.38f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Full Screen",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                // Top-Right: Remove Favorite button
                                IconButton(
                                    onClick = { onToggleFavorite(color.id) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(3.dp)
                                        .size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.38f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Remove Favorite",
                                        tint = appColors.accentCoral,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }

                                // Bottom-Left: Page badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "P.${color.page}",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = color.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = color.id,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = appColors.textSecondary
                                )
                                Text(
                                    text = color.hex,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium,
                                    color = appColors.accentTeal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class ComboLayoutMode(val title: String) {
    COLUMNS("Columns"),
    ROWS("Rows"),
    GRID("Grid")
}

@Composable
fun CustomCombinationView(
    favoriteColors: List<ColorItem>,
    onBack: () -> Unit,
    onFullScreenColor: (ColorItem) -> Unit,
    onCopyCombo: (List<ColorItem>) -> Unit
) {
    val appColors = LocalAppColors.current
    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf<String?>(null) }

    // Selected color IDs from favorites only (up to 4)
    var selectedIds by remember(favoriteColors) {
        mutableStateOf(favoriteColors.take(4).map { it.id }.toMutableList())
    }

    var layoutMode by remember { mutableStateOf(ComboLayoutMode.COLUMNS) }
    var showLabels by remember { mutableStateOf(true) }
    var isPureFullScreen by remember { mutableStateOf(false) }

    val selectedColors = remember(selectedIds, favoriteColors) {
        selectedIds.mapNotNull { id -> favoriteColors.firstOrNull { it.id.equals(id, ignoreCase = true) } }
            .ifEmpty { favoriteColors.take(1) }
    }

    // Pure Full Screen Mode: fills the entire screen with the joint colors without borders or gaps!
    if (isPureFullScreen) {
        BackHandler { isPureFullScreen = false }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            CombinationCanvas(
                colors = selectedColors,
                layoutMode = layoutMode,
                showLabels = showLabels,
                modifier = Modifier.fillMaxSize()
            )

            // Minimalist floating controls
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showLabels = !showLabels },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (showLabels) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle labels",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { isPureFullScreen = false },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(appColors.surface)
                        .border(1.dp, appColors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = appColors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Custom Combination",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(appColors.accentCoral.copy(alpha = 0.16f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${selectedColors.size}/4",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.accentCoral
                            )
                        }
                    }
                    Text(
                        text = "Joint seamless palette from your favorites",
                        fontSize = 11.5.sp,
                        color = appColors.textTertiary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Copy Combo button
                IconButton(
                    onClick = {
                        val hexList = selectedColors.joinToString(", ") { "${it.name} (${it.hex})" }
                        clipboardManager.setText(AnnotatedString(hexList))
                        onCopyCombo(selectedColors)
                        copiedNotice = "Copied ${selectedColors.size} shades"
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(appColors.surface)
                        .border(1.dp, appColors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Combo",
                        tint = appColors.accentTeal,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                // Full Screen button
                IconButton(
                    onClick = { isPureFullScreen = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(appColors.surface)
                        .border(1.dp, appColors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Full Screen",
                        tint = appColors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Joint Combination Canvas (Takes the primary hero space)
        // Completely joint together without any borders or gaps!
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.2.dp, appColors.border)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
            ) {
                CombinationCanvas(
                    colors = selectedColors,
                    layoutMode = layoutMode,
                    showLabels = showLabels,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Toolbar: Layout switcher (Columns / Rows / Grid), Shuffle, Labels toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Layout mode selector pills
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(appColors.surfaceElevated)
                    .border(1.dp, appColors.border, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComboLayoutMode.values().forEach { mode ->
                    val isSelected = layoutMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) appColors.accentCoral else Color.Transparent)
                            .clickable { layoutMode = mode }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = mode.title,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else appColors.textSecondary
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Shuffle favorites button
                if (favoriteColors.size > 1) {
                    IconButton(
                        onClick = {
                            val shuffled = favoriteColors.shuffled().take(4).map { it.id }
                            selectedIds = shuffled.toMutableList()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(appColors.surfaceElevated)
                            .border(1.dp, appColors.border, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = appColors.accentTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Show/Hide labels toggle
                IconButton(
                    onClick = { showLabels = !showLabels },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(appColors.surfaceElevated)
                        .border(1.dp, appColors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = if (showLabels) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle labels",
                        tint = if (showLabels) appColors.accentCoral else appColors.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Bottom Favorites Palette Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pick From Favorites (${selectedColors.size}/4)",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = appColors.textPrimary
                )
                Text(
                    text = "Tap to add or replace",
                    fontSize = 11.sp,
                    color = appColors.textTertiary
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteColors, key = { it.id }) { color ->
                    val isSelected = selectedIds.contains(color.id)
                    val slotIndex = selectedIds.indexOf(color.id)

                    Card(
                        modifier = Modifier
                            .width(82.dp)
                            .clickable {
                                val current = selectedIds.toMutableList()
                                if (isSelected) {
                                    if (current.size > 1) {
                                        current.remove(color.id)
                                        selectedIds = current
                                    }
                                } else {
                                    if (current.size < 4) {
                                        current.add(color.id)
                                    } else {
                                        // Replace 4th color
                                        current[3] = color.id
                                    }
                                    selectedIds = current
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = appColors.surface),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 0.8.dp,
                            color = if (isSelected) appColors.accentCoral else appColors.border
                        )
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.toComposeColor())
                                    .border(0.6.dp, if (color.isLight()) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(appColors.accentCoral),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${slotIndex + 1}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = color.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = color.id,
                                fontSize = 9.sp,
                                color = appColors.textTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CombinationCanvas(
    colors: List<ColorItem>,
    layoutMode: ComboLayoutMode,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    if (colors.isEmpty()) return

    when (layoutMode) {
        ComboLayoutMode.COLUMNS -> {
            Row(modifier = modifier) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color.toComposeColor())
                    ) {
                        if (showLabels) {
                            ColorOverlayBadge(
                                color = color,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        }
        ComboLayoutMode.ROWS -> {
            Column(modifier = modifier) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(color.toComposeColor())
                    ) {
                        if (showLabels) {
                            ColorOverlayBadge(
                                color = color,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 14.dp)
                            )
                        }
                    }
                }
            }
        }
        ComboLayoutMode.GRID -> {
            when (colors.size) {
                1 -> {
                    Box(
                        modifier = modifier.background(colors[0].toComposeColor())
                    ) {
                        if (showLabels) {
                            ColorOverlayBadge(
                                color = colors[0],
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp)
                            )
                        }
                    }
                }
                2 -> {
                    Row(modifier = modifier) {
                        colors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(color.toComposeColor())
                            ) {
                                if (showLabels) {
                                    ColorOverlayBadge(
                                        color = color,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    Column(modifier = modifier) {
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[0].toComposeColor())
                            ) {
                                if (showLabels) {
                                    ColorOverlayBadge(
                                        color = colors[0],
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[1].toComposeColor())
                            ) {
                                if (showLabels) {
                                    ColorOverlayBadge(
                                        color = colors[1],
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(colors[2].toComposeColor())
                        ) {
                            if (showLabels) {
                                ColorOverlayBadge(
                                    color = colors[2],
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
                else -> {
                    // 4 colors in 2x2 grid
                    Column(modifier = modifier) {
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[0].toComposeColor())
                            ) {
                                if (showLabels) {
                                    ColorOverlayBadge(
                                        color = colors[0],
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[1].toComposeColor())
                            ) {
                                if (showLabels) {
                                    ColorOverlayBadge(
                                        color = colors[1],
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[2].toComposeColor())
                            ) {
                                if (showLabels) {
                                    ColorOverlayBadge(
                                        color = colors[2],
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[3].toComposeColor())
                            ) {
                                if (showLabels) {
                                    ColorOverlayBadge(
                                        color = colors[3],
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorOverlayBadge(
    color: ColorItem,
    modifier: Modifier = Modifier
) {
    val isLight = color.isLight()
    val badgeBg = if (isLight) Color.Black.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.65f)
    val textCol = if (isLight) Color.White else Color.Black

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeBg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = color.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textCol,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${color.id} • ${color.hex}",
                fontSize = 9.sp,
                color = textCol.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun FullScreenColorView(
    color: ColorItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onCopy: (String, String) -> Unit
) {
    var showOverlay by remember { mutableStateOf(true) }
    val composeColor = color.toComposeColor()
    val isLightColor = color.isLight()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(composeColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    showOverlay = !showOverlay
                }
        ) {
            AnimatedVisibility(
                visible = showOverlay,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Close Button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close full screen",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Spectra Page Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "ASIAN PAINTS SPECTRA • PAGE ${color.page}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Actions: Favorite & Share
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) NovaCoral else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = onShare,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }

                    // Center subtle prompt
                    Text(
                        text = "Tap anywhere to toggle info",
                        fontSize = 12.sp,
                        color = if (isLightColor) Color.Black.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(
                                if (isLightColor) Color.White.copy(alpha = 0.55f) else Color.Black.copy(alpha = 0.45f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    )

                    // Bottom Floating Info Card
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 28.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF101726).copy(alpha = 0.94f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = color.name,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Shade ${color.id} • Page ${color.page} • ${color.category.displayName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NovaTeal
                                    )
                                }

                                // Quick copy hex pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .border(0.8.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                        .clickable { onCopy(color.hex, "HEX") }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = color.hex,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy HEX",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Values row: RGB, HSL, Tone
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.07f))
                                        .padding(vertical = 8.dp, horizontal = 10.dp)
                                ) {
                                    Column {
                                        Text("RGB", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NovaTeal)
                                        Text(color.rgb, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.07f))
                                        .padding(vertical = 8.dp, horizontal = 10.dp)
                                ) {
                                    Column {
                                        Text("HSL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NovaCoral)
                                        Text(color.hsl, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.07f))
                                        .padding(vertical = 8.dp, horizontal = 10.dp)
                                ) {
                                    Column {
                                        Text("TONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NovaViolet)
                                        Text(if (isLightColor) "Light / Airy" else "Deep / Rich", fontSize = 11.5.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabBar(
    categories: List<ColorCategory>,
    selectedCategory: ColorCategory,
    categoryCounts: Map<ColorCategory, Int>,
    isSearching: Boolean,
    onCategorySelected: (ColorCategory) -> Unit
) {
    val appColors = LocalAppColors.current
    val selectedIndex = categories.indexOf(selectedCategory).coerceAtLeast(0)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = appColors.background,
        contentColor = appColors.accentTeal,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    height = 3.dp,
                    color = when (selectedCategory) {
                        ColorCategory.LIGHT -> appColors.accentGold
                        ColorCategory.MEDIUM -> appColors.accentCoral
                        ColorCategory.DARK -> appColors.accentViolet
                        ColorCategory.COMBINATION -> appColors.accentTeal
                    }
                )
            }
        },
        divider = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(appColors.border)
            )
        },
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            val accentColor = when (category) {
                ColorCategory.LIGHT -> appColors.accentGold
                ColorCategory.MEDIUM -> appColors.accentCoral
                ColorCategory.DARK -> appColors.accentViolet
                ColorCategory.COMBINATION -> appColors.accentTeal
            }

            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = category.displayName,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.5.sp,
                            color = if (isSelected) accentColor else appColors.textSecondary
                        )
                        if (isSearching) {
                            val count = categoryCounts[category] ?: 0
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) accentColor.copy(alpha = 0.2f) else appColors.surfaceElevated)
                                    .border(
                                        0.6.dp,
                                        if (isSelected) accentColor.copy(alpha = 0.5f) else appColors.border,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) accentColor else appColors.textTertiary
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PageGradientBar(
    category: ColorCategory,
    activePage: Int,
    colorsByPage: Map<Int, List<ColorItem>>,
    onPageGlided: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val pageAnchors = remember(category, colorsByPage) {
        val pages = if (category == ColorCategory.MEDIUM) {
            val sampled = (18..182 step 6).toMutableList()
            if (182 !in sampled) {
                sampled.add(182)
            }
            sampled
        } else {
            category.pageRange.toList()
        }
        pages.mapNotNull { pageNum ->
            val pageColors = colorsByPage[pageNum]
            val lastColor = pageColors?.lastOrNull()
            if (lastColor != null) {
                Pair(pageNum, lastColor)
            } else null
        }
    }

    var isGliding by remember { mutableStateOf(false) }
    var glideFraction by remember { mutableFloatStateOf(0f) }
    var glidePage by remember { mutableIntStateOf(activePage) }

    val totalPageSpan = remember(category) {
        (category.pageRange.last - category.pageRange.first).coerceAtLeast(1)
    }
    val targetFraction = remember(activePage, category, totalPageSpan) {
        ((activePage - category.pageRange.first).toFloat() / totalPageSpan).coerceIn(0f, 1f)
    }

    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "thumbFraction"
    )

    val currentFraction = if (isGliding) glideFraction else animatedFraction
    val displayPage = if (isGliding) glidePage else activePage
    val displayAnchor = remember(displayPage, colorsByPage) {
        colorsByPage[displayPage]?.lastOrNull() ?: pageAnchors.firstOrNull()?.second
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page Info Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "P.${category.pageRange.first}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = appColors.textTertiary
            )

            // Dynamic Active / Glided Page Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isGliding) appColors.surfaceElevated else appColors.surface)
                    .border(
                        width = if (isGliding) 1.2.dp else 0.8.dp,
                        color = if (isGliding) (displayAnchor?.toComposeColor() ?: appColors.accentCoral) else appColors.border,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 9.dp, vertical = 2.dp)
            ) {
                if (displayAnchor != null) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(displayAnchor.toComposeColor())
                            .border(0.6.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "Page $displayPage${if (displayAnchor != null) " • ${displayAnchor.name}" else ""}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "P.${category.pageRange.last}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = appColors.textTertiary
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Gradient Bar Track with Touch / Drag Gliding
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val density = LocalDensity.current
            val thumbWidthDp = 18.dp
            val thumbWidthPx = with(density) { thumbWidthDp.toPx() }
            val usableWidthPx = (totalWidthPx - thumbWidthPx).coerceAtLeast(1f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(pageAnchors, totalWidthPx) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            isGliding = true

                            fun updateFromTouch(posX: Float) {
                                val clampedX = posX.coerceIn(0f, totalWidthPx)
                                val frac = (clampedX / totalWidthPx).coerceIn(0f, 1f)
                                glideFraction = frac
                                val targetPage = (category.pageRange.first + (frac * totalPageSpan).roundToInt())
                                    .coerceIn(category.pageRange.first, category.pageRange.last)
                                if (targetPage != glidePage) {
                                    glidePage = targetPage
                                    onPageGlided(targetPage)
                                }
                            }

                            updateFromTouch(down.position.x)
                            down.consume()

                            try {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break
                                    if (change.changedToUp()) {
                                        change.consume()
                                        break
                                    }
                                    updateFromTouch(change.position.x)
                                    change.consume()
                                }
                            } finally {
                                isGliding = false
                            }
                        }
                    }
            ) {
                // The Small Boxes Color Swatch Track (matching page preview small boxes)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(7.dp))
                        .border(
                            1.dp,
                            if (appColors.isDark) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.12f),
                            RoundedCornerShape(7.dp)
                        )
                ) {
                    if (pageAnchors.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(NovaCoral)
                        )
                    } else {
                        pageAnchors.forEachIndexed { index, anchor ->
                            if (index > 0) {
                                Box(
                                    modifier = Modifier
                                        .width(0.6.dp)
                                        .fillMaxHeight()
                                        .background(
                                            if (appColors.isDark) Color.White.copy(alpha = 0.15f)
                                            else Color.Black.copy(alpha = 0.10f)
                                        )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(anchor.second.toComposeColor())
                            )
                        }
                    }
                }

                // The Draggable Thumb Indicator
                val thumbOffsetDp = with(density) { (currentFraction * usableWidthPx).toDp() }
                Box(
                    modifier = Modifier
                        .padding(start = thumbOffsetDp)
                        .width(thumbWidthDp)
                        .height(28.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val thumbColor = displayAnchor?.toComposeColor() ?: appColors.accentCoral
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(thumbColor)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySearchResults(
    query: String,
    onClearQuery: () -> Unit
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(appColors.surfaceElevated)
                    .border(1.dp, appColors.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = appColors.accentTeal,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No matching colors found",
                color = appColors.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (query.isNotBlank()) "No shades matching \"$query\" in any category." else "No shades found.",
                color = appColors.textTertiary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClearQuery,
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.surfaceElevated,
                    contentColor = appColors.accentTeal
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, appColors.border)
            ) {
                Text("Clear Search", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PageClubCard(
    page: ColorPage,
    favorites: Set<String>,
    searchQuery: String,
    onToggleFavorite: (String) -> Unit,
    onFullScreenClick: (ColorItem) -> Unit,
    onColorClick: (ColorItem) -> Unit,
    onCopyPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val accentColor = when (page.category) {
        ColorCategory.LIGHT -> appColors.accentGold
        ColorCategory.MEDIUM -> appColors.accentCoral
        ColorCategory.DARK -> appColors.accentViolet
        ColorCategory.COMBINATION -> appColors.accentTeal
    }

    val matchingShadeIds = remember(searchQuery, page.colors) {
        if (searchQuery.isBlank()) emptySet()
        else page.colors.filter { it.matchesQuery(searchQuery) }.map { it.id }.toSet()
    }
    val matchCount = matchingShadeIds.size

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surface),
        border = BorderStroke(
            1.dp,
            if (matchCount > 0 && searchQuery.isNotBlank()) accentColor.copy(alpha = 0.5f) else appColors.border
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Page Badge, Info, Match Badge, Mini-Strip, and Copy Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Page Number Badge Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.16f))
                            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PAGE ${page.pageNumber}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Title / Mood / Shades count
                    Column {
                        Text(
                            text = if (page.category == ColorCategory.COMBINATION) (page.name ?: "Page ${page.pageNumber} Harmony") else "${page.colors.size} Shades",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (page.category == ColorCategory.COMBINATION && !page.moodTag.isNullOrBlank()) {
                            Text(
                                text = page.moodTag,
                                fontSize = 11.sp,
                                color = appColors.accentTeal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (matchCount > 0 && searchQuery.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(appColors.accentCoral.copy(alpha = 0.2f))
                                .border(0.8.dp, appColors.accentCoral, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$matchCount match${if (matchCount > 1) "es" else ""}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.accentCoral
                            )
                        }
                    }
                }

                // Action buttons on right
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Mini color palette strip
                    Row(
                        modifier = Modifier
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .border(0.6.dp, if (appColors.isDark) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.10f), RoundedCornerShape(7.dp))
                    ) {
                        page.colors.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .width(7.dp)
                                    .fillMaxHeight()
                                    .background(c.toComposeColor())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onCopyPage,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy page hex codes",
                            tint = appColors.textTertiary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            if (page.category == ColorCategory.COMBINATION && !page.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = page.description,
                    fontSize = 11.5.sp,
                    color = appColors.textTertiary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shades Layout Clubbing
            when (page.category) {
                ColorCategory.LIGHT -> {
                    // Light colors: clubbed by 2*3 shades in Page 1 and every 6 colors
                    // 3 rows x 2 columns (6 shades total)
                    for (row in 0 until 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0 until 2) {
                                val index = row * 2 + col
                                val color = page.colors.getOrNull(index)
                                if (color != null) {
                                    ShadeCell(
                                        color = color,
                                        isFavorite = favorites.contains(color.id),
                                        isMatch = color.id in matchingShadeIds,
                                        onToggleFavorite = { onToggleFavorite(color.id) },
                                        onFullScreenClick = { onFullScreenClick(color) },
                                        onClick = { onColorClick(color) },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        if (row < 2) Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                ColorCategory.MEDIUM, ColorCategory.DARK -> {
                    // Medium & Dark colors: 2*4 clubbing (4 rows x 2 columns = 8 shades)
                    for (row in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0 until 2) {
                                val index = row * 2 + col
                                val color = page.colors.getOrNull(index)
                                if (color != null) {
                                    ShadeCell(
                                        color = color,
                                        isFavorite = favorites.contains(color.id),
                                        isMatch = color.id in matchingShadeIds,
                                        onToggleFavorite = { onToggleFavorite(color.id) },
                                        onFullScreenClick = { onFullScreenClick(color) },
                                        onClick = { onColorClick(color) },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        if (row < 3) Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                ColorCategory.COMBINATION -> {
                    // Combination tab: each page has 7 colors, 3 rows total:
                    // 2 shades in first row, 2 shades in second row, 3 shades in third row
                    // Row 0: 2 shades (index 0, 1)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (col in 0 until 2) {
                            val color = page.colors.getOrNull(col)
                            if (color != null) {
                                ShadeCell(
                                    color = color,
                                    isFavorite = favorites.contains(color.id),
                                    isMatch = color.id in matchingShadeIds,
                                    onToggleFavorite = { onToggleFavorite(color.id) },
                                    onFullScreenClick = { onFullScreenClick(color) },
                                    onClick = { onColorClick(color) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 1: 2 shades (index 2, 3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (col in 0 until 2) {
                            val color = page.colors.getOrNull(2 + col)
                            if (color != null) {
                                ShadeCell(
                                    color = color,
                                    isFavorite = favorites.contains(color.id),
                                    isMatch = color.id in matchingShadeIds,
                                    onToggleFavorite = { onToggleFavorite(color.id) },
                                    onFullScreenClick = { onFullScreenClick(color) },
                                    onClick = { onColorClick(color) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 2: 3 shades (index 4, 5, 6)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until 3) {
                            val color = page.colors.getOrNull(4 + col)
                            if (color != null) {
                                ShadeCell(
                                    color = color,
                                    isFavorite = favorites.contains(color.id),
                                    isMatch = color.id in matchingShadeIds,
                                    isCompact = true,
                                    onToggleFavorite = { onToggleFavorite(color.id) },
                                    onFullScreenClick = { onFullScreenClick(color) },
                                    onClick = { onColorClick(color) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShadeCell(
    color: ColorItem,
    isFavorite: Boolean,
    isMatch: Boolean,
    onToggleFavorite: () -> Unit,
    onFullScreenClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val appColors = LocalAppColors.current
    val composeColor = color.toComposeColor()
    val isLightColor = color.isLight()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(1.dp)
    ) {
        // Swatch Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCompact) 62.dp else 74.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(composeColor)
                .border(
                    width = if (isMatch) 2.dp else 0.8.dp,
                    color = if (isMatch) appColors.accentCoral else (if (isLightColor) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // Full Screen View Button at Top-Left (Symmetrical with Favorite)
            IconButton(
                onClick = onFullScreenClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(3.dp)
                    .size(24.dp)
                    .background(
                        Color.Black.copy(alpha = 0.38f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Full Screen View",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }

            // Favorite Button at Top-Right
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(24.dp)
                    .background(
                        Color.Black.copy(alpha = 0.38f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) appColors.accentCoral else Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }

            // MATCH badge if search match (at Bottom-Left to prevent any control overlap)
            if (isMatch) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(appColors.accentCoral)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "MATCH",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Shade Name
        Text(
            text = color.name,
            fontWeight = FontWeight.Bold,
            fontSize = if (isCompact) 11.sp else 12.5.sp,
            color = if (isMatch) appColors.accentCoral else appColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Shade Code & Hex
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = color.id,
                fontSize = if (isCompact) 9.5.sp else 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = appColors.textSecondary,
                maxLines = 1
            )
            Text(
                text = color.hex,
                fontSize = if (isCompact) 9.5.sp else 10.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                color = appColors.accentTeal,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorDetailSheet(
    color: ColorItem,
    onDismiss: () -> Unit,
    onCopy: (String, String) -> Unit,
    onShare: () -> Unit = {}
) {
    val appColors = LocalAppColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val composeColor = color.toComposeColor()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = appColors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(appColors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 32.dp)
        ) {
            // Big Color Swatch Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(composeColor)
                    .border(
                        1.dp,
                        if (color.isLight()) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = color.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (color.isLight()) Color.Black.copy(alpha = 0.85f) else Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Shade ${color.id} • Page ${color.page}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (color.isLight()) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = color.hex,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (color.isLight()) Color.Black.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category & Description
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Category: ${color.category.displayName} • Page ${color.page}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = appColors.accentTeal
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    color.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(appColors.surfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }

            if (color.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = color.description,
                    fontSize = 13.sp,
                    color = appColors.textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Values Table: Code, Page, Hex, RGB, HSL
            Text(
                text = "Color Codes & Information",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            CodeCopyRow(
                label = "SHADE",
                value = color.id,
                onCopy = { onCopy(color.id, "SHADE CODE") }
            )

            Spacer(modifier = Modifier.height(6.dp))

            CodeCopyRow(
                label = "PAGE",
                value = "Page ${color.page} (Asian Paints Spectra)",
                onCopy = { onCopy(color.page.toString(), "PAGE") }
            )

            Spacer(modifier = Modifier.height(6.dp))

            CodeCopyRow(
                label = "HEX",
                value = color.hex,
                onCopy = { onCopy(color.hex, "HEX") }
            )

            Spacer(modifier = Modifier.height(6.dp))

            CodeCopyRow(
                label = "RGB",
                value = color.rgb,
                onCopy = { onCopy("rgb(${color.rgb})", "RGB") }
            )

            Spacer(modifier = Modifier.height(6.dp))

            CodeCopyRow(
                label = "HSL",
                value = color.hsl,
                onCopy = { onCopy("hsl(${color.hsl})", "HSL") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // WCAG Contrast Preview Bar
            Text(
                text = "WCAG Accessibility Check",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // White text preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(composeColor)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "White Text Preview",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Black text preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(composeColor)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Black Text Preview",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onDismiss()
                    onShare()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accentTeal)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = if (appColors.isDark) Color(0xFF0D111D) else Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Shade Card with Note",
                    color = if (appColors.isDark) Color(0xFF0D111D) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CodeCopyRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(appColors.surfaceElevated)
            .border(1.dp, appColors.border, RoundedCornerShape(12.dp))
            .clickable { onCopy() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = appColors.accentTeal,
                modifier = Modifier.width(55.dp)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy $label",
            tint = appColors.textSecondary,
            modifier = Modifier.size(15.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D111D)
@Composable
fun ColorNovaAppPreview() {
    ColorNovaTheme {
        ColorNovaApp()
    }
}
