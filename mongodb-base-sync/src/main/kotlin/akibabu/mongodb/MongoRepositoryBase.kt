package akibabu.mongodb

import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.MongoCollection
import com.mongodb.kotlin.client.MongoDatabase
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
        if (!database.listCollectionNames().toList().contains(collectionName)) {
            logger.info("Creating collection $collectionName")
            database.createCollection(collectionName, CreateCollectionOptions())
            this.collection = database.getCollection(collectionName, clazz).apply {
                collectionOnCreation?.invoke(this)
            }
        } else {
            logger.info("Using existing collection $collectionName")
            this.collection = database.getCollection(collectionName, clazz)
        }

    }

    fun deleteAll() {
        collection.deleteMany(Filters.empty())
        logger.info("Deleted all documents in collection $collectionName")
    }

    fun insert(value: T) {
        collection.insertOne(value)
    }

    fun upsert(value: T) {
        collection.replaceOne(Filters.eq("_id", value.getId()), value, upsertOptions)
    }

    fun findAll(): List<T> {
        return collection.find(Filters.empty()).toList()
    }

    companion object {
        private val upsertOptions: ReplaceOptions = ReplaceOptions().upsert(true)
    }
}