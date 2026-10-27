package asian.paints.colornova.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import org.json.JSONObject
import java.io.InputStream

enum class ColorCategory(val displayName: String) {
    LIGHT("Light"),
    MEDIUM("Medium"),
    DARK("Dark"),
    COMBINATION("Combos")
}

val ColorCategory.pageRange: IntRange
    get() = when (this) {
        ColorCategory.LIGHT -> 1..17
        ColorCategory.MEDIUM -> 18..182
        ColorCategory.DARK -> 183..207
        ColorCategory.COMBINATION -> 208..225
    }


data class ColorItem(
    val id: String,
    val name: String,
    val hex: String,
    val category: ColorCategory,
    val page: Int = 1,
    val rgb: String = calculateRgb(hex),
    val hsl: String = calculateHsl(hex),
    val tags: List<String> = generateTags(hex, page),
    val description: String = "Color Nova shade $id • Page $page"
) {
    fun toComposeColor(): Color {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        return if (cleanHex.length == 6) {
            Color(0xFF000000 or colorInt)
        } else {
            Color(colorInt)
        }
    }

    fun isLight(): Boolean {
        val color = toComposeColor()
        val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
        return luminance > 0.55
    }

    fun matchesQuery(query: String): Boolean {
        if (query.isBlank()) return true
        val trimmed = query.trim().lowercase()
        val pageNum = query.trim()
            .removePrefix("page").removePrefix("pg").removePrefix("p.").removePrefix("p")
            .trim().toIntOrNull()

        return name.lowercase().contains(trimmed) ||
                id.lowercase().contains(trimmed) ||
                hex.lowercase().contains(trimmed) ||
                tags.any { it.lowercase().contains(trimmed) } ||
                description.lowercase().contains(trimmed) ||
                (pageNum != null && page == pageNum) ||
                "page $page".contains(trimmed)
    }

    companion object {
        fun calculateRgb(hex: String): String {
            val cleanHex = hex.removePrefix("#")
            return try {
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                "$r, $g, $b"
            } catch (e: Exception) {
                "0, 0, 0"
            }
        }

        fun calculateHsl(hex: String): String {
            val cleanHex = hex.removePrefix("#")
            return try {
                val r = cleanHex.substring(0, 2).toInt(16) / 255.0f
                val g = cleanHex.substring(2, 4).toInt(16) / 255.0f
                val b = cleanHex.substring(4, 6).toInt(16) / 255.0f
                val max = maxOf(r, g, b)
                val min = minOf(r, g, b)
                val delta = max - min
                val l = (max + min) / 2.0f
                val s = if (delta == 0f) 0f else if (l > 0.5f) delta / (2.0f - max - min) else delta / (max + min)
                val h = when {
                    delta == 0f -> 0f
                    max == r -> ((g - b) / delta + (if (g < b) 6f else 0f)) * 60f
                    max == g -> ((b - r) / delta + 2f) * 60f
                    else -> ((r - g) / delta + 4f) * 60f
                }
                "${h.toInt()}°, ${(s * 100).toInt()}%, ${(l * 100).toInt()}%"
            } catch (e: Exception) {
                "0°, 0%, 0%"
            }
        }

        fun generateTags(hex: String, page: Int): List<String> {
            val cleanHex = hex.removePrefix("#")
            val tags = mutableListOf<String>()
            try {
                val r = cleanHex.substring(0, 2).toInt(16) / 255.0f
                val g = cleanHex.substring(2, 4).toInt(16) / 255.0f
                val b = cleanHex.substring(4, 6).toInt(16) / 255.0f
                val max = maxOf(r, g, b)
                val min = minOf(r, g, b)
                val delta = max - min
                val l = (max + min) / 2.0f
                val s = if (delta == 0f) 0f else if (l > 0.5f) delta / (2.0f - max - min) else delta / (max + min)
                val h = when {
                    delta == 0f -> 0f
                    max == r -> ((g - b) / delta + (if (g < b) 6f else 0f)) * 60f
                    max == g -> ((b - r) / delta + 2f) * 60f
                    else -> ((r - g) / delta + 4f) * 60f
                }

                if (s <= 0.12f) {
                    tags.add("Neutral")
                    tags.add("Minimal")
                } else if (h in 15f..65f && s < 0.5f) {
                    tags.add("Earth")
                } else if (h <= 75f || h >= 330f) {
                    tags.add("Warm")
                } else if (h in 150f..270f) {
                    tags.add("Cool")
                } else {
                    tags.add("Warm")
                }

                if (l >= 0.85f) {
                    tags.add("Pastel")
                } else if (l <= 0.25f) {
                    tags.add("Dark")
                } else if (s >= 0.55f) {
                    tags.add("Vibrant")
                }
            } catch (e: Exception) {
                tags.add("Standard")
            }
            return tags.distinct()
        }
    }
}

data class ColorPalette(
    val id: String,
    val name: String,
    val harmonyType: String,
    val moodTag: String,
    val colors: List<ColorItem>,
    val description: String,
    val page: Int = 0
)

data class ColorPage(
    val pageNumber: Int,
    val category: ColorCategory,
    val colors: List<ColorItem>,
    val name: String? = null,
    val moodTag: String? = null,
    val description: String? = null
)

object SampleColorRepository {

    // Seed/Fallback Data from Asian Paints data.csv for instant preview or offline readiness
    private val seedLightColors = listOf(
        ColorItem("L146", "SONNET", "#F7F1E7", ColorCategory.LIGHT, 1),
        ColorItem("L145", "WHITE CAMEO", "#F7F0E6", ColorCategory.LIGHT, 1),
        ColorItem("L162", "BIENNALE WHITE", "#F0EEE8", ColorCategory.LIGHT, 1),
        ColorItem("L169", "SPOTLESS", "#EEEEE8", ColorCategory.LIGHT, 1),
        ColorItem("L154", "PIPE DREAM", "#F6F3E7", ColorCategory.LIGHT, 1),
        ColorItem("L150", "PRESSED LINEN", "#F6F0E3", ColorCategory.LIGHT, 1),
        ColorItem("L166", "SOUR CREAM", "#F0EFE9", ColorCategory.LIGHT, 2),
        ColorItem("8260", "CAMPHOR", "#EDEBE3", ColorCategory.LIGHT, 2),
        ColorItem("L171", "WHITE SILK", "#ECECE4", ColorCategory.LIGHT, 2),
        ColorItem("L164", "WHITE BUTTER", "#EAE7E1", ColorCategory.LIGHT, 2),
        ColorItem("L109", "ICY PEAK", "#E7E5DA", ColorCategory.LIGHT, 2),
        ColorItem("L186", "LAP OF HIMALAYA", "#E8E8E2", ColorCategory.LIGHT, 2),
        ColorItem("L155", "PALE SISAL", "#F5F2E6", ColorCategory.LIGHT, 3),
        ColorItem("L144", "LOVE SONG", "#F4F0E5", ColorCategory.LIGHT, 3),
        ColorItem("L172", "PURE COTTON", "#F1EEE0", ColorCategory.LIGHT, 3),
        ColorItem("0765", "MORNING GLORY", "#F1ECE2", ColorCategory.LIGHT, 3)
    )

    private val seedMediumColors = listOf(
        ColorItem("0766", "PURITY", "#EFEAE6", ColorCategory.MEDIUM, 18),
        ColorItem("7115", "PURPLE DYE", "#EDDEE4", ColorCategory.MEDIUM, 18),
        ColorItem("7114", "WISTERIA", "#DEC7D3", ColorCategory.MEDIUM, 18),
        ColorItem("7113", "VIOLET SHOW", "#D2B2C3", ColorCategory.MEDIUM, 18),
        ColorItem("7676", "GREEN REPUBLIC", "#E9EEDB", ColorCategory.MEDIUM, 60),
        ColorItem("7675", "SERENE VALLEY", "#E5ECD5", ColorCategory.MEDIUM, 60),
        ColorItem("7674", "ECHO OF SPRING", "#D6E5C2", ColorCategory.MEDIUM, 60),
        ColorItem("2387", "ENGLISH ELMS", "#C7DBB1", ColorCategory.MEDIUM, 60),
        ColorItem("7672", "SOUND OF NATURE", "#BCD1A1", ColorCategory.MEDIUM, 60),
        ColorItem("2370", "CASCADE GREEN", "#9EBC83", ColorCategory.MEDIUM, 60),
        ColorItem("7670", "GARDEN CRESS", "#89A763", ColorCategory.MEDIUM, 60)
    )

    private val seedDarkColors = listOf(
        ColorItem("9616", "Wisteria Lane", "#AD98C9", ColorCategory.DARK, 183),
        ColorItem("9617", "Dianthus", "#A584B9", ColorCategory.DARK, 183),
        ColorItem("8432", "NIGHT SKY", "#2B2D3A", ColorCategory.DARK, 183),
        ColorItem("8424", "INK BLACK", "#1F232B", ColorCategory.DARK, 183),
        ColorItem("8240", "OBSIDIAN SLATE", "#1B1E24", ColorCategory.DARK, 184)
    )

    private val seedComboColors = listOf(
        ColorItem("9645", "Stratos", "#DFE6EE", ColorCategory.COMBINATION, 208),
        ColorItem("9879", "Sands of Thar", "#E2BD89", ColorCategory.COMBINATION, 208),
        ColorItem("9602", "Catmint Blossom", "#CAB1DA", ColorCategory.COMBINATION, 208),
        ColorItem("9618", "Primrose Purple", "#73618D", ColorCategory.COMBINATION, 208),
        ColorItem("9778", "Matchamisu", "#D3EDB9", ColorCategory.COMBINATION, 208),
        ColorItem("9764", "Chalkboard Green", "#4A8348", ColorCategory.COMBINATION, 208),
        ColorItem("9960", "Ripe Tamarind", "#754D46", ColorCategory.COMBINATION, 208),
        ColorItem("K247", "Camellia Bud", "#F6DDCF", ColorCategory.COMBINATION, 209),
        ColorItem("9684", "Moonlight River", "#2B5971", ColorCategory.COMBINATION, 209),
        ColorItem("9522", "RIVER SAND", "#D2C5A9", ColorCategory.COMBINATION, 209),
        ColorItem("9431", "GARDEN GLOW", "#B99AAB", ColorCategory.COMBINATION, 209),
        ColorItem("K224", "Blush Cream", "#EDDAC7", ColorCategory.COMBINATION, 209),
        ColorItem("9848", "Nimboo Splash", "#EADB75", ColorCategory.COMBINATION, 209),
        ColorItem("9864", "Yellow Moss", "#D1AC2E", ColorCategory.COMBINATION, 209)
    )

    private val seedCombinations = listOf(
        ColorPalette(
            id = "P208",
            name = "Page 208 Harmony",
            harmonyType = "Curated 7-Color Strip",
            moodTag = "Stratos & Sands of Thar",
            colors = seedComboColors.take(7),
            description = "Curated 7-color harmony strip from Color Nova fan deck page 208.",
            page = 208
        ),
        ColorPalette(
            id = "P209",
            name = "Page 209 Harmony",
            harmonyType = "Curated 7-Color Strip",
            moodTag = "Camellia Bud & Moonlight River",
            colors = seedComboColors.drop(7).take(7),
            description = "Curated 7-color harmony strip from Color Nova fan deck page 209.",
            page = 209
        )
    )

    @Volatile
    var lightColors: List<ColorItem> = seedLightColors
        private set

    @Volatile
    var mediumColors: List<ColorItem> = seedMediumColors
        private set

    @Volatile
    var darkColors: List<ColorItem> = seedDarkColors
        private set

    @Volatile
    var comboColors: List<ColorItem> = seedComboColors
        private set

    @Volatile
    var combinations: List<ColorPalette> = seedCombinations
        private set

    private var isLoadedFromAssets = false

    @Synchronized
    fun loadFromAssets(context: Context) {
        if (isLoadedFromAssets) return
        try {
            val jsonStream: InputStream = context.assets.open("colors.json")
            val jsonString = jsonStream.bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            fun parseColor(obj: JSONObject, category: ColorCategory): ColorItem {
                val rawCode = obj.optString("c", obj.optString("code", ""))
                val code = if (rawCode.length == 3) "0$rawCode" else rawCode
                val name = obj.optString("n", obj.optString("name", ""))
                val hex = obj.optString("h", obj.optString("hex", "#FFFFFF"))
                val page = obj.optInt("p", obj.optInt("page", 1))
                return ColorItem(
                    id = code,
                    name = name,
                    hex = hex,
                    category = category,
                    page = page,
                    description = "Color Nova shade $code (Page $page)"
                )
            }

            // Light (Pages 1..17)
            val lightArr = root.optJSONArray("light")
            if (lightArr != null && lightArr.length() > 0) {
                val list = ArrayList<ColorItem>(lightArr.length())
                for (i in 0 until lightArr.length()) {
                    list.add(parseColor(lightArr.getJSONObject(i), ColorCategory.LIGHT))
                }
                lightColors = list
            }

            // Medium (Pages 18..182)
            val mediumArr = root.optJSONArray("medium")
            if (mediumArr != null && mediumArr.length() > 0) {
                val list = ArrayList<ColorItem>(mediumArr.length())
                for (i in 0 until mediumArr.length()) {
                    list.add(parseColor(mediumArr.getJSONObject(i), ColorCategory.MEDIUM))
                }
                mediumColors = list
            }

            // Dark (Pages 183..207)
            val darkArr = root.optJSONArray("dark")
            if (darkArr != null && darkArr.length() > 0) {
                val list = ArrayList<ColorItem>(darkArr.length())
                for (i in 0 until darkArr.length()) {
                    list.add(parseColor(darkArr.getJSONObject(i), ColorCategory.DARK))
                }
                darkColors = list
            }

            // Combo Individual Colors (Pages 208..225)
            val comboColorsArr = root.optJSONArray("combo_colors")
            if (comboColorsArr != null && comboColorsArr.length() > 0) {
                val list = ArrayList<ColorItem>(comboColorsArr.length())
                for (i in 0 until comboColorsArr.length()) {
                    list.add(parseColor(comboColorsArr.getJSONObject(i), ColorCategory.COMBINATION))
                }
                comboColors = list
            }

            // Combos (18 Palettes from Pages 208..225)
            val combosArr = root.optJSONArray("combos")
            if (combosArr != null && combosArr.length() > 0) {
                val list = ArrayList<ColorPalette>(combosArr.length())
                for (i in 0 until combosArr.length()) {
                    val paletteObj = combosArr.getJSONObject(i)
                    val colorArr = paletteObj.optJSONArray("c") ?: paletteObj.getJSONArray("colors")
                    val paletteColors = ArrayList<ColorItem>(colorArr.length())
                    for (j in 0 until colorArr.length()) {
                        paletteColors.add(parseColor(colorArr.getJSONObject(j), ColorCategory.COMBINATION))
                    }
                    val page = paletteObj.optInt("p", paletteObj.optInt("page", 208))
                    list.add(
                        ColorPalette(
                            id = paletteObj.optString("i", paletteObj.optString("id", "P$page")),
                            name = paletteObj.optString("n", paletteObj.optString("name", "Page $page Harmony")),
                            harmonyType = paletteObj.optString("ht", paletteObj.optString("harmonyType", "Curated 7-Color Strip")),
                            moodTag = paletteObj.optString("m", paletteObj.optString("moodTag", "Color Nova Fan Deck")),
                            colors = paletteColors,
                            description = paletteObj.optString("d", paletteObj.optString("description", "Curated combination from page $page")),
                            page = page
                        )
                    )
                }
                combinations = list
            }

            isLoadedFromAssets = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllColors(): List<ColorItem> = lightColors + mediumColors + darkColors + comboColors

    fun getColorById(id: String): ColorItem? = getAllColors().firstOrNull { it.id.equals(id, ignoreCase = true) }

    fun searchColors(query: String, category: ColorCategory?): List<ColorItem> {
        val baseList = when (category) {
            ColorCategory.LIGHT -> lightColors
            ColorCategory.MEDIUM -> mediumColors
            ColorCategory.DARK -> darkColors
            ColorCategory.COMBINATION -> comboColors
            null -> getAllColors()
        }
        if (query.isBlank()) return baseList

        val trimmed = query.trim().lowercase()
        val pageNum = query.trim()
            .removePrefix("page").removePrefix("pg").removePrefix("p.").removePrefix("p")
            .trim().toIntOrNull()

        return baseList.filter { color ->
            color.name.lowercase().contains(trimmed) ||
            color.id.lowercase().contains(trimmed) ||
            color.hex.lowercase().contains(trimmed) ||
            color.tags.any { it.lowercase().contains(trimmed) } ||
            color.description.lowercase().contains(trimmed) ||
            (pageNum != null && color.page == pageNum) ||
            "page ${color.page}".contains(trimmed)
        }
    }

    fun searchPalettes(query: String): List<ColorPalette> {
        if (query.isBlank()) return combinations
        val trimmed = query.trim().lowercase()
        val pageNum = query.trim()
            .removePrefix("page").removePrefix("pg").removePrefix("p.").removePrefix("p")
            .trim().toIntOrNull()

        return combinations.filter { palette ->
            palette.name.lowercase().contains(trimmed) ||
            palette.harmonyType.lowercase().contains(trimmed) ||
            palette.moodTag.lowercase().contains(trimmed) ||
            palette.description.lowercase().contains(trimmed) ||
            (pageNum != null && palette.page == pageNum) ||
            "page ${palette.page}".contains(trimmed) ||
            palette.colors.any {
                it.name.lowercase().contains(trimmed) ||
                it.id.lowercase().contains(trimmed) ||
                it.hex.lowercase().contains(trimmed)
            }
        }
    }

    fun getPages(category: ColorCategory): List<ColorPage> {
        return when (category) {
            ColorCategory.LIGHT -> {
                lightColors.groupBy { it.page }
                    .map { (page, colors) ->
                        ColorPage(
                            pageNumber = page,
                            category = ColorCategory.LIGHT,
                            colors = colors
                        )
                    }
                    .sortedBy { it.pageNumber }
            }
            ColorCategory.MEDIUM -> {
                mediumColors.groupBy { it.page }
                    .map { (page, colors) ->
                        ColorPage(
                            pageNumber = page,
                            category = ColorCategory.MEDIUM,
                            colors = colors
                        )
                    }
                    .sortedBy { it.pageNumber }
            }
            ColorCategory.DARK -> {
                darkColors.groupBy { it.page }
                    .map { (page, colors) ->
                        ColorPage(
                            pageNumber = page,
                            category = ColorCategory.DARK,
                            colors = colors
                        )
                    }
                    .sortedBy { it.pageNumber }
            }
            ColorCategory.COMBINATION -> {
                combinations.map { palette ->
                    ColorPage(
                        pageNumber = palette.page,
                        category = ColorCategory.COMBINATION,
                        colors = palette.colors,
                        name = palette.name,
                        moodTag = palette.moodTag,
                        description = palette.description
                    )
                }.sortedBy { it.pageNumber }
            }
        }
    }

    fun searchPages(query: String, category: ColorCategory): List<ColorPage> {
        val allPages = getPages(category)
        if (query.isBlank()) return allPages

        val trimmed = query.trim().lowercase()
        val pageNum = query.trim()
            .removePrefix("page").removePrefix("pg").removePrefix("p.").removePrefix("p")
            .trim().toIntOrNull()

        return allPages.filter { page ->
            (pageNum != null && page.pageNumber == pageNum) ||
            "page ${page.pageNumber}".contains(trimmed) ||
            (page.name?.lowercase()?.contains(trimmed) == true) ||
            (page.moodTag?.lowercase()?.contains(trimmed) == true) ||
            (page.description?.lowercase()?.contains(trimmed) == true) ||
            page.colors.any { color -> color.matchesQuery(query) }
        }
    }
}
