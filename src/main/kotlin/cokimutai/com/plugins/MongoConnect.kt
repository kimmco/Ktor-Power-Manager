package cokimutai.com.plugins

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.reactivestreams.KMongo

lateinit var database: CoroutineDatabase

fun Application.configureMongo() {
    val connectionString = "mongodb://localhost:27017" // Replace with your MongoDB connection string
    val client: CoroutineClient = KMongo.createClient(connectionString).coroutine
    database = client.getDatabase("your_database_name") // Replace with your database name
}