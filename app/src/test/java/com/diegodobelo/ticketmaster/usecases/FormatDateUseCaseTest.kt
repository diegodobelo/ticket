package com.diegodobelo.ticketmaster.usecases

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatDateUseCaseTest {

    val subject = FormatDateUseCase()

    @Test
    fun `check valid input formatting for different dates`() {
        assertEquals("Dec 22", subject("22-12-2024"))
        assertEquals("Oct 05", subject("05-10-2023"))
        assertEquals("May 01", subject("01-05-2020"))
        assertEquals("Jan 31", subject("31-01-2024"))
        assertEquals("Jul 07", subject("07-07-2022"))
    }

    @Test
    fun `check invalid input returns inoput date`() {
        assertEquals("2024-12", subject("2024-12"))
        assertEquals("abc", subject("abc"))
        assertEquals("", subject(""))
    }
}