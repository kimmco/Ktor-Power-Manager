package cokimutai.com.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
