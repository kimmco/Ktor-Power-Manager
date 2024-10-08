package cokimutai.com.data.user

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


@Serializable
data class User(
    val username: String,
    val email: String,
    val hashPassword: String,
    val role: String = "user",
    val salt: String,
    @BsonId val id: String = ObjectId().toString()
)
