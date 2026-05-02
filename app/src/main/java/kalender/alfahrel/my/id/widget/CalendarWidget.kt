package kalender.alfahrel.my.id.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import kalender.alfahrel.my.id.MainActivity
import kalender.alfahrel.my.id.R
import kalender.alfahrel.my.id.data.AppPreferences
import kalender.alfahrel.my.id.data.CountryHolidays
import kalender.alfahrel.my.id.model.HolidayType
import kalender.alfahrel.my.id.util.LocaleHelper
import java.util.Calendar

class CalendarWidget : AppWidgetProvider() {

    companion object {
        const val TAG = "CalendarWidget"
        const val ACTION_PREV = "kalender.alfahrel.my.id.WIDGET_PREV"
        const val ACTION_NEXT = "kalender.alfahrel.my.id.WIDGET_NEXT"
        const val EXTRA_WIDGET = "widget_id"
    }

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        ids.forEach {
            Log.d(TAG, "onUpdate widgetId=$it")
            updateWidget(ctx, mgr, it)
        }
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        super.onReceive(ctx, intent)

        when (intent.action) {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                Log.d(TAG, "onReceive system time action=${intent.action}, refreshing all widgets")
                val mgr = AppWidgetManager.getInstance(ctx)
                val ids = mgr.getAppWidgetIds(ComponentName(ctx, CalendarWidget::class.java))
                ids.forEach { updateWidget(ctx, mgr, it) }
                return
            }
        }

        val widgetId = intent.getIntExtra(EXTRA_WIDGET, AppWidgetManager.INVALID_APPWIDGET_ID)
        Log.d(TAG, "onReceive action=${intent.action} widgetId=$widgetId")

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

        val current = getOffset(ctx, widgetId)
        val newOffset = when (intent.action) {
            ACTION_PREV -> current - 1
            ACTION_NEXT -> current + 1
            else -> current
        }

        setOffset(ctx, widgetId, newOffset)
        updateWidget(ctx, AppWidgetManager.getInstance(ctx), widgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences("calendar_widget", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        appWidgetIds.forEach {
            Log.d(TAG, "onDeleted widgetId=$it")
            editor.remove("offset_$it")
        }
        editor.apply()
    }

    private fun updateWidget(ctx: Context, mgr: AppWidgetManager, widgetId: Int) {
        val language = AppPreferences.getLanguage(ctx)
        val localizedCtx = LocaleHelper.applyLocale(ctx, language.localeCode)

        val offset = getOffset(ctx, widgetId)
        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, offset) }

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)

        Log.d(TAG, "updateWidget widgetId=$widgetId year=$year month=$month offset=$offset")

        val monthNames = localizedCtx.resources.getStringArray(R.array.month_names)
        val monthYearText = "${monthNames[month]} $year"

        val views = RemoteViews(ctx.packageName, R.layout.widget_calendar)

        views.setTextViewText(R.id.widget_tv_month_year, monthYearText)

        views.setTextViewText(R.id.widget_label_sen, localizedCtx.getString(R.string.day_mon))
        views.setTextViewText(R.id.widget_label_sel, localizedCtx.getString(R.string.day_tue))
        views.setTextViewText(R.id.widget_label_rab, localizedCtx.getString(R.string.day_wed))
        views.setTextViewText(R.id.widget_label_kam, localizedCtx.getString(R.string.day_thu))
        views.setTextViewText(R.id.widget_label_jum, localizedCtx.getString(R.string.day_fri))
        views.setTextViewText(R.id.widget_label_sab, localizedCtx.getString(R.string.day_sat))
        views.setTextViewText(R.id.widget_label_min, localizedCtx.getString(R.string.day_sun))

        views.setOnClickPendingIntent(R.id.widget_btn_prev, buildIntent(ctx, widgetId, ACTION_PREV))
        views.setOnClickPendingIntent(R.id.widget_btn_next, buildIntent(ctx, widgetId, ACTION_NEXT))

        val openApp = PendingIntent.getActivity(
            ctx,
            0,
            Intent(ctx, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_tv_month_year, openApp)

        val serviceIntent = Intent(ctx, CalendarWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            putExtra("year", year)
            putExtra("month", month)
            data = Uri.parse("widget://$widgetId/$year/$month")
        }

        views.setRemoteAdapter(R.id.widget_grid_calendar, serviceIntent)
        views.setPendingIntentTemplate(R.id.widget_grid_calendar, openApp)

        mgr.updateAppWidget(widgetId, views)

        val partialViews = RemoteViews(ctx.packageName, R.layout.widget_calendar)
        partialViews.setTextViewText(R.id.widget_tv_month_year, monthYearText)
        mgr.partiallyUpdateAppWidget(widgetId, partialViews)

        mgr.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_grid_calendar)
    }

    private fun buildIntent(ctx: Context, widgetId: Int, action: String): PendingIntent {
        val intent = Intent(ctx, CalendarWidget::class.java).apply {
            this.action = action
            putExtra(EXTRA_WIDGET, widgetId)
            data = Uri.parse("widget://$widgetId/$action")
        }

        val requestCode = when (action) {
            ACTION_PREV -> widgetId * 2
            ACTION_NEXT -> widgetId * 2 + 1
            else -> widgetId
        }

        return PendingIntent.getBroadcast(
            ctx,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getOffset(ctx: Context, widgetId: Int): Int =
        ctx.getSharedPreferences("calendar_widget", Context.MODE_PRIVATE)
            .getInt("offset_$widgetId", 0)

    private fun setOffset(ctx: Context, widgetId: Int, value: Int) {
        ctx.getSharedPreferences("calendar_widget", Context.MODE_PRIVATE)
            .edit().putInt("offset_$widgetId", value).apply()
    }
}

class CalendarWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Log.d("CalendarWidget", "onGetViewFactory")
        return CalendarFactory(applicationContext, intent)
    }
}

class CalendarFactory(
    private val ctx: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    data class CellData(
        val day: Int,
        val isToday: Boolean,
        val isHoliday: Boolean,
        val isJointLeave: Boolean = false,
        val isSunday: Boolean,
        val isSaturday: Boolean = false,
        val isTrailing: Boolean = false
    )

    private val cells = mutableListOf<CellData>()
    private val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
    private var year = intent.getIntExtra("year", Calendar.getInstance().get(Calendar.YEAR))
    private var month = intent.getIntExtra("month", Calendar.getInstance().get(Calendar.MONTH))

    private fun getHolidays() = CountryHolidays.getHolidays(AppPreferences.getCountry(ctx))

    private fun refreshYearMonth() {
        val prefs = ctx.getSharedPreferences("calendar_widget", Context.MODE_PRIVATE)
        val offset = prefs.getInt("offset_$widgetId", 0)
        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, offset) }
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
    }

    override fun onCreate() {
        Log.d("CalendarWidget", "Factory onCreate year=$year month=$month")
        refreshYearMonth()
        load()
    }

    override fun onDataSetChanged() {
        Log.d("CalendarWidget", "Factory onDataSetChanged")
        refreshYearMonth()
        load()
    }

    override fun onDestroy() {}

    private fun load() {
        cells.clear()

        val holidays = getHolidays()
        val tmpCal = Calendar.getInstance().apply { set(year, month, 1) }
        val today = Calendar.getInstance()

        var firstDow = tmpCal.get(Calendar.DAY_OF_WEEK) - 2
        if (firstDow < 0) firstDow = 6

        // Leading trailing days (prev month)
        if (firstDow > 0) {
            val prevCal = Calendar.getInstance().apply {
                set(year, month, 1)
                add(Calendar.DAY_OF_MONTH, -firstDow)
            }
            for (i in 0 until firstDow) {
                val d = prevCal.get(Calendar.DAY_OF_MONTH)
                val m = prevCal.get(Calendar.MONTH)
                val y = prevCal.get(Calendar.YEAR)
                val dow = prevCal.get(Calendar.DAY_OF_WEEK)
                val isSaturday = dow == Calendar.SATURDAY
                val isSunday = dow == Calendar.SUNDAY
                val key = String.format("%04d-%02d-%02d", y, m + 1, d)
                val entry = holidays[key]
                val isHol = entry != null
                val isJointLeave = entry?.type == HolidayType.JOINT_LEAVE
                cells.add(CellData(d, false, isHol, isJointLeave, isSunday, isSaturday, isTrailing = true))
                prevCal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Current month days
        val daysInMonth = tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..daysInMonth) {
            tmpCal.set(year, month, day)
            val dow = tmpCal.get(Calendar.DAY_OF_WEEK)
            val isSunday = dow == Calendar.SUNDAY
            val isSaturday = dow == Calendar.SATURDAY
            val key = String.format("%04d-%02d-%02d", year, month + 1, day)
            val entry = holidays[key]
            val isHol = entry != null
            val isJointLeave = entry?.type == HolidayType.JOINT_LEAVE
            val isToday = year == today.get(Calendar.YEAR)
                    && month == today.get(Calendar.MONTH)
                    && day == today.get(Calendar.DAY_OF_MONTH)
            cells.add(CellData(day, isToday, isHol, isJointLeave, isSunday, isSaturday))
        }

        // Trailing days (next month)
        val remainder = cells.size % 7
        if (remainder != 0) {
            val nextCal = Calendar.getInstance().apply {
                set(year, month, daysInMonth)
                add(Calendar.DAY_OF_MONTH, 1)
            }
            val trailingCount = 7 - remainder
            for (i in 0 until trailingCount) {
                val d = nextCal.get(Calendar.DAY_OF_MONTH)
                val m = nextCal.get(Calendar.MONTH)
                val y = nextCal.get(Calendar.YEAR)
                val dow = nextCal.get(Calendar.DAY_OF_WEEK)
                val isSaturday = dow == Calendar.SATURDAY
                val isSunday = dow == Calendar.SUNDAY
                val key = String.format("%04d-%02d-%02d", y, m + 1, d)
                val entry = holidays[key]
                val isHol = entry != null
                val isJointLeave = entry?.type == HolidayType.JOINT_LEAVE
                cells.add(CellData(d, false, isHol, isJointLeave, isSunday, isSaturday, isTrailing = true))
                nextCal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        Log.d("CalendarWidget", "load complete size=${cells.size}")
    }

    override fun getCount(): Int = cells.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= cells.size) return RemoteViews(ctx.packageName, R.layout.widget_day_cell_normal)

        val cell = cells[position]

        if (cell.day == 0) return RemoteViews(ctx.packageName, R.layout.widget_day_cell_normal)

        if (cell.isTrailing) {
            val layout = when {
                cell.isJointLeave -> R.layout.widget_day_cell_trailing_joint_leave
                cell.isHoliday    -> R.layout.widget_day_cell_trailing_holiday
                cell.isSunday     -> R.layout.widget_day_cell_trailing_sunday
                cell.isSaturday   -> R.layout.widget_day_cell_trailing_saturday
                else              -> R.layout.widget_day_cell_trailing
            }
            return RemoteViews(ctx.packageName, layout).also {
                it.setTextViewText(R.id.widget_tv_day, cell.day.toString())
                it.setOnClickFillInIntent(R.id.widget_tv_day, Intent())
            }
        }

        val layout = when {
            cell.isToday      -> R.layout.widget_day_cell_today
            cell.isJointLeave -> R.layout.widget_day_cell_joint_leave
            cell.isHoliday    -> R.layout.widget_day_cell_holiday
            cell.isSunday     -> R.layout.widget_day_cell_sunday
            cell.isSaturday   -> R.layout.widget_day_cell_saturday
            else              -> R.layout.widget_day_cell_normal
        }

        return RemoteViews(ctx.packageName, layout).also {
            it.setTextViewText(R.id.widget_tv_day, cell.day.toString())
            it.setOnClickFillInIntent(R.id.widget_tv_day, Intent())
        }
    }

    override fun getLoadingView() = null
    override fun getViewTypeCount() = 10
    override fun getItemId(pos: Int) = pos.toLong()
    override fun hasStableIds() = true
}