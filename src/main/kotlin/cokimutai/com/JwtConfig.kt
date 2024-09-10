package cokimutai.com

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private const val secret = "1234"
    private const val issuer = "ktor.io"
    private const val validityInMs = 36_000_00 * 10
    private val algorithm = Algorithm.HMAC256(secret)

    fun verifier(): JWTVerifier {
      return JWT
          .require(algorithm)
          .withIssuer(issuer)
          .build()
    }

    fun generateToken(userId: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() =  Date(System.currentTimeMillis() + validityInMs)

}