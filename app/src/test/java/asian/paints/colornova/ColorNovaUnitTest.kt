package asian.paints.colornova

import asian.paints.colornova.data.ColorCategory
import asian.paints.colornova.data.SampleColorRepository
import org.junit.Assert.*
import org.junit.Test

class ColorNovaUnitTest {

    @Test
    fun testLightPagesClubbing() {
        val lightPages = SampleColorRepository.getPages(ColorCategory.LIGHT)
        assertTrue("Light pages should not be empty", lightPages.isNotEmpty())
        assertEquals(1, lightPages.first().pageNumber)
        // Check Page 1 has 6 shades for 2*3 clubbing
        assertEquals(6, lightPages.first().colors.size)
        // Check every 6 colors continues clubbing
        lightPages.forEach { page ->
            assertTrue("Every light page should have 6 shades or less in seed", page.colors.size in 1..6)
        }
    }

    @Test
    fun testMediumAndDarkPagesClubbing() {
        val mediumPages = SampleColorRepository.getPages(ColorCategory.MEDIUM)
        assertTrue("Medium pages should not be empty", mediumPages.isNotEmpty())

        val darkPages = SampleColorRepository.getPages(ColorCategory.DARK)
        assertTrue("Dark pages should not be empty", darkPages.isNotEmpty())
    }

    @Test
    fun testComboPagesClubbing() {
        val comboPages = SampleColorRepository.getPages(ColorCategory.COMBINATION)
        assertTrue("Combo pages should not be empty", comboPages.isNotEmpty())
        assertEquals(208, comboPages.first().pageNumber)
        // Each combo page has 7 colors (2 in row 1, 2 in row 2, 3 in row 3)
        assertEquals(7, comboPages.first().colors.size)
    }

    @Test
    fun testSearchAndDynamicTabSwitchingLogic() {
        // Search "SONNET"
        val sonnetInLight = SampleColorRepository.searchPages("SONNET", ColorCategory.LIGHT)
        val sonnetInDark = SampleColorRepository.searchPages("SONNET", ColorCategory.DARK)
        val sonnetInMedium = SampleColorRepository.searchPages("SONNET", ColorCategory.MEDIUM)
        val sonnetInCombo = SampleColorRepository.searchPages("SONNET", ColorCategory.COMBINATION)

        assertTrue("SONNET should match in Light", sonnetInLight.isNotEmpty())
        assertTrue("SONNET should be empty in Dark", sonnetInDark.isEmpty())
        assertTrue("SONNET should be empty in Medium", sonnetInMedium.isEmpty())
        assertTrue("SONNET should be empty in Combo", sonnetInCombo.isEmpty())

        // Search "OBSIDIAN"
        val obsidianInDark = SampleColorRepository.searchPages("OBSIDIAN", ColorCategory.DARK)
        val obsidianInLight = SampleColorRepository.searchPages("OBSIDIAN", ColorCategory.LIGHT)

        assertTrue("OBSIDIAN should match in Dark", obsidianInDark.isNotEmpty())
        assertTrue("OBSIDIAN should be empty in Light", obsidianInLight.isEmpty())

        // Search "Stratos"
        val stratosInCombo = SampleColorRepository.searchPages("Stratos", ColorCategory.COMBINATION)
        val stratosInLight = SampleColorRepository.searchPages("Stratos", ColorCategory.LIGHT)

        assertTrue("Stratos should match in Combo", stratosInCombo.isNotEmpty())
        assertTrue("Stratos should be empty in Light", stratosInLight.isEmpty())
    }
}
