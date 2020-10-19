package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * User settings tests
 */
class UserSettingsTestsIT: AbstractFunctionalTest() {
    @Test
    fun userSettingsTest() {
        TestBuilder().use {testBuilder ->
            val createdSettings = testBuilder.admin().userSettings().create("Mutatie 9")

            assertNotNull(createdSettings)
            assertEquals("Mutatie 9", createdSettings.homeAddress)

            val foundSettings = testBuilder.admin().userSettings().get()

            assertNotNull(foundSettings)
            assertEquals("Mutatie 9", foundSettings.homeAddress)

            val updatedSettings = testBuilder.admin().userSettings().update("Kuratie 9")
            assertNotNull(updatedSettings)
            assertEquals("Kuratie 9", updatedSettings.homeAddress)
        }
    }
}