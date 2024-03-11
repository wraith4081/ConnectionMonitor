package tr.com.wraith.connectionmonitor;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.List;

public class MongoHandler {
    private final MongoClient mongoClient;
    private MongoDatabase database;
    private boolean isDatabaseSpecified = false;
    public MongoHandler(String connectionString) {

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        this.mongoClient = MongoClients.create(settings);
}
    public void setDatabase(String database) {
        this.database = mongoClient.getDatabase(database);
        this.isDatabaseSpecified = true;
    }

    public boolean checkConnection() {
        try {
            mongoClient.getDatabase("players").runCommand(new Document("ping", 1));
            return true;
        } catch (MongoException e) {
            return  false;
        }
    }

    public void addDocument(String collectionName, Document document) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        collection.insertOne(document);
    }

    public List<Document> findDocuments(String collectionName, Bson filter) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);

        FindIterable<Document> results = collection.find(filter);
        return results.into(new ArrayList<>());
    }

    public void updateDocument(String collectionName, Document query, Bson update) {
        UpdateOptions options = new UpdateOptions().upsert(true);

        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        collection.updateOne(query, update, options);
    }

    public Document findUserDocument(UUID uuid) {
        MongoCollection<Document> collection = this.database.getCollection("players"); // Assuming a 'players' collection
        Document query = new Document("uuid", uuid.toString());
        return collection.find(query).first();
    }

    public void createUserDocument(UUID uuid) {
        MongoCollection<Document> collection = this.database.getCollection("players");
        Document document = new Document("uuid", uuid.toString())
                .append("connections", new ArrayList<>()); // Initialize with empty connections
        collection.insertOne(document);
    }

    public void updateLoginConnections(UUID uuid, long loginTime) {
        MongoCollection<Document> collection = this.database.getCollection("players");
        Document query = new Document("uuid", uuid.toString());
        Document update = new Document("$push", new Document("connections", new Document("login", loginTime)));

        collection.updateOne(query, update);
    }

    public void updateLogoutConnections(UUID uuid, long logoutTime) {
        MongoCollection<Document> collection = this.database.getCollection("players");
        Document query = new Document("uuid", uuid.toString());

        /*
        // Find the player document and get the size of the connections array
        Document playerDoc = collection.find(query).first();
        if (playerDoc == null) {
            System.out.println("Player document not found.");
            return;
        }

        List<Document> connections = playerDoc.getList("connections", Document.class);
        int lastIndex = connections.size() - 1;

        Document update = new Document("$set",
                new Document("connections." + lastIndex + ".logout", logoutTime));

        collection.updateOne(query, update);
         */

        Document update = new Document("$push", new Document("connections", new Document("logout", logoutTime)));

        collection.updateOne(query, update);
    }
}