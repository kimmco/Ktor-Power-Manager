package cokimutai.com.data.incidents

import kotlinx.serialization.Serializable

@Serializable
data class IncidentStatusUpdate(
    val id: String,
    val status: String = "received",
    val assignedTo: String? = null ,
)

