package kalender.alfahrel.my.id.data

import kalender.alfahrel.my.id.model.HolidayEntry
import kalender.alfahrel.my.id.model.HolidayType

object GermanyHolidaysData {
    val allHolidays: Map<String, HolidayEntry> = mapOf(

        "2026-01-01" to HolidayEntry(
            "Neujahr",
            "Neujahrstag – der erste Tag des neuen Jahres im gregorianischen Kalender.",
            HolidayType.NATIONAL
        ),
        "2026-01-06" to HolidayEntry(
            "Heilige Drei Könige",
            "Dreikönigstag – Fest der Erscheinung des Herrn. Gesetzlicher Feiertag in Baden-Württemberg, Bayern und Sachsen-Anhalt.",
            HolidayType.RELIGIOUS
        ),
        "2026-04-03" to HolidayEntry(
            "Karfreitag",
            "Gedenktag an die Kreuzigung und den Tod Jesu Christi. Gesetzlicher Feiertag in allen Bundesländern.",
            HolidayType.RELIGIOUS
        ),
        "2026-04-05" to HolidayEntry(
            "Ostersonntag",
            "Auferstehung Jesu Christi von den Toten. In Brandenburg gesetzlicher Feiertag.",
            HolidayType.RELIGIOUS
        ),
        "2026-04-06" to HolidayEntry(
            "Ostermontag",
            "Der Tag nach Ostersonntag. Gesetzlicher Feiertag in allen Bundesländern.",
            HolidayType.RELIGIOUS
        ),
        "2026-05-01" to HolidayEntry(
            "Tag der Arbeit",
            "Internationaler Tag der Arbeit. Gesetzlicher Feiertag in allen Bundesländern.",
            HolidayType.NATIONAL
        ),
        "2026-05-14" to HolidayEntry(
            "Christi Himmelfahrt",
            "Himmelfahrt Jesu Christi, 39 Tage nach Ostern. Gesetzlicher Feiertag in allen Bundesländern.",
            HolidayType.RELIGIOUS
        ),
        "2026-05-24" to HolidayEntry(
            "Pfingstsonntag",
            "Ausgießung des Heiligen Geistes über die Apostel. In Brandenburg gesetzlicher Feiertag.",
            HolidayType.RELIGIOUS
        ),
        "2026-05-25" to HolidayEntry(
            "Pfingstmontag",
            "Der Tag nach Pfingstsonntag. Gesetzlicher Feiertag in allen Bundesländern.",
            HolidayType.RELIGIOUS
        ),
        "2026-06-04" to HolidayEntry(
            "Fronleichnam",
            "Hochfest des Leibes und Blutes Christi. Gesetzlicher Feiertag in Bayern, Baden-Württemberg, Hessen, NRW, Rheinland-Pfalz, Saarland und Teilen anderer Bundesländer.",
            HolidayType.RELIGIOUS
        ),
        "2026-08-15" to HolidayEntry(
            "Mariä Himmelfahrt",
            "Aufnahme Mariens in den Himmel. Gesetzlicher Feiertag im Saarland und in Teilen Bayerns.",
            HolidayType.RELIGIOUS
        ),
        "2026-10-03" to HolidayEntry(
            "Tag der Deutschen Einheit",
            "Nationalfeiertag Deutschlands – Gedenktag zur Wiedervereinigung am 3. Oktober 1990.",
            HolidayType.NATIONAL
        ),
        "2026-10-31" to HolidayEntry(
            "Reformationstag",
            "Gedenktag an den Beginn der Reformation durch Martin Luther 1517. Gesetzlicher Feiertag in Brandenburg, Bremen, Hamburg, Mecklenburg-Vorpommern, Niedersachsen, Sachsen, Sachsen-Anhalt, Schleswig-Holstein und Thüringen.",
            HolidayType.RELIGIOUS
        ),
        "2026-11-01" to HolidayEntry(
            "Allerheiligen",
            "Gedenktag aller Heiligen. Gesetzlicher Feiertag in Bayern, Baden-Württemberg, NRW, Rheinland-Pfalz und Saarland.",
            HolidayType.RELIGIOUS
        ),
        "2026-11-18" to HolidayEntry(
            "Buß- und Bettag",
            "Tag der Buße und des Gebets. Gesetzlicher Feiertag nur in Sachsen.",
            HolidayType.RELIGIOUS
        ),
        "2026-12-25" to HolidayEntry(
            "1. Weihnachtstag",
            "Erster Weihnachtsfeiertag – Geburt Jesu Christi. Gesetzlicher Feiertag in allen Bundesländern.",
            HolidayType.RELIGIOUS
        ),
        "2026-12-26" to HolidayEntry(
            "2. Weihnachtstag",
            "Zweiter Weihnachtsfeiertag. Gesetzlicher Feiertag in allen Bundesländern.",
            HolidayType.RELIGIOUS
        ),
    )
}