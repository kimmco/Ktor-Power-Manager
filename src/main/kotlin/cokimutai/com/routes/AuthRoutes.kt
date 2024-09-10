package cokimutai.com.routes

import cokimutai.com.data.requests.AuthRequest
import cokimutai.com.data.responses.AuthResponse
import cokimutai.com.data.user.User
import cokimutai.com.data.user.UserDataSource
import cokimutai.com.security.hashing.HashingService
import cokimutai.com.security.hashing.SaltedHash
import cokimutai.com.security.token.TokenClaim
import cokimutai.com.security.token.TokenConfig
import cokimutai.com.security.token.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("signup") {
        val request = kotlin.runCatching<AuthRequest?> { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val areFieldsBlank = request.userName.isBlank() || request.password.isBlank()
        val isPasswordShort = request.password.length < 4
        if (areFieldsBlank || isPasswordShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.userName,
            email = "example@cokimutai.co.ke",
            hashPassword = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("signin") {
        val request = kotlin.runCatching<AuthRequest?> { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = userDataSource.getUserByUserName(request.userName)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect Username or password")
            return@post
        }
        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.hashPassword,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect Username or password")
            return@post
        }
        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userName",
                value = user.username
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message =  AuthResponse(
                token = token
            )
        )
    }

}

fun Route.authenticate(){
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getUserId() {
    authenticate {
        get("userdetails") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userName", String::class)
            call.respond(HttpStatusCode.OK, "Your userName is: $userId")
        }
    }
}