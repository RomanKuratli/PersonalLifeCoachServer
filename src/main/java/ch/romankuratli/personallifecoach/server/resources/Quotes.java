package ch.romankuratli.personallifecoach.server.resources;

import ch.romankuratli.personallifecoach.server.MongoDbConnector;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

public class Quotes implements Resource {
    private final static Logger LOGGER = Logger.getLogger(Quotes.class.getName());
    private final static MongoCollection<Document> QUOTES_COL = MongoDbConnector.getCollection("quotes");

    private static String quoteDocToJson(Document doc) {
        doc.remove("quote_hash");
        doc.put("_id", doc.get("_id").toString());
        return doc.toJson();
    }

    public Quotes(){}

    @Override
    public String getSubPath() {
        return "/quotes";
    }

    @Override
    public Resource[] getSubResources() {
        return new Resource[]{new RandomQuote(), new QuoteById()};
    }

    @Override
    public Route handleGet() {
        return (req, res) -> {
            List<String> resultList = new ArrayList<>();
            for (Document doc: QUOTES_COL.find()) {
                resultList.add(quoteDocToJson(doc));
            }
            return resultList;
        };
    }

    @Override
    public Route handlePost() {
        return (req, res) -> {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(req.body());
            Document doc = new Document(jsonObj);
            // add the index value 'quote hash'
            doc.put("quote_hash", doc.get("quote").hashCode());
            QUOTES_COL.insertOne(doc);
            return doc;
        };
    }

    static class QuoteById implements Resource {
        @Override
        public String getSubPath() {
            return "/:id";
        }

        @Override
        public Route handleDelete() {
            return (req, res) -> {
                QUOTES_COL.deleteOne(eq("_id", new ObjectId("57506d62f57802807471dd41")));
                return "{\"msg\": \"ok\"}";
            };
        }
    }

    static class RandomQuote implements Resource {
        private static final Random RAND = new Random();
        @Override
        public String getSubPath() {
            return "/random";
        }

        @Override
        public Route handleGet() {
            return (req, res) -> {
                List<Document> quotes = new ArrayList<>();
                for(Document doc : QUOTES_COL.find()) quotes.add(doc);
                return Quotes.quoteDocToJson(quotes.get(RAND.nextInt(quotes.size())));
            };
        }
    }
}
