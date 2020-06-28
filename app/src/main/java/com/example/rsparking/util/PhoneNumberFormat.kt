package com.example.rsparking.util

fun formatPhoneNumber(number: String): String? {
    var number = number
    number = number.substring(0, number.length - 4) + "-" + number.substring(
        number.length - 4,
        number.length
    )
    number = number.substring(0, number.length - 8) + ")" + number.substring(
        number.length - 8,
        number.length
    )
    number = number.substring(0, number.length - 12) + "(" + number.substring(
        number.length - 12,
        number.length
    )
    return number
}