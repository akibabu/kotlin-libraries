package akibabu.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.MongoClient
import com.mongodb.kotlin.client.MongoDatabase
import java.util.concurrent.TimeUnit

class MongoDb(
    connectionString: String,
    databaseName: String,
    configurer: ((MongoClientSettings.Builder) -> Unit)? = null
) {

    private val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .applyToSocketSettings {
            it.connectTimeout(6, TimeUnit.SECONDS)
            it.readTimeout(6, TimeUnit.SECONDS)
        }
        .retryWrites(true)
        .retryReads(true)
        .apply {
            configurer?.invoke(this)
        }
        .build()
    val mongoClient: MongoClient = MongoClient.create(settings)
    val database: MongoDatabase = mongoClient.getDatabase(databaseName)
}