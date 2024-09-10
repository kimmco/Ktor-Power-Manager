package cokimutai.com

import cokimutai.com.data.incidents.IncidentDataSource
import cokimutai.com.data.incidents.MongoIncidentDataSource
import cokimutai.com.data.user.MongoUserDataSource
import cokimutai.com.plugins.*
import cokimutai.com.security.hashing.SHA256HashingService
import cokimutai.com.security.token.JwtTokenService
import cokimutai.com.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
  /*  embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = ApplicationConfig("application.yaml")  // Ensure it's pointing to the right config file
        connector {
            port = config.property("ktor.deployment.port").getString().toInt()
            host = config.property("ktor.deployment.host").getString()
        }
        module(Application::module)
    }).start(wait = true) */
}

fun Application.module() {
    val mongoPassword = System.getenv("MONGO_PW")
    val dbName = "power-manager"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://cokimutai:$mongoPassword@cokimutaicluster.igzlppw.mongodb.net/$dbName?retryWrites=true&w=majority&appName=CokimutaiCluster"
    ).coroutine
        .getDatabase(dbName)

    val userDataSource = MongoUserDataSource(db)
    val incidentDataSource = MongoIncidentDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = "http://0.0.0.0:8080",// environment.config.property("jwt.issuer").getString(),
        audience = "users" , // environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60 * 60 * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()


    configureSecurity(tokenConfig)
    configureRouting(hashingService,userDataSource,incidentDataSource, tokenService,tokenConfig)
    configureSerialization()
    configureMonitoring()
    //configureHTTP()
    //configureMongo()
}
