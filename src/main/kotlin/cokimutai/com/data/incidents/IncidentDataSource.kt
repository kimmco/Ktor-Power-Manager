package cokimutai.com.data.incidents

interface IncidentDataSource {
    suspend fun reportIncident(incident: Incident): Boolean
    suspend fun fetchIncidents(): List<Incident>
    suspend fun assignIncident(update: IncidentStatusUpdate): Boolean
    suspend fun updateStatus(status: IncidentStatusUpdate): Boolean
    suspend fun viewMyIncidents(currentUser: String): List<Incident>
   // suspend fun viewAssignedIncidents(): List<Incident>

}