package akibabu.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.MongoClient
import com.mongodb.kotlin.client.MongoDatabase

object MongoDb {

    fun getDatabase(connectionString: String, databaseName: String): MongoDatabase {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(connectionString))
            .retryWrites(true)
            .retryReads(true)
            .build()
        val mongoClient: MongoClient = MongoClient.create(settings)
        return mongoClient.getDatabase(databaseName)
    }
}