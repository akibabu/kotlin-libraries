package akibabu.mongodb

import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.MongoCollection
import com.mongodb.kotlin.client.MongoDatabase
import org.apache.logging.log4j.Logger
import java.util.function.Consumer

abstract class MongoRepositoryBase<T : Any>(
    clazz: Class<T>,
    private val collectionName: String,
    database: MongoDatabase,
    collectionOnCreation: Consumer<MongoCollection<T>>? = null,
    protected val logger: Logger
) {

    protected val collection: MongoCollection<T>

    init {
        if (!database.listCollectionNames().toList().contains(collectionName)) {
            logger.info("Creating collection $collectionName")
            database.createCollection(collectionName, CreateCollectionOptions())
            this.collection = database.getCollection(collectionName, clazz).apply {
                collectionOnCreation?.accept(this)
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

    fun findAll(): List<T> {
        return collection.find(Filters.empty()).toList()
    }
}