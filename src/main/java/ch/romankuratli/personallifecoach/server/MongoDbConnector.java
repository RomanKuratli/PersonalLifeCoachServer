package ch.romankuratli.personallifecoach.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Connects to the MongoDB database and provides access to collections
 */
public class MongoDbConnector {
    private static MongoClient client;
    private static final Logger logger = Logger.getLogger(MongoDbConnector.class.getName());
    private static final String dbName = "PersonalLifeCoach";
    private static MongoDatabase db;

    private MongoDbConnector(){}

    public static void connect() throws IOException {
        client = MongoClients.create();
        if (client == null) {
            String msg = "Could not connect to MongoDB";
            logger.severe(msg);
            throw new IOException(msg);
        }
        logger.info("Successfully connected to MongoDB");
        db = client.getDatabase(dbName);
        if (db == null) {
            String msg = "Could not load database " + dbName;
            logger.severe(msg);
            throw new IOException(msg);
        }
        logger.info("Successfully loaded database " + dbName);
    }

    private static boolean collectionExists(String colName) {
        for( String existingCol : db.listCollectionNames()) {
            if (colName.equals(existingCol)) return true;
        }
        return false;
    }

    public static MongoCollection<Document> getCollection(String colName) {
        if (db == null) {
            String msg = "connect() must be called prior to requesting collection " + colName;
            logger.severe(msg);
            throw new IllegalStateException(msg);
        }
        if (!collectionExists(colName)) {
            String msg = "Could not find collection in database: " + colName;
            logger.severe("Could not find collection in database: " + colName);
            throw new IllegalArgumentException(msg);
        }
        return db.getCollection(colName);
    }
}
