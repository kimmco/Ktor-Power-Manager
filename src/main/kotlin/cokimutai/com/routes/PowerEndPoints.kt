package cokimutai.com.routes

import cokimutai.com.data.incidents.Incident
import cokimutai.com.data.incidents.IncidentStatusUpdate
import cokimutai.com.data.incidents.IncidentDataSource
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.*


fun Route.insertIncidents(
    incidentDataSource: IncidentDataSource
) {
    authenticate {
        post("/incident") {
            // val incident = call.receive<Incident>()
            val incident =
                kotlin.runCatching<Incident?> { call.receiveNullable<Incident>() }.getOrNull()
                    ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
            val areFieldsBlank = incident.description.isBlank() || incident.location.isBlank()

            if (areFieldsBlank) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            val report = Incident(
                location = incident.location,
                time = incident.time,
                description = incident.description,
                reportedBy = incident.reportedBy
            )

            val wasAcknowledged = incidentDataSource.reportIncident(report)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getIncidents(
    incidentDataSource: IncidentDataSource
){
    authenticate {
        get("/incidents") {
            val incidents = incidentDataSource.fetchIncidents()
            call.respond(HttpStatusCode.OK, incidents)
        }
    }
}

fun Route.assignIncident(
    incidentDataSource: IncidentDataSource
) {
    authenticate {
        put("/assign/{id}") {
            val id =
                call.parameters["id"] ?: throw IllegalArgumentException("Invalid incident ID")
            val assignedTo = call.receive<IncidentStatusUpdate>().assignedTo
            val isAssignedNull = assignedTo.isNullOrBlank()
            if (isAssignedNull) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }
            val update = IncidentStatusUpdate(
                id = id,
                assignedTo = assignedTo!!,
            )
            val updatedIncidentSuccessful = incidentDataSource.assignIncident(update)

            if (!updatedIncidentSuccessful){
                call.respond(HttpStatusCode.Conflict, "Incident not found")
                return@put
            }
            call.respond(HttpStatusCode.OK, " Incident successfully assigned.")
        }
    }

}

fun Route.incidentStatusUpdate(
    incidentDataSource: IncidentDataSource
) {
    authenticate {
        put("/updateStatus/{id}") {
            val id =
                call.parameters["id"] ?: throw IllegalArgumentException("Invalid incident ID")
            val status = call.receive<IncidentStatusUpdate>().status
            val update = IncidentStatusUpdate(
                id = id,
                status = status
            )
            val updatedIncidentSuccessful = incidentDataSource.updateStatus(update)

            if (!updatedIncidentSuccessful){
                call.respond(HttpStatusCode.Conflict, "Incident not found")
                return@put
            }
            call.respond(HttpStatusCode.OK, "Incident successfully updated")
        }
    }

}
fun Route.viewMyIncidents(
    incidentDataSource: IncidentDataSource
){
    authenticate {
        get("/myReports") {
            val principal = call.principal<JWTPrincipal>() ?: return@get call.respond(
                HttpStatusCode.Unauthorized
            )

            val currentUser = principal.getClaim("userName", String::class)

            val data = incidentDataSource.viewMyIncidents(currentUser!!)
            call.respond(data)
        }
    }
}