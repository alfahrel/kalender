package kalender.alfahrel.my.id

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kalender.alfahrel.my.id.adapter.HolidayAdapter
import kalender.alfahrel.my.id.adapter.MonthPagerAdapter
import kalender.alfahrel.my.id.data.AppLanguage
import kalender.alfahrel.my.id.data.AppPreferences
import kalender.alfahrel.my.id.data.Country
import kalender.alfahrel.my.id.data.CountryHolidays
import kalender.alfahrel.my.id.model.HolidayInfo
import java.util.Calendar

class MainActivity : BaseActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var viewPager: ViewPager2
    private lateinit var rvHolidays: RecyclerView
    private lateinit var tvNoHoliday: LinearLayout
    private lateinit var fabCurrentMonth: FloatingActionButton

    private var currentCountry: Country = Country.INDONESIA
    private var currentLanguage: AppLanguage = AppLanguage.INDONESIAN

    companion object {
        const val START_POSITION = 1200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        toolbar         = findViewById(R.id.toolbar)
        tvMonthYear     = findViewById(R.id.tvMonthYear)
        btnPrev         = findViewById(R.id.btnPrev)
        btnNext         = findViewById(R.id.btnNext)
        viewPager       = findViewById(R.id.viewPager)
        rvHolidays      = findViewById(R.id.rvHolidays)
        tvNoHoliday     = findViewById(R.id.tvNoHoliday)
        fabCurrentMonth = findViewById(R.id.fabCurrentMonth)

        setSupportActionBar(toolbar)

        currentCountry  = AppPreferences.getCountry(this)
        currentLanguage = AppPreferences.getLanguage(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appBarLayout)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        rvHolidays.layoutManager = LinearLayoutManager(this)
        rvHolidays.itemAnimator  = null

        viewPager.adapter = MonthPagerAdapter(this)
        viewPager.setCurrentItem(START_POSITION, false)

        val initialCal   = pageToCalendar(START_POSITION)
        val initialYear  = initialCal.get(Calendar.YEAR)
        val initialMonth = initialCal.get(Calendar.MONTH)
        val monthNames   = resources.getStringArray(R.array.month_names)

        supportActionBar?.title = "${monthNames[initialMonth]} $initialYear"
        tvMonthYear.text = monthNames[initialMonth]
        updateHolidays(initialYear, initialMonth)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val cal   = pageToCalendar(position)
                val year  = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val names = resources.getStringArray(R.array.month_names)
                supportActionBar?.title = "${names[month]} $year"
                tvMonthYear.text = names[month]
                updateHolidays(year, month)
            }
        })

        viewPager.getChildAt(0)?.let { recyclerView ->
            recyclerView.isNestedScrollingEnabled = false
            (recyclerView as? RecyclerView)?.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {})
        }

        viewPager.setPageTransformer { page, _ ->
            page.parent.requestDisallowInterceptTouchEvent(true)
        }

        btnPrev.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem - 1, true) }
        btnNext.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem + 1, true) }
        fabCurrentMonth.setOnClickListener { viewPager.setCurrentItem(START_POSITION, true) }
    }

    fun pageToCalendar(position: Int): Calendar {
        val offset = position - START_POSITION
        return Calendar.getInstance().apply { add(Calendar.MONTH, offset) }
    }

    fun getCurrentHolidays() = CountryHolidays.getHolidays(currentCountry)

    private fun updateHolidays(year: Int, month: Int) {
        val holidays = getHolidaysForMonth(year, month)
        if (holidays.isEmpty()) {
            rvHolidays.visibility  = View.GONE
            tvNoHoliday.visibility = View.VISIBLE
            tvNoHoliday.alpha = 0f
            tvNoHoliday.translationY = 40f
            tvNoHoliday.animate().alpha(1f).translationY(0f).setDuration(300).setStartDelay(100).start()
        } else {
            rvHolidays.visibility  = View.VISIBLE
            tvNoHoliday.visibility = View.GONE
            rvHolidays.adapter = HolidayAdapter(holidays)
        }
    }

    private fun getHolidaysForMonth(year: Int, month: Int): List<HolidayInfo> {
        val holidays = CountryHolidays.getHolidays(currentCountry)
        val result   = mutableListOf<HolidayInfo>()
        val tmpCal   = Calendar.getInstance().apply { set(year, month, 1) }
        val days     = tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..days) {
            val key   = String.format("%04d-%02d-%02d", year, month + 1, day)
            val entry = holidays[key] ?: continue
            result.add(HolidayInfo(day, month + 1, year, entry.name, entry.description, entry.type))
        }
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_calendar, menu)
        return true
    }

    private fun refreshWidgets() {
        val mgr = AppWidgetManager.getInstance(this)
        val ids = mgr.getAppWidgetIds(
            android.content.ComponentName(this, kalender.alfahrel.my.id.widget.CalendarWidget::class.java)
        )
        if (ids.isNotEmpty()) {
            val intent = Intent(this, kalender.alfahrel.my.id.widget.CalendarWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            sendBroadcast(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> { showSettingsBottomSheet(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view   = layoutInflater.inflate(R.layout.bottom_sheet_settings, null)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        view.findViewById<TextView>(R.id.tvAppVersion).text =
            getString(R.string.settings_version, versionName)

        val tvCountryValue = view.findViewById<TextView>(R.id.tvCountryValue)
        tvCountryValue.text = getString(currentCountry.displayNameRes)

        val tvLanguageValue = view.findViewById<TextView>(R.id.tvLanguageValue)
        tvLanguageValue.text = currentLanguage.displayName

        view.findViewById<LinearLayout>(R.id.itemCountry).setOnClickListener {
            dialog.dismiss()
            showCountryPicker { selected ->
                if (selected != currentCountry) {
                    currentCountry = selected
                    AppPreferences.setCountry(this, selected)
                    refreshWidgets()
                    viewPager.adapter = MonthPagerAdapter(this)
                    viewPager.setCurrentItem(START_POSITION, false)
                    val cal = pageToCalendar(START_POSITION)
                    updateHolidays(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                }
            }
        }

        view.findViewById<LinearLayout>(R.id.itemLanguage).setOnClickListener {
            dialog.dismiss()
            showLanguagePicker { selected ->
                if (selected != currentLanguage) {
                    AppPreferences.setLanguage(this, selected)
                    refreshWidgets()
                    recreate()
                }
            }
        }

        view.findViewById<LinearLayout>(R.id.itemPrivacy).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://kalender.alfahrel.my.id/privacy.html")))
        }

        view.findViewById<LinearLayout>(R.id.itemAbout).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/alfahrel/kalender")))
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showCountryPicker(onCountrySelected: (Country) -> Unit) {
        val countries  = Country.entries.toTypedArray()
        val labels     = countries.map { getString(it.displayNameRes) }.toTypedArray()
        val currentIdx = countries.indexOf(currentCountry)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.picker_country_title))
            .setSingleChoiceItems(labels, currentIdx) { dialog, which ->
                onCountrySelected(countries[which])
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.picker_cancel), null)
            .show()
    }

    private fun showLanguagePicker(onLanguageSelected: (AppLanguage) -> Unit) {
        val languages  = AppLanguage.entries.toTypedArray()
        val labels     = languages.map { it.displayName }.toTypedArray()
        val currentIdx = languages.indexOf(currentLanguage)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.picker_language_title))
            .setSingleChoiceItems(labels, currentIdx) { dialog, which ->
                onLanguageSelected(languages[which])
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.picker_cancel), null)
            .show()
    }
}