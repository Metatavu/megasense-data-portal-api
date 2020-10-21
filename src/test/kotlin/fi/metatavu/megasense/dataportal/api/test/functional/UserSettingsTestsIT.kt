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
            val createdSettings = testBuilder.admin().userSettings().create("Mutatie 9", "50708", "Mutala", "Suomi")

            assertNotNull(createdSettings)
            assertEquals("Mutatie 9", createdSettings.homeAddress?.streetAddress)
            assertEquals("50708", createdSettings.homeAddress?.postalCode)
            assertEquals("Mutala", createdSettings.homeAddress?.city)
            assertEquals("Suomi", createdSettings.homeAddress?.country)

            val foundSettings = testBuilder.admin().userSettings().get()

            assertNotNull(foundSettings)
            assertEquals("Mutatie 9", foundSettings.homeAddress?.streetAddress)
            assertEquals("50708", foundSettings.homeAddress?.postalCode)
            assertEquals("Mutala", foundSettings.homeAddress?.city)
            assertEquals("Suomi", foundSettings.homeAddress?.country)

            val updatedSettings = testBuilder.admin().userSettings().update("Kuratie 19", "70898", "Kurala", "Syrjälä")
            assertNotNull(updatedSettings)
            assertEquals("Kuratie 19", updatedSettings.homeAddress?.streetAddress)
            assertEquals("70898", updatedSettings.homeAddress?.postalCode)
            assertEquals("Kurala", updatedSettings.homeAddress?.city)
            assertEquals("Syrjälä", updatedSettings.homeAddress?.country)
        }
    }
}