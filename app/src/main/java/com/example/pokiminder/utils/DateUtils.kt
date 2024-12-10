package com.example.pokiminder.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek

// Function to get the start and end of the current week
@RequiresApi(Build.VERSION_CODES.O)
fun getStartAndEndOfWeek(): Pair<LocalDate, LocalDate> {
    val today = LocalDate.now()
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = startOfWeek.plusDays(6) // The end of the week is 6 days after the start
    return Pair(startOfWeek, endOfWeek)
}

// Function to get the start and end of the current month
@RequiresApi(Build.VERSION_CODES.O)
fun getStartAndEndOfMonth(): Pair<LocalDate, LocalDate> {
    val today = LocalDate.now()
    val startOfMonth = today.withDayOfMonth(1) // First day of the month
    val endOfMonth = today.withDayOfMonth(today.lengthOfMonth()) // Last day of the month
    return Pair(startOfMonth, endOfMonth)
}

// Function to get the start and end of the current year
@RequiresApi(Build.VERSION_CODES.O)
fun getStartAndEndOfYear(): Pair<LocalDate, LocalDate> {
    val today = LocalDate.now()
    val startOfYear = today.withDayOfYear(1) // First day of the year
    val endOfYear = today.withDayOfYear(today.lengthOfYear()) // Last day of the year
    return Pair(startOfYear, endOfYear)
}
