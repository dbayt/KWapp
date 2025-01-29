package com.kwapp.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d.M.yyyy", Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            date?.let { outputFormat.format(it) } ?: inputDate // Return formatted date or original if parsing fails
        } catch (e: Exception) {
            inputDate // In case of error, return the original date
        }
    }
}