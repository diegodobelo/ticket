package com.diegodobelo.ticketmaster.usecases

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import javax.inject.Inject

class FormatDateUseCase @Inject constructor() {

    @SuppressLint("SimpleDateFormat")
    operator fun invoke(date: String): String {
        return try {
            val dateParser = SimpleDateFormat("dd-MM-yyyy")
            val dateFormatter = SimpleDateFormat("MMM dd")
            val parsedDate = dateParser.parse(date)

            if (parsedDate != null ) {
                dateFormatter.format(parsedDate)
            } else {
                date
            }
        } catch (e: ParseException) {
            date
        }
    }
}