package de.liruhg.lirucloud.library.util

import java.math.BigDecimal
import java.math.RoundingMode

class RoundUtils {

    companion object {
        fun roundDouble(input: Double, places: Int): Double {
            var bigDecimal = BigDecimal(input.toString())
            bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP)
            return bigDecimal.toDouble()
        }
    }
}