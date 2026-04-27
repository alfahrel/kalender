package kalender.alfahrel.my.id

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import kalender.alfahrel.my.id.data.AppPreferences
import kalender.alfahrel.my.id.util.LocaleHelper

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val language = AppPreferences.getLanguage(newBase)
        super.attachBaseContext(LocaleHelper.applyLocale(newBase, language.localeCode))
    }
}