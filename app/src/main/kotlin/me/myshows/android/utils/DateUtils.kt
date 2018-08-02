package me.myshows.android.utils

import java.text.SimpleDateFormat
import java.util.*

private val FORMATTER = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
private val FORMATTER_ISO8601 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

fun parse(rawDate: String?): Date? =
        when (rawDate?.isEmpty() == false) {
            true -> FORMATTER.parse(rawDate)
            else -> null
        }

fun parseInMillis(rawDate: String?): Long = parse(rawDate)?.time ?: Long.MAX_VALUE

fun parseISO8601(isoString: String?): Date? =
        when (isoString?.isEmpty() == false) {
            true -> FORMATTER_ISO8601.parse(isoString)
            else -> null
        }

fun parseInMillisISO8601(isoString: String?): Long = parseISO8601(isoString)?.time
        ?: java.lang.Long.MAX_VALUE
