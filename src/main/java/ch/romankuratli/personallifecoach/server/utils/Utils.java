package ch.romankuratli.personallifecoach.server.utils;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;

import java.util.logging.Logger;

public class Utils {
    private Utils(){} // hide constructor, utility class
    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    public static Document getBodyJsonDoc(Request req) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(req.body());
        return new Document(jsonObj);
    }
}
