package cokimutai.com.data.incidents

import com.mongodb.client.model.Filters.eq
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoIncidentDataSource(
    db: CoroutineDatabase
): IncidentDataSource {
    private val incidentCollection = db.getCollection<Incident>()

    override suspend fun reportIncident(incident: Incident): Boolean {
       return incidentCollection.insertOne(incident).wasAcknowledged()
    }

    override suspend fun fetchIncidents(): List<Incident> {
        return try {
            incidentCollection.find().toList()
        }catch (e: Exception) {
            e.printStackTrace()
            emptyList<Incident>()
        }
    }

    override suspend fun assignIncident(update: IncidentStatusUpdate): Boolean {
        return try {
            val updateResult = incidentCollection.updateOne(
                filter = eq("_id", update.id),
                update = setValue(Incident::assignedTo, update.assignedTo)
            )
            updateResult.modifiedCount > 0
        }catch (e: Exception) {
            //
            false
        }
    }

    override suspend fun updateStatus(status: IncidentStatusUpdate): Boolean {
        return try {
            // Find the incident by its ID and update its status
            val updateResult = incidentCollection.updateOne(
                filter = eq("_id", status.id), // Matching the incident by its unique ID
                update = setValue(Incident::status, status.status) // Updating the status field
            )

            // Return true if at least one document was modified, indicating success
            updateResult.modifiedCount > 0
        } catch (e: Exception) {
            // Log error if needed, or handle it according to your error handling strategy
            false
        }
    }

    override suspend fun viewMyIncidents(currentUser: String): List<Incident> {
        return try {
            // Fetch incidents that were reported by the current user
            incidentCollection.find(Incident::reportedBy eq currentUser).toList()
        } catch (e: Exception) {
            // Log error or handle accordingly
            emptyList() // Return an empty list if fetching fails
        }
    }
}