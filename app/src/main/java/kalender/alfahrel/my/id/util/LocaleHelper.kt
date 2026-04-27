package kalender.alfahrel.my.id.util

import android.content.Context
import android.os.Build
import java.util.Locale

object LocaleHelper {
    fun applyLocale(ctx: Context, localeCode: String): Context {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = ctx.resources.configuration
        config.setLocale(locale)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ctx.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
            ctx
        }
    }
}