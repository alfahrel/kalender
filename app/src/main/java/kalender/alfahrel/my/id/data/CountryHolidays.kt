package kalender.alfahrel.my.id.data

import kalender.alfahrel.my.id.model.HolidayEntry

import androidx.annotation.StringRes
import kalender.alfahrel.my.id.R

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