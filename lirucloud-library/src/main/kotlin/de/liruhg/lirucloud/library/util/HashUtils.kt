package de.liruhg.lirucloud.library.util

import at.favre.lib.crypto.bcrypt.BCrypt
import java.security.MessageDigest

class HashUtils {

    companion object {
        fun hashStringMD5(input: String): String {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(input.toByteArray())

            val hashBytes = messageDigest.digest()
            val hashBuilder = StringBuilder()

            hashBytes.forEach {
                hashBuilder.append(String.format("%02x", it))
            }

            return hashBuilder.toString()
        }

        fun hashStringBcrypt(password: String): String {
            return BCrypt.withDefaults().hashToString(12, password.toCharArray())
        }

        fun verifyStringBcrypt(password: String, hash: String): Boolean {
            return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
        }
    }
}