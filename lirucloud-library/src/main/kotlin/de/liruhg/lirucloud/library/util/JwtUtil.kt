package de.liruhg.lirucloud.library.util

import de.liruhg.lirucloud.library.user.CloudWebUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

class JwtUtil {

    companion object {
        private const val KEY: String =
            "MjM4NzU5OHQzZ2podnV3aGc4N3U5aGVydWhndXdpZWhncmh2aWV3aGhnNzgzaDRqaWd1dmVocjc4OWd2aGV3dXJpaGdudmh3MzQ4OTdnaDhlcmh1aWd2aGl1anJmZWRoc3ZnODc5cmVoZ3N1aTRoZ3VpZWo="

        fun isTokenValid(jwt: String, cloudWebUser: CloudWebUser): Boolean {
            return (this.extractEmail(jwt) == cloudWebUser.email) && !this.isTokenExpired(jwt)
        }

        private fun isTokenExpired(jwt: String): Boolean {
            return this.extractExpiration(jwt)?.before(Date(System.currentTimeMillis())) ?: true
        }

        fun generateToken(customClaims: Map<String, Any>, cloudWebUser: CloudWebUser, expiration: Date): String {
            return Jwts.builder()
                .claims(customClaims)
                .subject(cloudWebUser.email)
                .issuedAt(Date(System.currentTimeMillis()))
                .expiration(expiration)
                .signWith(this.getSigningKey())
                .compact()
        }

        fun extractEmail(jwt: String): String? {
            return this.extractClaim(jwt, Claims::getSubject)
        }

        fun extractExpiration(jwt: String): Date? {
            return this.extractClaim(jwt, Claims::getExpiration)
        }

        fun <T> extractClaim(jwt: String, claimResolver: (Claims) -> T): T? {
            return claimResolver(this.extractClaims(jwt))
        }

        private fun extractClaims(jwt: String): Claims {
            return Jwts
                .parser()
                .verifyWith(this.getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .payload
        }

        private fun getSigningKey(): SecretKey {
            val keyBuffer = Decoders.BASE64.decode(KEY)
            return Keys.hmacShaKeyFor(keyBuffer) as SecretKey
        }
    }
}