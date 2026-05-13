package alfahrel.my.id.kalender.data

import alfahrel.my.id.kalender.model.HolidayEntry

import androidx.annotation.StringRes
import alfahrel.my.id.kalender.R

enum class Country(
    @StringRes val displayNameRes: Int
) {
    INDONESIA(R.string.country_indonesia),
    GERMANY(R.string.country_germany)
}

object CountryHolidays {
    fun getHolidays(country: Country): Map<String, HolidayEntry> = when (country) {
        Country.INDONESIA -> HolidaysData.allHolidays
        Country.GERMANY   -> GermanyHolidaysData.allHolidays
    }
}