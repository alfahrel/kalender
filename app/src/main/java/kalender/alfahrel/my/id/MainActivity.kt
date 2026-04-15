package kalender.alfahrel.my.id

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kalender.alfahrel.my.id.data.HolidaysData.allHolidays
import kalender.alfahrel.my.id.adapter.CalendarAdapter
import kalender.alfahrel.my.id.adapter.HolidayAdapter
import kalender.alfahrel.my.id.model.CalendarDay
import kalender.alfahrel.my.id.model.HolidayInfo
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var rvCalendar: RecyclerView
    private lateinit var rvHolidays: RecyclerView
    private lateinit var tvNoHoliday: LinearLayout
    private lateinit var fabCurrentMonth: FloatingActionButton

    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        fabCurrentMonth = findViewById(R.id.fabCurrentMonth)
        fabCurrentMonth.setOnClickListener {
            calendar = Calendar.getInstance()
            updateCalendar()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        tvMonthYear  = findViewById(R.id.tvMonthYear)
        btnPrev      = findViewById(R.id.btnPrev)
        btnNext      = findViewById(R.id.btnNext)
        rvCalendar   = findViewById(R.id.rvCalendar)
        rvHolidays   = findViewById(R.id.rvHolidays)
        tvNoHoliday  = findViewById(R.id.tvNoHoliday)

        rvCalendar.layoutManager = GridLayoutManager(this, 7)
        rvCalendar.itemAnimator  = null

        rvHolidays.layoutManager = LinearLayoutManager(this)
        rvHolidays.itemAnimator  = null

        btnPrev.setOnClickListener { calendar.add(Calendar.MONTH, -1); updateCalendar() }
        btnNext.setOnClickListener { calendar.add(Calendar.MONTH,  1); updateCalendar() }

        updateCalendar()
    }

    private fun updateCalendar() {
        val year  = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val monthNames = listOf(
            "Januari","Februari","Maret","April","Mei","Juni",
            "Juli","Agustus","September","Oktober","November","Desember"
        )
        tvMonthYear.text = "${monthNames[month]} $year"

        rvCalendar.adapter = CalendarAdapter(buildDayList(year, month))

        val monthHols = getHolidaysForMonth(year, month)
        if (monthHols.isEmpty()) {
            rvHolidays.visibility  = View.GONE
            tvNoHoliday.visibility = View.VISIBLE
        } else {
            rvHolidays.visibility  = View.VISIBLE
            tvNoHoliday.visibility = View.GONE
            rvHolidays.adapter = HolidayAdapter(monthHols)
        }
    }

    private fun buildDayList(year: Int, month: Int): List<CalendarDay> {
        val days    = mutableListOf<CalendarDay>()
        val tmpCal  = Calendar.getInstance().apply { set(year, month, 1) }

        var firstDow = tmpCal.get(Calendar.DAY_OF_WEEK) - 2
        if (firstDow < 0) firstDow = 6
        repeat(firstDow) { days.add(CalendarDay(0, false, false, false, null, null, null)) }

        val daysInMonth = tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val today       = Calendar.getInstance()

        for (day in 1..daysInMonth) {
            tmpCal.set(year, month, day)
            val dow      = tmpCal.get(Calendar.DAY_OF_WEEK)
            val isSunday = dow == Calendar.SUNDAY
            val key      = String.format("%04d-%02d-%02d", year, month + 1, day)
            val entry    = allHolidays[key]
            val isToday  = year  == today.get(Calendar.YEAR)
                    && month == today.get(Calendar.MONTH)
                    && day   == today.get(Calendar.DAY_OF_MONTH)
            days.add(
                CalendarDay(
                    day,
                    isToday,
                    entry != null,
                    isSunday,
                    entry?.name,
                    entry?.description,
                    entry?.type
                )
            )
        }
        return days
    }

    private fun getHolidaysForMonth(year: Int, month: Int): List<HolidayInfo> {
        val result = mutableListOf<HolidayInfo>()
        val tmpCal = Calendar.getInstance().apply { set(year, month, 1) }
        val days   = tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..days) {
            val key   = String.format("%04d-%02d-%02d", year, month + 1, day)
            val entry = allHolidays[key] ?: continue
            result.add(HolidayInfo(day, month + 1, year, entry.name, entry.description, entry.type))
        }
        return result
    }
}