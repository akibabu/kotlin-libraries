package akibabu.mongodb

import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.Logger

abstract class MongoRepositoryBase<T : MongoDbo>(
    clazz: Class<T>,
    private val collectionName: String,
    database: MongoDatabase,
    collectionOnCreation: ((MongoCollection<T>) -> Unit)? = null,
    protected val logger: Logger
) {

    protected val collection: MongoCollection<T>

    init {
        collection = runBlocking {
            if (!database.listCollectionNames().toList().contains(collectionName)) {
                logger.info("Creating collection $collectionName")
                database.createCollection(collectionName, CreateCollectionOptions())
                database.getCollection(collectionName, clazz).apply {
                    collectionOnCreation?.invoke(this)
                }
            } else {
                logger.info("Using existing collection $collectionName")
                database.getCollection(collectionName, clazz)
            }
        }
    }

    suspend fun deleteAll() {
        collection.deleteMany(Filters.empty())
        logger.info("Deleted all documents in collection $collectionName")
    }

    suspend fun insert(value: T) {
        collection.insertOne(value)
    }

    suspend fun upsert(value: T) {
        collection.replaceOne(Filters.eq("_id", value.getId()), value, upsertOptions)
    }

    fun findAll(): Flow<T> {
        return collection.find(Filters.empty())
    }

    companion object {
        private val upsertOptions: ReplaceOptions = ReplaceOptions().upsert(true)
    }
}