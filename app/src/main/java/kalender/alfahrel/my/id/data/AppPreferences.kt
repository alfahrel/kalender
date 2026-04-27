package kalender.alfahrel.my.id.data

import android.content.Context

object AppPreferences {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_COUNTRY  = "selected_country"
    private const val KEY_LANGUAGE = "selected_language"

    fun getCountry(ctx: Context): Country {
        val name = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_COUNTRY, Country.INDONESIA.name)
        return Country.entries.find { it.name == name } ?: Country.INDONESIA
    }

    fun setCountry(ctx: Context, country: Country) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_COUNTRY, country.name).apply()
    }

    fun getLanguage(ctx: Context): AppLanguage {
        val name = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, AppLanguage.INDONESIAN.name)
        return AppLanguage.entries.find { it.name == name } ?: AppLanguage.INDONESIAN
    }

    fun setLanguage(ctx: Context, language: AppLanguage) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANGUAGE, language.name).apply()
    }
}