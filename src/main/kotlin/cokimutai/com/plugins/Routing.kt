package cokimutai.com.plugins

import cokimutai.com.routes.authenticate
import cokimutai.com.data.incidents.Incident
import cokimutai.com.data.incidents.IncidentDataSource
import cokimutai.com.data.user.User
import cokimutai.com.data.user.UserDataSource
import cokimutai.com.security.hashing.HashingService
import cokimutai.com.security.token.TokenConfig
import cokimutai.com.security.token.TokenService
import cokimutai.com.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun Application.configureRouting(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    incidentDataSource: IncidentDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("poweroutage")
    val userCollection = database.getCollection<User>()
    val incidentCollection = database.getCollection<Incident>()
    routing {
     /*   post("/register") {
            val user = call.receive<User>()
            userCollection.insertOne(user)
            call.respond(user)
        }
        post("/login") {
            // Implement login logic and return JWT token
            // Receive the login request with a JSON body containing the username and password
            val loginRequest = call.receive<LoginRequest>()

            // Fetch the user from the database by username
            val user = userCollection.findOne(LoginRequest::username eq loginRequest.username)

            // Check if the user exists and the password matches
            if (user == null || !BCrypt.checkpw(loginRequest.password, user.hashPassword)) {
                // If authentication fails, respond with a 401 Unauthorized status
                call.respond(HttpStatusCode.Unauthorized, "Invalid Credentials")
                return@post
            }
            // Generate a JWT token for the authenticated user
            val  token = JwtConfig.generateToken(user.username)

            // Respond with the generated token
            call.respond(mapOf("token" to token))
        }

        authenticate("auth-jwt") {
            get("/secure") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                call.respond("Hello, User with ID: $userId")
            }
            post("/incidents") {
                val incident = call.receive<Incident>()
                incidentCollection.insertOne(incident)
                call.respond(incident)
            }

            get("/incidents") {
                val incidents = incidentCollection.find().toList()
                call.respond(incidents)
            }
            post("/updateStatus") {
                // Receive the request body as an IncidentStatusUpdate object
                val statusUpdate = call.receive<IncidentStatusUpdate>()

                // Access the status property
                val status = statusUpdate.status

                // Process the status (e.g., update in the database)
                // Assume there's a function updateIncidentStatus that handles this
                // TODO updateIncidentStatus(status)

                // Respond back to the client
                call.respondText("Status updated to: $status")
            }

            put("/incidents/{id}") {
                val id =
                    call.parameters["id"] ?: throw IllegalArgumentException("Invalid incident ID")
                val status = call.receive<IncidentStatusUpdate>().status
                val updatedIncident = incidentCollection.findOneAndUpdate(
                    Incident::id eq id,
                    setValue(Incident::status, status)
                )
                call.respond(updatedIncident ?: "Incident not found")
            }
        }  */
        signIn(hashingService,userDataSource,tokenService,tokenConfig)
        signUp(hashingService,userDataSource)
        authenticate()
        getUserId()
        insertIncidents(incidentDataSource)
        getIncidents(incidentDataSource)
        assignIncident(incidentDataSource)
        incidentStatusUpdate(incidentDataSource)
        viewMyIncidents(incidentDataSource)
    }
}
