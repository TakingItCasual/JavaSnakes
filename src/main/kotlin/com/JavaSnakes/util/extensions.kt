package com.javasnakes.util

fun Int.asOrdinal(): String {
    return this.toString() + when (this % 100) {
        11, 12, 13 -> "th"
        else -> when (this % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}
