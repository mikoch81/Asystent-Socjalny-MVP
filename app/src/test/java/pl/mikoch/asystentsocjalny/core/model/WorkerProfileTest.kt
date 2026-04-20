package pl.mikoch.asystentsocjalny.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkerProfileTest {

    @Test
    fun emptyProfile_isNotComplete() {
        assertFalse(WorkerProfile.EMPTY.isComplete)
        assertEquals("", WorkerProfile.EMPTY.fullName)
    }

    @Test
    fun completeProfile_isComplete_whenFirstAndLastNamePresent() {
        val p = WorkerProfile(firstName = "Anna", lastName = "Kowalska")
        assertTrue(p.isComplete)
        assertEquals("Anna Kowalska", p.fullName)
    }

    @Test
    fun blankNames_areNotComplete() {
        val p = WorkerProfile(firstName = "  ", lastName = "Kowalska")
        assertFalse(p.isComplete)
    }

    @Test
    fun signatureLine_includesPositionAndUnit_whenProvided() {
        val p = WorkerProfile(
            firstName = "Anna",
            lastName = "Kowalska",
            position = "Pracownik socjalny",
            unit = "MOPS Zgierz"
        )
        val s = p.signatureLine
        assertTrue(s.contains("Anna Kowalska"))
        assertTrue(s.contains("Pracownik socjalny"))
        assertTrue(s.contains("MOPS Zgierz"))
    }

    @Test
    fun signatureLine_handlesMissingOptionalFields() {
        val p = WorkerProfile(firstName = "Anna", lastName = "Kowalska")
        // Should not crash and should still contain the name
        assertTrue(p.signatureLine.contains("Anna Kowalska"))
    }
}
