package com.JavaSnakes.util

fun Int.asOrdinal(): String {
    return when (this % 100) {
        11, 12, 13 -> this.toString() + "th"
        else -> this.toString() + when (this % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}
