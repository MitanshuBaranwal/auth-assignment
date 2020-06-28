package com.assignment.utils

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom


fun generateOtp(): String {
    val generatedToken = StringBuilder()
    try {
        val number: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
        for (i in 0 until 6) {
            generatedToken.append(number.nextInt(9))
        }
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return generatedToken.toString()
}