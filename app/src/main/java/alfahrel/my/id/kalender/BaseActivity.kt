package alfahrel.my.id.kalender

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import alfahrel.my.id.kalender.data.AppPreferences
import alfahrel.my.id.kalender.util.LocaleHelper

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val language = AppPreferences.getLanguage(newBase)
        super.attachBaseContext(LocaleHelper.applyLocale(newBase, language.localeCode))
    }
}