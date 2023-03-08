package com.example.tippingapp

import kotlin.math.roundToInt


fun calcTip (bill: String, split: Int, percent: Float): Double {
    return if(bill.isNotEmpty()) {
        percent * (bill.toDouble()/split)
    } else {
        return 0.00
    }
}

fun calcTotalOwed (bill: String, split: Int, percent: Float): Double {
    return if(bill.isNotEmpty()) {
        (calcTip(bill, split, percent)/split) + (bill.toDouble()/split).toDouble()
    } else {
        return 0.00
    }

}

fun roundTo2Decimals(number: Double): Double{
    return (number * 100.00).roundToInt() / 100.00
}