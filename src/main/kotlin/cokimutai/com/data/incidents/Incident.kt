package cokimutai.com.data.incidents

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Incident(
    val location: String,
    val time: Long,
    val description: String,
    val status: String = "received",
    val reportedBy: String,
    val assignedTo: String? = null,
    @BsonId val id: String = ObjectId().toString()
)
