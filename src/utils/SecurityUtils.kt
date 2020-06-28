package com.assignment.utils

import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val hashKey = hex("dc05ad28ecf2d4a49f33b0ee4fe72b3f")
private val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

fun hashPassword(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}